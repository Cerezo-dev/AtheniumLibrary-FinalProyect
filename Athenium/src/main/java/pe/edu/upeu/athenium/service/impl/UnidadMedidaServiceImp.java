package pe.edu.upeu.athenium.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.dto.ComboBoxOption;
import pe.edu.upeu.athenium.model.UnidadMedida;
import pe.edu.upeu.athenium.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.repository.UnidadMedidaRepository;
import pe.edu.upeu.athenium.service.IUnidadMedidaService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UnidadMedidaServiceImp extends CrudGenericoServiceImp<UnidadMedida, Long> implements IUnidadMedidaService {
    private final UnidadMedidaRepository unidadMedidaRepository;
    @Override
    protected ICrudGenericoRepository<UnidadMedida, Long> getRepo() {
        return unidadMedidaRepository;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar=new ArrayList<>();
        ComboBoxOption cb;
        for(UnidadMedida cate : unidadMedidaRepository.findAll()) {
            cb=new ComboBoxOption();
            cb.setKey(String.valueOf(cate.getIdUnidad()));
            cb.setValue(cate.getNombreMedida());
            listar.add(cb);
        }
        return listar;
    }

}
