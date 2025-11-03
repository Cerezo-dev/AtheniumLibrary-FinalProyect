package pe.edu.upeu.athenium.devoluciones.service;

import pe.edu.upeu.athenium.devoluciones.entity.Devoluciones;
import java.util.List;
import java.util.Optional;

public interface IDevolucionesService {

    // CREATE
    Devoluciones guardarDevolucion(Devoluciones devolucion);
    Devoluciones registrarDevolucion(Integer folioUsuario, Integer libroId);

    // READ
    List<Devoluciones> obtenerTodasDevoluciones();
    Optional<Devoluciones> obtenerDevolucionPorId(Long id);
    List<Devoluciones> obtenerDevolucionesPorFolioUsuario(Integer folioUsuario);
    List<Devoluciones> obtenerDevolucionesPorLibroId(Integer libroId);
    List<Devoluciones> obtenerDevolucionesPorEstado(String estado);

    // UPDATE
    Devoluciones actualizarDevolucion(Devoluciones devolucion);

    // DELETE
    void eliminarDevolucion(Long id);

    // Validaciones
    boolean validarDevolucion(Integer folioUsuario, Integer libroId);
    boolean existeDevolucion(Integer folioUsuario, Integer libroId);
}