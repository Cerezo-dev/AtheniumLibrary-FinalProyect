package pe.edu.upeu.syslibrary.service;

import pe.edu.upeu.syslibrary.dto.ModeloDataAutocomplet;
import pe.edu.upeu.syslibrary.model.Libro;

import java.util.List;

public interface ILibroService extends ICrudGenericService<Libro, Long> {
    List<ModeloDataAutocomplet> listAutoCompletLibro(String titulo);
    // MÉTODO QUE DEBES AGREGAR PARA SOLUCIONAR EL ERROR DE COMPILACIÓN EN EL CONTROLLER
    List<Libro> findByTituloContainingIgnoreCase(String titulo);
    // Opcional: búsqueda por autor, editorial o idioma
    List<Libro> buscarPorAutor(String autor);
    List<Libro> buscarPorEditorial(String editorial);
    List<Libro> buscarPorIdioma(String idioma);
    List<Libro> findAll();
   // List<Libro> findByTituloContainingIgnoreCase(String titulo);
    void deleteById(Long id);

    // Para el dashboard
    long count();
    long countAvailableCopies();
}
