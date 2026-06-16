package cl.jorge.resena.service;

import cl.jorge.resena.client.ClienteClient;
import cl.jorge.resena.client.PagoClient;
import cl.jorge.resena.client.PedidoClient;
import cl.jorge.resena.client.ProductoClient;
import cl.jorge.resena.dto.ActualizarEstadoRequest;
import cl.jorge.resena.dto.ResenaRequest;
import cl.jorge.resena.dto.RespuestaResenaDTO;
import cl.jorge.resena.dto.RespuestaResenaRequest;
import cl.jorge.resena.exception.DuplicateResourceException;
import cl.jorge.resena.model.EstadoResena;
import cl.jorge.resena.model.Resena;
import cl.jorge.resena.model.RespuestaResena;
import cl.jorge.resena.repository.ResenaRepository;
import cl.jorge.resena.repository.RespuestaResenaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
    private Resena resena;

    @BeforeEach
    void setUp() {
        request = new ResenaRequest();
        request.setClienteId(1L);
        request.setProductoId(2L);
        request.setPedidoId(3L);
        request.setCalificacion(5);
        request.setTitulo("Excelente");
        request.setComentario("Todo perfecto");

        resena = new Resena();
        resena.setId(1L);
        resena.setClienteId(1L);
        resena.setProductoId(2L);
        resena.setPedidoId(3L);
        resena.setCalificacion(5);
        resena.setTitulo("Excelente");
        resena.setComentario("Todo perfecto");
        resena.setEstado(EstadoResena.PENDIENTE);
        resena.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void crearResena_cuandoTodoEsValido_retornaDTO() {
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(new cl.jorge.resena.dto.ClienteResumenDTO());
        when(productoClient.obtenerProductoPorId(2L)).thenReturn(new cl.jorge.resena.dto.ProductoResumenDTO());
        when(pedidoClient.obtenerPedidoPorId(3L)).thenReturn(new Object());
        when(resenaRepository.findByClienteIdAndProductoIdAndPedidoId(1L, 2L, 3L)).thenReturn(Optional.empty());
        when(resenaRepository.save(any(Resena.class))).thenReturn(resena);

        var resultado = service.crearResena(request);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEstado()).isEqualTo(EstadoResena.PENDIENTE);
    }

    @Test
    void crearResena_cuandoExisteDuplicado_lanzaExcepcion() {
        when(clienteClient.obtenerClientePorId(1L)).thenReturn(new cl.jorge.resena.dto.ClienteResumenDTO());
        when(productoClient.obtenerProductoPorId(2L)).thenReturn(new cl.jorge.resena.dto.ProductoResumenDTO());
        when(pedidoClient.obtenerPedidoPorId(3L)).thenReturn(new Object());
        when(resenaRepository.findByClienteIdAndProductoIdAndPedidoId(1L, 2L, 3L)).thenReturn(Optional.of(resena));

        assertThatThrownBy(() -> service.crearResena(request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void actualizarEstado_cuandoTransicionEsValida_retornaDTO() {
        ActualizarEstadoRequest estadoRequest = new ActualizarEstadoRequest();
        estadoRequest.setNuevoEstado(EstadoResena.APROBADA);
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));
        when(resenaRepository.save(any(Resena.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var resultado = service.actualizarEstado(1L, estadoRequest);

        assertThat(resultado.getEstado()).isEqualTo(EstadoResena.APROBADA);
    }

    @Test
    void agregarRespuesta_cuandoResenaEstaAprobada_retornaRespuesta() {
        resena.setEstado(EstadoResena.APROBADA);
        RespuestaResena respuesta = new RespuestaResena();
        respuesta.setId(10L);
        respuesta.setResena(resena);
        respuesta.setAutor("Soporte");
        respuesta.setContenido("Gracias por tu comentario");
        respuesta.setFechaCreacion(LocalDateTime.now());

        RespuestaResenaRequest respuestaRequest = new RespuestaResenaRequest();
        respuestaRequest.setAutor("Soporte");
        respuestaRequest.setContenido("Gracias por tu comentario");

        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));
        when(respuestaResenaRepository.save(any(RespuestaResena.class))).thenReturn(respuesta);

        RespuestaResenaDTO resultado = service.agregarRespuesta(1L, respuestaRequest);

        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getAutor()).isEqualTo("Soporte");
    }
}
