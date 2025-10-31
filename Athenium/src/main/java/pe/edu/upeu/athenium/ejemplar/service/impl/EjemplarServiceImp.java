package pe.edu.upeu.athenium.ejemplar.service.impl;

import pe.edu.upeu.athenium.ejemplar.entity.Ejemplar;
import pe.edu.upeu.athenium.ejemplar.service.IEjemplarService;
import pe.edu.upeu.athenium.common.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.common.service.impl.CrudGenericoServiceImp;

public class EjemplarServiceImp extends CrudGenericoServiceImp<Ejemplar, String> implements IEjemplarService {
    //temp
    @Override
    protected ICrudGenericoRepository<Ejemplar, String> getRepo() {
        return null;
    }
}
