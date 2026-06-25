package duoc.cl.productos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import duoc.cl.productos.dto.ProductoDTO;
import duoc.cl.productos.dto.ProductoRequest;
import duoc.cl.productos.exception.ProductoDuplicadoException;
import duoc.cl.productos.service.ProductoService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
@DisplayName("Pruebas Unitarias - ProductoController")
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductoRequest requestValido;
    private ProductoDTO dtoRetornado;

    @BeforeEach
    void setUp() {
        requestValido = new ProductoRequest();
        requestValido.setNombre("Teclado Mecánico");
        requestValido.setPrecio(BigDecimal.valueOf(45000));
        requestValido.setCantidad(20);
        requestValido.setProveedorId(1L);
        requestValido.setDescripcion("Teclado mecánico retroiluminado RGB switches rojos");
        requestValido.setCategoriaId(1L);

        dtoRetornado = new ProductoDTO();
        dtoRetornado.setId(10L);
        dtoRetornado.setNombre("Teclado Mecánico");
        dtoRetornado.setCantidad(20);
        dtoRetornado.setNombreProveedor("Distribuidora Tech Chile");
    }

    @Test
    @DisplayName("POST /api/v1/productos - Debe retornar 201 Created al guardar exitosamente")
    void debeRetornar201AlGuardarExitosamente() throws Exception {
        Mockito.when(productoService.guardar(Mockito.any(ProductoRequest.class))).thenReturn(dtoRetornado);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.nombre").value("Teclado Mecánico"));
    }

    @Test
    @DisplayName("POST /api/v1/productos - Debe retornar 409 Conflict cuando el producto ya existe")
    void debeRetornarErrorCuandoProductoEstaDuplicado() throws Exception {
        Mockito.when(productoService.guardar(Mockito.any(ProductoRequest.class)))
                .thenThrow(new ProductoDuplicadoException("Teclado Mecánico"));

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isConflict());
    }
}