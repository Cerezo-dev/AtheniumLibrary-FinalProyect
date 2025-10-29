package pe.edu.upeu.athenium.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.model.Proveedor;
import pe.edu.upeu.athenium.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.repository.ProveedorRepository;
import pe.edu.upeu.athenium.service.IProveedorService;

@RequiredArgsConstructor
@Service
public class ProveedorServiceImp extends CrudGenericoServiceImp<Proveedor, Long> implements IProveedorService {
    private final ProveedorRepository proveedorRepository;
    @Override
    protected ICrudGenericoRepository<Proveedor, Long> getRepo() {
        return proveedorRepository;
    }
}
