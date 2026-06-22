package duoc.cl.categoria.service;

import duoc.cl.categoria.dto.CategoriaDTO;
import duoc.cl.categoria.dto.CategoriaRequest;
import duoc.cl.categoria.exception.CategoriaNotFoundException;
import duoc.cl.categoria.model.CategoriaModel;
import duoc.cl.categoria.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoriaService - Pruebas Unitarias")
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaService categoriaService;

    private CategoriaModel categoriaBebestibles;
    private CategoriaRequest requestBebestibles;

    @BeforeEach
    void setUp() {
        categoriaBebestibles = CategoriaModel.builder()
                .id(1L)
                .nombre("Bebestibles")
                .descripcion("Aguas, jugos y bebidas")
                .build();

        requestBebestibles = new CategoriaRequest();
        requestBebestibles.setNombre("Bebestibles");
        requestBebestibles.setDescripcion("Aguas, jugos y bebidas");
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 1: GUARDAR
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("guardar()")
    class GuardarTests {

        @Test
        @DisplayName("Debe guardar una categoría cuando el nombre es único")
        void guardar_nombreUnico_retornaCategoriaDTO() {
            // Given
            when(repository.findByNombreIgnoreCase("Bebestibles")).thenReturn(Optional.empty());
            when(repository.save(any(CategoriaModel.class))).thenReturn(categoriaBebestibles);

            // When
            CategoriaDTO resultado = categoriaService.guardar(requestBebestibles);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNombre()).isEqualTo("Bebestibles");
            assertThat(resultado.getDescripcion()).isEqualTo("Aguas, jugos y bebidas");
            verify(repository, times(1)).save(any(CategoriaModel.class));
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException cuando el nombre ya existe")
        void guardar_nombreDuplicado_lanzaIllegalArgumentException() {
            // Given
            when(repository.findByNombreIgnoreCase("Bebestibles")).thenReturn(Optional.of(categoriaBebestibles));

            // When / Then
            assertThatThrownBy(() -> categoriaService.guardar(requestBebestibles))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Ya existe una categoría con el nombre: Bebestibles");

            verify(repository, never()).save(any());
        }
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 2: LISTAR
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("listar()")
    class ListarTests {

        @Test
        @DisplayName("Debe retornar lista de categorías cuando existen registros")
        void listar_existenCategorias_retornaLista() {
            // Given
            CategoriaModel categoriaLacteos = CategoriaModel.builder()
                    .id(2L)
                    .nombre("Lácteos")
                    .descripcion("Leche, quesos y yogures")
                    .build();

            when(repository.findAll()).thenReturn(List.of(categoriaBebestibles, categoriaLacteos));

            // When
            List<CategoriaDTO> resultado = categoriaService.listar();

            // Then
            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0).getNombre()).isEqualTo("Bebestibles");
            assertThat(resultado.get(1).getNombre()).isEqualTo("Lácteos");
            verify(repository, times(1)).findAll();
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay categorías")
        void listar_sinCategorias_retornaListaVacia() {
            // Given
            when(repository.findAll()).thenReturn(List.of());

            // When
            List<CategoriaDTO> resultado = categoriaService.listar();

            // Then
            assertThat(resultado).isNotNull().isEmpty();
            verify(repository, times(1)).findAll();
        }
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 3: BUSCAR POR ID
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("buscarPorId()")
    class BuscarPorIdTests {

        @Test
        @DisplayName("Debe retornar la categoría cuando el ID existe")
        void buscarPorId_idExiste_retornaCategoriaDTO() {
            // Given
            when(repository.findById(1L)).thenReturn(Optional.of(categoriaBebestibles));

            // When
            CategoriaDTO resultado = categoriaService.buscarPorId(1L);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNombre()).isEqualTo("Bebestibles");
        }

        @Test
        @DisplayName("Debe lanzar CategoriaNotFoundException cuando el ID no existe")
        void buscarPorId_idNoExiste_lanzaCategoriaNotFoundException() {
            // Given
            when(repository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> categoriaService.buscarPorId(99L))
                    .isInstanceOf(CategoriaNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 4: ACTUALIZAR
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("actualizar()")
    class ActualizarTests {

        @Test
        @DisplayName("Debe actualizar la categoría correctamente cuando los datos son válidos")
        void actualizar_datosValidos_retornaCategoriaActualizada() {
            // Given
            CategoriaRequest requestActualizado = new CategoriaRequest();
            requestActualizado.setNombre("Bebestibles Premium");
            requestActualizado.setDescripcion("Aguas, jugos y bebidas importadas");

            CategoriaModel categoriaActualizada = CategoriaModel.builder()
                    .id(1L)
                    .nombre("Bebestibles Premium")
                    .descripcion("Aguas, jugos y bebidas importadas")
                    .build();

            when(repository.findById(1L)).thenReturn(Optional.of(categoriaBebestibles));
            when(repository.findByNombreIgnoreCase("Bebestibles Premium")).thenReturn(Optional.empty());
            when(repository.save(any(CategoriaModel.class))).thenReturn(categoriaActualizada);

            // When
            CategoriaDTO resultado = categoriaService.actualizar(1L, requestActualizado);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNombre()).isEqualTo("Bebestibles Premium");
            assertThat(resultado.getDescripcion()).isEqualTo("Aguas, jugos y bebidas importadas");
            verify(repository, times(1)).save(any(CategoriaModel.class));
        }

        @Test
        @DisplayName("Debe lanzar CategoriaNotFoundException cuando el ID a actualizar no existe")
        void actualizar_idNoExiste_lanzaCategoriaNotFoundException() {
            // Given
            when(repository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> categoriaService.actualizar(99L, requestBebestibles))
                    .isInstanceOf(CategoriaNotFoundException.class)
                    .hasMessageContaining("99");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar IllegalArgumentException cuando el nuevo nombre ya está en uso por otra categoría")
        void actualizar_nombreConflictivoConOtroId_lanzaIllegalArgumentException() {
            // Given
            CategoriaRequest requestNombreExistente = new CategoriaRequest();
            requestNombreExistente.setNombre("Lácteos");
            requestNombreExistente.setDescripcion("Descripción");

            CategoriaModel categoriaLacteos = CategoriaModel.builder()
                    .id(2L)
                    .nombre("Lácteos")
                    .descripcion("Leche, quesos y yogures")
                    .build();

            when(repository.findById(1L)).thenReturn(Optional.of(categoriaBebestibles));
            when(repository.findByNombreIgnoreCase("Lácteos")).thenReturn(Optional.of(categoriaLacteos));

            // When / Then
            assertThatThrownBy(() -> categoriaService.actualizar(1L, requestNombreExistente))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Ya existe otra categoría con el nombre: Lácteos");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Debe permitir actualizar cuando el nombre se mantiene igual en la misma categoría")
        void actualizar_mismoNombreMismoId_actualizaExitosamente() {
            // Given
            CategoriaRequest requestMismoNombre = new CategoriaRequest();
            requestMismoNombre.setNombre("Bebestibles");
            requestMismoNombre.setDescripcion("Nueva descripción");

            CategoriaModel categoriaActualizada = CategoriaModel.builder()
                    .id(1L)
                    .nombre("Bebestibles")
                    .descripcion("Nueva descripción")
                    .build();

            when(repository.findById(1L)).thenReturn(Optional.of(categoriaBebestibles));
            when(repository.findByNombreIgnoreCase("Bebestibles")).thenReturn(Optional.of(categoriaBebestibles));
            when(repository.save(any(CategoriaModel.class))).thenReturn(categoriaActualizada);

            // When
            CategoriaDTO resultado = categoriaService.actualizar(1L, requestMismoNombre);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNombre()).isEqualTo("Bebestibles");
            assertThat(resultado.getDescripcion()).isEqualTo("Nueva descripción");
            verify(repository, times(1)).save(any(CategoriaModel.class));
        }
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 5: ELIMINAR
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("eliminar()")
    class EliminarTests {

        @Test
        @DisplayName("Debe eliminar la categoría cuando el ID existe")
        void eliminar_idExiste_eliminaCorrectamente() {
            // Given
            when(repository.existsById(1L)).thenReturn(true);
            doNothing().when(repository).deleteById(1L);

            // When
            categoriaService.eliminar(1L);

            // Then
            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar CategoriaNotFoundException al eliminar un ID inexistente")
        void eliminar_idNoExiste_lanzaCategoriaNotFoundException() {
            // Given
            when(repository.existsById(99L)).thenReturn(false);

            // When / Then
            assertThatThrownBy(() -> categoriaService.eliminar(99L))
                    .isInstanceOf(CategoriaNotFoundException.class)
                    .hasMessageContaining("99");

            verify(repository, never()).deleteById(any());
        }
    }
}
