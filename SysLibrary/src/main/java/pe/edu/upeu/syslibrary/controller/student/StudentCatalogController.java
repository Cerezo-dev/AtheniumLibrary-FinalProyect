package pe.edu.upeu.syslibrary.controller.student;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.dto.SessionManager;
import pe.edu.upeu.syslibrary.model.Ejemplar;
import pe.edu.upeu.syslibrary.model.Libro;
import pe.edu.upeu.syslibrary.model.Prestamo; // Importar
import pe.edu.upeu.syslibrary.repositorio.EjemplarRepository;
import pe.edu.upeu.syslibrary.repositorio.LibroRepository;
import pe.edu.upeu.syslibrary.service.EmailService; // Importar
import pe.edu.upeu.syslibrary.service.PrestamoService;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Controller
public class StudentCatalogController {

    @Autowired private LibroRepository libroRepository;
    @Autowired private EjemplarRepository ejemplarRepository;
    @Autowired private PrestamoService prestamoService;

    // CORRECCIÓN 1: Inyectar EmailService que faltaba
    @Autowired private EmailService emailService;

    @FXML private FlowPane booksContainer;
    @FXML private TextField txtSearch;

    @FXML
    public void initialize() {
        cargarLibrosLocales("");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        cargarLibrosLocales(txtSearch.getText().trim());
    }

    private void cargarLibrosLocales(String query) {
        booksContainer.getChildren().clear();
        List<Libro> libros;

        if (query.isEmpty()) {
            libros = libroRepository.findAll();
        } else {
            libros = libroRepository.findByTituloContainingIgnoreCase(query);
        }

        if (libros.isEmpty()) {
            booksContainer.getChildren().add(new Label("No se encontraron libros en la biblioteca."));
        } else {
            for (Libro libro : libros) {
                booksContainer.getChildren().add(crearTarjetaLibroLocal(libro));
            }
        }
    }

    private VBox crearTarjetaLibroLocal(Libro libro) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2); -fx-padding: 10; -fx-pref-width: 180; -fx-pref-height: 320;");
        card.setSpacing(10);
        card.setAlignment(Pos.TOP_CENTER);

        StackPane imgContainer = new StackPane();
        imgContainer.setStyle("-fx-background-color: #eee; -fx-pref-height: 200;");
        try {
            String ruta = libro.getUrlPortada();
            if(ruta != null && !ruta.startsWith("http")) ruta = Paths.get(ruta).toUri().toString();

            ImageView imgView = new ImageView(new Image(ruta != null ? ruta : "", true));
            imgView.setFitHeight(200); imgView.setFitWidth(140); imgView.setPreserveRatio(true);
            imgContainer.getChildren().add(imgView);
        } catch (Exception e) { imgContainer.getChildren().add(new Label("Sin Imagen")); }

        Label lblTitulo = new Label(libro.getTitulo());
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        lblTitulo.setWrapText(true);

        Button btnAccion = new Button("Prestar");
        btnAccion.setMaxWidth(Double.MAX_VALUE);
        btnAccion.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-cursor: hand;");

        // CORRECCIÓN 2: Ahora llama al método correcto
        btnAccion.setOnAction(e -> procesarPrestamo(libro));

        card.getChildren().addAll(imgContainer, lblTitulo, btnAccion);
        return card;
    }

    // CORRECCIÓN 3: Método renombrado y completado correctamente
    private void procesarPrestamo(Libro libro) {
        Optional<Ejemplar> ejemplarOpt = ejemplarRepository.findByLibroAndEstado(libro, pe.edu.upeu.syslibrary.enums.EstadoEjemplar.DISPONIBLE).stream().findFirst();

        if (ejemplarOpt.isPresent()) {
            try {
                Long idUsuario = SessionManager.getInstance().getUserId();
                Prestamo prestamo = prestamoService.registrarPrestamo(ejemplarOpt.get().getIdEjemplar(), idUsuario);

                // Enviar correo (ahora emailService está inyectado)
                CompletableFuture.runAsync(() -> emailService.enviarNotificacionPrestamo(prestamo));

                mostrarAlerta("¡Éxito!", "Préstamo registrado. Revisa tu correo.", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                mostrarAlerta("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Agotado", "No hay ejemplares disponibles.", Alert.AlertType.WARNING);
        }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo); alert.setTitle(titulo); alert.setHeaderText(null); alert.setContentText(contenido); alert.showAndWait();
    }
}