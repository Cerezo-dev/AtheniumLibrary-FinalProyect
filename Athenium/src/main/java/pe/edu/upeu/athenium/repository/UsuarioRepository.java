package pe.edu.upeu.athenium.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.athenium.model.Usuario;

public interface UsuarioRepository extends ICrudGenericoRepository<Usuario, Long> {

    @Query(value = "SELECT u.* FROM athenium_usuario u WHERE u.email = :email", nativeQuery = true)
    Usuario buscarUsuario(@Param("email") String email);

    @Query(value = "SELECT u.* FROM athenium_usuario u WHERE u.email = :email AND u.password = :password", nativeQuery = true)
    Usuario loginUsuario(@Param("email") String email, @Param("password") String password);

}


//*
// @Query(value = "SELECT u.* FROM athenium_usuario u WHERE u.user=:userx ",  nativeQuery = true)
//    Usuario buscarUsuario(@Param("userx") String userx);
//
//    @Query(value = "SELECT u.* FROM athenium_usuario u WHERE u.user=:user and u.clave=:clave", nativeQuery = true)
//    Usuario loginUsuario(@Param("user") String user, @Param("clave") String clave);*/
