package pe.edu.upeu.syslibrary.controller.student;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.dto.SessionManager;
import pe.edu.upeu.syslibrary.model.Ejemplar;
import pe.edu.upeu.syslibrary.model.Libro;
import pe.edu.upeu.syslibrary.repositorio.EjemplarRepository;
import pe.edu.upeu.syslibrary.repositorio.LibroRepository;
import pe.edu.upeu.syslibrary.service.GoogleBooksService;
import pe.edu.upeu.syslibrary.service.PrestamoService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import pe.edu.upeu.syslibrary.model.Prestamo; // Importar Modelo Prestamo
import pe.edu.upeu.syslibrary.service.EmailService; // Importar EmailService

@Controller
public class StudentDashboardController {

    @Autowired private ApplicationContext context;
    @Autowired private GoogleBooksService googleService;
    @Autowired private LibroRepository libroRepository;     // Para buscar en BD Local
    @Autowired private EjemplarRepository ejemplarRepository; // Para buscar stock
    @Autowired private PrestamoService prestamoService;
    @Autowired private EmailService emailService;

    @FXML private Label lblStudentName;
    @FXML private FlowPane booksContainer; // El contenedor de tarjetas
    @FXML private TextField txtSearch;     // Necesitas agregar fx:id="txtSearch" en el FXML

    @FXML
    public void initialize() {
        String user = SessionManager.getInstance().getUserName();
        lblStudentName.setText(user != null ? user : "Estudiante");

        // Cargar libros por defecto al iniciar (ej. "Programación")
        buscarLibrosEnGoogle("Technology");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String query = txtSearch.getText().trim();
        if (!query.isEmpty()) {
            buscarLibrosEnGoogle(query);
        }
    }

    private void buscarLibrosEnGoogle(String query) {
        // Ejecutamos en un hilo separado para no congelar la interfaz mientras descarga datos
        CompletableFuture.runAsync(() -> {
            List<GoogleBooksService.GoogleBookDto> libros = googleService.buscarLibros(query);

            // Volvemos al hilo de JavaFX para actualizar la UI
            Platform.runLater(() -> mostrarLibros(libros));
        });
    }

    private void mostrarLibros(List<GoogleBooksService.GoogleBookDto> libros) {
        booksContainer.getChildren().clear();

        for (GoogleBooksService.GoogleBookDto libroGoogle : libros) {
            // --- CREACIÓN DE LA TARJETA (CARD) ---
            VBox card = new VBox();
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2); -fx-padding: 10; -fx-pref-width: 180; -fx-pref-height: 320;");
            card.setSpacing(10);
            card.setAlignment(Pos.TOP_CENTER);

            // 1. Imagen
            StackPane imgContainer = new StackPane();
            imgContainer.setStyle("-fx-background-color: #eee; -fx-pref-height: 200;");

            if (libroGoogle.getImagenUrl() != null) {
                try {
                    ImageView imgView = new ImageView(new Image(libroGoogle.getImagenUrl(), true)); // true = background loading
                    imgView.setFitHeight(200);
                    imgView.setFitWidth(140);
                    imgView.setPreserveRatio(true);
                    imgContainer.getChildren().add(imgView);
                } catch (Exception e) {
                    imgContainer.getChildren().add(new Label("Sin Imagen"));
                }
            } else {
                imgContainer.getChildren().add(new Label("Sin Imagen"));
            }

            // 2. Título y Autor
            Label lblTitulo = new Label(libroGoogle.getTitulo());
            lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            lblTitulo.setWrapText(true);
            lblTitulo.setMaxHeight(40); // Limitar altura

            Label lblAutor = new Label(libroGoogle.getAutor());
            lblAutor.setStyle("-fx-text-fill: #777; -fx-font-size: 12px;");

            // 3. Botón de Acción
            Button btnAccion = new Button("Solicitar");
            btnAccion.setMaxWidth(Double.MAX_VALUE);

            // Lógica del Botón: Verificar si existe en NUESTRA biblioteca
            btnAccion.setOnAction(e -> procesarSolicitud(libroGoogle));

            // Estilos del botón
            btnAccion.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

            // Agregar todo a la tarjeta
            card.getChildren().addAll(imgContainer, lblTitulo, lblAutor, btnAccion);

            // Agregar tarjeta al panel principal
            booksContainer.getChildren().add(card);
        }
    }

    private void procesarSolicitud(GoogleBooksService.GoogleBookDto libroGoogle) {
        // AQUÍ CONECTAMOS LO VIRTUAL CON LO REAL

        // 1. Buscar en BD local por Título (o ISBN si lo tuviéramos limpio)
        // Usamos contains para ser flexibles
        List<Libro> librosEnBiblioteca = libroRepository.findByTituloContainingIgnoreCase(libroGoogle.getTitulo());

        if (librosEnBiblioteca.isEmpty()) {
            mostrarAlerta("No disponible", "Lo sentimos, este libro no está en el catálogo físico de la biblioteca.", Alert.AlertType.WARNING);
            return;
        }

        // 2. Verificamos si hay ejemplares disponibles del primer resultado coincidente
        Libro libroEncontrado = librosEnBiblioteca.get(0);

        // Buscamos un ejemplar DISPONIBLE
        Optional<Ejemplar> ejemplarOpt = ejemplarRepository.findByLibroAndEstado(libroEncontrado, pe.edu.upeu.syslibrary.enums.EstadoEjemplar.DISPONIBLE)
                .stream().findFirst();

        if (ejemplarOpt.isPresent()) {
            try {
                Long idUsuario = SessionManager.getInstance().getUserId();
                Ejemplar ejemplar = ejemplarOpt.get();

                // 1. REGISTRAR PRÉSTAMO Y OBTENER EL OBJETO CREADO
                // Asegúrate que tu PrestamoService.registrarPrestamo retorne el objeto 'Prestamo'
                Prestamo prestamoGenerado = prestamoService.registrarPrestamo(ejemplar.getIdEjemplar(), idUsuario);

                // 2. ENVIAR CORREO EN SEGUNDO PLANO (Async para no trabar la UI)
                CompletableFuture.runAsync(() -> {
                    emailService.enviarNotificacionPrestamo(prestamoGenerado);
                });

                mostrarAlerta("¡Éxito!", "Préstamo registrado. Hemos enviado los detalles a tu correo.", Alert.AlertType.INFORMATION);

            } catch (Exception ex) {
                mostrarAlerta("Error", "No se pudo procesar: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Agotado", "Tenemos el libro, pero todos los ejemplares están prestados actualmente.", Alert.AlertType.WARNING);
        }
    }

    // ... métodos de logout y navegación existentes ...

    @FXML
    private void logout(ActionEvent event) {
        // ... (tu código de logout existente) ...
        try {
            SessionManager.getInstance().setUserId(null);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}