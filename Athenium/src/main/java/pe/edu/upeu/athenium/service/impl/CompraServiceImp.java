package pe.edu.upeu.athenium.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.model.Compra;
import pe.edu.upeu.athenium.repository.CompraRepository;
import pe.edu.upeu.athenium.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.service.ICompraService;

@RequiredArgsConstructor
@Service
public class CompraServiceImp extends CrudGenericoServiceImp<Compra,Long> implements ICompraService {

    private final CompraRepository compraRepository;

    @Override
    protected ICrudGenericoRepository<Compra, Long> getRepo() {
        return compraRepository;
    }
}
