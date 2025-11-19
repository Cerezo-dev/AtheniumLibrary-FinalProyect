package pe.edu.upeu.syslibrary.repositorio;

import pe.edu.upeu.syslibrary.model.Prestamo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrestamoRepository extends ICrudGenericRepository<Prestamo, Long> {
    // Listar préstamos activos de un usuario
    @Query("SELECT p FROM Prestamo p WHERE p.usuario.idUsuario = :idUsuario AND p.estado = :estado")
    List<Prestamo> findPrestamosActivosPorUsuario(@Param("idUsuario") Long idUsuario,
                                                  @Param("estado") String estado);

    // También puedes usar query derivada si prefieres:
    // List<Prestamo> findByUsuario_IdUsuarioAndEstado(Long idUsuario, String estado);
}
