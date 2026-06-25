package duoc.cl.proveedor.service;

import duoc.cl.proveedor.dto.ProveedorDTO;
import duoc.cl.proveedor.dto.ProveedorRequest;
import duoc.cl.proveedor.exception.ProveedorNotFoundException;
import duoc.cl.proveedor.model.Proveedor;
import duoc.cl.proveedor.repository.ProveedorRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProveedorService {

    private final ProveedorRepository repository;

    public ProveedorService(ProveedorRepository repository) {
        this.repository = repository;
    }

    public ProveedorDTO guardar(ProveedorRequest request){
        if(repository.existsByCorreo(request.getCorreo())){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El correo ya fue ingresado"
            );
        }

        if(repository.existsByRut(request.getRut())){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El rut ya existe"
            );
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(request.getNombre());
        proveedor.setRut(request.getRut());
        proveedor.setCorreo(request.getCorreo());
        proveedor.setDireccion(request.getDireccion());
        proveedor.setTelefono(request.getTelefono());

        return mapToDTO(repository.save(proveedor));
    }

    public List<ProveedorDTO> listar(){
        return repository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ProveedorDTO buscar(Long id){
        Proveedor proveedor = repository.findById(id)
                .orElseThrow(() -> new ProveedorNotFoundException(id));
        return mapToDTO(proveedor);
    }

    public ProveedorDTO actualizar(Long id, ProveedorRequest request) {
        Proveedor proveedor = repository.findById(id)
                .orElseThrow(() -> new ProveedorNotFoundException(id));

        if (!proveedor.getRut().equals(request.getRut()) && repository.existsByRut(request.getRut())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El RUT ingresado ya está registrado por otro proveedor");
        }
        if (!proveedor.getCorreo().equals(request.getCorreo()) && repository.existsByCorreo(request.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo ingresado ya está registrado por otro proveedor");
        }

        proveedor.setNombre(request.getNombre());
        proveedor.setRut(request.getRut());
        proveedor.setCorreo(request.getCorreo());
        proveedor.setDireccion(request.getDireccion());
        proveedor.setTelefono(request.getTelefono());

        return mapToDTO(repository.save(proveedor));
    }

    public void eliminar(Long id) {
        Proveedor proveedor = repository.findById(id)
                .orElseThrow(() -> new ProveedorNotFoundException(id));
        repository.delete(proveedor);
    }

    // Mapeo
    private ProveedorDTO mapToDTO(Proveedor proveedor){
        ProveedorDTO dto = new ProveedorDTO();
        dto.setId(proveedor.getId());
        dto.setNombre(proveedor.getNombre());
        dto.setRut(proveedor.getRut());
        dto.setCorreo(proveedor.getCorreo());
        dto.setDireccion(proveedor.getDireccion());
        dto.setTelefono(proveedor.getTelefono());
        return dto;
    }
}