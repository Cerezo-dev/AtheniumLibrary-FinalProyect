package pe.edu.upeu.syslibrary.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solicitud_libro")
public class SolicitudLibro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSolicitud;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario; // El estudiante que pide el libro

    private String titulo;
    private String autor;
    private String isbn;

    @Column(length = 500)
    private String urlPortada;

    @Column(nullable = false)
    private LocalDateTime fechaSolicitud;

    // Estados: PENDIENTE, APROBADA, RECHAZADA
    private String estado;
}