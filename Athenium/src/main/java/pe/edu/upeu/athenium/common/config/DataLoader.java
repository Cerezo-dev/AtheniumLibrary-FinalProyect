package pe.edu.upeu.athenium.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pe.edu.upeu.athenium.genero.entity.Genero;
import pe.edu.upeu.athenium.genero.repository.GeneroRepository;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final GeneroRepository generoRepository;

    @Override
    public void run(String... args) throws Exception {
        cargarGeneros();
    }

    private void cargarGeneros() {
        try {
            // Verificar si ya existen géneros
            long count = generoRepository.count();
            System.out.println("=== DATA LOADER ===");
            System.out.println("Géneros encontrados en BD: " + count);

            if (count == 0) {
                List<Genero> generos = Arrays.asList(
                        new Genero(null, "Ficción"),
                        new Genero(null, "Ciencia"),
                        new Genero(null, "Tecnología"),
                        new Genero(null, "Ingeniería de Software"),
                        new Genero(null, "Matemáticas"),
                        new Genero(null, "Física"),
                        new Genero(null, "Química"),
                        new Genero(null, "Biología"),
                        new Genero(null, "Historia"),
                        new Genero(null, "Filosofía"),
                        new Genero(null, "Literatura"),
                        new Genero(null, "Arte"),
                        new Genero(null, "Música"),
                        new Genero(null, "Deportes"),
                        new Genero(null, "Salud"),
                        new Genero(null, "Economía"),
                        new Genero(null, "Negocios"),
                        new Genero(null, "Psicología"),
                        new Genero(null, "Educación")
                );

                generoRepository.saveAll(generos);
                System.out.println("✅ Géneros cargados exitosamente: " + generos.size());

                // Verificar que se guardaron
                long nuevosCount = generoRepository.count();
                System.out.println("✅ Total de géneros después de carga: " + nuevosCount);
            } else {
                System.out.println("✅ Ya existen géneros en la base de datos");

                // Opcional: listar los géneros existentes
                List<Genero> generosExistentes = generoRepository.findAll();
                System.out.println("Géneros disponibles:");
                generosExistentes.forEach(g -> System.out.println("  - " + g.getNombre()));
            }
            System.out.println("=== FIN DATA LOADER ===");

        } catch (Exception e) {
            System.err.println("❌ Error al cargar géneros: " + e.getMessage());
            e.printStackTrace();
        }
    }
}