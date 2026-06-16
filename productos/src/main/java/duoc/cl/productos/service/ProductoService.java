package duoc.cl.productos.service;

import duoc.cl.productos.client.CategoriaClient;
import duoc.cl.productos.client.ProveedorClient;
import duoc.cl.productos.dto.ProductoDTO;
import duoc.cl.productos.dto.ProductoRequest;
import duoc.cl.productos.exception.ProductoNotFoundException;
import duoc.cl.productos.model.Producto;
import duoc.cl.productos.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final ProveedorClient proveedorClient;

    @Autowired
    private CategoriaClient categoriaClient;


    public ProductoService(ProductoRepository repository,
                           ProveedorClient proveedorClient) {
        this.repository = repository;
        this.proveedorClient = proveedorClient;
    }

    public ProductoDTO guardar(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setCantidad(request.getCantidad());
        producto.setProveedorId(request.getProveedorId());
        return mapToDTO(repository.save(producto));
    }

    public List<ProductoDTO> listar() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProductoDTO buscar(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        return mapToDTO(producto);
    }

    public List<ProductoDTO> buscarPorNombre(String nombre) {

        return repository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> listarConStock() {

        return repository.findByCantidadGreaterThan(0).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Page<ProductoDTO> listarPaginado(Pageable pageable) {
        return repository.findAll(pageable).map(this::mapToDTO);
    }

    public ProductoDTO actualizar(Long id, ProductoRequest request) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));

        try {
            proveedorClient.obtenerProveedor(request.getProveedorId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El proveedor asociado no existe en el sistema.");
        }

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setCantidad(request.getCantidad());
        producto.setProveedorId(request.getProveedorId());

        return mapToDTO(repository.save(producto));
    }


    private ProductoDTO mapToDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setCantidad(producto.getCantidad());

        if (producto.getProveedorId() != null) {
            try {
                var proveedorRemote = proveedorClient.obtenerProveedor(producto.getProveedorId());
                if (proveedorRemote != null) {
                    dto.setNombreProveedor(proveedorRemote.getNombre());
                }
            } catch (Exception e) {
                dto.setNombreProveedor("Proveedor no disponible");
            }
        }

        return dto;
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ProductoNotFoundException(id);
        }
        repository.deleteById(id);
    }


}
