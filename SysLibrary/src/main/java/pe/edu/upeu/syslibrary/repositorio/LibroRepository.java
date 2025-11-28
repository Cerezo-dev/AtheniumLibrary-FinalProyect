package pe.edu.upeu.syslibrary.repositorio;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.syslibrary.model.Libro;

import java.util.List;

@Repository
public interface LibroRepository extends ICrudGenericRepository<Libro, Long> {

    // --- MÉTODOS DE BÚSQUEDA BÁSICOS ---

    // Este es el que usa el StudentDashboardController para buscar el libro de Google en tu BD local
    List<Libro> findByTituloContainingIgnoreCase(String titulo);

    List<Libro> findByAutorContainingIgnoreCase(String autor);

    // --- CONSULTAS AVANZADAS Y DASHBOARD ---

    @Query("SELECT l FROM Libro l WHERE l.disponibles > 0")
    List<Libro> findLibrosDisponibles();

    @Query("SELECT SUM(l.disponibles) FROM Libro l")
    Long sumAvailableCopies();

    boolean existsByCategoria_IdCategoria(Long idCategoria);

    // Búsqueda simple por texto y categoría
    @Query("SELECT l FROM Libro l WHERE " +
            "(:texto IS NULL OR :texto = '' OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :texto, '%')) OR LOWER(l.autor) LIKE LOWER(CONCAT('%', :texto, '%')) OR l.isbn LIKE CONCAT('%', :texto, '%')) " +
            "AND (:idCategoria IS NULL OR l.categoria.idCategoria = :idCategoria)")
    List<Libro> filtrarLibros(@Param("texto") String texto, @Param("idCategoria") Long idCategoria);

    // Búsqueda Maestra Avanzada (Se mantiene tu lógica compleja)
    @Query("SELECT l FROM Libro l WHERE " +
            "(:texto IS NULL OR :texto = '' OR LOWER(l.titulo) LIKE LOWER(CONCAT('%', :texto, '%')) OR LOWER(l.autor) LIKE LOWER(CONCAT('%', :texto, '%')) OR l.isbn LIKE CONCAT('%', :texto, '%')) " +
            "AND (:idCategoria IS NULL OR l.categoria.idCategoria = :idCategoria) " +
            "AND (:estado IS NULL OR l.estadoFisico = :estado) " +
            "AND (:anioDesde IS NULL OR l.anio >= :anioDesde) " +
            "AND (:anioHasta IS NULL OR l.anio <= :anioHasta) " +
            "AND (:ubicacion IS NULL OR :ubicacion = '' OR LOWER(l.ubicacion) LIKE LOWER(CONCAT('%', :ubicacion, '%'))) " +
            "AND (:soloDisponibles IS NULL OR (:soloDisponibles = TRUE AND l.disponibles > 0) OR (:soloDisponibles = FALSE AND l.disponibles = 0))")
    List<Libro> filtrarLibrosAvanzado(
            @Param("texto") String texto,
            @Param("idCategoria") Long idCategoria,
            @Param("estado") String estado,
            @Param("anioDesde") Integer anioDesde,
            @Param("anioHasta") Integer anioHasta,
            @Param("ubicacion") String ubicacion,
            @Param("soloDisponibles") Boolean soloDisponibles
    );
}