package pe.edu.upeu.library.crudlibrary.servicios;

import org.springframework.stereotype.Service;
import pe.edu.upeu.library.crudlibrary.modelo.Usuarios;
import pe.edu.upeu.library.crudlibrary.repositorio.UsuariosRepository;
import pe.edu.upeu.library.crudlibrary.servicios.UsuariosServicioI;

import java.util.List;

@Service
public class UsuariosServicioImp extends UsuariosRepository implements UsuariosServicioI {

    @Override
    public void save(Usuarios usuario) {
        usuarios.add(usuario);
    }

    @Override
    public Usuarios update(Usuarios usuario, int index) {
        usuarios.set(index, usuario);
        return usuario;
    }

    @Override
    public void delete(int index) {
        usuarios.remove(index);
    }

    @Override
    public Usuarios findById(int index) {
        return usuarios.get(index);
    }

    @Override
    public List<Usuarios> findAll() {
        if (usuarios.isEmpty()) {
            return super.findAll();
        }
        return usuarios;
    }
}
