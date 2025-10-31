package pe.edu.upeu.athenium.ubicacion.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.common.service.impl.CrudGenericoServiceImp;
import pe.edu.upeu.athenium.common.dto.ComboBoxOption;
import pe.edu.upeu.athenium.ubicacion.entity.Ubicacion;
import pe.edu.upeu.athenium.common.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.ubicacion.repository.UbicacionRepository;
import pe.edu.upeu.athenium.ubicacion.service.IUbicacionService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UbicacionServiceImp extends CrudGenericoServiceImp<Ubicacion, Long> implements IUbicacionService {
    private final UbicacionRepository ubicacionRepository;
    @Override
    protected ICrudGenericoRepository<Ubicacion, Long> getRepo() {
        return ubicacionRepository;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        List<ComboBoxOption> listar=new ArrayList<>();
        ComboBoxOption cb;
        for(Ubicacion cate : ubicacionRepository.findAll()) {
            cb=new ComboBoxOption();
            cb.setKey(String.valueOf(cate.getId()));
            cb.setValue(cate.getNombre());
            listar.add(cb);
        }
        return listar;
    }

}
