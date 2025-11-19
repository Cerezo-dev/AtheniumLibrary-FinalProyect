package pe.edu.upeu.syslibrary.service.imp;

import pe.edu.upeu.syslibrary.dto.ModeloDataAutocomplet;
import pe.edu.upeu.syslibrary.model.Libro;
import pe.edu.upeu.syslibrary.repositorio.ICrudGenericRepository;
import pe.edu.upeu.syslibrary.repositorio.LibroRepository;
import pe.edu.upeu.syslibrary.service.ILibroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibroServiceImpl extends CrudGenericServiceImp<Libro, Long>
        implements ILibroService {

    private final LibroRepository libroRepository;
    @Override
    public List<Libro> findByTituloContainingIgnoreCase(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo);
    }
    @Override
    protected ICrudGenericRepository<Libro, Long> getRepo() {
        return libroRepository; // <-- Aquí se conecta el repositorio con la implementación genérica
    }

    @Override
    public List<ModeloDataAutocomplet> listAutoCompletLibro(String filtro) {
        return libroRepository.findByTituloContainingIgnoreCase(filtro)
                .stream()
                .map(libro -> new ModeloDataAutocomplet(
                        libro.getIdLibro().toString(),
                        libro.getTitulo(),
                        libro.getAutor()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Libro> buscarPorAutor(String autor) {
        return libroRepository.findByAutorContainingIgnoreCase(autor);
    }

    @Override
    public List<Libro> buscarPorEditorial(String editorial) {
        return libroRepository.findByEditorialContainingIgnoreCase(editorial);
    }
    @Override
    public List<Libro> findAll() {
        List<Libro> lista = getRepo().findAll();
        System.out.println("📘 Libros recuperados del repositorio: " + lista.size());
        return lista;
    }

    @Override
    public List<Libro> buscarPorIdioma(String idioma) {
        return libroRepository.findByIdioma(idioma);
    }
    @Override
    public long count() {
        return libroRepository.count();
    }

    @Override
    public long countAvailableCopies() {
        Long suma = libroRepository.sumAvailableCopies();
        return suma != null ? suma : 0;
    }
}
