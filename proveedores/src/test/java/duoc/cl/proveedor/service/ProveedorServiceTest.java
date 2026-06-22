package duoc.cl.proveedor.service;

import duoc.cl.proveedor.dto.ProveedorDTO;
import duoc.cl.proveedor.dto.ProveedorRequest;
import duoc.cl.proveedor.exception.ProveedorNotFoundException;
import duoc.cl.proveedor.model.Proveedor;
import duoc.cl.proveedor.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProveedorService Test")
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository repository;

    @InjectMocks
    private ProveedorService service;

    private ProveedorRequest request;
    private Proveedor proveedor;
    private Proveedor proveedorGuardado;

    @BeforeEach
    void setUp() {
        request = new ProveedorRequest();
        request.setNombre("Proveedor Test");
        request.setRut("12345678-9");
        request.setCorreo("test@test.com");
        request.setDireccion("Direccion test");
        request.setTelefono("123456789");

        proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Proveedor Test");
        proveedor.setRut("12345678-9");
        proveedor.setCorreo("test@test.com");
        proveedor.setDireccion("Direccion test");
        proveedor.setTelefono("123456789");

        proveedorGuardado = new Proveedor();
        proveedorGuardado.setId(1L);
        proveedorGuardado.setNombre("Proveedor Test");
        proveedorGuardado.setRut("12345678-9");
        proveedorGuardado.setCorreo("test@test.com");
        proveedorGuardado.setDireccion("Direccion test");
        proveedorGuardado.setTelefono("123456789");
    }

    @Nested
    @DisplayName("Guardar proveedor")
    class GuardarProveedor {

        @Test
        @DisplayName("should save and return ProveedorDTO when email and rut are unique")
        void guardarSuccess() {
            when(repository.existsByCorreo(request.getCorreo())).thenReturn(false);
            when(repository.existsByRut(request.getRut())).thenReturn(false);
            when(repository.save(any(Proveedor.class))).thenReturn(proveedorGuardado);

            ProveedorDTO result = service.guardar(request);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNombre()).isEqualTo("Proveedor Test");
            verify(repository).save(any(Proveedor.class));
        }

        @Test
        @DisplayName("should throw ResponseStatusException when email is duplicate")
        void guardarDuplicateEmail() {
            when(repository.existsByCorreo(request.getCorreo())).thenReturn(true);

            assertThatThrownBy(() -> service.guardar(request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                    .hasMessageContaining("correo ya fue ingresado");

            verify(repository, never()).save(any(Proveedor.class));
        }

        @Test
        @DisplayName("should throw ResponseStatusException when rut is duplicate")
        void guardarDuplicateRut() {
            when(repository.existsByCorreo(request.getCorreo())).thenReturn(false);
            when(repository.existsByRut(request.getRut())).thenReturn(true);

            assertThatThrownBy(() -> service.guardar(request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                    .hasMessageContaining("rut ya existe");

            verify(repository, never()).save(any(Proveedor.class));
        }
    }

    @Nested
    @DisplayName("Listar proveedores")
    class ListarProveedores {

        @Test
        @DisplayName("should return list of ProveedorDTO when proveedores exist")
        void listarReturnsList() {
            when(repository.findAll()).thenReturn(List.of(proveedor));

            List<ProveedorDTO> result = service.listar();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNombre()).isEqualTo("Proveedor Test");
        }

        @Test
        @DisplayName("should return empty list when no proveedores exist")
        void listarEmptyList() {
            when(repository.findAll()).thenReturn(Collections.emptyList());

            List<ProveedorDTO> result = service.listar();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Buscar proveedor por ID")
    class BuscarProveedor {

        @Test
        @DisplayName("should return ProveedorDTO when proveedor is found")
        void buscarFound() {
            when(repository.findById(1L)).thenReturn(Optional.of(proveedor));

            ProveedorDTO result = service.buscar(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNombre()).isEqualTo("Proveedor Test");
        }

        @Test
        @DisplayName("should throw ProveedorNotFoundException when proveedor is not found")
        void buscarNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscar(99L))
                    .isInstanceOf(ProveedorNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("Actualizar proveedor")
    class ActualizarProveedor {

        @Test
        @DisplayName("should update and return ProveedorDTO when no conflicts exist")
        void actualizarSuccess() {
            when(repository.findById(1L)).thenReturn(Optional.of(proveedor));
            when(repository.save(any(Proveedor.class))).thenReturn(proveedorGuardado);

            ProveedorDTO result = service.actualizar(1L, request);

            assertThat(result).isNotNull();
            assertThat(result.getNombre()).isEqualTo("Proveedor Test");
            verify(repository).save(any(Proveedor.class));
        }

        @Test
        @DisplayName("should throw ProveedorNotFoundException when proveedor does not exist")
        void actualizarNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizar(99L, request))
                    .isInstanceOf(ProveedorNotFoundException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("should throw ResponseStatusException when rut conflicts with another proveedor")
        void actualizarRutConflict() {
            ProveedorRequest requestOtroRut = new ProveedorRequest();
            requestOtroRut.setNombre("Otro");
            requestOtroRut.setRut("98765432-1");
            requestOtroRut.setCorreo("test@test.com");
            requestOtroRut.setDireccion("Otra direccion");
            requestOtroRut.setTelefono("987654321");

            when(repository.findById(1L)).thenReturn(Optional.of(proveedor));
            when(repository.existsByRut("98765432-1")).thenReturn(true);

            assertThatThrownBy(() -> service.actualizar(1L, requestOtroRut))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                    .hasMessageContaining("RUT ingresado ya está registrado");

            verify(repository, never()).save(any(Proveedor.class));
        }

        @Test
        @DisplayName("should throw ResponseStatusException when correo conflicts with another proveedor")
        void actualizarCorreoConflict() {
            ProveedorRequest requestOtroCorreo = new ProveedorRequest();
            requestOtroCorreo.setNombre("Otro");
            requestOtroCorreo.setRut("12345678-9");
            requestOtroCorreo.setCorreo("otro@test.com");
            requestOtroCorreo.setDireccion("Otra direccion");
            requestOtroCorreo.setTelefono("987654321");

            when(repository.findById(1L)).thenReturn(Optional.of(proveedor));
            when(repository.existsByCorreo("otro@test.com")).thenReturn(true);

            assertThatThrownBy(() -> service.actualizar(1L, requestOtroCorreo))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                    .hasMessageContaining("correo ingresado ya está registrado");

            verify(repository, never()).save(any(Proveedor.class));
        }

        @Test
        @DisplayName("should allow update when same rut belongs to same proveedor")
        void actualizarSameRutSameIdAllowed() {
            when(repository.findById(1L)).thenReturn(Optional.of(proveedor));
            when(repository.save(any(Proveedor.class))).thenReturn(proveedorGuardado);

            ProveedorDTO result = service.actualizar(1L, request);

            assertThat(result).isNotNull();
            assertThat(result.getNombre()).isEqualTo("Proveedor Test");
            verify(repository).save(any(Proveedor.class));
        }
    }

    @Nested
    @DisplayName("Eliminar proveedor")
    class EliminarProveedor {

        @Test
        @DisplayName("should delete proveedor when it exists")
        void eliminarSuccess() {
            when(repository.findById(1L)).thenReturn(Optional.of(proveedor));

            service.eliminar(1L);

            verify(repository).delete(proveedor);
        }

        @Test
        @DisplayName("should throw ProveedorNotFoundException when proveedor does not exist")
        void eliminarNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.eliminar(99L))
                    .isInstanceOf(ProveedorNotFoundException.class)
                    .hasMessageContaining("99");

            verify(repository, never()).delete(any(Proveedor.class));
        }
    }
}
