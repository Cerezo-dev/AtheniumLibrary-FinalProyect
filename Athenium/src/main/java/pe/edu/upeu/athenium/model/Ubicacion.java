package pe.edu.upeu.athenium.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * almacenar la ubicación física.
 */
@Data
@Entity
@Table(name = "athenium_ubicacion")
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre; // "Piso 2, Estante B-05", "Piso 1, Clásicos"
}
