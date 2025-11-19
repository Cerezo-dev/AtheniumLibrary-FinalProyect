package pe.edu.upeu.syslibrary.repositorio;

import pe.edu.upeu.syslibrary.model.Libro;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends ICrudGenericRepository<Libro, Long> {

    // Buscar por título
    List<Libro> findByTituloContainingIgnoreCase(String titulo);

    // Buscar por categoría
    List<Libro> findByCategoriaIdCategoria(Long idCategoria);

    // Buscar por autor
    List<Libro> findByAutorContainingIgnoreCase(String autor);

    // Buscar por editorial
    List<Libro> findByEditorialContainingIgnoreCase(String editorial);

    // Buscar por idioma
    List<Libro> findByIdioma(String idioma);

    // Buscar libros disponibles (stock > 0)
    @Query("SELECT l FROM Libro l WHERE l.disponibles > 0")
    List<Libro> findLibrosDisponibles();
    @Query("SELECT SUM(l.disponibles) FROM Libro l")
    Long sumAvailableCopies();
}
