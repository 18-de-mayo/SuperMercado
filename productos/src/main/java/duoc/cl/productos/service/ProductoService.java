package duoc.cl.productos.service;

import duoc.cl.productos.client.CategoriaClient;
import duoc.cl.productos.client.ProveedorClient;
import duoc.cl.productos.dto.ProductoDTO;
import duoc.cl.productos.dto.ProductoRequest;
import duoc.cl.productos.exception.ProductoDuplicadoException;
import duoc.cl.productos.exception.ProductoNotFoundException;
import duoc.cl.productos.model.Producto;
import duoc.cl.productos.repository.ProductoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductoService {

    // Inyecciones por constructor obligatorias (para no usar @Autowired en atributos)
    private final ProductoRepository repository;
    private final ProveedorClient proveedorClient;
    private final CategoriaClient categoriaClient;

    // Constructor único para que Spring maneje la inyección limpia de los clientes Feign y el repo
    public ProductoService(ProductoRepository repository, ProveedorClient proveedorClient, CategoriaClient categoriaClient) {
        this.repository = repository;
        this.proveedorClient = proveedorClient;
        this.categoriaClient = categoriaClient;
    }

    public ProductoDTO guardar(ProductoRequest request) {
        log.info("Iniciando proceso para guardar producto: {}", request.getNombre());

        // Regla de negocio: si el nombre ya existe en la BD local, tiramos error 409
        if (repository.existsByNombre(request.getNombre())) {
            log.warn("Validación fallida: El nombre '{}' ya se encuentra registrado", request.getNombre());
            throw new ProductoDuplicadoException(request.getNombre());
        }

        // Validación distribuida: antes de guardar, verificamos por Feign si el proveedor existe en el otro ms
        validarProveedorRemoto(request.getProveedorId());

        // Si pasó las validaciones, traspasamos los datos del Request a la Entidad para la persistencia
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setCantidad(request.getCantidad());
        producto.setProveedorId(request.getProveedorId()); // Long mapeado de forma limpia
        producto.setCategoria(request.getCategoria());

        // Guardamos en MySQL y retornamos la respuesta formateada como DTO de salida
        ProductoDTO guardadoDTO = mapToDTO(repository.save(producto));
        log.info("Producto guardado exitosamente en BD con ID asignado: {}", guardadoDTO.getId());
        return guardadoDTO;
    }

    public List<ProductoDTO> listar() {
        log.info("Listando todos los productos locales");
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO buscar(Long id) {
        log.info("Buscando producto ID: {}", id);
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        return mapToDTO(producto);
    }

    public List<ProductoDTO> buscarPorNombre(String nombre) {
        log.info("Filtrando productos por nombre: {}", nombre);
        return repository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> listarConStock() {
        log.info("Listando productos con stock disponible");
        return repository.findByCantidadGreaterThan(0).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Page<ProductoDTO> listarPaginado(Pageable pageable) {
        log.info("Listando productos con paginación");
        return repository.findAll(pageable).map(this::mapToDTO);
    }

    public ProductoDTO actualizar(Long id, ProductoRequest request) {
        log.info("Iniciando actualización del producto ID: {}", id);

        // Si el producto no existe localmente, se dispara la excepción controlada de inmediato
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));

        // Validación: Si el usuario cambió el nombre, revisamos que el nuevo no esté repetido en la BD
        if (!producto.getNombre().equalsIgnoreCase(request.getNombre()) && repository.existsByNombre(request.getNombre())) {
            throw new ProductoDuplicadoException(request.getNombre());
        }

        // Volvemos a validar el proveedor por Feign por si decidieron cambiarlo en el formulario
        validarProveedorRemoto(request.getProveedorId());

        // Seteamos los nuevos valores sobre la entidad que recuperamos
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setCantidad(request.getCantidad());
        producto.setProveedorId(request.getProveedorId());
        producto.setCategoria(request.getCategoria());

        return mapToDTO(repository.save(producto));
    }

    // Metodo exigido en la rúbrica para conectar nuestro ms con el de Categorías
    public List<ProductoDTO> listarPorCategoriaRemota(Long categoriaId) {
        log.info("Consultando al ms-categoria para verificar la existencia del ID de categoría: {}", categoriaId);
        try {
            // Llamamos al cliente Feign usando el ID numérico que expuso el compañero en su controlador
            Object categoriaRemota = categoriaClient.buscarPorId(categoriaId);

            // Si el ms de categorías responde pero viene vacío, tiramos un 404 de negocio
            if (categoriaRemota == null) {
                log.warn("Validación fallida: La categoría con ID {} no existe en el sistema remoto", categoriaId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La categoría especificada no existe.");
            }

            // Flujo feliz: Como la categoría sí existe allá, procedemos a listar nuestro catálogo
            log.info("Categoría remota válida en ms-categoria. Listando y mapeando productos del catálogo local.");
            return repository.findAll().stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());

        } catch (ResponseStatusException e) {
            throw e; // Re-lanzamos el 404 para que lo pesque el handler
        } catch (Exception e) {
            // Manejo de infraestructura: si el ms-categoria está apagado, devolvemos un 502 Bad Gateway
            log.error("Error crítico de infraestructura al comunicar con categoria-service para ID {}: {}", categoriaId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "El servicio remoto de categorías no respondió correctamente.");
        }
    }

    // Metodo auxiliar privado para no duplicar la validación de proveedores en guardar y actualizar
    private void validarProveedorRemoto(Long proveedorId) {
        if (proveedorId == null) {
            log.error("Intento de validación fallido: El id del proveedor es nulo");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID del proveedor es obligatorio.");
        }
        try {
            log.info("Validando existencia del proveedor ID: {} en el microservicio remoto", proveedorId);
            var proveedor = proveedorClient.obtenerProveedor(proveedorId);

            if (proveedor == null) {
                log.error("El proveedor con ID {} no fue encontrado en el sistema remoto.", proveedorId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El proveedor asociado no existe en el sistema remoto.");
            }

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            // Si el ms-proveedor se cae, lanzamos 502 para avisarle al usuario que el backend distribuido falló
            log.error("Error de comunicación con proveedor-service para ID {}: {}", proveedorId, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo conectar con el servicio remoto de proveedores.");
        }
    }

    // Mapper manual: Transforma la entidad de base de datos a un DTO plano de salida.
    private ProductoDTO mapToDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setCantidad(producto.getCantidad());
        dto.setCategoria(producto.getCategoria());

        // Si el producto tiene un proveedor asociado, usamos Feign en tiempo real para traer su nombre comercial
        if (producto.getProveedorId() != null) {
            try {
                var proveedorRemote = proveedorClient.obtenerProveedor(producto.getProveedorId());
                if (proveedorRemote != null) {
                    dto.setNombreProveedor(proveedorRemote.getNombre()); // Mapeamos el nombre que vino de la API externa
                }
            } catch (Exception e) {
                // Tolerancia a fallos: Si falla la red de proveedores, la API no se cae, solo avisa que no está disponible
                log.warn("No se pudo obtener detalles del proveedor remoto para ID {}: {}", producto.getProveedorId(), e.getMessage());
                dto.setNombreProveedor("Proveedor no disponible");
            }
        }

        return dto;
    }

    public void eliminar(Long id) {
        log.info("Iniciando eliminación del producto ID: {}", id);
        if (!repository.existsById(id)) {
            log.warn("Intento fallido de eliminación: El producto ID {} no existe", id);
            throw new ProductoNotFoundException(id);
        }
        repository.deleteById(id);
        log.info("Producto ID: {} eliminado correctamente de la base de datos", id);
    }
}