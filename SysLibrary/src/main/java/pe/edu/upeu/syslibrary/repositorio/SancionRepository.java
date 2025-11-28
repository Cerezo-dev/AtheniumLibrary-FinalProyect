package pe.edu.upeu.syslibrary.repositorio;

import org.springframework.stereotype.Repository;
import pe.edu.upeu.syslibrary.model.Sancion;

@Repository
public interface SancionRepository extends ICrudGenericRepository<Sancion, Long> {
    // Aquí puedes agregar métodos como findByEstado("PENDIENTE") para reportes
}