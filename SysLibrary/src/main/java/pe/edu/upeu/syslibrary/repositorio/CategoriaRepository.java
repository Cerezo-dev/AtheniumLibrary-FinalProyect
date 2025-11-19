package pe.edu.upeu.syslibrary.repositorio;

import pe.edu.upeu.syslibrary.model.Categoria;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends ICrudGenericRepository<Categoria, Long> {
    Categoria findByNombre(String nombre);

    /// Método adicional que puede ser útil
    boolean existsByNombre(String nombre);

}

