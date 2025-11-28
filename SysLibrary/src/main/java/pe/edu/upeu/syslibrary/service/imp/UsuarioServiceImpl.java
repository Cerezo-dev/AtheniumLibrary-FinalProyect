package pe.edu.upeu.syslibrary.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para transacciones
import pe.edu.upeu.syslibrary.model.Perfil;
import pe.edu.upeu.syslibrary.model.Usuario;
import pe.edu.upeu.syslibrary.repositorio.ICrudGenericRepository;
import pe.edu.upeu.syslibrary.repositorio.PerfilRepository;
import pe.edu.upeu.syslibrary.repositorio.UsuarioRepository;
import pe.edu.upeu.syslibrary.service.IUsuarioService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional // Asegura que las operaciones de BD sean atómicas
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
        Usuario user = usuarioRepository.findByEmail(email);
        if (user == null) return null;

        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    // --- MÉTODO EXISTENTE (ADMIN/BIBLIOTECARIO) ---
    @Override
    public Usuario registrarNuevoUsuario(String nombre, String apellidos, String email, String clave, String nombrePerfil) {
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

    // --- NUEVO MÉTODO (MERGE): LOGICA ESTUDIANTE ---
    @Override
    public Usuario registrarEstudiante(String email, String codigo) {
        // 1. Validar duplicidad de Email
        if (usuarioRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("El correo " + email + " ya está registrado.");
        }

        // 2. Validar duplicidad de Código (si tienes el método en repo)
        if (usuarioRepository.findByCodigoEstudiante(codigo).isPresent()) {
            throw new IllegalArgumentException("El código " + codigo + " ya está registrado.");
        }

        // 3. Buscar Perfil ESTUDIANTE
        Perfil perfilEstudiante = perfilRepository.findByNombre("ESTUDIANTE");
        if (perfilEstudiante == null) {
            // Fallback por si la BD está vacía o se llama diferente
            throw new IllegalStateException("Error del sistema: El rol ESTUDIANTE no existe.");
        }

        // 4. Crear Usuario con datos por defecto
        Usuario u = new Usuario();
        u.setEmail(email);
        u.setCodigoEstudiante(codigo);
        u.setPerfil(perfilEstudiante);
        u.setEstado("ACTIVO");

        // Datos Placeholder (ya que el formulario simplificado no los pide)
        // El estudiante podrá actualizarlos luego en su perfil
        u.setNombre("Estudiante");
        u.setApellidos("Nuevo");

        // 5. REGLA DE NEGOCIO: La contraseña es el código encriptado
        u.setPassword(passwordEncoder.encode(codigo));

        return usuarioRepository.save(u);
    }

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
        return usuarioRepository.findByDniOrCodigoEstudiante(busqueda, busqueda);
    }
}