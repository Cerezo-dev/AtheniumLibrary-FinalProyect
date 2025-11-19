/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.edu.upeu.syslibrary.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 En la BD, los nombres serán "ESTUDIANTE", "DOCENTE", "BIBLIOTECARIO", "ADMINISTRADOR".
 */
@Data
@Entity
@Table(name = "athenium_perfil")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil")
    private Long idPerfil;

    @Column(nullable = false, length = 50)
    private String nombre; // Ej. "ESTUDIANTE", "DOCENTE", etc.
}
