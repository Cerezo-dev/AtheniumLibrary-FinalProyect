package pe.edu.upeu.athenium.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.dto.ComboBoxOption;
import pe.edu.upeu.athenium.model.Marca;
import pe.edu.upeu.athenium.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.repository.MarcaRepository;
import pe.edu.upeu.athenium.service.IMarcaService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MarcaServiceImp extends CrudGenericoServiceImp<Marca, Long> implements IMarcaService {
    private final MarcaRepository marcaRepository;

    @Override
    protected ICrudGenericoRepository<Marca, Long> getRepo() {
        return marcaRepository;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar=new ArrayList<>();
        ComboBoxOption cb;
        for(Marca cate : marcaRepository.findAll()) {
            cb=new ComboBoxOption();
            cb.setKey(String.valueOf(cate.getIdMarca()));
            cb.setValue(cate.getNombre());
            listar.add(cb);
        }
        return listar;
    }

}
