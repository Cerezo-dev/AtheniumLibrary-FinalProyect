package pe.edu.upeu.syslibrary.service;

import pe.edu.upeu.syslibrary.model.Usuario;
import java.util.Optional;

public interface IUsuarioService extends ICrudGenericService<Usuario, Long> {

    // Buscar usuario por email (usado para validaciones)
    Usuario findByEmail(String email);

    // Lógica de login (verifica contraseña encriptada)
    Usuario loginUsuario(String email, String password);

    // Registro completo (Admin/Bibliotecario)
    Usuario registrarNuevoUsuario(String nombre, String apellidos, String email, String clave, String nombrePerfil);

    // Registro simplificado (Estudiantes)
    Usuario registrarEstudiante(String email, String codigo);

    // Métodos de búsqueda específicos para el módulo de Préstamos
    Optional<Usuario> buscarPorDni(String dni);
    Optional<Usuario> buscarPorCodigo(String codigo);
    Optional<Usuario> buscarPorDniOCodigo(String busqueda);
}