package pe.edu.upeu.syslibrary.service;

import pe.edu.upeu.syslibrary.model.Libro;
import java.util.List;

public interface ILibroService extends ICrudGenericService<Libro, Long> {

    // Métodos existentes
    List<Libro> findByTituloContainingIgnoreCase(String titulo);
    List<Libro> findAll();
    void deleteById(Long id);
    long count();
    long countAvailableCopies();

    // --- NUEVO MÉTODO PARA FILTRADO AVANZADO ---
    List<Libro> filtrarLibrosAvanzado(
            String texto,
            Long idCategoria,
            String estadoFisico,
            Integer anioDesde,
            Integer anioHasta,
            String ubicacion,
            Boolean soloDisponibles
    );
}