package pe.edu.upeu.athenium.service;

import pe.edu.upeu.athenium.dto.ComboBoxOption;
import pe.edu.upeu.athenium.model.Marca;

import java.util.List;

public interface IMarcaService extends ICrudGenericoService<Marca,Long>{
    List<ComboBoxOption> listarCombobox();
}
