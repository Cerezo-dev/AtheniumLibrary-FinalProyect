package pe.edu.upeu.syslibrary.repositorio;

import pe.edu.upeu.syslibrary.enums.EstadoPrestamo;
import pe.edu.upeu.syslibrary.model.Prestamo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrestamoRepository extends ICrudGenericRepository<Prestamo, Long> {

    // CORREGIDO: Cambiamos 'p.estudiante' por 'p.usuario' y el parámetro
    @Query("SELECT p FROM Prestamo p WHERE p.usuario.idUsuario = :idUsuario AND p.estado = :estado")
    List<Prestamo> findPrestamosActivosPorUsuario(@Param("idUsuario") Long idUsuario,
                                                  @Param("estado") EstadoPrestamo estado);
    // En PrestamoRepository.java
    @Query("SELECT p FROM Prestamo p WHERE p.ejemplar.codigo = :codigoEjemplar AND p.estado = 'ACTIVO'")
    Optional<Prestamo> findByEjemplarCodigo(@Param("codigoEjemplar") String codigoEjemplar);

}