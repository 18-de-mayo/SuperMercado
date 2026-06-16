package com.microservicio.cliente.service;

import com.microservicio.cliente.dto.ClienteRequestDTO;
import com.microservicio.cliente.dto.ClienteResponseDTO;
import com.microservicio.cliente.exception.ClienteNotFoundException;
import com.microservicio.cliente.exception.ClienteYaExisteException;
import com.microservicio.cliente.exception.EstadoInvalidoException;
import com.microservicio.cliente.model.Cliente;
import com.microservicio.cliente.model.Cliente.EstadoCliente;
import com.microservicio.cliente.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de clientes.
 * Patrón: Given – When – Then
 * Se mockea ClienteRepository para aislar la lógica de negocio.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteServiceImpl - Pruebas Unitarias")
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente clienteActivo;
    private ClienteRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        clienteActivo = Cliente.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .rut("12345678-9")
                .email("juan@email.com")
                .telefono("912345678")
                .direccion("Av. Siempre Viva 123")
                .ciudad("Santiago")
                .region("Metropolitana")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .estado(EstadoCliente.ACTIVO)
                .fechaRegistro(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .build();

        requestDTO = ClienteRequestDTO.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .rut("12345678-9")
                .email("juan@email.com")
                .telefono("912345678")
                .direccion("Av. Siempre Viva 123")
                .ciudad("Santiago")
                .region("Metropolitana")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .build();
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 1: CREAR CLIENTE
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("crearCliente()")
    class CrearClienteTests {

        @Test
        @DisplayName("Debe crear un cliente exitosamente cuando el email y RUT son únicos")
        void crearCliente_emailYRutUnicos_retornaClienteCreado() {
            // Given
            when(clienteRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
            when(clienteRepository.existsByRut(requestDTO.getRut())).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteActivo);

            // When
            ClienteResponseDTO resultado = clienteService.crearCliente(requestDTO);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getEmail()).isEqualTo("juan@email.com");
            assertThat(resultado.getNombre()).isEqualTo("Juan");
            assertThat(resultado.getEstado()).isEqualTo(EstadoCliente.ACTIVO);
            verify(clienteRepository, times(1)).save(any(Cliente.class));
        }

        @Test
        @DisplayName("Debe lanzar ClienteYaExisteException cuando el email ya está registrado")
        void crearCliente_emailDuplicado_lanzaClienteYaExisteException() {
            // Given
            when(clienteRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

            // When / Then
            assertThatThrownBy(() -> clienteService.crearCliente(requestDTO))
                    .isInstanceOf(ClienteYaExisteException.class)
                    .hasMessageContaining("juan@email.com");

            verify(clienteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar ClienteYaExisteException cuando el RUT ya está registrado")
        void crearCliente_rutDuplicado_lanzaClienteYaExisteException() {
            // Given
            when(clienteRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
            when(clienteRepository.existsByRut(requestDTO.getRut())).thenReturn(true);

            // When / Then
            assertThatThrownBy(() -> clienteService.crearCliente(requestDTO))
                    .isInstanceOf(ClienteYaExisteException.class)
                    .hasMessageContaining("12345678-9");

            verify(clienteRepository, never()).save(any());
        }

        @Test
        @DisplayName("El cliente nuevo debe tener estado ACTIVO por defecto")
        void crearCliente_nuevoCliente_estadoEsActivoPorDefecto() {
            // Given
            when(clienteRepository.existsByEmail(anyString())).thenReturn(false);
            when(clienteRepository.existsByRut(anyString())).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> {
                Cliente c = inv.getArgument(0);
                c.setId(1L);
                c.setFechaRegistro(LocalDateTime.now());
                return c;
            });

            // When
            ClienteResponseDTO resultado = clienteService.crearCliente(requestDTO);

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoCliente.ACTIVO);
        }
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 2: OBTENER CLIENTE
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("obtenerClientePorId()")
    class ObtenerClienteTests {

        @Test
        @DisplayName("Debe retornar el cliente cuando el ID existe")
        void obtenerPorId_idExiste_retornaCliente() {
            // Given
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteActivo));

            // When
            ClienteResponseDTO resultado = clienteService.obtenerClientePorId(1L);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getEmail()).isEqualTo("juan@email.com");
        }

        @Test
        @DisplayName("Debe lanzar ClienteNotFoundException cuando el ID no existe")
        void obtenerPorId_idNoExiste_lanzaClienteNotFoundException() {
            // Given
            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> clienteService.obtenerClientePorId(99L))
                    .isInstanceOf(ClienteNotFoundException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("Debe retornar cliente al buscar por email existente")
        void obtenerPorEmail_emailExiste_retornaCliente() {
            // Given
            when(clienteRepository.findByEmail("juan@email.com"))
                    .thenReturn(Optional.of(clienteActivo));

            // When
            ClienteResponseDTO resultado = clienteService.obtenerClientePorEmail("juan@email.com");

            // Then
            assertThat(resultado.getEmail()).isEqualTo("juan@email.com");
        }

        @Test
        @DisplayName("Debe retornar cliente al buscar por RUT existente")
        void obtenerPorRut_rutExiste_retornaCliente() {
            // Given
            when(clienteRepository.findByRut("12345678-9"))
                    .thenReturn(Optional.of(clienteActivo));

            // When
            ClienteResponseDTO resultado = clienteService.obtenerClientePorRut("12345678-9");

            // Then
            assertThat(resultado.getRut()).isEqualTo("12345678-9");
        }

        @Test
        @DisplayName("Debe retornar lista completa de clientes")
        void listarClientes_retornaLista() {
            // Given
            when(clienteRepository.findAll()).thenReturn(List.of(clienteActivo));

            // When
            List<ClienteResponseDTO> resultado = clienteService.listarClientes();

            // Then
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getNombre()).isEqualTo("Juan");
        }
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 3: ACTUALIZAR CLIENTE
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("actualizarCliente()")
    class ActualizarClienteTests {

        @Test
        @DisplayName("Debe actualizar el cliente cuando los nuevos datos son válidos")
        void actualizarCliente_datosValidos_retornaClienteActualizado() {
            // Given
            ClienteRequestDTO nuevosDatos = ClienteRequestDTO.builder()
                    .nombre("Juan Carlos")
                    .apellido("Pérez")
                    .rut("12345678-9")
                    .email("juan@email.com")
                    .telefono("987654321")
                    .direccion("Nueva Dirección 456")
                    .ciudad("Valparaíso")
                    .region("Valparaíso")
                    .fechaNacimiento(LocalDate.of(1990, 5, 15))
                    .build();

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteActivo));
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            ClienteResponseDTO resultado = clienteService.actualizarCliente(1L, nuevosDatos);

            // Then
            assertThat(resultado.getNombre()).isEqualTo("Juan Carlos");
            assertThat(resultado.getCiudad()).isEqualTo("Valparaíso");
            verify(clienteRepository, times(1)).save(any(Cliente.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el nuevo email ya existe en otro cliente")
        void actualizarCliente_emailDuplicado_lanzaClienteYaExisteException() {
            // Given
            ClienteRequestDTO dtoEmailCambiado = ClienteRequestDTO.builder()
                    .nombre("Juan").apellido("Pérez").rut("12345678-9")
                    .email("otro@email.com")
                    .telefono("912345678").direccion("Dir").ciudad("Santiago")
                    .region("RM").fechaNacimiento(LocalDate.of(1990, 1, 1))
                    .build();

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteActivo));
            when(clienteRepository.existsByEmail("otro@email.com")).thenReturn(true);

            // When / Then
            assertThatThrownBy(() -> clienteService.actualizarCliente(1L, dtoEmailCambiado))
                    .isInstanceOf(ClienteYaExisteException.class)
                    .hasMessageContaining("otro@email.com");
        }
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 4: CAMBIO DE ESTADO
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("cambiarEstado()")
    class CambiarEstadoTests {

        @Test
        @DisplayName("Debe cambiar estado de ACTIVO a INACTIVO correctamente")
        void cambiarEstado_activoAInactivo_exitoso() {
            // Given
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteActivo));
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            ClienteResponseDTO resultado = clienteService.cambiarEstado(1L, EstadoCliente.INACTIVO);

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoCliente.INACTIVO);
        }

        @Test
        @DisplayName("Debe cambiar estado de ACTIVO a SUSPENDIDO correctamente")
        void cambiarEstado_activoASuspendido_exitoso() {
            // Given
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteActivo));
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            ClienteResponseDTO resultado = clienteService.cambiarEstado(1L, EstadoCliente.SUSPENDIDO);

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoCliente.SUSPENDIDO);
        }

        /**
         * Regla de negocio clave: SUSPENDIDO → ACTIVO no está permitido directamente.
         * El cliente debe pasar por INACTIVO primero.
         */
        @Test
        @DisplayName("Debe lanzar EstadoInvalidoException al intentar SUSPENDIDO → ACTIVO")
        void cambiarEstado_suspendidoAActivo_lanzaEstadoInvalidoException() {
            // Given
            clienteActivo.setEstado(EstadoCliente.SUSPENDIDO);
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteActivo));

            // When / Then
            assertThatThrownBy(() -> clienteService.cambiarEstado(1L, EstadoCliente.ACTIVO))
                    .isInstanceOf(EstadoInvalidoException.class)
                    .hasMessageContaining("INACTIVO");

            verify(clienteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe permitir SUSPENDIDO → INACTIVO (transición válida)")
        void cambiarEstado_suspendidoAInactivo_exitoso() {
            // Given
            clienteActivo.setEstado(EstadoCliente.SUSPENDIDO);
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteActivo));
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            ClienteResponseDTO resultado = clienteService.cambiarEstado(1L, EstadoCliente.INACTIVO);

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoCliente.INACTIVO);
        }
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 5: ELIMINAR CLIENTE
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("eliminarCliente()")
    class EliminarClienteTests {

        @Test
        @DisplayName("Debe eliminar el cliente cuando el ID existe")
        void eliminarCliente_idExiste_eliminaCorrectamente() {
            // Given
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteActivo));
            doNothing().when(clienteRepository).delete(clienteActivo);

            // When
            clienteService.eliminarCliente(1L);

            // Then
            verify(clienteRepository, times(1)).delete(clienteActivo);
        }

        @Test
        @DisplayName("Debe lanzar ClienteNotFoundException al intentar eliminar ID inexistente")
        void eliminarCliente_idNoExiste_lanzaClienteNotFoundException() {
            // Given
            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> clienteService.eliminarCliente(99L))
                    .isInstanceOf(ClienteNotFoundException.class);

            verify(clienteRepository, never()).delete(any());
        }
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 6: VERIFICAR ESTADO ACTIVO
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("clienteEstaActivo()")
    class ClienteEstaActivoTests {

        @Test
        @DisplayName("Debe retornar true cuando el cliente está ACTIVO")
        void clienteEstaActivo_estadoActivo_retornaTrue() {
            // Given
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteActivo));

            // When
            boolean resultado = clienteService.clienteEstaActivo(1L);

            // Then
            assertThat(resultado).isTrue();
        }

        @Test
        @DisplayName("Debe retornar false cuando el cliente está INACTIVO")
        void clienteEstaActivo_estadoInactivo_retornaFalse() {
            // Given
            clienteActivo.setEstado(EstadoCliente.INACTIVO);
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteActivo));

            // When
            boolean resultado = clienteService.clienteEstaActivo(1L);

            // Then
            assertThat(resultado).isFalse();
        }

        @Test
        @DisplayName("Debe retornar false cuando el cliente está SUSPENDIDO")
        void clienteEstaActivo_estadoSuspendido_retornaFalse() {
            // Given
            clienteActivo.setEstado(EstadoCliente.SUSPENDIDO);
            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteActivo));

            // When
            boolean resultado = clienteService.clienteEstaActivo(1L);

            // Then
            assertThat(resultado).isFalse();
        }
    }
}
