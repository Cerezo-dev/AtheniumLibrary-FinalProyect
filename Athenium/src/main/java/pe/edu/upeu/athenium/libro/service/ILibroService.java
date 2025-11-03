package pe.edu.upeu.athenium.libro.service;

import pe.edu.upeu.athenium.common.service.ICrudGenericoService;
import pe.edu.upeu.athenium.libro.entity.Libro;

import java.util.List;

public interface ILibroService extends ICrudGenericoService<Libro, Long> {

    // Métodos específicos para Libro
    List<Libro> buscarPorTitulo(String titulo);
    List<Libro> buscarPorAutor(String autor);
    Libro buscarPorIsbn(String isbn);
    List<Libro> buscarPorGenero(Long generoId);
    List<Libro> filtrarLibros(String filtro);

    // Validaciones específicas
    boolean existeIsbn(String isbn);
    boolean existeIsbnParaOtroLibro(String isbn, Long id);
}