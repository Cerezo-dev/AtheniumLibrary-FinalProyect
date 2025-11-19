package pe.edu.upeu.syslibrary.model;

import pe.edu.upeu.syslibrary.enums.EstadoEjemplar;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ejemplar")
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejemplar")
    private Long idEjemplar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro", nullable = false,
            foreignKey = @ForeignKey(name = "FK_LIBRO_EJEMPLAR"))
    @ToString.Exclude // evita bucles en toString() cuando hay relaciones bidireccionales
    @EqualsAndHashCode.Exclude
    private Libro libro;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo; // ej. "EJ-0001"

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoEjemplar estado; // "Disponible", "Prestado", "Dañado", etc.
}
