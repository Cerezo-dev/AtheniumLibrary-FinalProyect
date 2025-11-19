package pe.edu.upeu.athenium.reserva.entity;

import jakarta.persistence.*;
import lombok.Data;
import pe.edu.upeu.athenium.ejemplar.entity.Ejemplar;
import pe.edu.upeu.athenium.usuario.entity.Usuario;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "athenium_reserva")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReserva;

    @ManyToOne
    @JoinColumn(name = "id_ejemplar")
    private Ejemplar ejemplar;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private LocalDateTime fechaReserva;
    private LocalDateTime vencimiento;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    public enum EstadoReserva { PENDIENTE, CONFIRMADA, CANCELADA }
}
