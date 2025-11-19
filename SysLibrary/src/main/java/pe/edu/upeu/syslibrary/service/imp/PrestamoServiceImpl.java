package pe.edu.upeu.syslibrary.service.imp;

import pe.edu.upeu.syslibrary.enums.EstadoEjemplar;
import pe.edu.upeu.syslibrary.enums.EstadoPrestamo;
import pe.edu.upeu.syslibrary.model.Ejemplar;
import pe.edu.upeu.syslibrary.model.Prestamo;
import pe.edu.upeu.syslibrary.model.Usuario;
import pe.edu.upeu.syslibrary.repositorio.EjemplarRepository;
import pe.edu.upeu.syslibrary.repositorio.PrestamoRepository;
import pe.edu.upeu.syslibrary.repositorio.UsuarioRepository;
import pe.edu.upeu.syslibrary.service.PrestamoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoServiceImpl extends CrudGenericServiceImp<Prestamo, Long> implements PrestamoService {


    private final PrestamoRepository prestamoRepository;
    private final EjemplarRepository ejemplarRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected PrestamoRepository getRepo() {
        return prestamoRepository;
    }

    @Override
    public List<Prestamo> findPrestamosActivosPorUsuario(Long idUsuario) {
        return prestamoRepository.findPrestamosActivosPorUsuario(idUsuario, EstadoPrestamo.ACTIVO.name());
    }

    @Override
    @Transactional
    public Prestamo registrarPrestamo(Long idEjemplar, Long idUsuario) {
        Ejemplar ejemplar = ejemplarRepository.findById(idEjemplar)
                .orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));
        if (ejemplar.getEstado() != EstadoEjemplar.DISPONIBLE) {
            throw new RuntimeException("Ejemplar no disponible");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Prestamo prestamo = Prestamo.builder()
                .ejemplar(ejemplar)
                .usuario(usuario)
                .fechaPrestamo(LocalDateTime.now())
                .estado(EstadoPrestamo.ACTIVO)
                .build();

        // Actualizar estado del ejemplar y del libro
        ejemplar.setEstado(EstadoEjemplar.PRESTADO);
        ejemplar.getLibro().setDisponibles(ejemplar.getLibro().getDisponibles() - 1);

        ejemplarRepository.save(ejemplar);
        return prestamoRepository.save(prestamo);
    }

    @Override
    @Transactional
    public Prestamo devolverPrestamo(Long idPrestamo) {
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Prestamo no encontrado"));

        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new RuntimeException("Prestamo ya devuelto");
        }

        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucion(LocalDateTime.now());

        Ejemplar ejemplar = prestamo.getEjemplar();
        ejemplar.setEstado(EstadoEjemplar.DISPONIBLE);
        ejemplar.getLibro().setDisponibles(ejemplar.getLibro().getDisponibles() + 1);

        ejemplarRepository.save(ejemplar);
        return prestamoRepository.save(prestamo);
    }
}
