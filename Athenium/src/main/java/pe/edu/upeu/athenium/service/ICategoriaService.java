package pe.edu.upeu.athenium.service;

import pe.edu.upeu.athenium.dto.ComboBoxOption;
import pe.edu.upeu.athenium.model.Categoria;

import java.util.List;

public interface ICategoriaService extends ICrudGenericoService<Categoria,Long> {

    List<ComboBoxOption> listarCombobox();

}
