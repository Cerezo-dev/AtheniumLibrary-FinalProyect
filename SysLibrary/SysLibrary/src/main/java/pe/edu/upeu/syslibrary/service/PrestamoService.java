package pe.edu.upeu.syslibrary.service;

import pe.edu.upeu.syslibrary.model.Prestamo;

import java.util.List;

public interface PrestamoService extends ICrudGenericService<Prestamo, Long> {

    // Registrar un nuevo préstamo
    Prestamo registrarPrestamo(Long idEjemplar, Long idUsuario);

    // Devolver un préstamo
    Prestamo devolverPrestamo(Long idPrestamo);

    // Listar préstamos activos de un usuario
    List<Prestamo> findPrestamosActivosPorUsuario(Long idUsuario);

}
