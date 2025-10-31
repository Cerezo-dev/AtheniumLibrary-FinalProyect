package pe.edu.upeu.athenium.genero.service;

import pe.edu.upeu.athenium.common.dto.ComboBoxOption;
import pe.edu.upeu.athenium.genero.entity.Genero;
import pe.edu.upeu.athenium.common.service.ICrudGenericoService;

import java.util.List;

public interface IGeneroService extends ICrudGenericoService<Genero,Long> {

    List<ComboBoxOption> listarCombobox();

}
