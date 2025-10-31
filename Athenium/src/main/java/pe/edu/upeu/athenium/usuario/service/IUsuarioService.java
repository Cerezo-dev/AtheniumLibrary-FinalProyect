package pe.edu.upeu.athenium.usuario.service;

import pe.edu.upeu.athenium.common.service.ICrudGenericoService;
import pe.edu.upeu.athenium.usuario.entity.Usuario;

public interface IUsuarioService extends ICrudGenericoService<Usuario,Long> {
    Usuario loginUsuario(String user, String clave);
    //Optional<Usuario> findByEmail(String email);
}
