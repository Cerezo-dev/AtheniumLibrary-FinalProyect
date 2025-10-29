package pe.edu.upeu.athenium.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.model.Perfil;
import pe.edu.upeu.athenium.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.repository.PerfilRepository;
import pe.edu.upeu.athenium.service.IPerfilService;

@RequiredArgsConstructor
@Service
public class PerfilServiceImp extends CrudGenericoServiceImp<Perfil, Long> implements IPerfilService {

    private final PerfilRepository perfilRepository;

    @Override
    protected ICrudGenericoRepository<Perfil, Long> getRepo() {
        return perfilRepository;
    }
}
