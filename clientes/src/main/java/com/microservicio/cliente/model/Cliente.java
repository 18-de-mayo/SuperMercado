package com.microservicio.cliente.model;// y aquí estamos modelando cliente

import jakarta.persistence.*;// dependencia jpa para mapear la clase a una tabla en la base de datos
import jakarta.validation.constraints.*;// dependencia llamada bean validation proveniente de jakarta que a su vez viene de hibernate, por ende este import viene de la dependencia hibernate
import lombok.*;// lombok simplemente

import java.time.LocalDate;// libreria interna de java para manejar fechas, es distinta a LocalDateTime porque una maneja solo fechas y la otra maneja fechas y horas. usamos dos dependencias porque LocalDateTime no puede no mostar la hora 
import java.time.LocalDateTime;//¿LocalDateTime siempre muestra hora, por eso se usan los dos imports?: la respuesta es sí, LocalDateTime siempre muestra la hora, por eso se usan los dos imports, porque LocalDate solo muestra la fecha y LocalDateTime muestra la fecha y la hora.

/**
 * Entidad JPA que representa a un cliente del sistema.
 * Cada cliente tiene una cuenta con datos personales, contacto y estado.
 */
@Entity// proviene de la libreria jpa, mapea la clase como una entidad en la base de datos
@Table(name = "clientes", uniqueConstraints = {//@Table especifica detalles para la tabla de bases de datos. ¿uniqueConstraints es un parametro?: sí. ¿cuantos parametros tiene @Table?: tiene la cantidad de parametros que se necesiten, por ejemplo, el numero que voy a decir ahora es 
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "rut")
})//reglas
@Data//de lombok
@NoArgsConstructor//de lombok
@AllArgsConstructor// de lombok
@Builder// de lombok
public class Cliente {

    @Id// de la libreria jpa. clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY)//jpa. ¿por qué aumenta de manera gradual?:
    private Long id;//0

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;//1

    @NotBlank(message = "El apellido no puede estar vacío")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellido;//2

    @NotBlank(message = "El RUT no puede estar vacío")
    @Pattern(regexp = "\\d{7,8}-[\\dkK]", message = "El RUT debe tener el formato 12345678-9")
    @Column(nullable = false, unique = true, length = 12)
    private String rut;//3

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe tener un formato válido")
    @Column(nullable = false, unique = true, length = 150)
    private String email;//4

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Pattern(regexp = "\\+?[0-9]{9,15}", message = "El teléfono debe contener entre 9 y 15 dígitos")//patern 
    @Column(nullable = false, length = 20)
    private String telefono;//5

    @NotBlank(message = "La dirección no puede estar vacía")
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    @Column(nullable = false)
    private String direccion;//6

    @NotBlank(message = "La ciudad no puede estar vacía")
    @Column(nullable = false, length = 100)
    private String ciudad;//7

    @NotBlank(message = "La región no puede estar vacía")
    @Column(nullable = false, length = 100)
    private String region;//8

    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;//9

    /**
     * Estado del cliente: ACTIVO, INACTIVO, SUSPENDIDO.
     * Un cliente INACTIVO no puede realizar pedidos.
     */
    @Enumerated(EnumType.STRING)//@Enumerated proviene de la libreria jpa
    @Column(nullable = false, length = 20)
    @Builder.Default//@Builder es notation de lombok, dice que el atributo... ¿será construido, por eso se llama builder en ingles?: sí
    private EstadoCliente estado = EstadoCliente.ACTIVO;//10

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;//11

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;//12

    /** Establece la fecha de registro antes de persistir por primera vez */
    @PrePersist
    protected void onCreate() {//¿esta funcion se ejecuta explicitamente?: no, gracias al @PrePersist que viene de jpa.
        this.fechaRegistro = LocalDateTime.now();//esto de programa porque así en el RespondeDTO no tengo que preocuparme de poner la fecha de registro, se pone automáticamente al crear el cliente.
        this.fechaActualizacion = LocalDateTime.now();//lo mismo, sigue al anterior, se pone automáticamente al crear el cliente.
        if (this.estado == null) {//y una decision, si el estado es nulo, se programa a activo. ¿esto contradice al @Builder.Default?: no. ¿si atravez de un endpoint creao un cliente, se crea con @Builder o con el onCrete?: ¿@Builder se una en inyeccion de dependencias?:  
            this.estado = EstadoCliente.ACTIVO;
        }
    }

    /** Actualiza la fecha de modificación antes de cada update */
    @PreUpdate//@PreUpdate es lo mismo que el PrePersist pero con las actualizaciones
    protected void onUpdate() {//protected significa hermetismo para esta funcion, no accesible desde fuera de la clase
        this.fechaActualizacion = LocalDateTime.now();
    }

    public enum EstadoCliente {//y las enumeraciones para el estado del cliente
        ACTIVO, INACTIVO, SUSPENDIDO
    }
}
