package pe.edu.upeu.syslibrary.repositorio;

import pe.edu.upeu.syslibrary.model.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends ICrudGenericRepository<Usuario, Long> {

    // 1. Validaciones de Login y Registro
    Usuario findByEmail(String email);

    // Login (Legacy - aunque en el Service estamos usando encoder, este método no estorba)
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.password = :password")
    Usuario loginUsuario(@Param("email") String email, @Param("password") String password);

    // 2. Búsquedas específicas (para validar duplicados en el registro)
    Optional<Usuario> findByDni(String dni);

    Optional<Usuario> findByCodigoEstudiante(String codigoEstudiante);

    // 3. Búsqueda Dual para el Préstamo (Clave para que funcione tu buscador)
    // Spring Data JPA generará automáticamente: WHERE u.dni = ?1 OR u.codigoEstudiante = ?2
    Optional<Usuario> findByDniOrCodigoEstudiante(String dni, String codigoEstudiante);

}