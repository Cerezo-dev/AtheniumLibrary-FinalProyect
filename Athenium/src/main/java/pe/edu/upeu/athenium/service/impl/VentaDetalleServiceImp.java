package pe.edu.upeu.athenium.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.model.VentaDetalle;
import pe.edu.upeu.athenium.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.repository.VentaDetalleRepository;
import pe.edu.upeu.athenium.service.IVentaDetalleService;

@RequiredArgsConstructor
@Service
public class VentaDetalleServiceImp extends CrudGenericoServiceImp<VentaDetalle, Long> implements IVentaDetalleService {
    private final VentaDetalleRepository ventaDetalleRepository;
    @Override
    protected ICrudGenericoRepository<VentaDetalle, Long> getRepo() {
        return ventaDetalleRepository;
    }
}
