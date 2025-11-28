package pe.edu.upeu.syslibrary.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.model.Libro;

import java.nio.file.Paths;

@Controller
public class LibroDetalleController {

    @FXML private ImageView imgPortada;
    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblISBN;
    @FXML private Label lblAnio;
    @FXML private Label lblUbicacion;
    @FXML private Label lblEstadoFisico; // Aquí mostraremos el estado o la disponibilidad
    @FXML private Label lblSinopsis;

    // Método clave: Recibe el libro y llena la interfaz
    public void setLibroData(Libro libro) {
        // 1. Llenado de Textos (Usamos un helper para evitar nulos)
        lblTitulo.setText(getTextoSeguro(libro.getTitulo()));
        lblAutor.setText(getTextoSeguro(libro.getAutor()));
        lblISBN.setText(libro.getIsbn() != null ? libro.getIsbn() : "N/A");
        lblAnio.setText(libro.getAnio() != null ? libro.getAnio().toString() : "N/A");
        lblUbicacion.setText(getTextoSeguro(libro.getUbicacion()));

        // Formato para estado/disponibilidad
        String estado = getTextoSeguro(libro.getEstadoFisico());
        if (libro.getDisponibles() != null && libro.getNumeroEjemplares() != null) {
            estado += " (" + libro.getDisponibles() + "/" + libro.getNumeroEjemplares() + " Disponibles)";
        }
        lblEstadoFisico.setText(estado);

        lblSinopsis.setText(libro.getDescripcion() != null && !libro.getDescripcion().isEmpty()
                ? libro.getDescripcion()
                : "Sin descripción disponible.");

        // 2. CARGAR IMAGEN (Lógica de Portabilidad)
        cargarImagen(libro.getUrlPortada());
    }

    private void cargarImagen(String rutaImagen) {
        if (rutaImagen != null && !rutaImagen.isBlank()) {
            try {
                String urlFinal;

                // Si ya es una URL completa (http o file:/), la usamos tal cual
                if (rutaImagen.startsWith("http") || rutaImagen.startsWith("file:")) {
                    urlFinal = rutaImagen;
                } else {
                    // Si es una ruta relativa (ej: imagenes_libros/foto.jpg),
                    // la convertimos a absoluta según la PC donde estemos.
                    urlFinal = Paths.get(rutaImagen).toAbsolutePath().toUri().toString();
                }

                // Cargar la imagen suavemente (background loading = true)
                imgPortada.setImage(new Image(urlFinal, true));

            } catch (Exception e) {
                System.out.println("Error al cargar imagen en detalle: " + e.getMessage());
                // Opcional: Poner imagen por defecto si falla
                // imgPortada.setImage(new Image(getClass().getResource("/images/no_cover.png").toExternalForm()));
            }
        } else {
            // Si no hay URL, limpiar el ImageView o poner imagen por defecto
            imgPortada.setImage(null);
        }
    }

    // Pequeña utilidad para evitar escribir muchos "if null"
    private String getTextoSeguro(String texto) {
        return (texto != null && !texto.isEmpty()) ? texto : "---";
    }

    @FXML
    private void handleCerrar(ActionEvent event) {
        // Cerrar la ventana modal
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
}