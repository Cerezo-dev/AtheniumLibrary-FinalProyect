package pe.edu.upeu.athenium.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.model.CompCarrito;
import pe.edu.upeu.athenium.repository.CompCarritoRepository;
import pe.edu.upeu.athenium.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.service.ICompCarritoService;

@RequiredArgsConstructor
@Service
public class CompCarritoServiceImp extends CrudGenericoServiceImp<CompCarrito,Long> implements ICompCarritoService {

    private final CompCarritoRepository compCarritoRepository;

    @Override
    protected ICrudGenericoRepository<CompCarrito, Long> getRepo() {
        return compCarritoRepository;
    }
}
