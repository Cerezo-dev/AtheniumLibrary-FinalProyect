package pe.edu.upeu.athenium.reserva.repository;

import pe.edu.upeu.athenium.common.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.reserva.entity.Reserva;

import java.util.List;
import java.util.Optional;

public interface ReservaRepository extends ICrudGenericoRepository<Reserva, Long> {
    List<Reserva> findByUsuarioIdAndEstado(Long usuarioId, Reserva.EstadoReserva estado);
    Optional<Reserva> findByEjemplarIdAndEstado(Long ejemplarId, Reserva.EstadoReserva estado);
}
