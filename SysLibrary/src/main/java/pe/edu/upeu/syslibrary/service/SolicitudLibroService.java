package pe.edu.upeu.syslibrary.service;

import pe.edu.upeu.syslibrary.model.SolicitudLibro;
import pe.edu.upeu.syslibrary.model.Usuario;

import java.util.List;

public interface SolicitudLibroService extends ICrudGenericService<SolicitudLibro, Long> {

    // Método específico para registrar una nueva solicitud desde el catálogo de Google
    SolicitudLibro registrarSolicitud(Usuario usuario, String titulo, String autor, String isbn, String urlPortada);

    // Método para filtrar solicitudes por estado (PENDIENTE, APROBADA, RECHAZADA)
    List<SolicitudLibro> listarPorEstado(String estado);
}