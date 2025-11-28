package pe.edu.upeu.syslibrary.service.imp;

import jakarta.transaction.Transactional;
import pe.edu.upeu.syslibrary.model.Categoria;
import pe.edu.upeu.syslibrary.repositorio.CategoriaRepository;
import pe.edu.upeu.syslibrary.repositorio.ICrudGenericRepository;
import pe.edu.upeu.syslibrary.repositorio.LibroRepository;
import pe.edu.upeu.syslibrary.service.ICategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl extends CrudGenericServiceImp<Categoria, Long>
        implements ICategoriaService {

    private final LibroRepository libroRepository;//Inyectamos LibroRepository
    private final CategoriaRepository categoriaRepository;

    @Override
    protected ICrudGenericRepository<Categoria, Long> getRepo() {

        return categoriaRepository;
    }


    @Override
    public boolean existsByNombre(String nombre) {

        return categoriaRepository.existsByNombre(nombre);
    }

    @Override
    @Transactional
    public void deleteById(Long id) { // O deleteById(Long id) dependiendo de cómo se llame en tu genérico

        // A. Verificamos si hay libros
        if (libroRepository.existsByCategoria_IdCategoria(id)) {
            throw new RuntimeException("No se puede eliminar la categoría porque tiene libros vinculados.");
        }

        // B. Si no hay libros, llamamos al método delete original del padre (super)
        super.deleteById(id);
    }
}