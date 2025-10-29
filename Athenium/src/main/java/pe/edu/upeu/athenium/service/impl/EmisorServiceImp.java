package pe.edu.upeu.athenium.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.model.Emisor;
import pe.edu.upeu.athenium.repository.EmisorRepository;
import pe.edu.upeu.athenium.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.service.IEmisorService;
@RequiredArgsConstructor
@Service
public class EmisorServiceImp extends CrudGenericoServiceImp<Emisor, Long> implements IEmisorService {
    private final EmisorRepository emisorRepository;

    @Override
    protected ICrudGenericoRepository<Emisor, Long> getRepo() {
        return null;
    }
}
