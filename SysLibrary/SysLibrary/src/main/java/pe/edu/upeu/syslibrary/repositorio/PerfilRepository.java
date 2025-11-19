package pe.edu.upeu.syslibrary.repositorio;


import org.springframework.stereotype.Repository;
import pe.edu.upeu.syslibrary.model.Perfil;
@Repository
public interface PerfilRepository extends  ICrudGenericRepository<Perfil,Long>{

    Perfil findByNombre(String nombre);

}
