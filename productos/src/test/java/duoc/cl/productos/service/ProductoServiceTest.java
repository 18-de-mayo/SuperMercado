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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        request.setPrecio(45000.0);
        request.setCantidad(20);
        request.setProveedorId(1L);
        request.setCategoria("Periféricos");

        productoGuardado = new Producto();
        productoGuardado.setId(10L);
        productoGuardado.setNombre("Teclado Mecánico");
        productoGuardado.setDescripcion("Teclado RGB Switch Blue");
        productoGuardado.setPrecio(45000.0);
        productoGuardado.setCantidad(20);
        productoGuardado.setProveedorId(1L);
        productoGuardado.setCategoria("Periféricos");

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

        ProductoDTO resultado = productoService.guardar(request);

        assertNotNull(resultado, "El DTO retornado no debería ser nulo");
        assertEquals(10L, resultado.getId(), "El ID debería coincidir con el asignado por la BD");
        assertEquals("Teclado Mecánico", resultado.getNombre());
        assertEquals("Distribuidora Tech Chile", resultado.getNombreProveedor(), "Debería mapear el nombre del proveedor remoto");

        verify(repository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("guardar: lanza ProductoDuplicadoException cuando el nombre ya está registrado en la BD local")
    void debeLanzarExcepcionCuandoProductoEstaDuplicado() {
        when(repository.existsByNombre(request.getNombre())).thenReturn(true);

        ProductoDuplicadoException excepcion = assertThrows(ProductoDuplicadoException.class,
                () -> productoService.guardar(request)
        );

        assertEquals("El producto 'Teclado Mecánico' ya existe en el sistema.", excepcion.getMessage());

        verify(repository, never()).save(any(Producto.class));
        verify(proveedorClient, never()).obtenerProveedor(anyLong());
    }

    @Test
    @DisplayName("guardar: lanza ResponseStatusException (404) cuando el proveedor remoto no existe")
    void debeLanzarExcepcionCuandoProveedorNoExiste() {
        when(repository.existsByNombre(request.getNombre())).thenReturn(false);
        when(proveedorClient.obtenerProveedor(request.getProveedorId())).thenReturn(null);

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class,
                () -> productoService.guardar(request)
        );

        assertEquals(HttpStatus.NOT_FOUND, excepcion.getStatusCode());
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
        when(repository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoDTO resultado = productoService.actualizar(idExistente, request);

        assertNotNull(resultado);
        assertEquals("Mouse Gamer RGB", resultado.getNombre());
        verify(repository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("actualizar: lanza ProductoNotFoundException si el ID buscado no existe en el catálogo local")
    void debeLanzarExcepcionCuandoProductoAActualizarNoExiste() {
        Long idInexistente = 999L;
        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class,
                () -> productoService.actualizar(idInexistente, request)
        );

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

        assertThrows(ProductoDuplicadoException.class,
                () -> productoService.actualizar(idExistente, request)
        );

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

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("buscar: obtiene un producto por su ID de manera exitosa")
    void debeBuscarProductoPorIdExitosamente() {
        Long idBuscado = 10L;
        when(repository.findById(idBuscado)).thenReturn(Optional.of(productoGuardado));

        ProductoDTO resultado = productoService.buscar(idBuscado);

        assertNotNull(resultado);
        assertEquals(idBuscado, resultado.getId());
        assertEquals("Teclado Mecánico", resultado.getNombre());
        verify(repository, times(1)).findById(idBuscado);
    }

    @Test
    @DisplayName("buscarPorNombre: encuentra una lista de productos que coincidan con el texto enviado")
    void debeBuscarProductosPorNombreContenido() {
        String textoBusqueda = "Teclado";
        when(repository.findByNombreContainingIgnoreCase(textoBusqueda)).thenReturn(List.of(productoGuardado));

        List<ProductoDTO> resultado = productoService.buscarPorNombre(textoBusqueda);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals("Teclado Mecánico", resultado.getFirst().getNombre());
        verify(repository, times(1)).findByNombreContainingIgnoreCase(textoBusqueda);
    }

    @Test
    @DisplayName("listarConStock: obtiene los productos cuya cantidad en inventario es mayor a cero")
    void debeListarProductosConStockDisponible() {
        when(repository.findByCantidadGreaterThan(0)).thenReturn(List.of(productoGuardado));

        List<ProductoDTO> resultado = productoService.listarConStock();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(repository, times(1)).findByCantidadGreaterThan(0);
    }

    @Test
    @DisplayName("eliminar: remueve el producto del repositorio si el ID existe localmente")
    void debeEliminarProductoCorrectamente() {
        Long idEliminar = 10L;
        when(repository.existsById(idEliminar)).thenReturn(true);
        doNothing().when(repository).deleteById(idEliminar);

        assertDoesNotThrow(() -> productoService.eliminar(idEliminar));

        verify(repository, times(1)).deleteById(idEliminar);
    }

    @Test
    @DisplayName("eliminar: lanza ProductoNotFoundException si el ID a borrar no existe en la BD")
    void debeLanzarExcepcionAlEliminarProductoInexistente() {
        Long idInexistente = 999L;
        when(repository.existsById(idInexistente)).thenReturn(false);

        assertThrows(ProductoNotFoundException.class,
                () -> productoService.eliminar(idInexistente)
        );

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
        when(repository.findAll()).thenReturn(List.of(productoGuardado));

        List<ProductoDTO> resultado = productoService.listarPorCategoriaRemota(categoriaId);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(categoriaClient, times(1)).buscarPorId(categoriaId);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("listarPorCategoriaRemota: lanza ResponseStatusException (502 Bad Gateway) si el microservicio remoto se cae")
    void debeLanzarBadGatewayCuandoCategoriaServiceFalla() {
        Long categoriaId = 5L;
        when(categoriaClient.buscarPorId(categoriaId)).thenThrow(new RuntimeException("Error de red"));

        ResponseStatusException excepcion = assertThrows(ResponseStatusException.class,
                () -> productoService.listarPorCategoriaRemota(categoriaId)
        );

        assertEquals(HttpStatus.BAD_GATEWAY, excepcion.getStatusCode());
        verify(repository, never()).findAll();
    }
}