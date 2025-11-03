package pe.edu.upeu.athenium.common.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.athenium.common.components.StageManager;
import pe.edu.upeu.athenium.common.components.Toast;
import pe.edu.upeu.athenium.libro.entity.Libro;
import pe.edu.upeu.athenium.libro.service.ILibroService;

@Controller
@Scope("prototype") // ¡MUY IMPORTANTE! Crea una nueva instancia por cada tarjeta.
public class BookCardController {

    // --- FXML de la Tarjeta ---
    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblUbicacion;
    @FXML private Label lblEstado;
    @FXML private Button btnReservar;

    // --- Servicios Inyectados ---
    @Autowired
    private ILibroService libroService; // El card ahora puede usar servicios

    // --- Estado Interno ---
    private Libro libro; // Cada tarjeta sabe qué libro representa
    private Runnable onDataChangedCallback; // Callback para notificar al HomeContentController que debe recargar

    /**
     * Paso A: El HomeContentController llama a este método para
     * inyectar el libro y el callback de refresco.
     */
    public void setData(Libro libro, Runnable onDataChangedCallback) {
        this.libro = libro;
        this.onDataChangedCallback = onDataChangedCallback;
        updateUI();
    }

    /**
     * Paso B: El controlador actualiza sus propios elementos visuales.
     */
    private void updateUI() {
        if (libro == null) return;

        lblTitulo.setText(libro.getTitulo());
        lblAutor.setText(libro.getAutor() + " (" + libro.getAnioPublicacion() + ")");

        // Adaptación: Usar Género en lugar de Ubicación
        if (libro.getGenero() != null) {
            lblUbicacion.setText("Género: " + libro.getGenero().getNombre());
        } else {
            lblUbicacion.setText("Género: N/A");
        }

        // Adaptación: Estado hardcodeado (tu Entidad no tiene 'estado')
        lblEstado.setText("Disponible");
        lblEstado.getStyleClass().setAll("card-status-available");

        // El botón está habilitado por defecto
        btnReservar.setDisable(false);
    }

    /**
     * Paso C: El botón de la tarjeta llama a este método (dentro de este controlador).
     */
    @FXML
    private void handleReservarLibro(ActionEvent event) {
        System.out.println("Reservar libro ID: " + libro.getId() + " - Título: " + libro.getTitulo());

        // Aquí iría tu lógica de negocio (ej. libroService.reservar(libro.getId()))
        // ...

        // Mostrar notificación Toast
        try {
            Stage stage = StageManager.getPrimaryStage();
            if (stage != null) {
                Toast.showToast(stage, "Reservado (Simulación): " + libro.getTitulo(), 3000, stage.getWidth() / 2 - 150, stage.getHeight() / 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Simulación: Cambiamos la UI de esta tarjeta
        lblEstado.setText("Reservado");
        lblEstado.getStyleClass().setAll("card-status-reserved"); // Asumiendo que tienes este CSS
        btnReservar.setDisable(true);
        btnReservar.setText("Reservado");

        // Paso D: Notificar al HomeContentController que los datos cambiaron
        // (para que pueda recargar la lista si otro usuario ve la misma pantalla)
        // if (onDataChangedCallback != null) {
        //     onDataChangedCallback.run();
        // }
    }
}