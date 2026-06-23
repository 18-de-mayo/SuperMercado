package duoc.cl.proveedor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import duoc.cl.proveedor.dto.ProveedorDTO;
import duoc.cl.proveedor.dto.ProveedorRequest;
import duoc.cl.proveedor.exception.ProveedorNotFoundException;
import duoc.cl.proveedor.service.ProveedorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Controlador - ProveedorController")
class ProveedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProveedorService service;

    @Autowired
    private ObjectMapper objectMapper;

    private ProveedorRequest requestValido;
    private ProveedorDTO dtoRetornado;

    @BeforeEach
    void setUp() {
        requestValido = new ProveedorRequest();
        requestValido.setNombre("Distribuidora Tech");
        requestValido.setRut("76.123.456-7");
        requestValido.setCorreo("contacto@tech.cl");
        requestValido.setDireccion("Av Providencia 1234");
        requestValido.setTelefono("+56911112222");

        dtoRetornado = new ProveedorDTO();
        dtoRetornado.setId(1L);
        dtoRetornado.setNombre("Distribuidora Tech");
        dtoRetornado.setRut("76.123.456-7");
        dtoRetornado.setCorreo("contacto@tech.cl");
        dtoRetornado.setDireccion("Av Providencia 1234");
        dtoRetornado.setTelefono("+56911112222");
    }

    @Test
    @DisplayName("POST /api/v1/proveedor - Debe crear un proveedor y retornar 201 Created")
    void debeCrearProveedorYRetornar201() throws Exception {
        Mockito.when(service.guardar(Mockito.any(ProveedorRequest.class))).thenReturn(dtoRetornado);

        mockMvc.perform(post("/api/v1/proveedor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("Distribuidora Tech"));
    }

    @Test
    @DisplayName("GET /api/v1/proveedor - Debe retornar la lista de proveedor con 200 OK")
    void debeListarProveedorYRetornar200() throws Exception {
        Mockito.when(service.listar()).thenReturn(List.of(dtoRetornado));

        mockMvc.perform(get("/api/v1/proveedor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Distribuidora Tech"));
    }

    @Test
    @DisplayName("GET /api/v1/proveedor/{id} - Debe retornar 404 Not Found si el proveedor no existe")
    void debeRetornar404CuandoProveedorNoExiste() throws Exception {
        Mockito.when(service.buscar(99L)).thenThrow(new ProveedorNotFoundException(99L));

        mockMvc.perform(get("/api/v1/proveedor/99"))
                .andExpect(status().isNotFound());
    }
}