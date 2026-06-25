package com.duoc.pedidos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.duoc.pedidos.client.ClienteClient;
import com.duoc.pedidos.dto.ClienteDTO;
import com.duoc.pedidos.dto.DetallePedidoDTO;
import com.duoc.pedidos.dto.DetallePedidoRequest;
import com.duoc.pedidos.dto.PedidoDTO;
import com.duoc.pedidos.dto.PedidoRequest;
import com.duoc.pedidos.exception.ClientesNotFoundException;
import com.duoc.pedidos.exception.PedidosNotFoundException;
import com.duoc.pedidos.model.DetallePedidos;
import com.duoc.pedidos.model.EstadoPedido;
import com.duoc.pedidos.model.Pedidos;
import com.duoc.pedidos.repository.PedidosRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidosService Unit Tests")
class PedidosServiceTest {

    @Mock
    private PedidosRepository pedidosRepository;

    @Mock
    private ClienteClient clienteClient;

    @InjectMocks
    private PedidosService pedidosService;

    private PedidoRequest request;
    private PedidoRequest requestSinDetalles;
    private Pedidos entity;
    private Pedidos savedEntity;
    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        DetallePedidoRequest detalleRequest = new DetallePedidoRequest();
        detalleRequest.setIdProducto(10L);
        detalleRequest.setCantidad(2);
        detalleRequest.setPrecioUnitario(BigDecimal.valueOf(1500));

        request = new PedidoRequest();
        request.setIdCliente(1L);
        request.setFechaPedido(LocalDateTime.of(2025, 6, 21, 0, 0));
        request.setEstadoPedido(EstadoPedido.PENDIENTE);
        request.setDetalles(List.of(detalleRequest));

        requestSinDetalles = new PedidoRequest();
        requestSinDetalles.setIdCliente(1L);
        requestSinDetalles.setFechaPedido(LocalDateTime.of(2025, 6, 21, 0, 0));
        requestSinDetalles.setEstadoPedido(EstadoPedido.PENDIENTE);
        requestSinDetalles.setDetalles(null);

        entity = new Pedidos();
        entity.setIdCliente(1L);
        entity.setFechaPedido(LocalDateTime.of(2025, 6, 21, 0, 0));
        entity.setEstadoPedido(EstadoPedido.PENDIENTE);

        savedEntity = new Pedidos();
        savedEntity.setId(1L);
        savedEntity.setIdCliente(1L);
        savedEntity.setFechaPedido(LocalDateTime.of(2025, 6, 21, 0, 0));
        savedEntity.setEstadoPedido(EstadoPedido.PENDIENTE);

        DetallePedidos detalle = new DetallePedidos();
        detalle.setId(1L);
        detalle.setIdProducto(10L);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(BigDecimal.valueOf(1500));
        detalle.setPedido(savedEntity);
        savedEntity.setDetalles(List.of(detalle));

        clienteDTO = new ClienteDTO();
        clienteDTO.setId(1L);
        clienteDTO.setNombre("Juan");
    }

    @Nested
    @DisplayName("Tests para crearPedido")
    class CrearPedidoTests {

        @Test
        @DisplayName("crearPedido con detalles y cliente existente — retorna PedidoDTO completo")
        void crearPedido_conDetalles_clienteExiste() {
            given(clienteClient.obtenerClientePorId(1L)).willReturn(clienteDTO);
            given(pedidosRepository.save(any(Pedidos.class))).willReturn(savedEntity);

            PedidoDTO result = pedidosService.crearPedido(request);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getIdCliente()).isEqualTo(1L);
            assertThat(result.getEstadoPedido()).isEqualTo(EstadoPedido.PENDIENTE);
            assertThat(result.getFechaPedido()).isEqualTo(LocalDateTime.of(2025, 6, 21, 0, 0));
            assertThat(result.getDetalles()).hasSize(1);
            assertThat(result.getDetalles().get(0).getIdProducto()).isEqualTo(10L);
            assertThat(result.getDetalles().get(0).getCantidad()).isEqualTo(2);
            assertThat(result.getDetalles().get(0).getPrecioUnitario()).isEqualTo(BigDecimal.valueOf(1500));
        }

        @Test
        @DisplayName("crearPedido con detalles nulos — retorna PedidoDTO sin detalles")
        void crearPedido_detallesNulos() {
            given(clienteClient.obtenerClientePorId(1L)).willReturn(clienteDTO);

            Pedidos savedWithoutDetails = new Pedidos();
            savedWithoutDetails.setId(2L);
            savedWithoutDetails.setIdCliente(1L);
            savedWithoutDetails.setFechaPedido(LocalDateTime.of(2025, 6, 21, 0, 0));
            savedWithoutDetails.setEstadoPedido(EstadoPedido.PENDIENTE);
            savedWithoutDetails.setDetalles(null);

            given(pedidosRepository.save(any(Pedidos.class))).willReturn(savedWithoutDetails);

            PedidoDTO result = pedidosService.crearPedido(requestSinDetalles);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(2L);
            assertThat(result.getDetalles()).isNull();
        }

        @Test
        @DisplayName("crearPedido lanza ClientesNotFoundException cuando el cliente no existe")
        void crearPedido_clienteNoExiste() {
            given(clienteClient.obtenerClientePorId(1L)).willThrow(new RuntimeException("Cliente no encontrado"));

            assertThatThrownBy(() -> pedidosService.crearPedido(request))
                    .isInstanceOf(ClientesNotFoundException.class);

            then(pedidosRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests para obtenerPedidos")
    class ObtenerPedidosTests {

        @Test
        @DisplayName("obtenerPedidos retorna lista de pedidos")
        void obtenerPedidos_retornaLista() {
            given(pedidosRepository.findAll()).willReturn(List.of(savedEntity));

            List<PedidoDTO> resultados = pedidosService.obtenerPedidos();

            assertThat(resultados).hasSize(1);
            assertThat(resultados.get(0).getId()).isEqualTo(1L);
            assertThat(resultados.get(0).getIdCliente()).isEqualTo(1L);
            assertThat(resultados.get(0).getDetalles()).hasSize(1);
        }

        @Test
        @DisplayName("obtenerPedidos retorna lista vacia cuando no hay pedidos")
        void obtenerPedidos_listaVacia() {
            given(pedidosRepository.findAll()).willReturn(Collections.emptyList());

            List<PedidoDTO> resultados = pedidosService.obtenerPedidos();

            assertThat(resultados).isEmpty();
        }
    }

    @Nested
    @DisplayName("Tests para buscarPorId")
    class BuscarPorIdTests {

        @Test
        @DisplayName("buscarPorId retorna PedidoDTO cuando el pedido existe")
        void buscarPorId_encontrado() {
            given(pedidosRepository.findById(1L)).willReturn(Optional.of(savedEntity));

            PedidoDTO result = pedidosService.buscarPorId(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("buscarPorId lanza PedidosNotFoundException cuando el pedido no existe")
        void buscarPorId_noEncontrado() {
            given(pedidosRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> pedidosService.buscarPorId(99L))
                    .isInstanceOf(PedidosNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Tests para actualizarPedido")
    class ActualizarPedidoTests {

        @Test
        @DisplayName("actualizarPedido exitoso — actualiza campos y retorna DTO")
        void actualizarPedido_exitoso() {
            given(clienteClient.obtenerClientePorId(1L)).willReturn(clienteDTO);
            given(pedidosRepository.findById(1L)).willReturn(Optional.of(entity));
            given(pedidosRepository.save(any(Pedidos.class))).willReturn(savedEntity);

            PedidoDTO result = pedidosService.actualizarPedido(1L, request);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getIdCliente()).isEqualTo(1L);
            assertThat(result.getEstadoPedido()).isEqualTo(EstadoPedido.PENDIENTE);
            assertThat(result.getFechaPedido()).isEqualTo(LocalDateTime.of(2025, 6, 21, 0, 0));
        }

        @Test
        @DisplayName("actualizarPedido lanza PedidosNotFoundException cuando el pedido no existe")
        void actualizarPedido_noEncontrado() {
            given(clienteClient.obtenerClientePorId(1L)).willReturn(clienteDTO);
            given(pedidosRepository.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> pedidosService.actualizarPedido(1L, request))
                    .isInstanceOf(PedidosNotFoundException.class);
        }

        @Test
        @DisplayName("actualizarPedido lanza ClientesNotFoundException cuando el cliente no existe")
        void actualizarPedido_clienteNoEncontrado() {
            given(clienteClient.obtenerClientePorId(1L)).willThrow(new RuntimeException("Cliente no encontrado"));

            assertThatThrownBy(() -> pedidosService.actualizarPedido(1L, request))
                    .isInstanceOf(ClientesNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Tests para eliminarPedido")
    class EliminarPedidoTests {

        @Test
        @DisplayName("eliminarPedido elimina cuando el pedido existe")
        void eliminarPedido_exitoso() {
            given(pedidosRepository.findById(1L)).willReturn(Optional.of(savedEntity));

            pedidosService.eliminarPedido(1L);

            then(pedidosRepository).should().deleteById(1L);
        }

        @Test
        @DisplayName("eliminarPedido lanza PedidosNotFoundException cuando no existe")
        void eliminarPedido_noEncontrado() {
            given(pedidosRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> pedidosService.eliminarPedido(99L))
                    .isInstanceOf(PedidosNotFoundException.class);

            then(pedidosRepository).should(never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Tests para convertirADTO (metodo privado)")
    class ConvertirADTOTests {

        @Test
        @DisplayName("convertirADTO con null retorna null")
        void convertirADTO_nullRetornaNull() {
            PedidoDTO result = ReflectionTestUtils.invokeMethod(pedidosService, "convertirADTO", (Object) null);

            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("Tests para validarCliente (metodo privado)")
    class ValidarClienteTests {

        @Test
        @DisplayName("validarCliente no lanza excepcion cuando el cliente existe")
        void validarCliente_clienteExiste() {
            given(clienteClient.obtenerClientePorId(1L)).willReturn(clienteDTO);

            ReflectionTestUtils.invokeMethod(pedidosService, "validarCliente", 1L);
        }

        @Test
        @DisplayName("validarCliente lanza ClientesNotFoundException cuando el cliente no existe")
        void validarCliente_clienteNoExiste() {
            given(clienteClient.obtenerClientePorId(99L)).willThrow(new RuntimeException("Cliente no encontrado"));

            assertThatThrownBy(() ->
                    ReflectionTestUtils.invokeMethod(pedidosService, "validarCliente", 99L))
                    .isInstanceOf(ClientesNotFoundException.class);
        }
    }
}
