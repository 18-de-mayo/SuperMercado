package com.microservicio.pago.service;

import com.microservicio.pago.client.ClienteClient;
import com.microservicio.pago.client.PedidoClient;
import com.microservicio.pago.client.dto.PedidoResponseDTO;
import com.microservicio.pago.dto.PagoRequestDTO;
import com.microservicio.pago.dto.PagoResponseDTO;
import com.microservicio.pago.exception.EstadoPagoInvalidoException;
import com.microservicio.pago.exception.PagoNotFoundException;
import com.microservicio.pago.exception.PagoYaExisteException;
import com.microservicio.pago.model.Pago;
import com.microservicio.pago.model.Pago.EstadoPago;
import com.microservicio.pago.model.Pago.MetodoPago;
import com.microservicio.pago.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de PagoServiceImpl.
 *
 * Patrón utilizado: Given – When – Then
 * Dependencias simuladas con Mockito: PagoRepository, PedidoClient, ClienteClient.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PagoServiceImpl - Pruebas Unitarias")
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private PedidoClient pedidoClient;

    @Mock
    private ClienteClient clienteClient;

    @InjectMocks
    private PagoServiceImpl pagoService;

    // ── Fixtures ─────────────────────────────────────────────────────────────

    private Pago pagoPendiente;
    private Pago pagoCompletado;
    private PagoRequestDTO requestDTO;
    private PedidoResponseDTO pedidoDTO;

    @BeforeEach
    void setUp() {
        pagoPendiente = Pago.builder()
                .id(1L)
                .pedidoId(10L)
                .clienteId(3L)
                .numeroRecibo("REC-2025-000001")
                .monto(new BigDecimal("15990.50"))
                .metodoPago(MetodoPago.TARJETA_DEBITO)
                .estado(EstadoPago.PENDIENTE)
                .notas("Pago de prueba")
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        pagoCompletado = Pago.builder()
                .id(2L)
                .pedidoId(20L)
                .clienteId(3L)
                .numeroRecibo("REC-2025-000002")
                .monto(new BigDecimal("8500.00"))
                .metodoPago(MetodoPago.EFECTIVO)
                .estado(EstadoPago.COMPLETADO)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .fechaPago(LocalDateTime.now())
                .build();

        requestDTO = PagoRequestDTO.builder()
                .pedidoId(10L)
                .monto(new BigDecimal("15990.50"))
                .metodoPago(MetodoPago.TARJETA_DEBITO)
                .notas("Pago de prueba")
                .build();

        pedidoDTO = new PedidoResponseDTO();
        pedidoDTO.setId(10L);
        pedidoDTO.setClienteId(3L);
        pedidoDTO.setTotal(new BigDecimal("15990.50"));
        pedidoDTO.setEstado("CONFIRMADO");
    }

    // ════════════════════════════════════════════════════════════════════════
    // BLOQUE 1: CREAR PAGO
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("crearPago()")
    class CrearPagoTests {

        @Test
        @DisplayName("Debe crear un pago exitosamente cuando el pedido no tiene pago previo y el cliente está activo")
        void crearPago_pedidoSinPagoClienteActivo_retornaPagoCreado() {
            // Given
            when(pagoRepository.existsByPedidoId(10L)).thenReturn(false);
            when(pedidoClient.obtenerPedidoPorId(10L)).thenReturn(pedidoDTO);
            when(clienteClient.clienteEstaActivo(3L)).thenReturn(true);
            when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> {
                Pago p = inv.getArgument(0);
                p.setId(1L);
                p.setFechaCreacion(LocalDateTime.now());
                return p;
            });

            // When
            PagoResponseDTO resultado = pagoService.crearPago(requestDTO);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getPedidoId()).isEqualTo(10L);
            assertThat(resultado.getClienteId()).isEqualTo(3L);
            assertThat(resultado.getMonto()).isEqualByComparingTo("15990.50");
            assertThat(resultado.getEstado()).isEqualTo(EstadoPago.PENDIENTE);
            assertThat(resultado.getNumeroRecibo()).startsWith("REC-");
            verify(pagoRepository, times(2)).save(any(Pago.class)); // 1 inicial + 1 con recibo
        }

        @Test
        @DisplayName("Debe lanzar PagoYaExisteException cuando el pedido ya tiene un pago")
        void crearPago_pedidoConPagoExistente_lanzaPagoYaExisteException() {
            // Given
            when(pagoRepository.existsByPedidoId(10L)).thenReturn(true);

            // When / Then
            assertThatThrownBy(() -> pagoService.crearPago(requestDTO))
                    .isInstanceOf(PagoYaExisteException.class)
                    .hasMessageContaining("10");

            verify(pagoRepository, never()).save(any());
            verify(pedidoClient, never()).obtenerPedidoPorId(any());
        }

        @Test
        @DisplayName("Debe lanzar EstadoPagoInvalidoException cuando el cliente NO está activo")
        void crearPago_clienteInactivo_lanzaEstadoPagoInvalidoException() {
            // Given
            when(pagoRepository.existsByPedidoId(10L)).thenReturn(false);
            when(pedidoClient.obtenerPedidoPorId(10L)).thenReturn(pedidoDTO);
            when(clienteClient.clienteEstaActivo(3L)).thenReturn(false);

            // When / Then
            assertThatThrownBy(() -> pagoService.crearPago(requestDTO))
                    .isInstanceOf(EstadoPagoInvalidoException.class)
                    .hasMessageContaining("no está activo");

            verify(pagoRepository, never()).save(any());
        }

        @Test
        @DisplayName("El pago nuevo debe tener estado PENDIENTE por defecto")
        void crearPago_estadoInicial_esPendiente() {
            // Given
            when(pagoRepository.existsByPedidoId(anyLong())).thenReturn(false);
            when(pedidoClient.obtenerPedidoPorId(anyLong())).thenReturn(pedidoDTO);
            when(clienteClient.clienteEstaActivo(anyLong())).thenReturn(true);
            when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> {
                Pago p = inv.getArgument(0);
                p.setId(5L);
                return p;
            });

            // When
            PagoResponseDTO resultado = pagoService.crearPago(requestDTO);

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoPago.PENDIENTE);
        }

        @Test
        @DisplayName("El número de recibo debe tener formato REC-YYYY-NNNNNN")
        void crearPago_numeroRecibo_formatoCorrecto() {
            // Given
            when(pagoRepository.existsByPedidoId(anyLong())).thenReturn(false);
            when(pedidoClient.obtenerPedidoPorId(anyLong())).thenReturn(pedidoDTO);
            when(clienteClient.clienteEstaActivo(anyLong())).thenReturn(true);
            when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> {
                Pago p = inv.getArgument(0);
                p.setId(42L);
                return p;
            });

            // When
            PagoResponseDTO resultado = pagoService.crearPago(requestDTO);

            // Then
            assertThat(resultado.getNumeroRecibo()).matches("REC-\\d{4}-\\d{6}");
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // BLOQUE 2: CONSULTAS
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Consultas de pagos")
    class ConsultasTests {

        @Test
        @DisplayName("obtenerPorId: debe retornar el pago cuando el ID existe")
        void obtenerPorId_idExiste_retornaPago() {
            // Given
            when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoPendiente));

            // When
            PagoResponseDTO resultado = pagoService.obtenerPorId(1L);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNumeroRecibo()).isEqualTo("REC-2025-000001");
        }

        @Test
        @DisplayName("obtenerPorId: debe lanzar PagoNotFoundException cuando el ID no existe")
        void obtenerPorId_idNoExiste_lanzaPagoNotFoundException() {
            // Given
            when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> pagoService.obtenerPorId(99L))
                    .isInstanceOf(PagoNotFoundException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("obtenerPorPedidoId: debe retornar el pago asociado al pedido")
        void obtenerPorPedidoId_pedidoExiste_retornaPago() {
            // Given
            when(pagoRepository.findByPedidoId(10L)).thenReturn(Optional.of(pagoPendiente));

            // When
            PagoResponseDTO resultado = pagoService.obtenerPorPedidoId(10L);

            // Then
            assertThat(resultado.getPedidoId()).isEqualTo(10L);
        }

        @Test
        @DisplayName("obtenerPorPedidoId: debe lanzar excepción si el pedido no tiene pago")
        void obtenerPorPedidoId_sinPago_lanzaPagoNotFoundException() {
            // Given
            when(pagoRepository.findByPedidoId(999L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> pagoService.obtenerPorPedidoId(999L))
                    .isInstanceOf(PagoNotFoundException.class)
                    .hasMessageContaining("999");
        }

        @Test
        @DisplayName("obtenerPorNumeroRecibo: debe retornar el pago correcto")
        void obtenerPorNumeroRecibo_reciboExiste_retornaPago() {
            // Given
            when(pagoRepository.findByNumeroRecibo("REC-2025-000001"))
                    .thenReturn(Optional.of(pagoPendiente));

            // When
            PagoResponseDTO resultado = pagoService.obtenerPorNumeroRecibo("REC-2025-000001");

            // Then
            assertThat(resultado.getNumeroRecibo()).isEqualTo("REC-2025-000001");
        }

        @Test
        @DisplayName("listarTodos: debe retornar lista con todos los pagos")
        void listarTodos_retornaListaCompleta() {
            // Given
            when(pagoRepository.findAll()).thenReturn(List.of(pagoPendiente, pagoCompletado));

            // When
            List<PagoResponseDTO> resultado = pagoService.listarTodos();

            // Then
            assertThat(resultado).hasSize(2);
            assertThat(resultado).extracting(PagoResponseDTO::getId).contains(1L, 2L);
        }

        @Test
        @DisplayName("listarPorCliente: debe retornar los pagos del cliente dado")
        void listarPorCliente_clienteConPagos_retornaLista() {
            // Given
            when(pagoRepository.findByClienteId(3L))
                    .thenReturn(List.of(pagoPendiente, pagoCompletado));

            // When
            List<PagoResponseDTO> resultado = pagoService.listarPorCliente(3L);

            // Then
            assertThat(resultado).hasSize(2);
            assertThat(resultado).allMatch(p -> p.getClienteId().equals(3L));
        }

        @Test
        @DisplayName("listarPorEstado: debe filtrar correctamente por estado PENDIENTE")
        void listarPorEstado_estadoPendiente_retornaFiltrado() {
            // Given
            when(pagoRepository.findByEstado(EstadoPago.PENDIENTE))
                    .thenReturn(List.of(pagoPendiente));

            // When
            List<PagoResponseDTO> resultado = pagoService.listarPorEstado(EstadoPago.PENDIENTE);

            // Then
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoPago.PENDIENTE);
        }

        @Test
        @DisplayName("listarPorMetodoPago: debe filtrar por TARJETA_DEBITO")
        void listarPorMetodoPago_tarjetaDebito_retornaFiltrado() {
            // Given
            when(pagoRepository.findByMetodoPago(MetodoPago.TARJETA_DEBITO))
                    .thenReturn(List.of(pagoPendiente));

            // When
            List<PagoResponseDTO> resultado = pagoService.listarPorMetodoPago(MetodoPago.TARJETA_DEBITO);

            // Then
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getMetodoPago()).isEqualTo(MetodoPago.TARJETA_DEBITO);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // BLOQUE 3: CAMBIOS DE ESTADO (REGLAS DE NEGOCIO CRÍTICAS)
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("cambiarEstado() - Transiciones válidas e inválidas")
    class CambiarEstadoTests {

        @Test
        @DisplayName("PENDIENTE → COMPLETADO: transición válida; debe registrar fechaPago")
        void cambiarEstado_pendienteACompletado_exitoso() {
            // Given
            when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoPendiente));
            when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            PagoResponseDTO resultado = pagoService.cambiarEstado(1L, EstadoPago.COMPLETADO);

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoPago.COMPLETADO);
            assertThat(pagoPendiente.getFechaPago()).isNotNull();
            verify(pagoRepository, times(1)).save(pagoPendiente);
        }

        @Test
        @DisplayName("PENDIENTE → CANCELADO: transición válida")
        void cambiarEstado_pendienteACancelado_exitoso() {
            // Given
            when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoPendiente));
            when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            PagoResponseDTO resultado = pagoService.cambiarEstado(1L, EstadoPago.CANCELADO);

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoPago.CANCELADO);
        }

        @Test
        @DisplayName("PENDIENTE → FALLIDO: transición válida")
        void cambiarEstado_pendienteAFallido_exitoso() {
            // Given
            when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoPendiente));
            when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            PagoResponseDTO resultado = pagoService.cambiarEstado(1L, EstadoPago.FALLIDO);

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoPago.FALLIDO);
        }

        @Test
        @DisplayName("COMPLETADO → REEMBOLSADO: transición válida")
        void cambiarEstado_completadoAReembolsado_exitoso() {
            // Given
            when(pagoRepository.findById(2L)).thenReturn(Optional.of(pagoCompletado));
            when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            PagoResponseDTO resultado = pagoService.cambiarEstado(2L, EstadoPago.REEMBOLSADO);

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoPago.REEMBOLSADO);
        }

        /**
         * Regla de negocio clave: COMPLETADO no puede volver a PENDIENTE.
         */
        @Test
        @DisplayName("COMPLETADO → PENDIENTE: transición INVÁLIDA debe lanzar EstadoPagoInvalidoException")
        void cambiarEstado_completadoAPendiente_lanzaExcepcion() {
            // Given
            when(pagoRepository.findById(2L)).thenReturn(Optional.of(pagoCompletado));

            // When / Then
            assertThatThrownBy(() -> pagoService.cambiarEstado(2L, EstadoPago.PENDIENTE))
                    .isInstanceOf(EstadoPagoInvalidoException.class)
                    .hasMessageContaining("COMPLETADO")
                    .hasMessageContaining("PENDIENTE");

            verify(pagoRepository, never()).save(any());
        }

        @Test
        @DisplayName("CANCELADO → COMPLETADO: transición INVÁLIDA desde estado terminal")
        void cambiarEstado_canceladoACompletado_lanzaExcepcion() {
            // Given
            pagoPendiente.setEstado(EstadoPago.CANCELADO);
            when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoPendiente));

            // When / Then
            assertThatThrownBy(() -> pagoService.cambiarEstado(1L, EstadoPago.COMPLETADO))
                    .isInstanceOf(EstadoPagoInvalidoException.class);

            verify(pagoRepository, never()).save(any());
        }

        @Test
        @DisplayName("FALLIDO → PENDIENTE: transición INVÁLIDA desde estado terminal")
        void cambiarEstado_fallidoAPendiente_lanzaExcepcion() {
            // Given
            pagoPendiente.setEstado(EstadoPago.FALLIDO);
            when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoPendiente));

            // When / Then
            assertThatThrownBy(() -> pagoService.cambiarEstado(1L, EstadoPago.PENDIENTE))
                    .isInstanceOf(EstadoPagoInvalidoException.class);
        }

        @Test
        @DisplayName("REEMBOLSADO → cualquier estado: transición INVÁLIDA")
        void cambiarEstado_reembolsadoACualquierEstado_lanzaExcepcion() {
            // Given
            pagoCompletado.setEstado(EstadoPago.REEMBOLSADO);
            when(pagoRepository.findById(2L)).thenReturn(Optional.of(pagoCompletado));

            // When / Then
            assertThatThrownBy(() -> pagoService.cambiarEstado(2L, EstadoPago.COMPLETADO))
                    .isInstanceOf(EstadoPagoInvalidoException.class);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // BLOQUE 4: ATAJOS DE CAMBIO DE ESTADO
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Métodos rápidos de cambio de estado")
    class MetodosRapidosEstadoTests {

        @Test
        @DisplayName("confirmarPago: debe delegar a cambiarEstado con COMPLETADO")
        void confirmarPago_pagoValido_retornaCompletado() {
            // Given
            when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoPendiente));
            when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            PagoResponseDTO resultado = pagoService.confirmarPago(1L);

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoPago.COMPLETADO);
        }

        @Test
        @DisplayName("cancelarPago: debe delegar a cambiarEstado con CANCELADO")
        void cancelarPago_pagoValido_retornaCancelado() {
            // Given
            when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoPendiente));
            when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            PagoResponseDTO resultado = pagoService.cancelarPago(1L);

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoPago.CANCELADO);
        }

        @Test
        @DisplayName("marcarComoFallido: debe delegar a cambiarEstado con FALLIDO")
        void marcarComoFallido_pagoValido_retornaFallido() {
            // Given
            when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoPendiente));
            when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            PagoResponseDTO resultado = pagoService.marcarComoFallido(1L);

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoPago.FALLIDO);
        }

        @Test
        @DisplayName("reembolsarPago: debe delegar a cambiarEstado con REEMBOLSADO")
        void reembolsarPago_pagoCompletado_retornaReembolsado() {
            // Given
            when(pagoRepository.findById(2L)).thenReturn(Optional.of(pagoCompletado));
            when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            PagoResponseDTO resultado = pagoService.reembolsarPago(2L);

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoPago.REEMBOLSADO);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // BLOQUE 5: ELIMINACIÓN
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("eliminarPago()")
    class EliminarPagoTests {

        @Test
        @DisplayName("Debe eliminar correctamente un pago CANCELADO")
        void eliminarPago_estadoCancelado_eliminaExitosamente() {
            // Given
            pagoPendiente.setEstado(EstadoPago.CANCELADO);
            when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoPendiente));
            doNothing().when(pagoRepository).delete(pagoPendiente);

            // When
            pagoService.eliminarPago(1L);

            // Then
            verify(pagoRepository, times(1)).delete(pagoPendiente);
        }

        @Test
        @DisplayName("Debe eliminar correctamente un pago FALLIDO")
        void eliminarPago_estadoFallido_eliminaExitosamente() {
            // Given
            pagoPendiente.setEstado(EstadoPago.FALLIDO);
            when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoPendiente));
            doNothing().when(pagoRepository).delete(pagoPendiente);

            // When
            pagoService.eliminarPago(1L);

            // Then
            verify(pagoRepository, times(1)).delete(pagoPendiente);
        }

        @Test
        @DisplayName("Debe lanzar EstadoPagoInvalidoException al intentar eliminar un pago PENDIENTE")
        void eliminarPago_estadoPendiente_lanzaExcepcion() {
            // Given
            when(pagoRepository.findById(1L)).thenReturn(Optional.of(pagoPendiente));

            // When / Then
            assertThatThrownBy(() -> pagoService.eliminarPago(1L))
                    .isInstanceOf(EstadoPagoInvalidoException.class)
                    .hasMessageContaining("CANCELADO o FALLIDO");

            verify(pagoRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Debe lanzar PagoNotFoundException al intentar eliminar un ID inexistente")
        void eliminarPago_idNoExiste_lanzaPagoNotFoundException() {
            // Given
            when(pagoRepository.findById(999L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> pagoService.eliminarPago(999L))
                    .isInstanceOf(PagoNotFoundException.class);

            verify(pagoRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción al intentar eliminar un pago COMPLETADO")
        void eliminarPago_estadoCompletado_lanzaExcepcion() {
            // Given
            when(pagoRepository.findById(2L)).thenReturn(Optional.of(pagoCompletado));

            // When / Then
            assertThatThrownBy(() -> pagoService.eliminarPago(2L))
                    .isInstanceOf(EstadoPagoInvalidoException.class);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // BLOQUE 6: REPORTE FINANCIERO
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("calcularTotalCompletados()")
    class ReporteTests {

        @Test
        @DisplayName("Debe retornar el total sumado de pagos COMPLETADOS en el rango dado")
        void calcularTotal_rangoValido_retornaSuma() {
            // Given
            LocalDateTime desde = LocalDateTime.of(2025, 1, 1, 0, 0);
            LocalDateTime hasta = LocalDateTime.of(2025, 12, 31, 23, 59);
            when(pagoRepository.sumarMontosCompletadosEntreFechas(desde, hasta))
                    .thenReturn(new BigDecimal("50000.00"));

            // When
            BigDecimal total = pagoService.calcularTotalCompletados(desde, hasta);

            // Then
            assertThat(total).isEqualByComparingTo("50000.00");
        }

        @Test
        @DisplayName("Debe retornar 0 cuando no hay pagos COMPLETADOS en el rango")
        void calcularTotal_sinPagosCompletados_retornaCero() {
            // Given
            LocalDateTime desde = LocalDateTime.of(2024, 1, 1, 0, 0);
            LocalDateTime hasta = LocalDateTime.of(2024, 12, 31, 23, 59);
            when(pagoRepository.sumarMontosCompletadosEntreFechas(desde, hasta))
                    .thenReturn(BigDecimal.ZERO);

            // When
            BigDecimal total = pagoService.calcularTotalCompletados(desde, hasta);

            // Then
            assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // BLOQUE 7: MAPEO DTO (verificación de completitud del DTO)
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("mapToDTO() - Integridad del mapeo")
    class MapToDTOTests {

        @Test
        @DisplayName("El DTO de respuesta debe contener todos los campos de la entidad")
        void mapToDTO_entidadCompleta_dtoCamposCorrectos() {
            // Given (el servicio expone mapToDTO como package-private para tests)
            Pago pago = Pago.builder()
                    .id(99L)
                    .pedidoId(50L)
                    .clienteId(7L)
                    .numeroRecibo("REC-2025-000099")
                    .monto(new BigDecimal("999.99"))
                    .metodoPago(MetodoPago.TRANSFERENCIA_BANCARIA)
                    .estado(EstadoPago.COMPLETADO)
                    .notas("Nota de prueba")
                    .fechaCreacion(LocalDateTime.of(2025, 6, 1, 10, 0))
                    .fechaActualizacion(LocalDateTime.of(2025, 6, 1, 11, 0))
                    .fechaPago(LocalDateTime.of(2025, 6, 1, 11, 0))
                    .build();

            // When
            PagoResponseDTO dto = pagoService.mapToDTO(pago);

            // Then
            assertThat(dto.getId()).isEqualTo(99L);
            assertThat(dto.getPedidoId()).isEqualTo(50L);
            assertThat(dto.getClienteId()).isEqualTo(7L);
            assertThat(dto.getNumeroRecibo()).isEqualTo("REC-2025-000099");
            assertThat(dto.getMonto()).isEqualByComparingTo("999.99");
            assertThat(dto.getMetodoPago()).isEqualTo(MetodoPago.TRANSFERENCIA_BANCARIA);
            assertThat(dto.getEstado()).isEqualTo(EstadoPago.COMPLETADO);
            assertThat(dto.getNotas()).isEqualTo("Nota de prueba");
            assertThat(dto.getFechaPago()).isNotNull();
        }
    }
}
