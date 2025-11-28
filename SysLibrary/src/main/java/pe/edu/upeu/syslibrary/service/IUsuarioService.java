package pe.edu.upeu.syslibrary.service;

import pe.edu.upeu.syslibrary.model.Usuario;
import java.util.Optional;

public interface IUsuarioService extends ICrudGenericService<Usuario, Long> {

    Usuario findByEmail(String email);

    Usuario loginUsuario(String email, String password);

    // Registro para Administrativos y Bibliotecarios (Formulario completo)
    Usuario registrarNuevoUsuario(String nombre, String apellidos, String email, String clave, String nombrePerfil);

    // --- NUEVO: Registro Simplificado para Estudiantes ---
    // (La contraseña será el mismo código automáticamente)
    Usuario registrarEstudiante(String email, String codigo);

    Optional<Usuario> buscarPorDni(String dni);
    Optional<Usuario> buscarPorCodigo(String codigo);
    Optional<Usuario> buscarPorDniOCodigo(String busqueda);
}