package pe.edu.upeu.syslibrary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellidos", length = 150)
    private String apellidos;

    @Column(name = "dni", unique = true, length = 20)
    private String dni;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER) // Eager para que cargue el rol al iniciar sesión
    @JoinColumn(name = "id_perfil", nullable = false)
    private Perfil perfil;

    // --- NUEVOS CAMPOS (ANTES ESTABAN EN LA TABLA ESTUDIANTE) ---
    // Son nullable=true porque un Admin o Bibliotecario no tiene estos datos

    @Column(name = "codigo_estudiante", unique = true, nullable = true, length = 20)
    private String codigoEstudiante;

    @Column(name = "carrera", nullable = true, length = 100)
    private String carrera;

    @Column(name = "telefono", nullable = true, length = 20)
    private String telefono;
    @Column(name = "estado", length = 20)
    private String estado;
}