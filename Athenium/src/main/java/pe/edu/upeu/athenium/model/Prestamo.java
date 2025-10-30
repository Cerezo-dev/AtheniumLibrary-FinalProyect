package pe.edu.upeu.athenium.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Esta es la transacción principal. Conecta un Usuario (quién) con un Ejemplar (qué).
 */
@Data
@Entity
@Table(name = "athenium_prestamo")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el Usuario que PIDE el libro (antes 'Cliente'
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario_prestado_a", referencedColumnName = "id")
    private Usuario usuario; // El Estudiante o Docente

    // Relación con el Ejemplar FÍSICO que se presta
    @OneToOne(optional = false) // Un préstamo por un ejemplar específico
    @JoinColumn(name = "id_ejemplar", referencedColumnName = "id")
    private Ejemplar ejemplar;

    // Relación con el Usuario que REGISTRA el préstamo (antes 'Usuario' en Venta
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario_bibliotecario", referencedColumnName = "id")
    private Usuario bibliotecario; // El que tiene rol "BIBLIOTECARIO"


    /*
    * A partir de este punto podria salir prestamo_Detalle siguiendo
    * Lo de di 'divide y venceras'
     */

    @Column(nullable = false)
    private LocalDateTime fechaPrestamo;

    @Column(nullable = false)
    private LocalDateTime fechaVencimiento; // (RF-Préstamos)

    private LocalDateTime fechaDevolucion; // Null si aún no se ha devuelto
}
