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
    private List<ClienteDTO> clientes;

    @BeforeEach
    void setUp() {
        DetallePedidoRequest detalleRequest = new DetallePedidoRequest();
        detalleRequest.setIdProducto(10);
        detalleRequest.setCantidad(2);
        detalleRequest.setPrecioUnitario(BigDecimal.valueOf(1500));

        request = new PedidoRequest();
        request.setIdCliente(1);
        request.setFechaPedido(LocalDateTime.of(2025, 6, 21, 0, 0));
        request.setEstadoPedido(EstadoPedido.PENDIENTE);
        request.setDetalles(List.of(detalleRequest));

        requestSinDetalles = new PedidoRequest();
        requestSinDetalles.setIdCliente(1);
        requestSinDetalles.setFechaPedido(LocalDateTime.of(2025, 6, 21, 0, 0));
        requestSinDetalles.setEstadoPedido(EstadoPedido.PENDIENTE);
        requestSinDetalles.setDetalles(null);

        entity = new Pedidos();
        entity.setIdCliente(1);
        entity.setFechaPedido(LocalDateTime.of(2025, 6, 21, 0, 0));
        entity.setEstadoPedido(EstadoPedido.PENDIENTE);

        savedEntity = new Pedidos();
        savedEntity.setId(1);
        savedEntity.setIdCliente(1);
        savedEntity.setFechaPedido(LocalDateTime.of(2025, 6, 21, 0, 0));
        savedEntity.setEstadoPedido(EstadoPedido.PENDIENTE);

        DetallePedidos detalle = new DetallePedidos();
        detalle.setId(1);
        detalle.setIdProducto(10);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(BigDecimal.valueOf(1500));
        detalle.setPedido(savedEntity);
        savedEntity.setDetalles(List.of(detalle));

        clienteDTO = new ClienteDTO();
        clienteDTO.setId(1);
        clienteDTO.setNombre("Juan");
        clientes = List.of(clienteDTO);
    }

    @Nested
    @DisplayName("Tests para crearPedido")
    class CrearPedidoTests {

        @Test
        @DisplayName("crearPedido con detalles y cliente existente — retorna PedidoDTO completo")
        void crearPedido_conDetalles_clienteExiste() {
            given(clienteClient.obtenerClientes()).willReturn(clientes);
            given(pedidosRepository.save(any(Pedidos.class))).willReturn(savedEntity);

            PedidoDTO result = pedidosService.crearPedido(request);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getIdCliente()).isEqualTo(1);
            assertThat(result.getEstadoPedido()).isEqualTo(EstadoPedido.PENDIENTE);
            assertThat(result.getFechaPedido()).isEqualTo(LocalDateTime.of(2025, 6, 21, 0, 0));
            assertThat(result.getDetalles()).hasSize(1);
            assertThat(result.getDetalles().get(0).getIdProducto()).isEqualTo(10);
            assertThat(result.getDetalles().get(0).getCantidad()).isEqualTo(2);
            assertThat(result.getDetalles().get(0).getPrecioUnitario()).isEqualTo(BigDecimal.valueOf(1500));
        }

        @Test
        @DisplayName("crearPedido con detalles nulos — retorna PedidoDTO sin detalles")
        void crearPedido_detallesNulos() {
            given(clienteClient.obtenerClientes()).willReturn(clientes);

            Pedidos savedWithoutDetails = new Pedidos();
            savedWithoutDetails.setId(2);
            savedWithoutDetails.setIdCliente(1);
            savedWithoutDetails.setFechaPedido(LocalDateTime.of(2025, 6, 21, 0, 0));
            savedWithoutDetails.setEstadoPedido(EstadoPedido.PENDIENTE);
            savedWithoutDetails.setDetalles(null);

            given(pedidosRepository.save(any(Pedidos.class))).willReturn(savedWithoutDetails);

            PedidoDTO result = pedidosService.crearPedido(requestSinDetalles);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(2);
            assertThat(result.getDetalles()).isNull();
        }

        @Test
        @DisplayName("crearPedido lanza ClientesNotFoundException cuando el cliente no existe")
        void crearPedido_clienteNoExiste() {
            given(clienteClient.obtenerClientes()).willReturn(Collections.emptyList());

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
            assertThat(resultados.get(0).getId()).isEqualTo(1);
            assertThat(resultados.get(0).getIdCliente()).isEqualTo(1);
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
            given(pedidosRepository.findById(1)).willReturn(Optional.of(savedEntity));

            PedidoDTO result = pedidosService.buscarPorId(1);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
        }

        @Test
        @DisplayName("buscarPorId lanza PedidosNotFoundException cuando el pedido no existe")
        void buscarPorId_noEncontrado() {
            given(pedidosRepository.findById(99)).willReturn(Optional.empty());

            assertThatThrownBy(() -> pedidosService.buscarPorId(99))
                    .isInstanceOf(PedidosNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Tests para actualizarPedido")
    class ActualizarPedidoTests {

        @Test
        @DisplayName("actualizarPedido exitoso — actualiza campos y retorna DTO")
        void actualizarPedido_exitoso() {
            given(clienteClient.obtenerClientes()).willReturn(clientes);
            given(pedidosRepository.findById(1)).willReturn(Optional.of(entity));
            given(pedidosRepository.save(any(Pedidos.class))).willReturn(savedEntity);

            PedidoDTO result = pedidosService.actualizarPedido(1, request);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getIdCliente()).isEqualTo(1);
            assertThat(result.getEstadoPedido()).isEqualTo(EstadoPedido.PENDIENTE);
            assertThat(result.getFechaPedido()).isEqualTo(LocalDateTime.of(2025, 6, 21, 0, 0));
        }

        @Test
        @DisplayName("actualizarPedido lanza PedidosNotFoundException cuando el pedido no existe")
        void actualizarPedido_noEncontrado() {
            given(clienteClient.obtenerClientes()).willReturn(clientes);
            given(pedidosRepository.findById(1)).willReturn(Optional.empty());

            assertThatThrownBy(() -> pedidosService.actualizarPedido(1, request))
                    .isInstanceOf(PedidosNotFoundException.class);
        }

        @Test
        @DisplayName("actualizarPedido lanza ClientesNotFoundException cuando el cliente no existe")
        void actualizarPedido_clienteNoEncontrado() {
            given(clienteClient.obtenerClientes()).willReturn(Collections.emptyList());

            assertThatThrownBy(() -> pedidosService.actualizarPedido(1, request))
                    .isInstanceOf(ClientesNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Tests para eliminarPedido")
    class EliminarPedidoTests {

        @Test
        @DisplayName("eliminarPedido elimina cuando el pedido existe")
        void eliminarPedido_exitoso() {
            given(pedidosRepository.findById(1)).willReturn(Optional.of(savedEntity));

            pedidosService.eliminarPedido(1);

            then(pedidosRepository).should().deleteById(1);
        }

        @Test
        @DisplayName("eliminarPedido lanza PedidosNotFoundException cuando no existe")
        void eliminarPedido_noEncontrado() {
            given(pedidosRepository.findById(99)).willReturn(Optional.empty());

            assertThatThrownBy(() -> pedidosService.eliminarPedido(99))
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
            given(clienteClient.obtenerClientes()).willReturn(clientes);

            ReflectionTestUtils.invokeMethod(pedidosService, "validarCliente", 1);
        }

        @Test
        @DisplayName("validarCliente lanza ClientesNotFoundException cuando el cliente no existe")
        void validarCliente_clienteNoExiste() {
            given(clienteClient.obtenerClientes()).willReturn(clientes);

            assertThatThrownBy(() ->
                    ReflectionTestUtils.invokeMethod(pedidosService, "validarCliente", 99))
                    .isInstanceOf(ClientesNotFoundException.class);
        }
    }
}
