package pe.edu.upeu.syslibrary.repositorio;

import org.springframework.stereotype.Repository;
import pe.edu.upeu.syslibrary.model.SolicitudLibro;
import java.util.List;

@Repository
public interface SolicitudLibroRepository extends ICrudGenericRepository<SolicitudLibro, Long> {
    List<SolicitudLibro> findByEstado(String estado);
}