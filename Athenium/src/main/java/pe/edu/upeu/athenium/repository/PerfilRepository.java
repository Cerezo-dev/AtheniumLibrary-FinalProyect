package pe.edu.upeu.athenium.repository;


import pe.edu.upeu.athenium.model.Perfil;

public interface PerfilRepository extends  ICrudGenericoRepository<Perfil,Long>{

    Perfil findByNombre(String nombre);

}
