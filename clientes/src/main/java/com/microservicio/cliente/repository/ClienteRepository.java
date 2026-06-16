package com.microservicio.cliente.repository;

import com.microservicio.cliente.model.Cliente;
import com.microservicio.cliente.model.Cliente.EstadoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para operaciones CRUD sobre la entidad Cliente.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
/*
Palabras del sujeto (antes de By):

find…By, read…By, get…By, query…By, search…By, stream…By

exists…By

count…By

delete…By, remove…By

…First<number>…, …Top<number>…

…Distinct…

Palabras del predicado (después de By):

Lógicas: And, Or

Comparación / rango: Is, Equals (o sin keyword), Between, LessThan, LessThanEqual, GreaterThan, GreaterThanEqual

Fechas: Before, IsBefore, After, IsAfter

Nulos / vacío: IsNull, Null, IsNotNull, NotNull, IsEmpty, Empty, IsNotEmpty, NotEmpty

Colecciones: In, IsIn, NotIn, IsNotIn

Texto: Like, IsLike, NotLike, IsNotLike, Containing, IsContaining, Contains, StartingWith, IsStartingWith, StartsWith, EndingWith, IsEndingWith, EndsWith

Booleanos: True, IsTrue, False, IsFalse

Otros: Exists, Near, IsNear, Regex, MatchesRegex, Matches, Within, IsWithin

Modificadores adicionales:

IgnoreCase, IgnoringCase

AllIgnoreCase, AllIgnoringCase

OrderBy…Asc/Desc (por ejemplo OrderByFirstnameAscLastnameDesc)
    */

    /** Busca un cliente por su email */
    Cliente findByEmail(String email);//¿"Email" en el nombre de la funcion es una palabra sin efecto en la deribacion de la consulta?, ¿Por qué se pone "Email" con mayuscula en el nombre de la funcion si el atributo no se escribe con mayuscula, afecta esto a la derivacion de la consulta?: 

    /** Busca un cliente por su RUT */
    Cliente findByRut(String rut);

    /** Verifica si existe un cliente con el email dado */
    boolean existsByEmail(String email);//palabras clave de JpaRepository:

    /** Verifica si existe un cliente con el RUT dado */
    boolean existsByRut(String rut);

    /** Retorna todos los clientes con un estado específico */
    List<Cliente> findByEstado(EstadoCliente estado);

    /** Busca clientes por ciudad */
    List<Cliente> findByCiudadIgnoreCase(String ciudad);

    /**
     * Busca clientes cuyo nombre o apellido contenga el texto dado.
     * Útil para búsquedas parciales.
     */
    @Query("SELECT c FROM Cliente c WHERE " +//c es un alias para cliente. el segundo c, el where se usa para decir
           "LOWER(c.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +//el parametro dentro de lower es lenguaje sql?, ¿qué significa '%'?: 
           "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))")//¿el "LIKE" significa que puede esatr tanto en la izquierda como en la derecha de la query?: 
    List<Cliente> buscarPorNombreOApellido(String texto);
}
