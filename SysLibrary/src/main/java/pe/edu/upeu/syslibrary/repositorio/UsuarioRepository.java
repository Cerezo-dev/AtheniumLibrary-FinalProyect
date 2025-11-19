package pe.edu.upeu.syslibrary.repositorio;

import pe.edu.upeu.syslibrary.model.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends ICrudGenericRepository<Usuario, Long> {

    // Buscar por email
    Usuario findByEmail(String email);

    // Login usando email y password
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.password = :password")
    Usuario loginUsuario(@Param("email") String email, @Param("password") String password);
}
