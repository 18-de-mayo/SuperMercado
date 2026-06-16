package duoc.cl.productos.exception;

import duoc.cl.productos.exception.ProductoDuplicadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // manejar producto no encontrado
    @ExceptionHandler(duoc.cl.productos.exception.ProductoNotFoundException.class)
    public ResponseEntity<?> handleNotFound(duoc.cl.productos.exception.ProductoNotFoundException ex){

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", 404);
        error.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // manejar producto duplicado
    @ExceptionHandler(ProductoDuplicadoException.class)
    public ResponseEntity<?> handleDuplicado(ProductoDuplicadoException ex){

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", 400);
        error.put("error", ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }
}