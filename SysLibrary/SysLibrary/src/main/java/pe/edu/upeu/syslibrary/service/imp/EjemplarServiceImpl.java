package pe.edu.upeu.syslibrary.service.imp;

import pe.edu.upeu.syslibrary.model.Ejemplar;
import pe.edu.upeu.syslibrary.repositorio.EjemplarRepository;
import pe.edu.upeu.syslibrary.service.EjemplarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EjemplarServiceImpl
        extends CrudGenericServiceImp<Ejemplar, Long>
        implements EjemplarService {


    private final EjemplarRepository ejemplarRepository;

    @Override
    protected EjemplarRepository getRepo() {
        return ejemplarRepository; // conecta el repositorio con el CRUD genérico
    }

    @Override
    public List<Ejemplar> findDisponibles(String filtro) {
        return ejemplarRepository.findDisponibles("%" + filtro + "%");
    }
}