package pe.edu.upeu.syslibrary.repositorio;

import pe.edu.upeu.syslibrary.model.Perfil; // <--- OJO: Aquí gestionamos PERFIL
import org.springframework.stereotype.Repository;

@Repository
public interface PerfilRepository extends ICrudGenericRepository<Perfil, Long> {

    // Método útil para buscar perfiles por nombre (ej: buscar si existe "ADMIN")
    Perfil findByNombre(String nombre);

}