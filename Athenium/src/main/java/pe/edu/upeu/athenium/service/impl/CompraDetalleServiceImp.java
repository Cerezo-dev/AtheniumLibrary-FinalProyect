package pe.edu.upeu.athenium.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.model.CompraDetalle;
import pe.edu.upeu.athenium.repository.CompraDetalleRepository;
import pe.edu.upeu.athenium.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.service.ICompraDetalleService;

@RequiredArgsConstructor
@Service
public class CompraDetalleServiceImp extends CrudGenericoServiceImp<CompraDetalle, Long> implements ICompraDetalleService {

    private final CompraDetalleRepository compraDetalleRepository;

    @Override
    protected ICrudGenericoRepository<CompraDetalle, Long> getRepo() {
        return compraDetalleRepository;
    }
}
