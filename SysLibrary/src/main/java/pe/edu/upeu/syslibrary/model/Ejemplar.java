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

    // --- CAMBIO IMPORTANTE AQUÍ ---
    // Cambiamos LAZY por EAGER.
    // Así, cuando busques un ejemplar, TRAERÁ AUTOMÁTICAMENTE los datos del libro (Título, Autor).
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_libro", nullable = false,
            foreignKey = @ForeignKey(name = "FK_LIBRO_EJEMPLAR"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Libro libro;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoEjemplar estado;
}