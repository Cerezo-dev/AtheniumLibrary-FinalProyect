package pe.edu.upeu.athenium.usuario.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.common.service.impl.CrudGenericoServiceImp;
import pe.edu.upeu.athenium.common.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.perfil.entity.Perfil;
import pe.edu.upeu.athenium.perfil.repository.PerfilRepository;
import pe.edu.upeu.athenium.usuario.entity.Usuario;
import pe.edu.upeu.athenium.usuario.repository.UsuarioRepository;
import pe.edu.upeu.athenium.usuario.service.IUsuarioService;

@RequiredArgsConstructor
@Service
public class UsuarioServiceImp extends CrudGenericoServiceImp<Usuario, Long> implements IUsuarioService {

    //Inyeccion de dependencias de @RequiredArgsConstructor
    private final UsuarioRepository usuarioRepository; //Para acceder a los datos en DB
    private final PasswordEncoder passwordEncoder; //Para encriptar/ verificar la clave
    //
    private final PerfilRepository perfilRepository;

    /** aca esta
    * Logica para el login de usuario
     * Logica para guardar o actualizar un usuario
     *
     * */

    @Override
    protected ICrudGenericoRepository<Usuario, Long> getRepo() {
        return usuarioRepository; //este metodo retorna el repositorio generico para Crud
    }
    //Logica para guardar o actualizar un usuario
    @Override
    public Usuario save(Usuario usuario){
        //Si el idUsuario es nulo (nuevo usuario) o la clave no estar vacia (actualizacion de clave)
        if (usuario.getId() == null || (usuario.getPassword() != null && !usuario.getPassword().isEmpty())) {
            //Encriptar la clave antes de guardarla
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario registrarNuevoUsuario(String nombre, String apellido, String email, String clave) {
        // 1. Lógica de negocio: Obtener el perfil por defecto
        Perfil perfilDefecto = perfilRepository.findByNombre("ESTUDIANTE");
        if (perfilDefecto == null) {
            throw new IllegalStateException("Perfil 'ESTUDIANTE' no encontrado. Error de configuración.");
        }

        // 2. Creación de la Entidad y Asignación de valores de negocio
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setEmail(email);
        // La encriptación y guardado ya están en el save() general si lo llamas bien.
        nuevoUsuario.setPassword(clave); // La encriptación ocurre en el save general
        nuevoUsuario.setEstado("ACTIVO"); // Decisión de negocio
        nuevoUsuario.setPerfil(perfilDefecto);

        // 3. Persistencia
        return save(nuevoUsuario); // Llama al método save que maneja la encriptación
    }
// Asegúrate de inyectar PerfilRepository en este servicio si aún no lo está
// private final PerfilRepository perfilRepository;

    //Logica para el login de usuario
    @Override
    public Usuario loginUsuario(String user, String clave) {
        Usuario u = usuarioRepository.buscarUsuario(user);  //Busca el usuario por su nombre de usuario (email)
        if (u == null) {
            return null;
        }
        boolean matches = passwordEncoder.matches(clave, u.getPassword());//Verifica si la clave proporcionada coincide con la clave encriptada
        return matches ? u : null;//Si coincide, retorna el usuario, sino retorna null
    }

    @Override
    public Usuario buscarUsuario(String email) {
        // Simplemente delega la llamada al repositorio
        return usuarioRepository.buscarUsuario(email);
    }
}
