package duoc.cl.despacho.service;

import duoc.cl.despacho.dto.DespachoDTO;
import duoc.cl.despacho.dto.DespachoRequest;
import duoc.cl.despacho.dto.ProveedorDTO;
import duoc.cl.despacho.exception.DespachoNotFoundException;
import duoc.cl.despacho.feign.PedidoFeignClient;
import duoc.cl.despacho.feign.ProveedorFeignClient;
import duoc.cl.despacho.model.Despacho;
import duoc.cl.despacho.repository.DespachoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        mockDespacho.setEstado("PENDIENTE");

        mockProveedor = new ProveedorDTO();
        mockProveedor.setId(5L);
        mockProveedor.setNombre("Duoc Puente Alto");
    }

    @Test
    @DisplayName("REGLA DE NEGOCIO: Crear despacho exitoso cuando Pedido y Proveedor existen")
    void givenValidRequest_whenGuardar_thenReturnDespachoDTO() {
        /*// Given estás lineas arrojaron un error al no encontrar al proveedor (5)
        when(pedidoFeignClient.obtenerPedido(100L)).thenReturn(new Object());
        when(proveedorFeignClient.obtenerProveedor(1L)).thenReturn(mockProveedor);
        when(repository.save(any(Despacho.class))).thenReturn(mockDespacho);
        */
        // Given
        when(pedidoFeignClient.obtenerPedido(anyLong())).thenReturn(new Object());
        when(proveedorFeignClient.obtenerProveedor(anyLong())).thenReturn(mockProveedor);
        when(repository.save(any(Despacho.class))).thenReturn(mockDespacho);


        // When
        DespachoDTO result = service.guardar(validRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("PENDIENTE", result.getEstado());
        assertEquals("Duoc Puente Alto", result.getNombreProveedor());
        verify(repository, times(1)).save(any(Despacho.class));
    }

    @Test
    @DisplayName("REGLA DE NEGOCIO: Bloquear creación si el pedido remoto no existe")
    void givenInvalidPedidoId_whenGuardar_theThrowResponseStatusException() {
        // Given
        when(pedidoFeignClient.obtenerPedido(100L)).thenThrow(new RuntimeException("Error HTTP 404"));

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
            service.guardar(validRequest));

        assertTrue(exception.getReason().contains("El pedido con ID 100 no existe"));
        verify(repository, never()).save(any(Despacho.class));
    }

    @Test
    @DisplayName("REGLA DE NEGOCIO: Buscar por ID lanza excepción si el Despacho no existe")
    void givenNonExistingId_whenBuscarPorId_thenThrowDespachoNotFoundException() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(DespachoNotFoundException.class, () -> service.buscarPorId(999L));
    }

    @Test
    @DisplayName("MÁQUINA DE ESTADOS: Permitir transición correcta de PENDIENTE a EN_RUTA")
    void givenDespachoPendiente_whenActualizarEstadoEnRuta_thenReturnUpdatedDTO() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(mockDespacho));
        when(repository.save(any(Despacho.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(proveedorFeignClient.obtenerProveedor(5L)).thenReturn(mockProveedor);

        // When
        DespachoDTO result = service.actualizarEstado(1L, "EN_RUTA");

        // Then
        assertNotNull(result);
        assertEquals("EN_RUTA", result.getEstado());
    }

    @Test
    @DisplayName("MÁQUINA DE ESTADOS: Lanzar excepción ante transición inválida (PENDIENTE -> ENTREGADO)")
    void givenDespachoPendiente_whenActualizarEstadoEntregado_thenThrowResponseStatusException() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(mockDespacho));

        // When & Then
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                service.actualizarEstado(1L, "ENTREGADO"));

        assertTrue(exception.getReason().contains("Transición inválida"));
        verify(repository, never()).save(any(Despacho.class));
    }


}
