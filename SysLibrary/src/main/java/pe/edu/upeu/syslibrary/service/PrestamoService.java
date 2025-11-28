package pe.edu.upeu.syslibrary.service;

import pe.edu.upeu.syslibrary.model.Prestamo;
import java.util.List;
import java.util.Optional;

public interface PrestamoService extends ICrudGenericService<Prestamo, Long> {

    // CORREGIDO: Recibe idUsuario
    Prestamo registrarPrestamo(Long idEjemplar, Long idUsuario);

    Prestamo devolverPrestamo(Long idPrestamo);

    // CORREGIDO: Recibe idUsuario
    List<Prestamo> findPrestamosActivosPorUsuario(Long idUsuario);
    // En PrestamoService.java (Interfaz)
    Optional<Prestamo> buscarPrestamoPorCodigoEjemplar(String codigoEjemplar);


}
