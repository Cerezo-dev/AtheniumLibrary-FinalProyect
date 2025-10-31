package pe.edu.upeu.athenium.libro.entity;

import jakarta.persistence.*;
import lombok.Data;
import pe.edu.upeu.athenium.ejemplar.entity.Ejemplar;
import pe.edu.upeu.athenium.genero.entity.Genero;

import java.util.List;

/**
 * Esta entidad representa el "título" (la idea abstracta del libro),
 * NO la copia física. No tiene "stock".
 * Un Libro puede tener muchos Ejemplares.
 */
@Data
@Entity
@Table(name = "athenium_libro")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, length = 150)
    private String autor;

    @Column(unique = true, length = 20)
    private String isbn;

    @Column(name = "anio_publicacion")
    private int anioPublicacion;

    // Relación con 'Genero' (antes 'Categoria' )
    @ManyToOne
    @JoinColumn(name = "id_genero", referencedColumnName = "id")
    private Genero genero;

    // ¡NUEVA RELACIÓN! Un Libro tiene muchas copias físicas (Ejemplares)
    // "mappedBy" indica que la entidad 'Ejemplar' maneja esta relación.
    @OneToMany(mappedBy = "libro")
    private List<Ejemplar> ejemplares;
}