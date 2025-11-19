package pe.edu.upeu.syslibrary.service.imp;

import pe.edu.upeu.syslibrary.model.Categoria;
import pe.edu.upeu.syslibrary.repositorio.CategoriaRepository;
import pe.edu.upeu.syslibrary.repositorio.ICrudGenericRepository;
import pe.edu.upeu.syslibrary.service.ICategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl extends CrudGenericServiceImp<Categoria, Long>
        implements ICategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    protected ICrudGenericRepository<Categoria, Long> getRepo() {
        return categoriaRepository;
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return categoriaRepository.existsByNombre(nombre);
    }
}