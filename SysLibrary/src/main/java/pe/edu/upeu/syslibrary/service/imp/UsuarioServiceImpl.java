package pe.edu.upeu.syslibrary.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.syslibrary.model.Perfil;
import pe.edu.upeu.syslibrary.model.Usuario;
import pe.edu.upeu.syslibrary.repositorio.ICrudGenericRepository;
import pe.edu.upeu.syslibrary.repositorio.PerfilRepository;
import pe.edu.upeu.syslibrary.repositorio.UsuarioRepository;
import pe.edu.upeu.syslibrary.service.IUsuarioService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl extends CrudGenericServiceImp<Usuario, Long> implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

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
        // 1. Buscamos por email usando el método del Repo
        Usuario user = usuarioRepository.findByEmail(email);

        if (user == null) return null;

        // 2. Validamos la contraseña encriptada (Mejor práctica que hacerlo en la Query SQL)
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    // --- REGISTRO COMPLETO (ADMIN / BIBLIOTECARIO) ---
    @Override
    public Usuario registrarNuevoUsuario(String nombre, String apellidos, String email, String clave, String nombrePerfil) {
        // Validamos que solo se puedan crear roles administrativos por aquí
        if (!nombrePerfil.equals("ADMINISTRADOR") && !nombrePerfil.equals("BIBLIOTECARIO")) {
            throw new IllegalArgumentException("Este método no permite registrar: " + nombrePerfil);
        }

        Perfil perfilSeleccionado = perfilRepository.findByNombre(nombrePerfil);
        if (perfilSeleccionado == null) {
            throw new IllegalStateException("El perfil '" + nombrePerfil + "' no existe en la BD.");
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellidos(apellidos);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setEstado("ACTIVO");
        nuevoUsuario.setPerfil(perfilSeleccionado);
        nuevoUsuario.setPassword(passwordEncoder.encode(clave));

        return usuarioRepository.save(nuevoUsuario);
    }

    // --- REGISTRO SIMPLIFICADO (ESTUDIANTES) ---
    @Override
    public Usuario registrarEstudiante(String email, String codigo) {
        // 1. Validar duplicidad de Email
        if (usuarioRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("El correo " + email + " ya está registrado.");
        }

        // 2. Validar duplicidad de Código usando tu método 'findByCodigoEstudiante'
        if (usuarioRepository.findByCodigoEstudiante(codigo).isPresent()) {
            throw new IllegalArgumentException("El código " + codigo + " ya está registrado.");
        }

        // 3. Buscar Perfil ESTUDIANTE
        Perfil perfilEstudiante = perfilRepository.findByNombre("ESTUDIANTE");
        if (perfilEstudiante == null) {
            throw new IllegalStateException("Error del sistema: El rol ESTUDIANTE no existe.");
        }

        // 4. Crear Usuario con datos por defecto
        Usuario u = new Usuario();
        u.setEmail(email);
        u.setCodigoEstudiante(codigo);
        u.setPerfil(perfilEstudiante);
        u.setEstado("ACTIVO");
        u.setNombre("Estudiante"); // Placeholder hasta que actualice perfil
        u.setApellidos("Nuevo");

        // 5. La contraseña es el código encriptado
        u.setPassword(passwordEncoder.encode(codigo));

        return usuarioRepository.save(u);
    }

    // --- MÉTODOS DE BÚSQUEDA (ADAPTADOS A TU REPO) ---

    @Override
    public Optional<Usuario> buscarPorDni(String dni) {
        return usuarioRepository.findByDni(dni);
    }

    @Override
    public Optional<Usuario> buscarPorCodigo(String codigo) {
        return usuarioRepository.findByCodigoEstudiante(codigo);
    }

    @Override
    public Optional<Usuario> buscarPorDniOCodigo(String busqueda) {
        // ADAPTACIÓN: Tu repo pide (String dni, String codigoEstudiante).
        // Pasamos 'busqueda' en ambos parámetros para que busque coincidencia en cualquiera de los dos.
        return usuarioRepository.findByDniOrCodigoEstudiante(busqueda, busqueda);
    }
}