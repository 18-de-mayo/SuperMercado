package duoc.cl.productos.service;

import duoc.cl.productos.client.CategoriaClient;
import duoc.cl.productos.client.ProveedorClient;
import duoc.cl.productos.dto.CategoriaDTO;
import duoc.cl.productos.dto.ProductoDTO;
import duoc.cl.productos.dto.ProductoRequest;
import duoc.cl.productos.dto.ProveedorDTO;
import duoc.cl.productos.exception.ProductoDuplicadoException;
import duoc.cl.productos.exception.ProductoNotFoundException;
import duoc.cl.productos.model.Producto;
import duoc.cl.productos.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - ProductoService")
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @Mock
    private ProveedorClient proveedorClient;

    @Mock
    private CategoriaClient categoriaClient;

    @InjectMocks
    private ProductoService productoService;

    private ProductoRequest request;
    private Producto productoGuardado;
    private ProveedorDTO proveedorRemoto;

    @BeforeEach
    void setUp() {
        request = new ProductoRequest();
        request.setNombre("Teclado Mecánico");
        request.setDescripcion("Teclado RGB Switch Blue");
        request.setPrecio(BigDecimal.valueOf(45000));
        request.setCantidad(20);
        request.setProveedorId(1L);
        request.setCategoriaId(1L);

        productoGuardado = new Producto();
        productoGuardado.setId(10L);
        productoGuardado.setNombre("Teclado Mecánico");
        productoGuardado.setDescripcion("Teclado RGB Switch Blue");
        productoGuardado.setPrecio(BigDecimal.valueOf(45000));
        productoGuardado.setCantidad(20);
        productoGuardado.setProveedorId(1L);
        productoGuardado.setCategoriaId(1L);

        proveedorRemoto = new ProveedorDTO();
        proveedorRemoto.setId(1L);
        proveedorRemoto.setNombre("Distribuidora Tech Chile");
    }

    // =========================================================================
    // PRUEBAS DE LA ACCIÓN: GUARDAR PRODUCTO
    // =========================================================================

    @Test
    @DisplayName("guardar: registra el producto exitosamente cuando los datos son válidos y el proveedor existe")
    void debeGuardarProductoExitosamente() {
        when(repository.existsByNombre(request.getNombre())).thenReturn(false);
        when(proveedorClient.obtenerProveedor(request.getProveedorId())).thenReturn(proveedorRemoto);
        when(repository.save(any(Producto.class))).thenReturn(productoGuardado);

        when(categoriaClient.buscarPorId(anyLong())).thenReturn(new CategoriaDTO());

        ProductoDTO resultado = productoService.guardar(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(10L);
        assertThat(resultado.getNombre()).isEqualTo("Teclado Mecánico");
        assertThat(resultado.getNombreProveedor()).isEqualTo("Distribuidora Tech Chile");

        verify(repository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("guardar: lanza ProductoDuplicadoException cuando el nombre ya está registrado en la BD local")
    void debeLanzarExcepcionCuandoProductoEstaDuplicado() {
        when(repository.existsByNombre(request.getNombre())).thenReturn(true);

        assertThatThrownBy(() -> productoService.guardar(request))
                .isInstanceOf(ProductoDuplicadoException.class)
                .hasMessage("El producto 'Teclado Mecánico' ya existe en el sistema.");

        verify(repository, never()).save(any(Producto.class));
        verify(proveedorClient, never()).obtenerProveedor(anyLong());
    }

    @Test
    @DisplayName("guardar: lanza ResponseStatusException (404) cuando el proveedor remoto no existe")
    void debeLanzarExcepcionCuandoProveedorNoExiste() {
        when(repository.existsByNombre(request.getNombre())).thenReturn(false);
        when(proveedorClient.obtenerProveedor(request.getProveedorId())).thenReturn(null);

        assertThatThrownBy(() -> productoService.guardar(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
        verify(repository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("guardar: lanza BAD_REQUEST cuando el ID del proveedor es nulo")
    void debeLanzarExcepcionCuandoProveedorIdEsNulo() {
        request.setProveedorId(null);

        assertThatThrownBy(() -> productoService.guardar(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
        verify(repository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("guardar: lanza BAD_GATEWAY cuando el microservicio de categorías no responde")
    void debeLanzarBadGatewayCuandoCategoriaFallaAlGuardar() {
        when(repository.existsByNombre(request.getNombre())).thenReturn(false);
        when(proveedorClient.obtenerProveedor(request.getProveedorId())).thenReturn(proveedorRemoto);
        when(categoriaClient.buscarPorId(anyLong())).thenThrow(new RuntimeException("Error de red"));

        assertThatThrownBy(() -> productoService.guardar(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_GATEWAY);
        verify(repository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("guardar: lanza BAD_REQUEST cuando el ID de categoría es nulo")
    void debeLanzarExcepcionCuandoCategoriaIdEsNulo() {
        when(repository.existsByNombre(request.getNombre())).thenReturn(false);
        when(proveedorClient.obtenerProveedor(request.getProveedorId())).thenReturn(proveedorRemoto);
        request.setCategoriaId(null);

        assertThatThrownBy(() -> productoService.guardar(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST)
                .hasMessageContaining("ID de la categoría es obligatorio");
        verify(repository, never()).save(any(Producto.class));
    }

    // =========================================================================
    // PRUEBAS DE LA ACCIÓN: ACTUALIZAR PRODUCTO
    // =========================================================================

    @Test
    @DisplayName("actualizar: modifica un producto local exitosamente con datos válidos")
    void debeActualizarProductoExitosamente() {
        Long idExistente = 1L;
        Producto productoExistente = new Producto();
        productoExistente.setId(idExistente);
        productoExistente.setNombre("Mouse Antiguo");

        request.setNombre("Mouse Gamer RGB");

        when(repository.findById(idExistente)).thenReturn(Optional.of(productoExistente));
        when(repository.existsByNombre(request.getNombre())).thenReturn(false);
        when(proveedorClient.obtenerProveedor(request.getProveedorId())).thenReturn(proveedorRemoto);
        when(categoriaClient.buscarPorId(anyLong())).thenReturn(new CategoriaDTO());
        when(repository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoDTO resultado = productoService.actualizar(idExistente, request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Mouse Gamer RGB");
        verify(repository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("actualizar: lanza ProductoNotFoundException si el ID buscado no existe en el catálogo local")
    void debeLanzarExcepcionCuandoProductoAActualizarNoExiste() {
        Long idInexistente = 999L;
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.actualizar(idInexistente, request))
                .isInstanceOf(ProductoNotFoundException.class);

        verify(repository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("actualizar: lanza ProductoDuplicadoException si el nuevo nombre ya lo usa otro producto")
    void debeLanzarExcepcionCuandoNuevoNombreYaExisteAlActualizar() {
        Long idExistente = 1L;
        Producto productoOriginal = new Producto();
        productoOriginal.setId(idExistente);
        productoOriginal.setNombre("Mouse Antiguo");

        request.setNombre("Teclado Mecánico");

        when(repository.findById(idExistente)).thenReturn(Optional.of(productoOriginal));
        when(repository.existsByNombre(request.getNombre())).thenReturn(true);

        assertThatThrownBy(() -> productoService.actualizar(idExistente, request))
                .isInstanceOf(ProductoDuplicadoException.class);

        verify(repository, never()).save(any(Producto.class));
    }

    // =========================================================================
    // NUEVOS METODOS AGREGADOS PARA COBERTURA COMPLETA
    // =========================================================================

    @Test
    @DisplayName("listar: obtiene todos los productos registrados en la base de datos")
    void debeListarTodosLosProductos() {
        when(repository.findAll()).thenReturn(List.of(productoGuardado));

        List<ProductoDTO> resultado = productoService.listar();

        assertThat(resultado).isNotNull().hasSize(1);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("listarPaginado: retorna productos paginados correctamente")
    void debeListarProductosPaginados() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> page = new PageImpl<>(List.of(productoGuardado));
        when(repository.findAll((Pageable) any())).thenReturn(page);

        Page<ProductoDTO> resultado = productoService.listarPaginado(pageable);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().getFirst().getNombre()).isEqualTo("Teclado Mecánico");
        verify(repository, times(1)).findAll((Pageable) any());
    }

    @Test
    @DisplayName("buscar: obtiene un producto por su ID de manera exitosa")
    void debeBuscarProductoPorIdExitosamente() {
        Long idBuscado = 10L;
        when(repository.findById(idBuscado)).thenReturn(Optional.of(productoGuardado));

        ProductoDTO resultado = productoService.buscar(idBuscado);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(idBuscado);
        assertThat(resultado.getNombre()).isEqualTo("Teclado Mecánico");
        verify(repository, times(1)).findById(idBuscado);
    }

    @Test
    @DisplayName("buscarPorNombre: encuentra una lista de productos que coincidan con el texto enviado")
    void debeBuscarProductosPorNombreContenido() {
        String textoBusqueda = "Teclado";
        when(repository.findByNombreContainingIgnoreCase(textoBusqueda)).thenReturn(List.of(productoGuardado));

        List<ProductoDTO> resultado = productoService.buscarPorNombre(textoBusqueda);

        assertThat(resultado).isNotEmpty();
        assertThat(resultado.getFirst().getNombre()).isEqualTo("Teclado Mecánico");
        verify(repository, times(1)).findByNombreContainingIgnoreCase(textoBusqueda);
    }

    @Test
    @DisplayName("listarConStock: obtiene los productos cuya cantidad en inventario es mayor a cero")
    void debeListarProductosConStockDisponible() {
        when(repository.findByCantidadGreaterThan(0)).thenReturn(List.of(productoGuardado));

        List<ProductoDTO> resultado = productoService.listarConStock();

        assertThat(resultado).isNotEmpty();
        verify(repository, times(1)).findByCantidadGreaterThan(0);
    }

    @Test
    @DisplayName("eliminar: remueve el producto del repositorio si el ID existe localmente")
    void debeEliminarProductoCorrectamente() {
        Long idEliminar = 10L;
        when(repository.existsById(idEliminar)).thenReturn(true);
        doNothing().when(repository).deleteById(idEliminar);

        assertThatNoException().isThrownBy(() -> productoService.eliminar(idEliminar));

        verify(repository, times(1)).deleteById(idEliminar);
    }

    @Test
    @DisplayName("eliminar: lanza ProductoNotFoundException si el ID a borrar no existe en la BD")
    void debeLanzarExcepcionAlEliminarProductoInexistente() {
        Long idInexistente = 999L;
        when(repository.existsById(idInexistente)).thenReturn(false);

        assertThatThrownBy(() -> productoService.eliminar(idInexistente))
                .isInstanceOf(ProductoNotFoundException.class);

        verify(repository, never()).deleteById(anyLong());
    }

    // =========================================================================
    // PRUEBAS DE INTEGRACIÓN DISTRIBUIDA
    // =========================================================================

    @Test
    @DisplayName("listarPorCategoriaRemota: retorna la lista si el microservicio de categorías valida el ID remoto")
    void debeListarPorCategoriaRemotaExitosamente() {
        Long categoriaId = 5L;
        CategoriaDTO categoriaMock = new CategoriaDTO();
        categoriaMock.setId(5L);
        categoriaMock.setNombre("Periféricos");

        when(categoriaClient.buscarPorId(categoriaId)).thenReturn(categoriaMock);
        when(repository.findByCategoriaId(categoriaId)).thenReturn(List.of(productoGuardado));

        List<ProductoDTO> resultado = productoService.listarPorCategoriaRemota(categoriaId);

        assertThat(resultado).isNotNull().hasSize(1);
        verify(categoriaClient, times(1)).buscarPorId(categoriaId);
        verify(repository, times(1)).findByCategoriaId(categoriaId);
    }

    @Test
    @DisplayName("listarPorCategoriaRemota: lanza ResponseStatusException (502 Bad Gateway) si el microservicio remoto se cae")
    void debeLanzarBadGatewayCuandoCategoriaServiceFalla() {
        Long categoriaId = 5L;
        when(categoriaClient.buscarPorId(categoriaId)).thenThrow(new RuntimeException("Error de red"));

        assertThatThrownBy(() -> productoService.listarPorCategoriaRemota(categoriaId))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_GATEWAY);
        verify(repository, never()).findAll();
    }
}