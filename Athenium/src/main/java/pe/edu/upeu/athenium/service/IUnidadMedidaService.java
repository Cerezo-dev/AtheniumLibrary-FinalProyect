package pe.edu.upeu.athenium.service;

import pe.edu.upeu.athenium.dto.ComboBoxOption;
import pe.edu.upeu.athenium.model.UnidadMedida;

import java.util.List;

public interface IUnidadMedidaService extends  ICrudGenericoService<UnidadMedida,Long> {
    List<ComboBoxOption> listarCombobox();

}
