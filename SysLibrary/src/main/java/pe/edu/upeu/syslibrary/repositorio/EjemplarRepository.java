package pe.edu.upeu.syslibrary.repositorio;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.syslibrary.enums.EstadoEjemplar;
import pe.edu.upeu.syslibrary.model.Ejemplar;
import pe.edu.upeu.syslibrary.model.Libro;

import java.util.List;
import java.util.Optional;

@Repository
public interface EjemplarRepository extends ICrudGenericRepository<Ejemplar, Long> {

    // Método vital para el Dashboard de Estudiante: Busca copias físicas según el estado (DISPONIBLE)
    List<Ejemplar> findByLibroAndEstado(Libro libro, EstadoEjemplar estado);

    // Búsqueda por código de barras (usado en préstamos administrativos)
    Optional<Ejemplar> findByCodigo(String codigo);

    // Tu consulta personalizada para filtros (se mantiene igual)
    @Query("SELECT e FROM Ejemplar e " +
            "WHERE e.estado = 'DISPONIBLE' " +
            "AND (LOWER(e.codigo) LIKE LOWER(:filtro) " +
            "OR LOWER(e.libro.titulo) LIKE LOWER(:filtro))")
    List<Ejemplar> findDisponibles(@Param("filtro") String filtro);
}