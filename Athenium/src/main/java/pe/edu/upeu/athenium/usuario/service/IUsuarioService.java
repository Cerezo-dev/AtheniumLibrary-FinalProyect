package pe.edu.upeu.athenium.usuario.service;

import pe.edu.upeu.athenium.common.service.ICrudGenericoService;
import pe.edu.upeu.athenium.usuario.entity.Usuario;

// Servicio específico para la entidad Usuario

public interface IUsuarioService extends ICrudGenericoService<Usuario,Long> {
    // Método para el login de usuario
    Usuario loginUsuario(String user, String clave);
    //Método para registrar un nuevo usuario
    Usuario registrarNuevoUsuario(String nombre, String apellido, String email, String clave);

}
