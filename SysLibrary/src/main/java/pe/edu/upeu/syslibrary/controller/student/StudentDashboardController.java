package pe.edu.upeu.syslibrary.controller.student;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.dto.SessionManager;

import java.io.IOException;

@Controller
public class StudentDashboardController {

    @Autowired private ApplicationContext context;

    @FXML private Label lblStudentName;
    @FXML private BorderPane mainContainer; // Asegúrate de que tu FXML tenga fx:id="mainContainer" en el BorderPane

    @FXML
    public void initialize() {
        String user = SessionManager.getInstance().getUserName();
        lblStudentName.setText(user != null ? user : "Estudiante");

        // Cargar Catálogo por defecto al iniciar
        showCatalog(null);
    }

    // --- MÉTODOS DE NAVEGACIÓN (BOTONES SUPERIORES) ---

    @FXML
    private void showCatalog(ActionEvent event) {
        cargarVista("/fxml/student/student_catalog.fxml");
    }

    @FXML
    private void showDiscover(ActionEvent event) {
        cargarVista("/fxml/student/student_discover.fxml");
    }

    @FXML
    private void showLoans(ActionEvent event) {
        cargarVista("/fxml/student/student_loans.fxml");
    }

    @FXML
    private void showFavorites(ActionEvent event) {
        // Placeholder hasta que crees student_favorites.fxml
        System.out.println("Favoritos próximamente...");
    }

    // --- MÉTODO CRÍTICO: CARGADOR DE VISTAS ---
    private void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(context::getBean);
            Parent view = loader.load();

            // Reemplaza el contenido del centro del BorderPane
            if (mainContainer != null) {
                mainContainer.setCenter(view);
            } else {
                System.err.println("Error: mainContainer es null. Verifica tu FXML.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error cargando vista: " + fxmlPath);
        }
    }

    // --- LOGOUT ---
    @FXML
    private void logout(ActionEvent event) {
        try {
            SessionManager.getInstance().setUserId(null);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- MÉTODOS LEGACY (PARA EVITAR ERRORES DE FXML VIEJOS) ---
    // Si tu FXML aún llama a handleSearch, lo dejamos vacío para que no rompa,
    // pero la lógica real ahora vive en StudentCatalogController.
    @FXML
    private void handleSearch(ActionEvent event) {
        // La búsqueda ahora se maneja dentro de las vistas hijas (Catalog/Discover)
    }
}