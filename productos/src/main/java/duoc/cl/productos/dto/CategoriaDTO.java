package duoc.cl.productos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modelo de datos que representa una categoría de productos")
public class CategoriaDTO {

    @Schema(example = "1", description = "ID autoincremental de la categoría", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(example = "Bebidas", description = "Nombre de la categoría")
    private String nombre;

    @Schema(example = "Bebidas de fantasía, jugos y aguas minerales", description = "Descripción detallada de la categoría")
    private String descripcion;

    public CategoriaDTO() {}

    public CategoriaDTO(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}