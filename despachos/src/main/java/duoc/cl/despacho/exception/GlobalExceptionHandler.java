package duoc.cl.despacho.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Manejar cuando un despacho no existe (404)
    @ExceptionHandler(DespachoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(DespachoNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        //response.put("timestamp", LocalDateTime.now());
        //response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", ex.getMessage());
        log.error("Error: {}", response);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Manejar errores de validación de los @NotBlank (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.put(error.getField(), error.getDefaultMessage());
        });

        log.error("Error: {}", errores);

        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }
}
