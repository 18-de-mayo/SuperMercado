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

import static org.assertj.core.api.Assertions.*;
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

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getRut()).isEqualTo("76.123.456-7");
        verify(repository, times(1)).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("guardar: lanza ResponseStatusException cuando el correo ya existe")
    void debeLanzarExcepcionCuandoCorreoDuplicado() {
        when(repository.existsByCorreo(request.getCorreo())).thenReturn(true);

        assertThatThrownBy(() -> service.guardar(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                .hasMessageContaining("El correo ya fue ingresado");
        verify(repository, never()).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("guardar: lanza ResponseStatusException cuando el RUT ya existe")
    void debeLanzarExcepcionCuandoRutDuplicado() {
        when(repository.existsByCorreo(request.getCorreo())).thenReturn(false);
        when(repository.existsByRut(request.getRut())).thenReturn(true);

        assertThatThrownBy(() -> service.guardar(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                .hasMessageContaining("El rut ya existe");
        verify(repository, never()).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("listar: obtiene todas las entidades mapeadas correctamente a DTO")
    void debeListarProveedores() {
        when(repository.findAll()).thenReturn(List.of(proveedorGuardado));

        List<ProveedorDTO> resultado = service.listar();

        assertThat(resultado).isNotEmpty().hasSize(1);
        assertThat(resultado.getFirst().getNombre()).isEqualTo("Distribuidora Tech");
    }

    @Test
    @DisplayName("buscar: obtiene el DTO completo si el ID existe")
    void debeBuscarPorIdExitosamente() {
        when(repository.findById(1L)).thenReturn(Optional.of(proveedorGuardado));

        ProveedorDTO resultado = service.buscar(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getCorreo()).isEqualTo("contacto@tech.cl");
    }

    @Test
    @DisplayName("buscar: lanza ProveedorNotFoundException si el ID no existe")
    void debeLanzarExcepcionSiIdNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(99L)).isInstanceOf(ProveedorNotFoundException.class);
    }

    @Test
    @DisplayName("actualizar: modifica datos exitosamente si no colisiona con otros registros")
    void debeActualizarExitosamente() {
        when(repository.findById(1L)).thenReturn(Optional.of(proveedorGuardado));
        when(repository.save(any(Proveedor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        request.setNombre("Tech Corp");
        ProveedorDTO resultado = service.actualizar(1L, request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Tech Corp");
    }

    @Test
    @DisplayName("actualizar: lanza NotFoundException si el ID no existe")
    void debeLanzarExcepcionCuandoProveedorAActualizarNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(999L, request))
                .isInstanceOf(ProveedorNotFoundException.class);
        verify(repository, never()).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("actualizar: lanza BAD_REQUEST cuando el RUT nuevo ya está registrado por otro proveedor")
    void debeLanzarExcepcionCuandoRutYaExisteAlActualizar() {
        Proveedor proveedorOriginal = new Proveedor();
        proveedorOriginal.setId(1L);
        proveedorOriginal.setRut("76.123.456-7");
        proveedorOriginal.setCorreo("contacto@tech.cl");

        request.setRut("11.111.111-1");
        when(repository.findById(1L)).thenReturn(Optional.of(proveedorOriginal));
        when(repository.existsByRut(request.getRut())).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                .hasMessageContaining("RUT ingresado ya está registrado");
        verify(repository, never()).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("actualizar: lanza BAD_REQUEST cuando el correo nuevo ya está registrado por otro proveedor")
    void debeLanzarExcepcionCuandoCorreoYaExisteAlActualizar() {
        Proveedor proveedorOriginal = new Proveedor();
        proveedorOriginal.setId(1L);
        proveedorOriginal.setRut("76.123.456-7");
        proveedorOriginal.setCorreo("contacto@tech.cl");

        request.setCorreo("otro@correo.cl");
        when(repository.findById(1L)).thenReturn(Optional.of(proveedorOriginal));
        when(repository.existsByCorreo(request.getCorreo())).thenReturn(true);

        assertThatThrownBy(() -> service.actualizar(1L, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                .hasMessageContaining("correo ingresado ya está registrado");
        verify(repository, never()).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("eliminar: borra el registro físicamente si se localiza en la BD")
    void debeEliminarExitosamente() {
        when(repository.findById(1L)).thenReturn(Optional.of(proveedorGuardado));
        doNothing().when(repository).delete(proveedorGuardado);

        assertThatNoException().isThrownBy(() -> service.eliminar(1L));
        verify(repository, times(1)).delete(proveedorGuardado);
    }

    @Test
    @DisplayName("eliminar: lanza NotFoundException si el ID no existe")
    void debeLanzarExcepcionAlEliminarProveedorInexistente() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar(999L))
                .isInstanceOf(ProveedorNotFoundException.class);
        verify(repository, never()).delete(any(Proveedor.class));
    }
}