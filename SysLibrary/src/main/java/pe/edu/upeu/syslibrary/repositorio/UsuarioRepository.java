package pe.edu.upeu.syslibrary.repositorio;

import pe.edu.upeu.syslibrary.model.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends ICrudGenericRepository<Usuario, Long> {

    // Buscar por email (existente)
    Usuario findByEmail(String email);

    // Login (existente)
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.password = :password")
    Usuario loginUsuario(@Param("email") String email, @Param("password") String password);

    // --- NUEVO: Necesario para el Préstamo ---
    // Busca por DNI
    Optional<Usuario> findByDni(String dni);

    // Busca por Código de Estudiante (por si prefieres usar el carné universitario)
    Optional<Usuario> findByCodigoEstudiante(String codigoEstudiante);
    // Busca por el DNI o el Codigo
    Optional<Usuario> findByDniOrCodigoEstudiante(String dni, String codigoEstudiante);


}