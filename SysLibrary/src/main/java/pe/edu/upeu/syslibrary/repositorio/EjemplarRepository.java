package pe.edu.upeu.syslibrary.repositorio;

import pe.edu.upeu.syslibrary.model.Ejemplar;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EjemplarRepository extends ICrudGenericRepository<Ejemplar, Long> {

    @Query("SELECT e FROM Ejemplar e " +
            "WHERE e.estado = 'DISPONIBLE' " +
            "AND (LOWER(e.codigo) LIKE LOWER(:filtro) " +
            "OR LOWER(e.libro.titulo) LIKE LOWER(:filtro))")
    List<Ejemplar> findDisponibles(@Param("filtro") String filtro);

}
