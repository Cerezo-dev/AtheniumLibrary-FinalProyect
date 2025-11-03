package pe.edu.upeu.athenium.devoluciones.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "athenium_devoluciones")
public class Devoluciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "folio_usuario", nullable = false)
    private Integer folioUsuario;

    @Column(name = "libro_id", nullable = false)
    private Integer libroId;

    @Column(name = "fecha_devolucion", nullable = false)
    private LocalDate fechaDevolucion;

    @Column(nullable = false, length = 20)
    private String estado;

    @PrePersist
    protected void onCreate() {
        this.fechaDevolucion = LocalDate.now();
        if (this.estado == null) {
            this.estado = "DEVUELTO";
        }
    }
}