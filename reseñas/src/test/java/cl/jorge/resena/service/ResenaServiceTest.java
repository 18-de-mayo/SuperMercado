package cl.jorge.resena.service;

import cl.jorge.resena.client.ClienteClient;
import cl.jorge.resena.client.PagoClient;
import cl.jorge.resena.client.PedidoClient;
import cl.jorge.resena.client.ProductoClient;
import cl.jorge.resena.dto.*;
import cl.jorge.resena.exception.DuplicateResourceException;
import cl.jorge.resena.exception.EstadoInvalidoException;
import cl.jorge.resena.exception.ResourceNotFoundException;
import cl.jorge.resena.model.EstadoResena;
import cl.jorge.resena.model.Resena;
import cl.jorge.resena.model.RespuestaResena;
import cl.jorge.resena.repository.ResenaRepository;
import cl.jorge.resena.repository.RespuestaResenaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;

    @Mock
    private RespuestaResenaRepository respuestaResenaRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private PedidoClient pedidoClient;

    @Mock
    private PagoClient pagoClient;

    @InjectMocks
    private ResenaService service;

    private ResenaRequest request;
    private Resena resenaPendiente;
    private Resena resenaAprobada;
    private Resena resenaRechazada;

    @BeforeEach
    void setUp() {
        request = new ResenaRequest();
        request.setClienteId(1L);
        request.setProductoId(2L);
        request.setPedidoId(3L);
        request.setCalificacion(5);
        request.setTitulo("Excelente");
        request.setComentario("Todo perfecto");

        resenaPendiente = new Resena();
        resenaPendiente.setId(1L);
        resenaPendiente.setClienteId(1L);
        resenaPendiente.setProductoId(2L);
        resenaPendiente.setPedidoId(3L);
        resenaPendiente.setCalificacion(5);
        resenaPendiente.setTitulo("Excelente");
        resenaPendiente.setComentario("Todo perfecto");
        resenaPendiente.setEstado(EstadoResena.PENDIENTE);
        resenaPendiente.setFechaCreacion(LocalDateTime.now());

        resenaAprobada = new Resena();
        resenaAprobada.setId(2L);
        resenaAprobada.setClienteId(1L);
        resenaAprobada.setProductoId(2L);
        resenaAprobada.setPedidoId(3L);
        resenaAprobada.setCalificacion(4);
        resenaAprobada.setTitulo("Bueno");
        resenaAprobada.setComentario("Muy buen producto");
        resenaAprobada.setEstado(EstadoResena.APROBADA);
        resenaAprobada.setFechaCreacion(LocalDateTime.now());

        resenaRechazada = new Resena();
        resenaRechazada.setId(3L);
        resenaRechazada.setClienteId(2L);
        resenaRechazada.setProductoId(2L);
        resenaRechazada.setPedidoId(4L);
        resenaRechazada.setCalificacion(2);
        resenaRechazada.setTitulo("Malo");
        resenaRechazada.setComentario("Producto defectuoso");
        resenaRechazada.setEstado(EstadoResena.RECHAZADA);
        resenaRechazada.setFechaCreacion(LocalDateTime.now());
    }

    @Nested
    @DisplayName("crearResena()")
    class CrearResenaTests {

        @Test
        @DisplayName("Debe crear una resena exitosamente cuando todo es valido")
        void givenDatosValidos_whenCrearResena_thenRetornaDTO() {
            when(clienteClient.obtenerClientePorId(1L)).thenReturn(new ClienteResumenDTO());
            when(productoClient.obtenerProductoPorId(2L)).thenReturn(new ProductoResumenDTO());
            when(pedidoClient.obtenerPedidoPorId(3L)).thenReturn(new Object());
            when(resenaRepository.findByClienteIdAndProductoIdAndPedidoId(1L, 2L, 3L)).thenReturn(Optional.empty());
            when(resenaRepository.save(any(Resena.class))).thenReturn(resenaPendiente);

            var resultado = service.crearResena(request);

            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getEstado()).isEqualTo(EstadoResena.PENDIENTE);
        }

        @Test
        @DisplayName("Debe lanzar DuplicateResourceException cuando ya existe una resena duplicada")
        void givenResenaDuplicada_whenCrearResena_thenLanzaDuplicateResourceException() {
            when(clienteClient.obtenerClientePorId(1L)).thenReturn(new ClienteResumenDTO());
            when(productoClient.obtenerProductoPorId(2L)).thenReturn(new ProductoResumenDTO());
            when(pedidoClient.obtenerPedidoPorId(3L)).thenReturn(new Object());
            when(resenaRepository.findByClienteIdAndProductoIdAndPedidoId(1L, 2L, 3L)).thenReturn(Optional.of(resenaPendiente));

            assertThatThrownBy(() -> service.crearResena(request))
                    .isInstanceOf(DuplicateResourceException.class);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el cliente no existe")
        void givenClienteNoExiste_whenCrearResena_thenLanzaResourceNotFoundException() {
            when(clienteClient.obtenerClientePorId(1L)).thenThrow(new RuntimeException("Cliente no encontrado"));

            assertThatThrownBy(() -> service.crearResena(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("El cliente con ID 1 no existe");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el producto no existe")
        void givenProductoNoExiste_whenCrearResena_thenLanzaResourceNotFoundException() {
            when(clienteClient.obtenerClientePorId(1L)).thenReturn(new ClienteResumenDTO());
            when(productoClient.obtenerProductoPorId(2L)).thenThrow(new RuntimeException("Producto no encontrado"));

            assertThatThrownBy(() -> service.crearResena(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("El producto con ID 2 no existe");
        }
    }

    @Nested
    @DisplayName("obtenerTodas()")
    class ObtenerTodasTests {

        @Test
        @DisplayName("Debe retornar lista de resenas cuando existen registros")
        void givenResenasExisten_whenObtenerTodas_thenRetornaLista() {
            when(resenaRepository.findAll()).thenReturn(List.of(resenaPendiente, resenaAprobada));

            List<ResenaDTO> resultado = service.obtenerTodas();

            assertThat(resultado).hasSize(2);
            assertThat(resultado).extracting(ResenaDTO::getId).containsExactlyInAnyOrder(1L, 2L);
        }

        @Test
        @DisplayName("Debe retornar lista vacia cuando no hay resenas")
        void givenNoHayResenas_whenObtenerTodas_thenRetornaListaVacia() {
            when(resenaRepository.findAll()).thenReturn(List.of());

            List<ResenaDTO> resultado = service.obtenerTodas();

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorIdTests {

        @Test
        @DisplayName("Debe retornar resena cuando el ID existe")
        void givenIdExiste_whenObtenerPorId_thenRetornaDTO() {
            when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaPendiente));

            ResenaDTO resultado = service.obtenerPorId(1L);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getEstado()).isEqualTo(EstadoResena.PENDIENTE);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el ID no existe")
        void givenIdNoExiste_whenObtenerPorId_thenLanzaResourceNotFoundException() {
            when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.obtenerPorId(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("obtenerPorCliente()")
    class ObtenerPorClienteTests {

        @Test
        @DisplayName("Debe retornar resenas de un cliente especifico")
        void givenClienteId_whenObtenerPorCliente_thenRetornaLista() {
            when(resenaRepository.findByClienteId(1L)).thenReturn(List.of(resenaPendiente, resenaAprobada));

            List<ResenaDTO> resultado = service.obtenerPorCliente(1L);

            assertThat(resultado).hasSize(2);
            assertThat(resultado).allMatch(r -> r.getClienteId().equals(1L));
        }
    }

    @Nested
    @DisplayName("obtenerPorProducto()")
    class ObtenerPorProductoTests {

        @Test
        @DisplayName("Debe retornar resenas de un producto especifico")
        void givenProductoId_whenObtenerPorProducto_thenRetornaLista() {
            when(resenaRepository.findByProductoId(2L)).thenReturn(List.of(resenaPendiente, resenaAprobada));

            List<ResenaDTO> resultado = service.obtenerPorProducto(2L);

            assertThat(resultado).hasSize(2);
            assertThat(resultado).allMatch(r -> r.getProductoId().equals(2L));
        }
    }

    @Nested
    @DisplayName("obtenerAprobadasPorProducto()")
    class ObtenerAprobadasPorProductoTests {

        @Test
        @DisplayName("Debe retornar solo resenas aprobadas de un producto")
        void givenProductoId_whenObtenerAprobadasPorProducto_thenRetornaListaAprobadas() {
            when(resenaRepository.findByProductoIdAndEstado(2L, EstadoResena.APROBADA)).thenReturn(List.of(resenaAprobada));

            List<ResenaDTO> resultado = service.obtenerResenaAprobadasPorProducto(2L);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoResena.APROBADA);
        }
    }

    @Nested
    @DisplayName("obtenerPorEstado()")
    class ObtenerPorEstadoTests {

        @Test
        @DisplayName("Debe filtrar resenas por estado")
        void givenEstado_whenObtenerPorEstado_thenRetornaListaFiltrada() {
            when(resenaRepository.findByEstado(EstadoResena.PENDIENTE)).thenReturn(List.of(resenaPendiente));

            List<ResenaDTO> resultado = service.obtenerPorEstado(EstadoResena.PENDIENTE);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoResena.PENDIENTE);
        }
    }

    @Nested
    @DisplayName("actualizarResena()")
    class ActualizarResenaTests {

        @Test
        @DisplayName("Debe actualizar resena exitosamente cuando esta en PENDIENTE")
        void givenResenaPendiente_whenActualizarResena_thenRetornaDTOActualizado() {
            when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaPendiente));
            when(resenaRepository.save(any(Resena.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ResenaDTO resultado = service.actualizarResena(1L, request);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getCalificacion()).isEqualTo(5);
            assertThat(resultado.getComentario()).isEqualTo("Todo perfecto");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando la resena no existe")
        void givenIdNoExiste_whenActualizarResena_thenLanzaResourceNotFoundException() {
            when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizarResena(99L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("actualizarEstado()")
    class ActualizarEstadoTests {

        @Test
        @DisplayName("Debe permitir transicion de PENDIENTE a APROBADA")
        void givenPendiente_whenActualizarEstadoAprobada_thenRetornaDTO() {
            ActualizarEstadoRequest estadoRequest = new ActualizarEstadoRequest();
            estadoRequest.setNuevoEstado(EstadoResena.APROBADA);
            when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaPendiente));
            when(resenaRepository.save(any(Resena.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var resultado = service.actualizarEstado(1L, estadoRequest);

            assertThat(resultado.getEstado()).isEqualTo(EstadoResena.APROBADA);
        }

        @Test
        @DisplayName("Debe permitir transicion de APROBADA a RECHAZADA")
        void givenAprobada_whenActualizarEstadoRechazada_thenRetornaDTO() {
            ActualizarEstadoRequest estadoRequest = new ActualizarEstadoRequest();
            estadoRequest.setNuevoEstado(EstadoResena.RECHAZADA);
            when(resenaRepository.findById(2L)).thenReturn(Optional.of(resenaAprobada));
            when(resenaRepository.save(any(Resena.class))).thenAnswer(invocation -> invocation.getArgument(0));

            var resultado = service.actualizarEstado(2L, estadoRequest);

            assertThat(resultado.getEstado()).isEqualTo(EstadoResena.RECHAZADA);
        }

        @Test
        @DisplayName("Debe rechazar transicion de PENDIENTE a PENDIENTE (mismo estado)")
        void givenPendiente_whenActualizarEstadoPendiente_thenLanzaEstadoInvalidoException() {
            ActualizarEstadoRequest estadoRequest = new ActualizarEstadoRequest();
            estadoRequest.setNuevoEstado(EstadoResena.PENDIENTE);
            when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaPendiente));

            assertThatThrownBy(() -> service.actualizarEstado(1L, estadoRequest))
                    .isInstanceOf(EstadoInvalidoException.class)
                    .hasMessageContaining("Transicion no permitida");
            verify(resenaRepository, never()).save(any(Resena.class));
        }

        @Test
        @DisplayName("Debe rechazar transicion de RECHAZADA a APROBADA (estado terminal)")
        void givenRechazada_whenActualizarEstadoAprobada_thenLanzaEstadoInvalidoException() {
            ActualizarEstadoRequest estadoRequest = new ActualizarEstadoRequest();
            estadoRequest.setNuevoEstado(EstadoResena.APROBADA);
            when(resenaRepository.findById(3L)).thenReturn(Optional.of(resenaRechazada));

            assertThatThrownBy(() -> service.actualizarEstado(3L, estadoRequest))
                    .isInstanceOf(EstadoInvalidoException.class)
                    .hasMessageContaining("Transicion no permitida");
            verify(resenaRepository, never()).save(any(Resena.class));
        }
    }

    @Nested
    @DisplayName("eliminarResena()")
    class EliminarResenaTests {

        @Test
        @DisplayName("Debe eliminar resena exitosamente cuando el ID existe")
        void givenIdExiste_whenEliminarResena_thenEliminaCorrectamente() {
            when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaPendiente));
            doNothing().when(resenaRepository).delete(resenaPendiente);

            service.eliminarResena(1L);

            verify(resenaRepository, times(1)).delete(resenaPendiente);
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el ID no existe")
        void givenIdNoExiste_whenEliminarResena_thenLanzaResourceNotFoundException() {
            when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.eliminarResena(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
            verify(resenaRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("agregarRespuesta()")
    class AgregarRespuestaTests {

        @Test
        @DisplayName("Debe agregar respuesta cuando la resena esta APROBADA")
        void givenResenaAprobada_whenAgregarRespuesta_thenRetornaRespuesta() {
            RespuestaResena respuesta = new RespuestaResena();
            respuesta.setId(10L);
            respuesta.setResena(resenaAprobada);
            respuesta.setAutor("Soporte");
            respuesta.setContenido("Gracias por tu comentario");
            respuesta.setFechaCreacion(LocalDateTime.now());

            RespuestaResenaRequest respuestaRequest = new RespuestaResenaRequest();
            respuestaRequest.setAutor("Soporte");
            respuestaRequest.setContenido("Gracias por tu comentario");

            when(resenaRepository.findById(2L)).thenReturn(Optional.of(resenaAprobada));
            when(respuestaResenaRepository.save(any(RespuestaResena.class))).thenReturn(respuesta);

            RespuestaResenaDTO resultado = service.agregarRespuesta(2L, respuestaRequest);

            assertThat(resultado.getId()).isEqualTo(10L);
            assertThat(resultado.getAutor()).isEqualTo("Soporte");
        }

        @Test
        @DisplayName("Debe lanzar EstadoInvalidoException cuando la resena no esta APROBADA")
        void givenResenaNoAprobada_whenAgregarRespuesta_thenLanzaEstadoInvalidoException() {
            RespuestaResenaRequest respuestaRequest = new RespuestaResenaRequest();
            respuestaRequest.setAutor("Soporte");
            respuestaRequest.setContenido("Gracias por tu comentario");

            when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaPendiente));

            assertThatThrownBy(() -> service.agregarRespuesta(1L, respuestaRequest))
                    .isInstanceOf(EstadoInvalidoException.class)
                    .hasMessageContaining("Solo se pueden responder resenas en estado APROBADA");
            verify(respuestaResenaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("obtenerRespuestas()")
    class ObtenerRespuestasTests {

        @Test
        @DisplayName("Debe retornar lista de respuestas de una resena")
        void givenResenaId_whenObtenerRespuestas_thenRetornaLista() {
            RespuestaResena respuesta = new RespuestaResena();
            respuesta.setId(10L);
            respuesta.setResena(resenaAprobada);
            respuesta.setAutor("Soporte");
            respuesta.setContenido("Gracias por tu comentario");
            respuesta.setFechaCreacion(LocalDateTime.now());

            when(resenaRepository.findById(2L)).thenReturn(Optional.of(resenaAprobada));
            when(respuestaResenaRepository.findByResenaId(2L)).thenReturn(List.of(respuesta));

            List<RespuestaResenaDTO> resultado = service.obtenerRespuestasPorResena(2L);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getAutor()).isEqualTo("Soporte");
        }
    }

    @Nested
    @DisplayName("obtenerResumenProducto()")
    class ObtenerResumenProductoTests {

        @Test
        @DisplayName("Debe retornar resumen con estadisticas de calificacion")
        void givenProductoId_whenObtenerResumenProducto_thenRetornaMapConEstadisticas() {
            when(resenaRepository.findByProductoIdAndEstado(2L, EstadoResena.APROBADA)).thenReturn(List.of(resenaAprobada));
            when(resenaRepository.calcularPromedioCalificacion(2L)).thenReturn(4.0);
            when(resenaRepository.countByProductoIdAndEstado(2L, EstadoResena.APROBADA)).thenReturn(1L);
            when(productoClient.obtenerProductoPorId(2L)).thenReturn(new ProductoResumenDTO());

            Map<String, Object> resultado = service.obtenerResumenProducto(2L);

            assertThat(resultado).containsKey("productoId");
            assertThat(resultado.get("productoId")).isEqualTo(2L);
            assertThat(resultado).containsKey("promedioCalificacion");
            assertThat(resultado).containsKey("totalResenasAprobadas");
            assertThat(resultado.get("totalResenasAprobadas")).isEqualTo(1L);
        }

        @Test
        @DisplayName("Debe retornar resumen con mensaje cuando producto-service falla")
        void givenProductoServiceFalla_whenObtenerResumenProducto_thenRetornaConMensaje() {
            when(resenaRepository.findByProductoIdAndEstado(2L, EstadoResena.APROBADA)).thenReturn(List.of(resenaAprobada));
            when(resenaRepository.calcularPromedioCalificacion(2L)).thenReturn(4.0);
            when(resenaRepository.countByProductoIdAndEstado(2L, EstadoResena.APROBADA)).thenReturn(1L);
            when(productoClient.obtenerProductoPorId(2L)).thenThrow(new RuntimeException("timeout"));

            Map<String, Object> resultado = service.obtenerResumenProducto(2L);

            assertThat(resultado.get("producto")).isEqualTo("Servicio de productos no disponible temporalmente");
        }
    }

    @Nested
    @DisplayName("obtenerResumenCliente()")
    class ObtenerResumenClienteTests {

        @Test
        @DisplayName("Debe retornar resumen con datos del cliente y pagos")
        void givenClienteId_whenObtenerResumenCliente_thenRetornaResumenCompleto() {
            when(resenaRepository.findByClienteId(1L)).thenReturn(List.of(resenaPendiente, resenaAprobada));
            when(clienteClient.obtenerClientePorId(1L)).thenReturn(new ClienteResumenDTO());
            when(pagoClient.obtenerPagosPorCliente(1L)).thenReturn(new Object());

            Map<String, Object> resultado = service.obtenerResumenCliente(1L);

            assertThat(resultado.get("clienteId")).isEqualTo(1L);
            assertThat(resultado.get("totalResenas")).isEqualTo(2);
            assertThat(resultado).containsKey("cliente");
            assertThat(resultado).containsKey("pagos");
        }

        @Test
        @DisplayName("Debe retornar resumen con mensaje cuando cliente-service falla")
        void givenClienteServiceFalla_whenObtenerResumenCliente_thenRetornaConMensaje() {
            when(resenaRepository.findByClienteId(1L)).thenReturn(List.of(resenaPendiente));
            when(clienteClient.obtenerClientePorId(1L)).thenThrow(new RuntimeException("timeout"));
            when(pagoClient.obtenerPagosPorCliente(1L)).thenReturn(new Object());

            Map<String, Object> resultado = service.obtenerResumenCliente(1L);

            assertThat(resultado.get("cliente")).isEqualTo("Servicio de clientes no disponible temporalmente");
            assertThat(resultado).containsKey("pagos");
        }

        @Test
        @DisplayName("Debe retornar resumen con mensaje cuando pago-service falla")
        void givenPagoServiceFalla_whenObtenerResumenCliente_thenRetornaConMensaje() {
            when(resenaRepository.findByClienteId(1L)).thenReturn(List.of(resenaPendiente));
            when(clienteClient.obtenerClientePorId(1L)).thenReturn(new ClienteResumenDTO());
            when(pagoClient.obtenerPagosPorCliente(1L)).thenThrow(new RuntimeException("timeout"));

            Map<String, Object> resultado = service.obtenerResumenCliente(1L);

            assertThat(resultado).containsKey("cliente");
            assertThat(resultado.get("pagos")).isEqualTo("Servicio de pagos no disponible temporalmente");
        }
    }

    @Nested
    @DisplayName("mapearADto() - respuestas no nulas")
    class MapearADtoRespuestasTests {

        @Test
        @DisplayName("Debe mapear resena con respuestas al DTO correctamente")
        void givenResenaConRespuestas_whenMapearADto_thenIncluyeRespuestas() {
            RespuestaResena respuesta = new RespuestaResena();
            respuesta.setId(10L);
            respuesta.setResena(resenaPendiente);
            respuesta.setAutor("Soporte");
            respuesta.setContenido("Gracias");
            respuesta.setFechaCreacion(LocalDateTime.now());

            resenaPendiente.setRespuestas(List.of(respuesta));
            when(resenaRepository.findAll()).thenReturn(List.of(resenaPendiente));

            List<ResenaDTO> resultado = service.obtenerTodas();

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getRespuestas()).hasSize(1);
            assertThat(resultado.get(0).getRespuestas().get(0).getAutor()).isEqualTo("Soporte");
        }
    }

    @Nested
    @DisplayName("Manejo de excepciones inesperadas")
    class ManejoExcepcionesInesperadasTests {

        @Test
        @DisplayName("Debe lanzar RuntimeException cuando crearResena falla inesperadamente")
        void givenErrorInesperado_whenCrearResena_thenLanzaRuntimeException() {
            when(clienteClient.obtenerClientePorId(1L)).thenReturn(new ClienteResumenDTO());
            when(productoClient.obtenerProductoPorId(2L)).thenReturn(new ProductoResumenDTO());
            when(pedidoClient.obtenerPedidoPorId(3L)).thenReturn(new Object());
            when(resenaRepository.findByClienteIdAndProductoIdAndPedidoId(1L, 2L, 3L)).thenReturn(Optional.empty());
            when(resenaRepository.save(any(Resena.class))).thenThrow(new RuntimeException("DB error"));

            assertThatThrownBy(() -> service.crearResena(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("No se pudo registrar la resena");
        }

        @Test
        @DisplayName("Debe lanzar RuntimeException cuando actualizarEstado falla inesperadamente")
        void givenErrorInesperado_whenActualizarEstado_thenLanzaRuntimeException() {
            ActualizarEstadoRequest estadoRequest = new ActualizarEstadoRequest();
            estadoRequest.setNuevoEstado(EstadoResena.APROBADA);
            when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaPendiente));
            when(resenaRepository.save(any(Resena.class))).thenThrow(new RuntimeException("DB error"));

            assertThatThrownBy(() -> service.actualizarEstado(1L, estadoRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("No se pudo actualizar el estado");
        }

        @Test
        @DisplayName("Debe lanzar RuntimeException cuando agregarRespuesta falla inesperadamente")
        void givenErrorInesperado_whenAgregarRespuesta_thenLanzaRuntimeException() {
            RespuestaResenaRequest respuestaRequest = new RespuestaResenaRequest();
            respuestaRequest.setAutor("Soporte");
            respuestaRequest.setContenido("Gracias");
            when(resenaRepository.findById(2L)).thenReturn(Optional.of(resenaAprobada));
            when(respuestaResenaRepository.save(any(RespuestaResena.class))).thenThrow(new RuntimeException("DB error"));

            assertThatThrownBy(() -> service.agregarRespuesta(2L, respuestaRequest))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("No se pudo agregar la respuesta");
        }
    }
}
