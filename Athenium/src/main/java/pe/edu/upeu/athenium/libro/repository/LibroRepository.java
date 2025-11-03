package pe.edu.upeu.athenium.libro.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.athenium.common.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.libro.entity.Libro;

import java.util.List;

public interface LibroRepository extends ICrudGenericoRepository<Libro, Long> {

    // Buscar libros por título (búsqueda parcial)
    List<Libro> findByTituloContainingIgnoreCase(String titulo);

    // Buscar libros por autor (búsqueda parcial)
    List<Libro> findByAutorContainingIgnoreCase(String autor);

    // Buscar libros por ISBN (búsqueda exacta)
    Libro findByIsbn(String isbn);

    // Buscar libros por género
    List<Libro> findByGeneroId(Long generoId);

    // Búsqueda avanzada: filtrar por título, autor o ISBN
    @Query("SELECT l FROM Libro l WHERE " +
            "LOWER(l.titulo) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(l.autor) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
            "LOWER(l.isbn) LIKE LOWER(CONCAT('%', :filtro, '%'))")
    List<Libro> filtrarLibros(@Param("filtro") String filtro);

    // Verificar si existe un libro con el mismo ISBN (excluyendo el actual para edición)
    @Query("SELECT COUNT(l) > 0 FROM Libro l WHERE l.isbn = :isbn AND l.id != :id")
    boolean existsByIsbnAndIdNot(@Param("isbn") String isbn, @Param("id") Long id);
}