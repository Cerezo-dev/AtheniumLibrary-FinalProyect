package pe.edu.upeu.athenium.ubicacion.service;

import pe.edu.upeu.athenium.common.service.ICrudGenericoService;
import pe.edu.upeu.athenium.common.dto.ComboBoxOption;
import pe.edu.upeu.athenium.ubicacion.entity.Ubicacion;

import java.util.List;

public interface IUbicacionService extends ICrudGenericoService<Ubicacion,Long> {
    List<ComboBoxOption> listarCombobox();

}
