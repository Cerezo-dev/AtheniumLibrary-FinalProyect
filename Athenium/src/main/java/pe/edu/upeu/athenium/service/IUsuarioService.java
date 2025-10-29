package pe.edu.upeu.athenium.service;

import pe.edu.upeu.athenium.model.Usuario;

public interface IUsuarioService extends ICrudGenericoService<Usuario,Long>{
    Usuario loginUsuario(String user, String clave);



}
