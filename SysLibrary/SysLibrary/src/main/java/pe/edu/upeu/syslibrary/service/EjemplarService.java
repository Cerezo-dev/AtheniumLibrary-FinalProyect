package pe.edu.upeu.syslibrary.service;

import pe.edu.upeu.syslibrary.model.Ejemplar;

import java.util.List;

public interface EjemplarService extends ICrudGenericService<Ejemplar, Long> {

    // ✅ Buscar ejemplares disponibles por título o código
    List<Ejemplar> findDisponibles(String filtro);
}
