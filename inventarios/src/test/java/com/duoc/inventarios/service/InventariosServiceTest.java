package com.duoc.inventarios.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.duoc.inventarios.client.ProductoClient;
import com.duoc.inventarios.dto.InventarioDTO;
import com.duoc.inventarios.dto.InventariosRequest;
import com.duoc.inventarios.dto.ProductoDTO;
import com.duoc.inventarios.exception.InventarioNotFoundException;
import com.duoc.inventarios.exception.ProductoNotFoundException;
import com.duoc.inventarios.model.Inventarios;
import com.duoc.inventarios.repository.InventariosRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventariosService Unit Tests")
class InventariosServiceTest {

    @Mock
    private InventariosRepository inventariosRepository;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private InventariosService inventariosService;

    private InventariosRequest request;
    private Inventarios entity;
    private Inventarios savedEntity;

    @BeforeEach
    void setUp() {
        request = new InventariosRequest();
        request.setProductoId(1L);
        request.setStockDisponible(50);
        request.setStockMinimo(10);

        entity = new Inventarios();
        entity.setProductoId(1L);
        entity.setStockDisponible(50);
        entity.setStockMinimo(10);

        savedEntity = new Inventarios();
        savedEntity.setId(1);
        savedEntity.setProductoId(1L);
        savedEntity.setStockDisponible(50);
        savedEntity.setStockMinimo(10);
        savedEntity.setFechaActualizacion(LocalDateTime.of(2025, 6, 21, 0, 0));
    }

    @Nested
    @DisplayName("Tests para crearInventario")
    class CrearInventarioTests {

        @Test
        @DisplayName("crearInventario exitoso — retorna InventarioDTO con datos correctos")
        void crearInventario_exitoso() {
            given(productoClient.obtenerProductoPorId(1L)).willReturn(new ProductoDTO());
            given(inventariosRepository.save(any(Inventarios.class))).willReturn(savedEntity);

            InventarioDTO result = inventariosService.crearInventario(request);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getProductoId()).isEqualTo(1L);
            assertThat(result.getStockDisponible()).isEqualTo(50);
            assertThat(result.getStockMinimo()).isEqualTo(10);
            assertThat(result.getFechaActualizacion()).isEqualTo(LocalDateTime.of(2025, 6, 21, 0, 0));
        }

        @Test
        @DisplayName("crearInventario lanza ProductoNotFoundException cuando el producto no existe")
        void crearInventario_productoNoEncontrado() {
            given(productoClient.obtenerProductoPorId(1L)).willThrow(new RuntimeException("Feign error"));

            assertThatThrownBy(() -> inventariosService.crearInventario(request))
                    .isInstanceOf(ProductoNotFoundException.class);

            then(inventariosRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("Tests para obtenerInventarios")
    class ObtenerInventariosTests {

        @Test
        @DisplayName("obtenerInventarios retorna lista con inventarios existentes")
        void obtenerInventarios_retornaLista() {
            given(inventariosRepository.findAll()).willReturn(List.of(savedEntity));

            List<InventarioDTO> resultados = inventariosService.obtenerInventarios();

            assertThat(resultados).hasSize(1);
            assertThat(resultados.get(0).getId()).isEqualTo(1);
            assertThat(resultados.get(0).getProductoId()).isEqualTo(1L);
            assertThat(resultados.get(0).getStockDisponible()).isEqualTo(50);
        }

        @Test
        @DisplayName("obtenerInventarios retorna lista vacia cuando no hay registros")
        void obtenerInventarios_listaVacia() {
            given(inventariosRepository.findAll()).willReturn(Collections.emptyList());

            List<InventarioDTO> resultados = inventariosService.obtenerInventarios();

            assertThat(resultados).isEmpty();
        }
    }

    @Nested
    @DisplayName("Tests para buscarInventarioPorId")
    class BuscarInventarioPorIdTests {

        @Test
        @DisplayName("buscarInventarioPorId retorna DTO cuando el inventario existe")
        void buscarInventarioPorId_encontrado() {
            given(inventariosRepository.findById(1)).willReturn(Optional.of(savedEntity));

            InventarioDTO result = inventariosService.buscarInventarioPorId(1);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getProductoId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("buscarInventarioPorId lanza InventarioNotFoundException cuando no existe")
        void buscarInventarioPorId_noEncontrado() {
            given(inventariosRepository.findById(99)).willReturn(Optional.empty());

            assertThatThrownBy(() -> inventariosService.buscarInventarioPorId(99))
                    .isInstanceOf(InventarioNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Tests para actualizarInventario")
    class ActualizarInventarioTests {

        @Test
        @DisplayName("actualizarInventario exitoso — actualiza y retorna DTO actualizado")
        void actualizarInventario_exitoso() {
            given(productoClient.obtenerProductoPorId(1L)).willReturn(new ProductoDTO());
            given(inventariosRepository.findById(1)).willReturn(Optional.of(entity));
            given(inventariosRepository.save(any(Inventarios.class))).willReturn(savedEntity);

            InventarioDTO result = inventariosService.actualizarInventario(1, request);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1);
            assertThat(result.getProductoId()).isEqualTo(1L);
            assertThat(result.getStockDisponible()).isEqualTo(50);
        }

        @Test
        @DisplayName("actualizarInventario lanza InventarioNotFoundException cuando el inventario no existe")
        void actualizarInventario_noEncontrado() {
            given(productoClient.obtenerProductoPorId(1L)).willReturn(new ProductoDTO());
            given(inventariosRepository.findById(1)).willReturn(Optional.empty());

            assertThatThrownBy(() -> inventariosService.actualizarInventario(1, request))
                    .isInstanceOf(InventarioNotFoundException.class);
        }

        @Test
        @DisplayName("actualizarInventario lanza ProductoNotFoundException cuando el producto no existe")
        void actualizarInventario_productoNoEncontrado() {
            given(productoClient.obtenerProductoPorId(1L)).willThrow(new RuntimeException("Feign error"));

            assertThatThrownBy(() -> inventariosService.actualizarInventario(1, request))
                    .isInstanceOf(ProductoNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Tests para eliminarInventario")
    class EliminarInventarioTests {

        @Test
        @DisplayName("eliminarInventario elimina cuando el inventario existe")
        void eliminarInventario_exitoso() {
            given(inventariosRepository.findById(1)).willReturn(Optional.of(savedEntity));

            inventariosService.eliminarInventario(1);

            then(inventariosRepository).should().deleteById(1);
        }

        @Test
        @DisplayName("eliminarInventario lanza InventarioNotFoundException cuando no existe")
        void eliminarInventario_noEncontrado() {
            given(inventariosRepository.findById(99)).willReturn(Optional.empty());

            assertThatThrownBy(() -> inventariosService.eliminarInventario(99))
                    .isInstanceOf(InventarioNotFoundException.class);

            then(inventariosRepository).should(never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Tests para validarProducto (via crearInventario)")
    class ValidarProductoTests {

        @Test
        @DisplayName("validarProducto no lanza excepcion cuando Feign responde exitosamente")
        void validarProducto_feignSuccess() {
            given(productoClient.obtenerProductoPorId(1L)).willReturn(new ProductoDTO());
            given(inventariosRepository.save(any(Inventarios.class))).willReturn(savedEntity);

            InventarioDTO result = inventariosService.crearInventario(request);

            assertThat(result).isNotNull();
            then(productoClient).should().obtenerProductoPorId(1L);
        }

        @Test
        @DisplayName("validarProducto lanza ProductoNotFoundException cuando Feign falla")
        void validarProducto_feignFailure() {
            given(productoClient.obtenerProductoPorId(1L)).willThrow(new RuntimeException("Feign error"));

            assertThatThrownBy(() -> inventariosService.crearInventario(request))
                    .isInstanceOf(ProductoNotFoundException.class);
        }
    }
}
