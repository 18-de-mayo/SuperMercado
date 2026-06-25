package duoc.cl.despacho.service;

import duoc.cl.despacho.dto.DespachoDTO;
import duoc.cl.despacho.dto.DespachoRequest;
import duoc.cl.despacho.dto.ProveedorDTO;
import duoc.cl.despacho.exception.DespachoNotFoundException;
import duoc.cl.despacho.client.PedidoFeignClient;
import duoc.cl.despacho.client.ProveedorFeignClient;
import duoc.cl.despacho.dto.PedidoResumenDTO;
import duoc.cl.despacho.model.Despacho;
import duoc.cl.despacho.model.EstadoDespacho;
import duoc.cl.despacho.repository.DespachoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DespachoServiceTest {

    @Mock
    private DespachoRepository repository;

    @Mock
    private PedidoFeignClient pedidoFeignClient;

    @Mock
    private ProveedorFeignClient proveedorFeignClient;

    @InjectMocks
    private DespachoService service;

    private DespachoRequest validRequest;
    private Despacho mockDespacho;
    private Despacho mockDespachoEnRuta;
    private Despacho mockDespachoEntregado;
    private ProveedorDTO mockProveedor;

    @BeforeEach
    void setUp() {
        validRequest = new DespachoRequest();
        validRequest.setPedidoId(100L);
        validRequest.setProveedorId(5L);
        validRequest.setDireccionDestino("Av. Concha y Toro 1340");
        validRequest.setComuna("Puente Alto");

        mockDespacho = new Despacho();
        mockDespacho.setId(1L);
        mockDespacho.setPedidoId(100L);
        mockDespacho.setProveedorId(5L);
        mockDespacho.setDireccionDestino("Av. Concha y Toro 1340");
        mockDespacho.setComuna("Puente Alto");
        mockDespacho.setEstado(EstadoDespacho.PENDIENTE);

        mockDespachoEnRuta = new Despacho();
        mockDespachoEnRuta.setId(1L);
        mockDespachoEnRuta.setPedidoId(100L);
        mockDespachoEnRuta.setProveedorId(5L);
        mockDespachoEnRuta.setDireccionDestino("Av. Concha y Toro 1340");
        mockDespachoEnRuta.setComuna("Puente Alto");
        mockDespachoEnRuta.setEstado(EstadoDespacho.EN_RUTA);

        mockDespachoEntregado = new Despacho();
        mockDespachoEntregado.setId(1L);
        mockDespachoEntregado.setPedidoId(100L);
        mockDespachoEntregado.setProveedorId(5L);
        mockDespachoEntregado.setDireccionDestino("Av. Concha y Toro 1340");
        mockDespachoEntregado.setComuna("Puente Alto");
        mockDespachoEntregado.setEstado(EstadoDespacho.ENTREGADO);

        mockProveedor = new ProveedorDTO();
        mockProveedor.setId(5L);
        mockProveedor.setNombre("Duoc Puente Alto");
    }

    @Nested
    @DisplayName("guardar()")
    class GuardarTests {

        @Test
        @DisplayName("Debe crear despacho exitosamente cuando Pedido y Proveedor existen")
        void givenValidRequest_whenGuardar_thenReturnDespachoDTO() {
            when(pedidoFeignClient.obtenerPedido(anyLong())).thenReturn(new PedidoResumenDTO());
            when(proveedorFeignClient.obtenerProveedor(anyLong())).thenReturn(mockProveedor);
            when(repository.save(any(Despacho.class))).thenReturn(mockDespacho);

            DespachoDTO result = service.guardar(validRequest);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEstado()).isEqualTo(EstadoDespacho.PENDIENTE);
            assertThat(result.getNombreProveedor()).isEqualTo("Duoc Puente Alto");
            verify(repository, times(1)).save(any(Despacho.class));
        }

        @Test
        @DisplayName("Debe lanzar exception cuando el pedido remoto no existe")
        void givenInvalidPedidoId_whenGuardar_thenThrowResponseStatusException() {
            when(pedidoFeignClient.obtenerPedido(100L)).thenThrow(new RuntimeException("Error HTTP 404"));

            assertThatThrownBy(() -> service.guardar(validRequest))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("El pedido con ID 100 no existe");
            verify(repository, never()).save(any(Despacho.class));
        }

        @Test
        @DisplayName("Debe lanzar exception cuando el proveedor remoto no existe")
        void givenInvalidProveedorId_whenGuardar_thenThrowResponseStatusException() {
            when(pedidoFeignClient.obtenerPedido(anyLong())).thenReturn(new PedidoResumenDTO());
            when(proveedorFeignClient.obtenerProveedor(anyLong())).thenThrow(new RuntimeException("Error HTTP 404"));

            assertThatThrownBy(() -> service.guardar(validRequest))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("El proveedor con ID 5 no existe");
            verify(repository, never()).save(any(Despacho.class));
        }
    }

    @Nested
    @DisplayName("listar()")
    class ListarTests {

        @Test
        @DisplayName("Debe retornar lista de despachos cuando existen registros")
        void givenDespachosExisten_whenListar_thenReturnList() {
            when(repository.findAll()).thenReturn(List.of(mockDespacho));
            when(proveedorFeignClient.obtenerProveedor(anyLong())).thenReturn(mockProveedor);

            List<DespachoDTO> result = service.listar();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getEstado()).isEqualTo(EstadoDespacho.PENDIENTE);
        }

        @Test
        @DisplayName("Debe retornar lista vacia cuando no hay despachos")
        void givenNoDespachos_whenListar_thenReturnEmptyList() {
            when(repository.findAll()).thenReturn(List.of());

            List<DespachoDTO> result = service.listar();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("actualizar()")
    class ActualizarTests {

        @Test
        @DisplayName("Debe actualizar despacho exitosamente cuando los datos son válidos")
        void givenValidRequest_whenActualizar_thenReturnDespachoDTO() {
            when(repository.findById(1L)).thenReturn(Optional.of(mockDespacho));
            when(proveedorFeignClient.obtenerProveedor(anyLong())).thenReturn(mockProveedor);
            when(repository.save(any(Despacho.class))).thenAnswer(invocation -> invocation.getArgument(0));

            DespachoDTO result = service.actualizar(1L, validRequest);

            assertThat(result).isNotNull();
            assertThat(result.getEstado()).isEqualTo(EstadoDespacho.PENDIENTE);
            verify(repository, times(1)).save(any(Despacho.class));
        }

        @Test
        @DisplayName("Debe lanzar exception cuando el despacho a actualizar no existe")
        void givenNonExistingId_whenActualizar_thenThrowDespachoNotFoundException() {
            when(repository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizar(999L, validRequest))
                    .isInstanceOf(DespachoNotFoundException.class);
            verify(repository, never()).save(any(Despacho.class));
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class EliminarTests {

        @Test
        @DisplayName("Debe eliminar exitosamente cuando el ID existe")
        void givenExistingId_whenEliminar_thenDeleteSuccessfully() {
            when(repository.findById(1L)).thenReturn(Optional.of(mockDespacho));
            doNothing().when(repository).delete(any(Despacho.class));

            assertThatNoException().isThrownBy(() -> service.eliminar(1L));
            verify(repository, times(1)).delete(any(Despacho.class));
        }

        @Test
        @DisplayName("Debe lanzar exception cuando el ID a eliminar no existe")
        void givenNonExistingId_whenEliminar_thenThrowDespachoNotFoundException() {
            when(repository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.eliminar(999L))
                    .isInstanceOf(DespachoNotFoundException.class);
            verify(repository, never()).delete(any(Despacho.class));
        }
    }

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorIdTests {

        @Test
        @DisplayName("Debe retornar despacho cuando el ID existe")
        void givenExistingId_whenBuscarPorId_thenReturnDespachoDTO() {
            when(repository.findById(1L)).thenReturn(Optional.of(mockDespacho));
            when(proveedorFeignClient.obtenerProveedor(anyLong())).thenReturn(mockProveedor);

            DespachoDTO result = service.buscarPorId(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEstado()).isEqualTo(EstadoDespacho.PENDIENTE);
        }

        @Test
        @DisplayName("Debe lanzar excepcion cuando el ID no existe")
        void givenNonExistingId_whenBuscarPorId_thenThrowDespachoNotFoundException() {
            when(repository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscarPorId(999L))
                    .isInstanceOf(DespachoNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("actualizarEstado()")
    class ActualizarEstadoTests {

        @Test
        @DisplayName("Debe permitir transicion de PENDIENTE a EN_RUTA")
        void givenDespachoPendiente_whenActualizarEstadoEnRuta_thenReturnUpdatedDTO() {
            when(repository.findById(1L)).thenReturn(Optional.of(mockDespacho));
            when(repository.save(any(Despacho.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(proveedorFeignClient.obtenerProveedor(5L)).thenReturn(mockProveedor);

            DespachoDTO result = service.actualizarEstado(1L, EstadoDespacho.EN_RUTA);

            assertThat(result).isNotNull();
            assertThat(result.getEstado()).isEqualTo(EstadoDespacho.EN_RUTA);
        }

        @Test
        @DisplayName("Debe permitir transicion de EN_RUTA a ENTREGADO")
        void givenDespachoEnRuta_whenActualizarEstadoEntregado_thenReturnUpdatedDTO() {
            when(repository.findById(1L)).thenReturn(Optional.of(mockDespachoEnRuta));
            when(repository.save(any(Despacho.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(proveedorFeignClient.obtenerProveedor(5L)).thenReturn(mockProveedor);

            DespachoDTO result = service.actualizarEstado(1L, EstadoDespacho.ENTREGADO);

            assertThat(result).isNotNull();
            assertThat(result.getEstado()).isEqualTo(EstadoDespacho.ENTREGADO);
        }

        @Test
        @DisplayName("Debe rechazar transicion de PENDIENTE a ENTREGADO (salto de estado)")
        void givenDespachoPendiente_whenActualizarEstadoEntregado_thenThrowResponseStatusException() {
            when(repository.findById(1L)).thenReturn(Optional.of(mockDespacho));

            assertThatThrownBy(() -> service.actualizarEstado(1L, EstadoDespacho.ENTREGADO))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Transición inválida");
            verify(repository, never()).save(any(Despacho.class));
        }

        @Test
        @DisplayName("Debe rechazar cualquier transicion desde estado ENTREGADO (terminal)")
        void givenDespachoEntregado_whenActualizarEstadoCualquier_thenThrowResponseStatusException() {
            when(repository.findById(1L)).thenReturn(Optional.of(mockDespachoEntregado));

            assertThatThrownBy(() -> service.actualizarEstado(1L, EstadoDespacho.EN_RUTA))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining("Transición inválida");
            verify(repository, never()).save(any(Despacho.class));
        }

        @Test
        @DisplayName("Debe lanzar DespachoNotFoundException cuando el despacho no existe")
        void givenNonExistingId_whenActualizarEstado_thenThrowDespachoNotFoundException() {
            when(repository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizarEstado(999L, EstadoDespacho.EN_RUTA))
                    .isInstanceOf(DespachoNotFoundException.class);
            verify(repository, never()).save(any(Despacho.class));
        }
    }
}
