package pe.edu.upeu.syslibrary.model;

import pe.edu.upeu.syslibrary.enums.GeneroLibro;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.syslibrary.utils.LocalDateAttributeConverter;
import jakarta.persistence.Convert;
///import pe.edu.upeu.syslibrary.util.LocalDateAttributeConverter;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "libro")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro")
    private Long idLibro;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "autor", length = 100)
    private String autor;

    @Column(name = "isbn", length = 20)
    private String isbn;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero")
    private GeneroLibro genero;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false,
            foreignKey = @ForeignKey(name = "FK_CATEGORIA_LIBRO"))
    private Categoria categoria;

    @Convert(converter = LocalDateAttributeConverter.class)
    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    @Column(name = "editorial", length = 100)
    private String editorial;

    @Column(name = "idioma", length = 50)
    private String idioma;

    @Column(name = "numero_paginas")
    private Integer numeroPaginas;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "numero_ejemplares")
    private Integer numeroEjemplares;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "disponibles")
    private Integer disponibles;
}
