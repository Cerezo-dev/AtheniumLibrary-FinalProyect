package pe.edu.upeu.athenium.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.model.Usuario;
import pe.edu.upeu.athenium.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.repository.UsuarioRepository;
import pe.edu.upeu.athenium.service.IUsuarioService;
@RequiredArgsConstructor
@Service
public class UsuarioServiceImp extends CrudGenericoServiceImp<Usuario, Long> implements IUsuarioService {
    private final UsuarioRepository usuarioRepository;
    @Override
    protected ICrudGenericoRepository<Usuario, Long> getRepo() {
        return usuarioRepository;
    }

    @Override
    public Usuario loginUsuario(String user, String clave) {
        return usuarioRepository.loginUsuario(user, clave);
    }
}
