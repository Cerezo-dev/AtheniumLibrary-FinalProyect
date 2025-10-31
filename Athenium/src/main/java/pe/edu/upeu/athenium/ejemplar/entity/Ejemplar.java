package pe.edu.upeu.athenium.ejemplar.entity;

import jakarta.persistence.*;
import lombok.Data;
import pe.edu.upeu.athenium.libro.entity.Libro;
import pe.edu.upeu.athenium.ubicacion.entity.Ubicacion;

/**
 * Representa la COPIA FÍSICA de un Libro.
 * Aquí es donde se controla la disponibilidad en tiempo real.
 */
@Data
@Entity
@Table(name = "athenium_ejemplar")
public class Ejemplar {

    public enum EstadoEjemplar {
        DISPONIBLE, // En estante
        PRESTADO,
        RESERVADO,
        EN_REPARACION
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigoEjemplar; // Un código de barras único para esta copia

    // Relación: Muchos ejemplares pertenecen a UN solo Libro
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_libro", referencedColumnName = "id")
    private Libro libro;

    // Relación: Un ejemplar está en UNA Ubicacion (antes 'UnidadMedida' [cite: 1285-1290])
    @ManyToOne(optional = false)
    @JoinColumn(name = "id_ubicacion", referencedColumnName = "id")
    private Ubicacion ubicacion; // "Piso 2, Estante B-05"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEjemplar estado; // (RF-Catálogo - Disponibilidad en Tiempo Real)
}