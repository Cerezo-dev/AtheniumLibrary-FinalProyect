package pe.edu.upeu.syslibrary.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upeu.syslibrary.model.Usuario;
import pe.edu.upeu.syslibrary.repositorio.ICrudGenericRepository;
import pe.edu.upeu.syslibrary.repositorio.UsuarioRepository;
import pe.edu.upeu.syslibrary.service.IUsuarioService;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl extends CrudGenericServiceImp<Usuario, Long> implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    protected ICrudGenericRepository<Usuario, Long> getRepo() {
        return usuarioRepository;
    }

    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Usuario loginUsuario(String email, String password) {
        Usuario user = usuarioRepository.findByEmail(email);
        if (user == null) return null;

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public Usuario saveUsuarioConPassword(Usuario usuario) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        usuario.setPassword(encoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }
}
