package pe.edu.upeu.athenium.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.athenium.dto.ComboBoxOption;
import pe.edu.upeu.athenium.model.Genero;
import pe.edu.upeu.athenium.repository.GeneroRepository;
import pe.edu.upeu.athenium.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.service.IGeneroService;

import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class GeneroServiceImp extends CrudGenericoServiceImp<Genero,Long> implements IGeneroService {

    private final GeneroRepository generoRepository;
    @Override
    protected ICrudGenericoRepository<Genero, Long> getRepo() {
        return generoRepository;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar=new ArrayList<>();
        ComboBoxOption cb;
        for(Genero cate : generoRepository.findAll()) {
            cb=new ComboBoxOption();
            cb.setKey(String.valueOf(cate.getId()));
            cb.setValue(cate.getNombre());
            listar.add(cb);
        }
        return listar;
    }

}
