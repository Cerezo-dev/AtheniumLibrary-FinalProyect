package pe.edu.upeu.athenium.libro.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.common.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.common.service.impl.CrudGenericoServiceImp;
import pe.edu.upeu.athenium.libro.entity.Libro;
import pe.edu.upeu.athenium.libro.repository.LibroRepository;
import pe.edu.upeu.athenium.libro.service.ILibroService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LibroServiceImp extends CrudGenericoServiceImp<Libro, Long> implements ILibroService {

    private final LibroRepository libroRepository;

    @Override
    protected ICrudGenericoRepository<Libro, Long> getRepo() {
        return libroRepository;
    }

    @Override
    public Libro save(Libro libro) {
        try {
            // Validar ISBN único antes de guardar
            if (libro.getIsbn() != null && !libro.getIsbn().trim().isEmpty()) {
                if (existeIsbn(libro.getIsbn())) {
                    throw new IllegalArgumentException("Ya existe un libro con el ISBN: " + libro.getIsbn());
                }
            }
            return libroRepository.save(libro);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos: " + e.getMessage());
        }
    }

    @Override
    public Libro update(Long id, Libro libro) {
        try {
            // Validar ISBN único antes de actualizar (excluyendo el libro actual)
            if (libro.getIsbn() != null && !libro.getIsbn().trim().isEmpty()) {
                if (existeIsbnParaOtroLibro(libro.getIsbn(), id)) {
                    throw new IllegalArgumentException("Ya existe otro libro con el ISBN: " + libro.getIsbn());
                }
            }
            return super.update(id, libro);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos: " + e.getMessage());
        }
    }

    @Override
    public List<Libro> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return libroRepository.findAll();
        }
        return libroRepository.findByTituloContainingIgnoreCase(titulo);
    }

    @Override
    public List<Libro> buscarPorAutor(String autor) {
        if (autor == null || autor.trim().isEmpty()) {
            return libroRepository.findAll();
        }
        return libroRepository.findByAutorContainingIgnoreCase(autor);
    }

    @Override
    public Libro buscarPorIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }
        return libroRepository.findByIsbn(isbn);
    }

    @Override
    public List<Libro> buscarPorGenero(Long generoId) {
        if (generoId == null || generoId <= 0) {
            return libroRepository.findAll();
        }
        return libroRepository.findByGeneroId(generoId);
    }

    @Override
    public List<Libro> filtrarLibros(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return libroRepository.findAll();
        }
        return libroRepository.filtrarLibros(filtro.trim());
    }

    @Override
    public boolean existeIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        return libroRepository.findByIsbn(isbn) != null;
    }

    @Override
    public boolean existeIsbnParaOtroLibro(String isbn, Long id) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        return libroRepository.existsByIsbnAndIdNot(isbn, id);
    }
}