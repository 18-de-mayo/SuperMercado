package duoc.cl.catalogo.service;

import duoc.cl.catalogo.dto.CampanaDTO;
import duoc.cl.catalogo.dto.CatalogoItemDTO;
import duoc.cl.catalogo.dto.ProductoDTO;
import duoc.cl.catalogo.feign.ProductoFeignClient;
import duoc.cl.catalogo.model.CatalogoCampana;
import duoc.cl.catalogo.model.CatalogoItem;
import duoc.cl.catalogo.repository.CatalogoCampanaRepository;
import duoc.cl.catalogo.repository.CatalogoItemRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CatalogoService - Pruebas Unitarias")
class CatalogoServiceTest {

    @Mock
    private CatalogoCampanaRepository campanaRepository;

    @Mock
    private CatalogoItemRepository itemRepository;

    @Mock
    private ProductoFeignClient productoFeignClient;

    @InjectMocks
    private CatalogoService catalogoService;

    private CatalogoCampana campanaVerano;
    private CatalogoItem itemLeche;
    private ProductoDTO productoDisponible;
    private ProductoDTO productoSinStock;

    @BeforeEach
    void setUp() {
        campanaVerano = new CatalogoCampana();
        campanaVerano.setId(1L);
        campanaVerano.setNombreCampana("Verano 2026");

        itemLeche = new CatalogoItem();
        itemLeche.setId(10L);
        itemLeche.setProductoId(100L);
        itemLeche.setPrecioCatalogo(1500.0);
        itemLeche.setPrecioOferta(1200.0);

        productoDisponible = new ProductoDTO();
        productoDisponible.setId(100L);
        productoDisponible.setNombre("Leche Entera");
        productoDisponible.setDescripcion("Leche de vaca 1L");
        productoDisponible.setCantidad(50);
        productoDisponible.setNombreProveedor("Lácteos SA");

        productoSinStock = new ProductoDTO();
        productoSinStock.setId(200L);
        productoSinStock.setNombre("Queso");
        productoSinStock.setDescripcion("Queso rallado");
        productoSinStock.setCantidad(0);
        productoSinStock.setNombreProveedor("Lácteos SA");
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 1: CREAR CAMPAÑA
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("crearCampana()")
    class CrearCampanaTests {

        @Test
        @DisplayName("Debe crear una campaña correctamente cuando los datos son válidos")
        void crearCampana_datosValidos_retornaCampanaDTO() {
            // Given
            CatalogoCampana campanaGuardada = new CatalogoCampana();
            campanaGuardada.setId(1L);
            campanaGuardada.setNombreCampana("Verano 2026");
            campanaGuardada.setItems(null);

            when(campanaRepository.save(any(CatalogoCampana.class))).thenReturn(campanaGuardada);

            // When
            CampanaDTO resultado = catalogoService.crearCampana("Verano 2026");

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNombreCampana()).isEqualTo("Verano 2026");
            assertThat(resultado.getItems()).isNotNull().isEmpty();
            verify(campanaRepository, times(1)).save(any(CatalogoCampana.class));
        }
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 2: AGREGAR PRODUCTO A CAMPAÑA
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("agregarProductoACampana()")
    class AgregarProductoACampanaTests {

        @Test
        @DisplayName("Debe agregar un producto a una campaña existente correctamente")
        void agregarProductoACampana_campanaExisteProductoExiste_retornaCampanaDTO() {
            // Given
            CatalogoCampana campanaConItem = new CatalogoCampana();
            campanaConItem.setId(1L);
            campanaConItem.setNombreCampana("Verano 2026");
            CatalogoItem savedItem = new CatalogoItem();
            savedItem.setId(10L);
            savedItem.setProductoId(100L);
            savedItem.setPrecioCatalogo(1500.0);
            savedItem.setPrecioOferta(1200.0);
            campanaConItem.getItems().add(savedItem);

            when(campanaRepository.findById(1L))
                    .thenReturn(Optional.of(campanaVerano))
                    .thenReturn(Optional.of(campanaConItem));
            when(productoFeignClient.buscarProducto(100L)).thenReturn(productoDisponible);
            when(itemRepository.save(any(CatalogoItem.class))).thenReturn(itemLeche);

            // When
            CampanaDTO resultado = catalogoService.agregarProductoACampana(1L, 100L, 1500.0, 1200.0);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getItems()).hasSize(1);
            assertThat(resultado.getItems().get(0).getProductoId()).isEqualTo(100L);
            verify(itemRepository, times(1)).save(any(CatalogoItem.class));
        }

        @Test
        @DisplayName("Debe lanzar ResponseStatusException NOT_FOUND cuando la campaña no existe")
        void agregarProductoACampana_campanaNoExiste_lanzaNotFound() {
            // Given
            when(campanaRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> catalogoService.agregarProductoACampana(99L, 100L, 1500.0, 1200.0))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                    .hasMessageContaining("Campaña no encontrada");

            verify(campanaRepository, never()).save(any());
            verify(itemRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar ResponseStatusException BAD_REQUEST cuando el producto no existe en el maestro")
        void agregarProductoACampana_productoNoExiste_lanzaBadRequest() {
            // Given
            when(campanaRepository.findById(1L)).thenReturn(Optional.of(campanaVerano));
            when(productoFeignClient.buscarProducto(999L)).thenThrow(new RuntimeException("Producto no encontrado"));

            // When / Then
            assertThatThrownBy(() -> catalogoService.agregarProductoACampana(1L, 999L, 1500.0, 1200.0))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                    .hasMessageContaining("no existe en el catálogo maestro");

            verify(itemRepository, never()).save(any());
        }
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 3: OBTENER CAMPAÑA
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("obtenerCampana()")
    class ObtenerCampanaTests {

        @Test
        @DisplayName("Debe retornar la campaña cuando el ID existe")
        void obtenerCampana_idExiste_retornaCampanaDTO() {
            // Given
            CatalogoItem item = new CatalogoItem();
            item.setId(10L);
            item.setProductoId(100L);
            item.setPrecioCatalogo(1500.0);
            item.setPrecioOferta(1200.0);
            item.setCampana(campanaVerano);
            campanaVerano.getItems().add(item);

            when(campanaRepository.findById(1L)).thenReturn(Optional.of(campanaVerano));
            when(productoFeignClient.buscarProducto(100L)).thenReturn(productoDisponible);

            // When
            CampanaDTO resultado = catalogoService.obtenerCampana(1L);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getNombreCampana()).isEqualTo("Verano 2026");
            assertThat(resultado.getItems()).hasSize(1);
            assertThat(resultado.getItems().get(0).getNombreProducto()).isEqualTo("Leche Entera");
        }

        @Test
        @DisplayName("Debe lanzar ResponseStatusException NOT_FOUND cuando la campaña no existe")
        void obtenerCampana_idNoExiste_lanzaNotFound() {
            // Given
            when(campanaRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> catalogoService.obtenerCampana(99L))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                    .hasMessageContaining("Campaña no encontrada");
        }
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 4: OBTENER ITEM INDIVIDUAL
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("obtenerItemIndividualPorId()")
    class ObtenerItemIndividualTests {

        @Test
        @DisplayName("Debe retornar el item con datos del producto cuando existe y tiene stock")
        void obtenerItemIndividualPorId_itemExisteStockDisponible_retornaItemDTO() {
            // Given
            when(itemRepository.findById(10L)).thenReturn(Optional.of(itemLeche));
            when(productoFeignClient.buscarProducto(100L)).thenReturn(productoDisponible);

            // When
            CatalogoItemDTO resultado = catalogoService.obtenerItemIndividualPorId(10L);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(10L);
            assertThat(resultado.getProductoId()).isEqualTo(100L);
            assertThat(resultado.getNombreProducto()).isEqualTo("Leche Entera");
            assertThat(resultado.getDescripcion()).isEqualTo("Leche de vaca 1L");
            assertThat(resultado.getNombreProveedor()).isEqualTo("Lácteos SA");
            assertThat(resultado.getPrecioCatalogo()).isEqualTo(1500.0);
            assertThat(resultado.getPrecioOferta()).isEqualTo(1200.0);
            assertThat(resultado.getEstadoStock()).isEqualTo("Disponible");
        }

        @Test
        @DisplayName("Debe retornar el item con estado 'Sin stock' cuando la cantidad es cero")
        void obtenerItemIndividualPorId_itemExisteSinStock_retornaEstadoSinStock() {
            // Given
            when(itemRepository.findById(10L)).thenReturn(Optional.of(itemLeche));
            when(productoFeignClient.buscarProducto(100L)).thenReturn(productoSinStock);

            // When
            CatalogoItemDTO resultado = catalogoService.obtenerItemIndividualPorId(10L);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getEstadoStock()).isEqualTo("Sin stock");
        }

        @Test
        @DisplayName("Debe retornar el item con valores 'No disponible' cuando el Feign falla")
        void obtenerItemIndividualPorId_feignFalla_retornaNoDisponible() {
            // Given
            when(itemRepository.findById(10L)).thenReturn(Optional.of(itemLeche));
            when(productoFeignClient.buscarProducto(100L)).thenThrow(new RuntimeException("Error de conexión"));

            // When
            CatalogoItemDTO resultado = catalogoService.obtenerItemIndividualPorId(10L);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getNombreProducto()).isEqualTo("No disponible");
            assertThat(resultado.getDescripcion()).isEqualTo("El producto fue eliminado.");
            assertThat(resultado.getNombreProveedor()).isEqualTo("N/A");
            assertThat(resultado.getEstadoStock()).isEqualTo("No disponible");
        }

        @Test
        @DisplayName("Debe lanzar ResponseStatusException NOT_FOUND cuando el item no existe")
        void obtenerItemIndividualPorId_itemNoExiste_lanzaNotFound() {
            // Given
            when(itemRepository.findById(99L)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> catalogoService.obtenerItemIndividualPorId(99L))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND)
                    .hasMessageContaining("no existe con ID: 99");
        }
    }

    // ════════════════════════════════════════════════════════
    // BLOQUE 5: MAPEO mapToCampanaDTO (items null)
    // ════════════════════════════════════════════════════════

    @Nested
    @DisplayName("mapToCampanaDTO() - items null")
    class MapToCampanaDTOTests {

        @Test
        @DisplayName("Debe retornar lista vacía cuando los items de la campaña son null")
        void mapToCampanaDTO_itemsNull_retornaListaVacia() {
            // Given
            CatalogoCampana campanaSinItems = new CatalogoCampana();
            campanaSinItems.setId(2L);
            campanaSinItems.setNombreCampana("Invierno 2026");
            campanaSinItems.setItems(null);

            when(campanaRepository.findById(2L)).thenReturn(Optional.of(campanaSinItems));

            // When
            CampanaDTO resultado = catalogoService.obtenerCampana(2L);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getItems()).isNotNull().isEmpty();
        }
    }
}
