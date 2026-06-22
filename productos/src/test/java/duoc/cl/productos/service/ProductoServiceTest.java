package duoc.cl.productos.service;

import duoc.cl.productos.client.ProveedorClient;
import duoc.cl.productos.dto.ProductoDTO;
import duoc.cl.productos.dto.ProductoRequest;
import duoc.cl.productos.dto.ProveedorDTO;
import duoc.cl.productos.exception.ProductoNotFoundException;
import duoc.cl.productos.model.Producto;
import duoc.cl.productos.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductoService Test")
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @Mock
    private ProveedorClient proveedorClient;

    @InjectMocks
    private ProductoService service;

    private ProductoRequest request;
    private Producto producto;
    private Producto productoGuardado;
    private ProveedorDTO proveedorDTO;

    @BeforeEach
    void setUp() {
        request = new ProductoRequest();
        request.setNombre("Test Producto");
        request.setDescripcion("Descripcion test");
        request.setPrecio(BigDecimal.valueOf(100.0));
        request.setCantidad(10);
        request.setProveedorId(1L);

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Test Producto");
        producto.setDescripcion("Descripcion test");
        producto.setPrecio(BigDecimal.valueOf(100.0));
        producto.setCantidad(10);
        producto.setProveedorId(1L);

        productoGuardado = new Producto();
        productoGuardado.setId(1L);
        productoGuardado.setNombre("Test Producto");
        productoGuardado.setDescripcion("Descripcion test");
        productoGuardado.setPrecio(BigDecimal.valueOf(100.0));
        productoGuardado.setCantidad(10);
        productoGuardado.setProveedorId(1L);

        proveedorDTO = new ProveedorDTO();
        proveedorDTO.setId(1L);
        proveedorDTO.setNombre("Proveedor Test");
    }

    @Nested
    @DisplayName("Guardar producto")
    class GuardarProducto {

        @Test
        @DisplayName("should save and return ProductoDTO when request is valid")
        void guardarSuccess() {
            when(repository.save(any(Producto.class))).thenReturn(productoGuardado);
            when(proveedorClient.obtenerProveedor(anyLong())).thenReturn(proveedorDTO);

            ProductoDTO result = service.guardar(request);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNombre()).isEqualTo("Test Producto");
            assertThat(result.getNombreProveedor()).isEqualTo("Proveedor Test");
            verify(repository).save(any(Producto.class));
        }
    }

    @Nested
    @DisplayName("Listar productos")
    class ListarProductos {

        @Test
        @DisplayName("should return list of ProductoDTO when products exist")
        void listarReturnsList() {
            when(repository.findAll()).thenReturn(List.of(producto));
            when(proveedorClient.obtenerProveedor(anyLong())).thenReturn(proveedorDTO);

            List<ProductoDTO> result = service.listar();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNombre()).isEqualTo("Test Producto");
        }

        @Test
        @DisplayName("should return empty list when no products exist")
        void listarEmptyList() {
            when(repository.findAll()).thenReturn(Collections.emptyList());

            List<ProductoDTO> result = service.listar();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Buscar producto por ID")
    class BuscarProducto {

        @Test
        @DisplayName("should return ProductoDTO when product is found")
        void buscarFound() {
            when(repository.findById(1L)).thenReturn(Optional.of(producto));
            when(proveedorClient.obtenerProveedor(anyLong())).thenReturn(proveedorDTO);

            ProductoDTO result = service.buscar(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getNombre()).isEqualTo("Test Producto");
            assertThat(result.getNombreProveedor()).isEqualTo("Proveedor Test");
        }

        @Test
        @DisplayName("should throw ProductoNotFoundException when product is not found")
        void buscarNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.buscar(99L))
                    .isInstanceOf(ProductoNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("Buscar productos por nombre")
    class BuscarPorNombre {

        @Test
        @DisplayName("should return matching products when name exists")
        void buscarPorNombreMatches() {
            when(repository.findByNombreContainingIgnoreCase("test")).thenReturn(List.of(producto));
            when(proveedorClient.obtenerProveedor(anyLong())).thenReturn(proveedorDTO);

            List<ProductoDTO> result = service.buscarPorNombre("test");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getNombre()).isEqualTo("Test Producto");
        }

        @Test
        @DisplayName("should return empty list when no products match")
        void buscarPorNombreNoMatches() {
            when(repository.findByNombreContainingIgnoreCase("xyz")).thenReturn(Collections.emptyList());

            List<ProductoDTO> result = service.buscarPorNombre("xyz");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Listar productos con stock")
    class ListarConStock {

        @Test
        @DisplayName("should return products with stock greater than zero")
        void listarConStockReturnsProducts() {
            when(repository.findByCantidadGreaterThan(0)).thenReturn(List.of(producto));
            when(proveedorClient.obtenerProveedor(anyLong())).thenReturn(proveedorDTO);

            List<ProductoDTO> result = service.listarConStock();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCantidad()).isEqualTo(10);
        }

        @Test
        @DisplayName("should return empty list when no products have stock")
        void listarConStockEmpty() {
            when(repository.findByCantidadGreaterThan(0)).thenReturn(Collections.emptyList());

            List<ProductoDTO> result = service.listarConStock();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Listar productos paginado")
    class ListarPaginado {

        @Test
        @DisplayName("should return page of ProductoDTO")
        void listarPaginadoReturnsPage() {
            Page<Producto> productPage = new PageImpl<>(List.of(producto));
            when(repository.findAll(any(Pageable.class))).thenReturn(productPage);
            when(proveedorClient.obtenerProveedor(anyLong())).thenReturn(proveedorDTO);

            Page<ProductoDTO> result = service.listarPaginado(Pageable.unpaged());

            assertThat(result).hasSize(1);
            assertThat(result.getContent().get(0).getNombre()).isEqualTo("Test Producto");
        }
    }

    @Nested
    @DisplayName("Actualizar producto")
    class ActualizarProducto {

        @Test
        @DisplayName("should update and return ProductoDTO when product and proveedor exist")
        void actualizarSuccess() {
            when(repository.findById(1L)).thenReturn(Optional.of(producto));
            when(proveedorClient.obtenerProveedor(anyLong())).thenReturn(proveedorDTO);
            when(repository.save(any(Producto.class))).thenReturn(productoGuardado);

            ProductoDTO result = service.actualizar(1L, request);

            assertThat(result).isNotNull();
            assertThat(result.getNombre()).isEqualTo("Test Producto");
            assertThat(result.getNombreProveedor()).isEqualTo("Proveedor Test");
            verify(repository).save(any(Producto.class));
        }

        @Test
        @DisplayName("should throw ProductoNotFoundException when product does not exist")
        void actualizarNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.actualizar(99L, request))
                    .isInstanceOf(ProductoNotFoundException.class)
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("should throw ResponseStatusException when proveedor does not exist")
        void actualizarProveedorNotFound() {
            when(repository.findById(1L)).thenReturn(Optional.of(producto));
            when(proveedorClient.obtenerProveedor(anyLong())).thenThrow(new RuntimeException("Feign error"));

            assertThatThrownBy(() -> service.actualizar(1L, request))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                    .hasMessageContaining("El proveedor asociado no existe en el sistema.");
        }
    }

    @Nested
    @DisplayName("Eliminar producto")
    class EliminarProducto {

        @Test
        @DisplayName("should delete product when it exists")
        void eliminarSuccess() {
            when(repository.existsById(1L)).thenReturn(true);

            service.eliminar(1L);

            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw ProductoNotFoundException when product does not exist")
        void eliminarNotFound() {
            when(repository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> service.eliminar(99L))
                    .isInstanceOf(ProductoNotFoundException.class)
                    .hasMessageContaining("99");

            verify(repository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("Map to DTO")
    class MapToDTO {

        @Test
        @DisplayName("should set nombreProveedor when proveedorId exists and Feign succeeds")
        void mapToDTOWithProveedorFeignSuccess() {
            when(repository.save(any(Producto.class))).thenReturn(productoGuardado);
            when(proveedorClient.obtenerProveedor(1L)).thenReturn(proveedorDTO);

            ProductoDTO result = service.guardar(request);

            assertThat(result.getNombreProveedor()).isEqualTo("Proveedor Test");
            verify(proveedorClient).obtenerProveedor(1L);
        }

        @Test
        @DisplayName("should skip Feign call when proveedorId is null")
        void mapToDTOWithProveedorIdNull() {
            Producto productoSinProveedor = new Producto();
            productoSinProveedor.setId(2L);
            productoSinProveedor.setNombre("Sin Proveedor");
            productoSinProveedor.setDescripcion("Sin proveedor");
            productoSinProveedor.setPrecio(BigDecimal.valueOf(50.0));
            productoSinProveedor.setCantidad(5);
            productoSinProveedor.setProveedorId(null);

            ProductoRequest requestSinProveedor = new ProductoRequest();
            requestSinProveedor.setNombre("Sin Proveedor");
            requestSinProveedor.setDescripcion("Sin proveedor");
            requestSinProveedor.setPrecio(BigDecimal.valueOf(50.0));
            requestSinProveedor.setCantidad(5);
            requestSinProveedor.setProveedorId(null);

            when(repository.save(any(Producto.class))).thenReturn(productoSinProveedor);

            ProductoDTO result = service.guardar(requestSinProveedor);

            assertThat(result.getNombreProveedor()).isNull();
            verify(proveedorClient, never()).obtenerProveedor(anyLong());
        }

        @Test
        @DisplayName("should set 'Proveedor no disponible' when Feign fails")
        void mapToDTOWithProveedorFeignFails() {
            when(repository.save(any(Producto.class))).thenReturn(productoGuardado);
            when(proveedorClient.obtenerProveedor(1L)).thenThrow(new RuntimeException("Feign error"));

            ProductoDTO result = service.guardar(request);

            assertThat(result.getNombreProveedor()).isEqualTo("Proveedor no disponible");
            verify(proveedorClient).obtenerProveedor(1L);
        }
    }
}
