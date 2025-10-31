package pe.edu.upeu.athenium.perfil.repository;


import pe.edu.upeu.athenium.perfil.entity.Perfil;
import pe.edu.upeu.athenium.common.repository.ICrudGenericoRepository;

public interface PerfilRepository extends ICrudGenericoRepository<Perfil,Long> {

    Perfil findByNombre(String nombre);

}
