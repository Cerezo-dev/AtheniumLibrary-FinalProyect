package pe.edu.upeu.athenium.genero.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.athenium.common.dto.ComboBoxOption;
import pe.edu.upeu.athenium.genero.entity.Genero;
import pe.edu.upeu.athenium.genero.repository.GeneroRepository;
import pe.edu.upeu.athenium.common.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.genero.service.IGeneroService;
import pe.edu.upeu.athenium.common.service.impl.CrudGenericoServiceImp;

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
