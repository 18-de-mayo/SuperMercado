package duoc.cl.categoria.config;

import duoc.cl.categoria.model.CategoriaModel;
import duoc.cl.categoria.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
@Slf4j // Inyecta el Logger asíncrono formal de Slf4j para cumplir con Observabilidad
public class DataLoader implements CommandLineRunner {

    private final CategoriaRepository repository;

    @Override
    public void run(String... args) throws Exception {
        // REQUERIMIENTO IE 3.1.6: Poblar la base de datos con volumen de datos realista si está vacía
        if (repository.count() == 0) {
            log.info("Iniciando el poblamiento automático de categorías con DataFaker...");

            // Inicializar DataFaker con configuración regional en español
            Faker faker = new Faker(new Locale("es"));

            // Lista de categorías semánticamente correctas para el modelo de negocio
            String[] categoriasPredeterminadas = {
                    "Lácteos y Huevos", "Carnicería", "Frutas y Verduras",
                    "Panadería y Pastelería", "Despensa", "Bebidas y Licores",
                    "Limpieza", "Perfumería", "Congelados", "Mascotas"
            };

            for (String nombreCat : categoriasPredeterminadas) {
                // Implementación limpia del patrón de diseño Builder
                CategoriaModel categoria = CategoriaModel.builder()
                        .nombre(nombreCat)
                        .descripcion(faker.lorem().sentence(8))
                        .build();

                repository.save(categoria);
            }

            log.info(">>> DATAFAKER: Base de datos poblada exitosamente con {} categorías iniciales de supermercado.", repository.count());
        }
    }
}