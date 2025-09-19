package pe.edu.upeu.library.crudlibrary.servicios;

import pe.edu.upeu.library.crudlibrary.modelo.Usuarios;

import java.util.List;

public interface UsuariosServicioI {
    void save(Usuarios usuario);
    List<Usuarios> findAll();
    Usuarios update(Usuarios usuario, int index);
    void delete(int index);

    Usuarios findById(int id);
}