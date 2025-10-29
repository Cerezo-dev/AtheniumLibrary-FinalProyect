/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.edu.upeu.athenium.model;

import jakarta.persistence.*;
import lombok.Data;

/**
    * Modelo de entidad para representar géneros de libros en la base de datos.
 */
@Data
@Entity
@Table(name = "athenium_genero")
public class Genero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre; // "Ficción", "Ciencia", "Ingeniería de Software"
}
