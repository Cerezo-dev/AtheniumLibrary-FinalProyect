package pe.edu.upeu.athenium.devoluciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.athenium.devoluciones.entity.Devoluciones;


import java.util.List;

@Repository
public interface DevolucionesRepository extends JpaRepository<Devoluciones, Long> {

    // Buscar devoluciones por folio de usuario
    List<Devoluciones> findByFolioUsuario(Integer folioUsuario);

    // Buscar devoluciones por libro ID
    List<Devoluciones> findByLibroId(Integer libroId);

    // Verificar si ya existe una devolución para un libro y usuario
    @Query("SELECT COUNT(d) > 0 FROM Devoluciones d WHERE d.folioUsuario = :folioUsuario AND d.libroId = :libroId")
    boolean existsByFolioUsuarioAndLibroId(@Param("folioUsuario") Integer folioUsuario,
                                           @Param("libroId") Integer libroId);

    // Buscar devoluciones por estado
    List<Devoluciones> findByEstado(String estado);
}