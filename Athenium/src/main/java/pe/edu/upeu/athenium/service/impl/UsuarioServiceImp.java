package pe.edu.upeu.athenium.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.model.Usuario;
import pe.edu.upeu.athenium.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.repository.UsuarioRepository;
import pe.edu.upeu.athenium.service.IUsuarioService;
@RequiredArgsConstructor
@Service
public class UsuarioServiceImp extends CrudGenericoServiceImp<Usuario, Long> implements IUsuarioService {

    //Inyeccion de dependencias de @RequiredArgsConstructor
    private final UsuarioRepository usuarioRepository; //Para accedere a los datos en DB
    private final PasswordEncoder passwordEncoder; //Para encriptar/ verificar la clave
    @Override
    protected ICrudGenericoRepository<Usuario, Long> getRepo() {
        return usuarioRepository; //este metodo retorna el repositorio generico para Crud
    }

    public Usuario save(Usuario usuario){
        //Si el idUsuario es nulo (nuevo usuario) o la clave no estar vacia (actualizacion de clave)
        if (usuario.getId() == null || (usuario.getPassword() != null && !usuario.getPassword().isEmpty())) {
            //Encriptar la clave antes de guardarla
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
        }

    @Override
    public Usuario loginUsuario(String user, String clave) {
        Usuario u = usuarioRepository.buscarUsuario(user);  //Busca el usuario por su nombre de usuario (email)
        if (u == null) {
            return null;
        }
        boolean matches = passwordEncoder.matches(clave, u.getPassword());//Verifica si la clave proporcionada coincide con la clave encriptada
        return matches ? u : null;//Si coincide, retorna el usuario, sino retorna null
    }
}
