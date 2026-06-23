package duoc.cl.proveedor.service;

import duoc.cl.proveedor.dto.ProveedorDTO;
import duoc.cl.proveedor.dto.ProveedorRequest;
import duoc.cl.proveedor.exception.ProveedorNotFoundException;
import duoc.cl.proveedor.model.Proveedor;
import duoc.cl.proveedor.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - ProveedorService")
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository repository;

    @InjectMocks
    private ProveedorService service;

    private ProveedorRequest request;
    private Proveedor proveedorGuardado;

    @BeforeEach
    void setUp() {
        request = new ProveedorRequest();
        request.setNombre("Distribuidora Tech");
        request.setRut("76.123.456-7");
        request.setCorreo("contacto@tech.cl");
        request.setDireccion("Av. Providencia 1234");
        request.setTelefono("+56911112222");

        proveedorGuardado = new Proveedor();
        proveedorGuardado.setId(1L);
        proveedorGuardado.setNombre("Distribuidora Tech");
        proveedorGuardado.setRut("76.123.456-7");
        proveedorGuardado.setCorreo("contacto@tech.cl");
        proveedorGuardado.setDireccion("Av. Providencia 1234");
        proveedorGuardado.setTelefono("+56911112222");
    }

    @Test
    @DisplayName("guardar: registra exitosamente cuando el rut y correo son únicos")
    void debeGuardarProveedorExitosamente() {
        when(repository.existsByCorreo(request.getCorreo())).thenReturn(false);
        when(repository.existsByRut(request.getRut())).thenReturn(false);
        when(repository.save(any(Proveedor.class))).thenReturn(proveedorGuardado);

        ProveedorDTO resultado = service.guardar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("76.123.456-7", resultado.getRut());
        verify(repository, times(1)).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("guardar: lanza ResponseStatusException cuando el correo ya existe")
    void debeLanzarExcepcionCuandoCorreoDuplicado() {
        when(repository.existsByCorreo(request.getCorreo())).thenReturn(true);

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class,
                () -> service.guardar(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, excepcion.getStatusCode());
        assertEquals("El correo ya fue ingresado", excepcion.getReason());
        verify(repository, never()).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("guardar: lanza ResponseStatusException cuando el RUT ya existe")
    void debeLanzarExcepcionCuandoRutDuplicado() {
        when(repository.existsByCorreo(request.getCorreo())).thenReturn(false);
        when(repository.existsByRut(request.getRut())).thenReturn(true);

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class,
                () -> service.guardar(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, excepcion.getStatusCode());
        assertEquals("El rut ya existe", excepcion.getReason());
        verify(repository, never()).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("listar: obtiene todas las entidades mapeadas correctamente a DTO")
    void debeListarProveedores() {
        when(repository.findAll()).thenReturn(List.of(proveedorGuardado));

        List<ProveedorDTO> resultado = service.listar();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Distribuidora Tech", resultado.getFirst().getNombre());
    }

    @Test
    @DisplayName("buscar: obtiene el DTO completo si el ID existe")
    void debeBuscarPorIdExitosamente() {
        when(repository.findById(1L)).thenReturn(Optional.of(proveedorGuardado));

        ProveedorDTO resultado = service.buscar(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("contacto@tech.cl", resultado.getCorreo());
    }

    @Test
    @DisplayName("buscar: lanza ProveedorNotFoundException si el ID no existe")
    void debeLanzarExcepcionSiIdNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProveedorNotFoundException.class, () -> service.buscar(99L));
    }

    @Test
    @DisplayName("actualizar: modifica datos exitosamente si no colisiona con otros registros")
    void debeActualizarExitosamente() {
        when(repository.findById(1L)).thenReturn(Optional.of(proveedorGuardado));
        when(repository.save(any(Proveedor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        request.setNombre("Tech Corp");
        ProveedorDTO resultado = service.actualizar(1L, request);

        assertNotNull(resultado);
        assertEquals("Tech Corp", resultado.getNombre());
    }

    @Test
    @DisplayName("eliminar: borra el registro físicamente si se localiza en la BD")
    void debeEliminarExitosamente() {
        when(repository.findById(1L)).thenReturn(Optional.of(proveedorGuardado));
        doNothing().when(repository).delete(proveedorGuardado);

        assertDoesNotThrow(() -> service.eliminar(1L));
        verify(repository, times(1)).delete(proveedorGuardado);
    }
}