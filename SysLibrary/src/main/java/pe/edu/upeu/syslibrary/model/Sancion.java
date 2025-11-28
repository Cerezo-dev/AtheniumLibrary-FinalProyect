package pe.edu.upeu.syslibrary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sancion")
public class Sancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSancion;

    // Relación 1 a 1: Una sanción pertenece a un préstamo específico
    @OneToOne
    @JoinColumn(name = "id_prestamo", nullable = false)
    private Prestamo prestamo;

    @Column(name = "dias_retraso", nullable = false)
    private Long diasRetraso;

    @Column(name = "monto", nullable = false)
    private Double monto; // Ej: 5.00

    @Column(name = "estado", length = 20)
    private String estado; // "PENDIENTE", "PAGADO"

    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;
}