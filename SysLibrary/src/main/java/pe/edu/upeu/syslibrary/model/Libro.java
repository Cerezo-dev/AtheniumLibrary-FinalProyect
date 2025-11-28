package pe.edu.upeu.syslibrary.model;

import jakarta.persistence.*; // Importa las anotaciones para la base de datos (JPA)
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.syslibrary.enums.GeneroLibro;

// Estas anotaciones de Lombok generan código repetitivo automáticamente (getters, setters, constructores)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Estas anotaciones le dicen a JPA que esta clase representa una tabla en la base de datos
@Entity
@Table(name = "libro")
public class Libro {

    // Clave primaria de la tabla (ID único para cada libro)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro")
    private Long idLibro;

    // Título del libro (obligatorio, hasta 150 caracteres)
    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    // Autor del libro
    @Column(name = "autor", length = 100)
    private String autor;

    // ISBN (International Standard Book Number)
    @Column(name = "isbn", length = 20)
    private String isbn;

    // Relación con la entidad Categoria (Muchos libros pertenecen a una categoría)
    @ManyToOne(fetch = FetchType.EAGER) // EAGER carga la categoría automáticamente
    @JoinColumn(name = "id_categoria", nullable = false, foreignKey = @ForeignKey(name = "FK_CATEGORIA_LIBRO"))
    private Categoria categoria;

    // Editorial del libro
    @Column(name = "editorial", length = 100)
    private String editorial;

    // Descripción larga del libro
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    // --- NUEVOS CAMPOS PARA EL FORMULARIO ---

    // Año de publicación como número entero (Ej: 2023)
    @Column(name = "anio")
    private Integer anio;

    // Cantidad Total de ejemplares
    @Column(name = "numero_ejemplares")
    private Integer numeroEjemplares;

    // Cantidad Disponible actualmente
    @Column(name = "disponibles")
    private Integer disponibles;

    // Ubicación física en la biblioteca (Ej: "Estante A-1")
    @Column(name = "ubicacion", length = 100)
    private String ubicacion;

    // Estado físico del libro (Ej: "Bueno", "Malo")
    @Column(name = "estado_fisico", length = 50)
    private String estadoFisico;

    // Código de barras interno de la biblioteca
    @Column(name = "codigo_barras", length = 50)
    private String codigoBarras;

    // URL de la imagen de portada
    @Column(name = "url_portada", length = 500)
    private String urlPortada;

    // --- CAMPOS NO USADOS EN EL NUEVO FORMULARIO (Se pueden mantener por compatibilidad) ---
    @Column(name = "idioma", length = 50) private String idioma;
    @Column(name = "numero_paginas") private Integer numeroPaginas;
    @Enumerated(EnumType.STRING) @Column(name = "genero") private GeneroLibro genero;
    @Column(name = "stock") private Integer stock;
}