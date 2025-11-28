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
import pe.edu.upeu.syslibrary.model.Usuario;
import pe.edu.upeu.syslibrary.repositorio.UsuarioRepository;
import pe.edu.upeu.syslibrary.service.GoogleBooksService;
import pe.edu.upeu.syslibrary.service.SolicitudLibroService; // Necesitas crear este servicio

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
public class StudentDiscoverController {

    @Autowired private GoogleBooksService googleService;
    @Autowired private SolicitudLibroService solicitudService;
    @Autowired private UsuarioRepository usuarioRepository;

    @FXML private TextField txtSearch;
    @FXML private FlowPane booksContainer;

    @FXML
    private void handleSearch(ActionEvent event) {
        String query = txtSearch.getText().trim();
        if(!query.isEmpty()) buscarEnGoogle(query);
    }

    private void buscarEnGoogle(String query) {
        booksContainer.getChildren().clear();
        booksContainer.getChildren().add(new Label("Buscando en Google Books..."));

        CompletableFuture.runAsync(() -> {
            List<GoogleBooksService.GoogleBookDto> libros = googleService.buscarLibros(query);
            Platform.runLater(() -> mostrarResultados(libros));
        });
    }

    private void mostrarResultados(List<GoogleBooksService.GoogleBookDto> libros) {
        booksContainer.getChildren().clear();
        for(GoogleBooksService.GoogleBookDto libro : libros) {
            booksContainer.getChildren().add(crearTarjetaGoogle(libro));
        }
    }

    private VBox crearTarjetaGoogle(GoogleBooksService.GoogleBookDto libro) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2); -fx-padding: 10; -fx-pref-width: 180;");
        card.setSpacing(10);
        card.setAlignment(Pos.TOP_CENTER);

        // Imagen (igual que antes) ...
        StackPane imgContainer = new StackPane();
        if (libro.getImagenUrl() != null) {
            ImageView img = new ImageView(new Image(libro.getImagenUrl(), true));
            img.setFitHeight(150); img.setFitWidth(100);
            imgContainer.getChildren().add(img);
        } else { imgContainer.getChildren().add(new Label("Sin img")); }

        Label lblTitulo = new Label(libro.getTitulo());
        lblTitulo.setWrapText(true);
        lblTitulo.setStyle("-fx-font-weight:bold;");

        // BOTÓN SOLICITAR (NO PRESTAR)
        Button btnSolicitar = new Button("Solicitar Adquisición");
        btnSolicitar.setMaxWidth(Double.MAX_VALUE);
        btnSolicitar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");

        btnSolicitar.setOnAction(e -> {
            Long userId = SessionManager.getInstance().getUserId();
            Usuario u = usuarioRepository.findById(userId).orElse(null);

            if(u != null) {
                solicitudService.registrarSolicitud(u, libro.getTitulo(), libro.getAutor(), libro.getIsbn(), libro.getImagenUrl());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "¡Solicitud enviada al administrador!");
                alert.show();
                btnSolicitar.setDisable(true);
                btnSolicitar.setText("Solicitado");
            }
        });

        card.getChildren().addAll(imgContainer, lblTitulo, btnSolicitar);
        return card;
    }
}