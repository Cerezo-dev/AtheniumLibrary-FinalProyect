package pe.edu.upeu.athenium.service;

import pe.edu.upeu.athenium.dto.ComboBoxOption;
import pe.edu.upeu.athenium.model.Genero;

import java.util.List;

public interface IGeneroService extends ICrudGenericoService<Genero,Long> {

    List<ComboBoxOption> listarCombobox();

}
