package pe.edu.upeu.syslibrary.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.syslibrary.model.SolicitudLibro;
import pe.edu.upeu.syslibrary.model.Usuario;
import pe.edu.upeu.syslibrary.repositorio.ICrudGenericRepository;
import pe.edu.upeu.syslibrary.repositorio.SolicitudLibroRepository;
import pe.edu.upeu.syslibrary.service.SolicitudLibroService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SolicitudLibroServiceImpl extends CrudGenericServiceImp<SolicitudLibro, Long> implements SolicitudLibroService {

    private final SolicitudLibroRepository repo;

    @Override
    protected ICrudGenericRepository<SolicitudLibro, Long> getRepo() {
        return repo;
    }

    @Override
    public SolicitudLibro registrarSolicitud(Usuario usuario, String titulo, String autor, String isbn, String urlPortada) {
        SolicitudLibro solicitud = new SolicitudLibro();
        solicitud.setUsuario(usuario);
        solicitud.setTitulo(titulo);
        solicitud.setAutor(autor);
        solicitud.setIsbn(isbn);
        solicitud.setUrlPortada(urlPortada);

        // Datos automáticos
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setEstado("PENDIENTE");

        return repo.save(solicitud);
    }

    @Override
    public List<SolicitudLibro> listarPorEstado(String estado) {
        return repo.findByEstado(estado);
    }
}