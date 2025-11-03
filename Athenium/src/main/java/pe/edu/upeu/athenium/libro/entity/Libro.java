package pe.edu.upeu.athenium.libro.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import pe.edu.upeu.athenium.genero.entity.Genero;

@Data
@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no puede exceder 255 caracteres")
    @Column(nullable = false, length = 255)
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    @Size(max = 255, message = "El autor no puede exceder 255 caracteres")
    @Column(nullable = false, length = 255)
    private String autor;

    @Size(max = 20, message = "El ISBN no puede exceder 20 caracteres")
    @Column(unique = true, length = 20)
    private String isbn;

    @Min(value = 1000, message = "El año de publicación debe ser mayor a 1000")
    @Max(value = 2100, message = "El año de publicación debe ser menor a 2100")
    @Column(name = "anio_publicacion")
    private Integer anioPublicacion;

    @NotNull(message = "El género es obligatorio")
    @ManyToOne(optional = false)
    @JoinColumn(name = "genero_id", referencedColumnName = "id")
    private Genero genero;
}