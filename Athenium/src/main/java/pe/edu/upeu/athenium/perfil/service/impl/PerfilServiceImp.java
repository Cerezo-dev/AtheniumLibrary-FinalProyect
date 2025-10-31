package pe.edu.upeu.athenium.perfil.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.perfil.entity.Perfil;
import pe.edu.upeu.athenium.common.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.perfil.repository.PerfilRepository;
import pe.edu.upeu.athenium.perfil.service.IPerfilService;
import pe.edu.upeu.athenium.common.service.impl.CrudGenericoServiceImp;

@RequiredArgsConstructor
@Service
public class PerfilServiceImp extends CrudGenericoServiceImp<Perfil, Long> implements IPerfilService {

    private final PerfilRepository perfilRepository;

    @Override
    protected ICrudGenericoRepository<Perfil, Long> getRepo() {
        return perfilRepository;
    }
}
