package pe.edu.upeu.athenium.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.athenium.model.Reserva;

import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioIdAndEstado(Long usuarioId, Reserva.EstadoReserva estado);
    Optional<Reserva> findByEjemplarIdAndEstado(Long ejemplarId, Reserva.EstadoReserva estado);
}
