package pe.edu.upeu.athenium.usuario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import pe.edu.upeu.athenium.perfil.entity.Perfil;


/**
 * Esta entidad reemplaza a 'Cliente.java' y a 'Usuario.java' del PDF.
 * Es la entidad base para TODOS los actores (Estudiante, Docente, etc.).
 * Su rol se define por la relación con 'Perfil'.
 * Implementa RBAC (Control de Acceso Basado en Roles) mediante la asociación con 'Perfil'.
 * Cada usuario tiene un perfil que determina sus permisos y accesos en el sistema.
 *
 * @author Cerezo
 *
 * Por el momento, en lugar de DNI se usa email como identificador único para login.
 */
@Data
@Entity
@Table(name = "athenium_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String email; // Usaremos email como 'user' para el login

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String apellido;

    @Column(nullable = false, length = 85)
    private String password;

    @Column(nullable = false, length = 20)
    private String estado; // Ej. "ACTIVO", "BLOQUEADO"

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_perfil", referencedColumnName = "id_perfil")
    private Perfil perfil;
}