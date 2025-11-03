package pe.edu.upeu.athenium.devoluciones.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.devoluciones.entity.Devoluciones;
import pe.edu.upeu.athenium.devoluciones.repository.DevolucionesRepository;
import pe.edu.upeu.athenium.devoluciones.service.IDevolucionesService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DevolucionesServiceImp implements IDevolucionesService {

    private final DevolucionesRepository devolucionesRepository;

    @Override
    public Devoluciones guardarDevolucion(Devoluciones devolucion) {
        try {
            // Validaciones antes de guardar
            if (!validarDevolucion(devolucion.getFolioUsuario(), devolucion.getLibroId())) {
                throw new IllegalArgumentException("Datos de devolución inválidos");
            }

            if (existeDevolucion(devolucion.getFolioUsuario(), devolucion.getLibroId())) {
                throw new IllegalArgumentException("Ya existe una devolución para este libro y usuario");
            }

            return devolucionesRepository.save(devolucion);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Error de integridad de datos: " + e.getMessage());
        }
    }

    @Override
    public Devoluciones registrarDevolucion(Integer folioUsuario, Integer libroId) {
        if (!validarDevolucion(folioUsuario, libroId)) {
            throw new IllegalArgumentException("Datos de devolución inválidos");
        }

        if (existeDevolucion(folioUsuario, libroId)) {
            throw new IllegalArgumentException("Ya existe una devolución para este libro y usuario");
        }

        Devoluciones devolucion = new Devoluciones();
        devolucion.setFolioUsuario(folioUsuario);
        devolucion.setLibroId(libroId);

        return guardarDevolucion(devolucion);
    }

    @Override
    public List<Devoluciones> obtenerTodasDevoluciones() {
        return devolucionesRepository.findAll();
    }

    @Override
    public Optional<Devoluciones> obtenerDevolucionPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        return devolucionesRepository.findById(id);
    }

    @Override
    public List<Devoluciones> obtenerDevolucionesPorFolioUsuario(Integer folioUsuario) {
        if (folioUsuario == null || folioUsuario <= 0) {
            throw new IllegalArgumentException("Folio de usuario inválido");
        }
        return devolucionesRepository.findByFolioUsuario(folioUsuario);
    }

    @Override
    public List<Devoluciones> obtenerDevolucionesPorLibroId(Integer libroId) {
        if (libroId == null || libroId <= 0) {
            throw new IllegalArgumentException("ID de libro inválido");
        }
        return devolucionesRepository.findByLibroId(libroId);
    }

    @Override
    public List<Devoluciones> obtenerDevolucionesPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("Estado inválido");
        }
        return devolucionesRepository.findByEstado(estado);
    }

    @Override
    public Devoluciones actualizarDevolucion(Devoluciones devolucion) {
        if (devolucion.getId() == null || devolucion.getId() <= 0) {
            throw new IllegalArgumentException("ID de devolución inválido para actualizar");
        }

        if (!devolucionesRepository.existsById(devolucion.getId())) {
            throw new IllegalArgumentException("Devolución no encontrada con ID: " + devolucion.getId());
        }

        if (!validarDevolucion(devolucion.getFolioUsuario(), devolucion.getLibroId())) {
            throw new IllegalArgumentException("Datos de devolución inválidos");
        }

        return devolucionesRepository.save(devolucion);
    }

    @Override
    public void eliminarDevolucion(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        if (!devolucionesRepository.existsById(id)) {
            throw new IllegalArgumentException("Devolución no encontrada con ID: " + id);
        }

        devolucionesRepository.deleteById(id);
    }

    @Override
    public boolean validarDevolucion(Integer folioUsuario, Integer libroId) {
        return folioUsuario != null && folioUsuario > 0 &&
                libroId != null && libroId > 0;
    }

    @Override
    public boolean existeDevolucion(Integer folioUsuario, Integer libroId) {
        return devolucionesRepository.existsByFolioUsuarioAndLibroId(folioUsuario, libroId);
    }
}