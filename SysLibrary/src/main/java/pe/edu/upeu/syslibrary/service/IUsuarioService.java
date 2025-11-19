package pe.edu.upeu.syslibrary.service;

import pe.edu.upeu.syslibrary.model.Usuario;

public interface IUsuarioService extends ICrudGenericService<Usuario, Long> {

    Usuario findByEmail(String email);

    Usuario loginUsuario(String email, String password);

}
