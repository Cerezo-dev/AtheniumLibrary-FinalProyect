package pe.edu.upeu.athenium.service;

import pe.edu.upeu.athenium.dto.ComboBoxOption;
import pe.edu.upeu.athenium.model.Ubicacion;

import java.util.List;

public interface IUbicacionService extends  ICrudGenericoService<Ubicacion,Long> {
    List<ComboBoxOption> listarCombobox();

}
