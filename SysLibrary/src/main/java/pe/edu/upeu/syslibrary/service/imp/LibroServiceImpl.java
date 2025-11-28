package pe.edu.upeu.syslibrary.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.syslibrary.model.Libro;
import pe.edu.upeu.syslibrary.repositorio.ICrudGenericRepository;
import pe.edu.upeu.syslibrary.repositorio.LibroRepository;
import pe.edu.upeu.syslibrary.service.ILibroService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibroServiceImpl extends CrudGenericServiceImp<Libro, Long> implements ILibroService {

    private final LibroRepository libroRepository;

    @Override
    protected ICrudGenericRepository<Libro, Long> getRepo() {
        return libroRepository;
    }

    @Override
    public List<Libro> findByTituloContainingIgnoreCase(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo);
    }

    @Override
    public List<Libro> findAll() {
        return libroRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        libroRepository.deleteById(id);
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

    // --- IMPLEMENTACIÓN DEL FILTRADO AVANZADO ---
    @Override
    public List<Libro> filtrarLibrosAvanzado(String texto, Long idCategoria, String estadoFisico, Integer anioDesde, Integer anioHasta, String ubicacion, Boolean soloDisponibles) {
        // 1. Limpieza de datos: Convertir entradas vacías o especiales a NULL para que el repositorio las ignore.

        // Texto de búsqueda
        String textoLimpio = (texto == null || texto.trim().isEmpty()) ? null : texto.trim();

        // Categoría (ID -1 significa "Todas")
        Long categoriaLimpia = (idCategoria == null || idCategoria == -1L) ? null : idCategoria;

        // Ubicación
        String ubicacionLimpia = (ubicacion == null || ubicacion.trim().isEmpty()) ? null : ubicacion.trim();

        // Los demás parámetros (estadoFisico, anioDesde, anioHasta, soloDisponibles)
        // ya vienen como objetos anulables (String, Integer, Boolean), por lo que si son null,
        // se pasan directamente como null.

        // 2. Llamada a la consulta maestra en el repositorio
        return libroRepository.filtrarLibrosAvanzado(
                textoLimpio,
                categoriaLimpia,
                estadoFisico,
                anioDesde,
                anioHasta,
                ubicacionLimpia,
                soloDisponibles
        );
    }
}