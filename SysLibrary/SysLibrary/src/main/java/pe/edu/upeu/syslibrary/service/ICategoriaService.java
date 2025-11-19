package pe.edu.upeu.syslibrary.service;

import pe.edu.upeu.syslibrary.model.Categoria;

public interface ICategoriaService extends ICrudGenericService<Categoria, Long> {
    boolean existsByNombre(String nombre); // Validación antes de guardar

}
