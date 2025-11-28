package pe.edu.upeu.syslibrary.service.imp;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.syslibrary.enums.EstadoEjemplar;
import pe.edu.upeu.syslibrary.enums.EstadoPrestamo;
import pe.edu.upeu.syslibrary.model.Ejemplar;
import pe.edu.upeu.syslibrary.model.Prestamo;
import pe.edu.upeu.syslibrary.model.Sancion;
import pe.edu.upeu.syslibrary.model.Usuario; // Importante
import pe.edu.upeu.syslibrary.repositorio.*;
import pe.edu.upeu.syslibrary.service.PrestamoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrestamoServiceImpl extends CrudGenericServiceImp<Prestamo, Long> implements PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final EjemplarRepository ejemplarRepository;

    // CORREGIDO: Inyectamos el repo de Usuario, ya no el de Estudiante
    private final UsuarioRepository usuarioRepository;

    @Override
    protected ICrudGenericRepository<Prestamo, Long> getRepo() {
        return prestamoRepository;
    }

    @Override
    public List<Prestamo> findPrestamosActivosPorUsuario(Long idUsuario) {
        // CORREGIDO: Llamamos al método actualizado del repositorio
        return prestamoRepository.findPrestamosActivosPorUsuario(idUsuario, EstadoPrestamo.ACTIVO);
    }

    @Override
    @Transactional
    public Prestamo registrarPrestamo(Long idEjemplar, Long idUsuario) {
        // 1. Buscar Ejemplar
        Ejemplar ejemplar = ejemplarRepository.findById(idEjemplar)
                .orElseThrow(() -> new RuntimeException("Ejemplar no encontrado"));

        if (ejemplar.getEstado() != EstadoEjemplar.DISPONIBLE) {
            throw new RuntimeException("El ejemplar no está disponible.");
        }

        // 2. Buscar USUARIO (Antes era Estudiante)
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // (Opcional) Aquí podrías validar si el usuario tiene perfil 'ESTUDIANTE' si quisieras ser estricto

        // 3. Crear Préstamo
        Prestamo prestamo = Prestamo.builder()
                .ejemplar(ejemplar)
                .usuario(usuario) // CORREGIDO: Asignamos el usuario
                .fechaPrestamo(LocalDateTime.now())
                .estado(EstadoPrestamo.ACTIVO)
                .fechaDevolucion(LocalDateTime.now().plusDays(3))
                .build();

        // 4. Actualizar Ejemplar
        ejemplar.setEstado(EstadoEjemplar.PRESTADO);
        if (ejemplar.getLibro().getDisponibles() > 0) {
            ejemplar.getLibro().setDisponibles(ejemplar.getLibro().getDisponibles() - 1);
        }

        ejemplarRepository.save(ejemplar);
        return prestamoRepository.save(prestamo);
    }

    // El método devolverPrestamo se queda igual, ese estaba bien.
    @Override
    @Transactional
    public Prestamo devolverPrestamo(Long idPrestamo) {
        // 1. Buscar el préstamo
        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new RuntimeException("Este préstamo ya fue devuelto anteriormente.");
        }

        // 2. Calcular Fechas
        LocalDateTime fechaDevolucionReal = LocalDateTime.now();
        LocalDateTime fechaPactada = prestamo.getFechaDevolucion();

        // 3. LÓGICA DE MULTA (SANCIÓN)
        if (fechaDevolucionReal.isAfter(fechaPactada)) {
            // Calculamos la diferencia en días
            long diasRetraso = java.time.temporal.ChronoUnit.DAYS.between(fechaPactada, fechaDevolucionReal);

            // Si hay retraso (incluso si es 1 día)
            if (diasRetraso > 0) {
                double costoPorDia = 2.00; // TARIFA: 2 soles por día (puedes cambiarlo)
                double montoMulta = diasRetraso * costoPorDia;

                // Crear la Sanción
                Sancion sancion = Sancion.builder()
                        .prestamo(prestamo)
                        .diasRetraso(diasRetraso)
                        .monto(montoMulta)
                        .estado("PENDIENTE") // Nace como deuda pendiente
                        .fechaEmision(fechaDevolucionReal)
                        .build();

                sancionRepository.save(sancion);
                System.out.println("⚠️ MULTA GENERADA: S/ " + montoMulta);
            }
        }

        // 4. Actualizar Préstamo a DEVUELTO
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucion(fechaDevolucionReal);

        // 5. Liberar el Ejemplar (Libro físico)
        Ejemplar ejemplar = prestamo.getEjemplar();
        ejemplar.setEstado(EstadoEjemplar.DISPONIBLE);

        // Devolver stock al libro padre
        ejemplar.getLibro().setDisponibles(ejemplar.getLibro().getDisponibles() + 1);
        ejemplarRepository.save(ejemplar);

        return prestamoRepository.save(prestamo);
    }
    // En PrestamoServiceImpl.java (Implementación)
    @Override
    public Optional<Prestamo> buscarPrestamoPorCodigoEjemplar(String codigoEjemplar) {
        return prestamoRepository.findByEjemplarCodigo(codigoEjemplar);
    }
    private final SancionRepository sancionRepository;


}