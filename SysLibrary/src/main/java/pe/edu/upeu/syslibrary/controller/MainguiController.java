package pe.edu.upeu.syslibrary.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.dto.SessionManager;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

@Controller
public class MainguiController {

    @FXML
    private BorderPane mainRoot;
    @FXML
    private VBox sidebar;
    @FXML
    private VBox contentArea;
    @FXML
    private Label lblDashboardTitle;
    @FXML
    private Label lblUserGreeting;

    @Autowired
    private ApplicationContext applicationContext;

    private boolean darkMode = false;
    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        // Muestra el nombre del usuario logueado desde SessionManager
        lblUserGreeting.setText("Bienvenido, " + SessionManager.getInstance().getUserName());
        showDashboard(null);
    }

    /**
     * Carga un archivo FXML desde resources y configura la inyección de controladores con Spring.
     */
    private Parent loadFxml(String fxmlPath) {
        try {
            Resource resource = applicationContext.getResource("classpath:" + fxmlPath);

            if (!resource.exists()) {
                throw new IOException("No se encontró el recurso FXML: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(resource.getURL());
            loader.setControllerFactory(applicationContext::getBean);
            return loader.load();

        } catch (IOException e) {
            System.err.println("Error al cargar FXML: " + fxmlPath);
            e.printStackTrace();
            return new Label("Error al cargar vista: " + fxmlPath);
        }
    }

    /**
     * Cambia el contenido central del panel principal.
     */
    private void switchView(String title, String fxmlPath) {
        lblDashboardTitle.setText(title);
        Parent view = loadFxml(fxmlPath);

        contentArea.getChildren().removeIf(node -> node != contentArea.getChildren().get(0));
        contentArea.getChildren().add(view);

        VBox.setVgrow(view, Priority.ALWAYS);
        System.out.println("Vista mostrada: " + title);
    }

    // --- MÉTODOS DE NAVEGACIÓN ---

    @FXML
    private void showDashboard(ActionEvent event) {
        switchView("Panel Principal", "/fxml/dashboard_view.fxml");
    }

    @FXML
    void showLibros(ActionEvent event) {
        switchView("Gestión de Libros", "/fxml/libro.fxml");
    }

    @FXML
    private void showExemplars(ActionEvent event) {
        switchView("Gestión de Ejemplares", "/fxml/exemplar_view.fxml");
    }

    @FXML
    private void showCategories(ActionEvent event) {
        switchView("Categorías", "/fxml/category_view.fxml");
    }

    @FXML
    void showAddUser(ActionEvent event) {
        switchView("Gestión de Usuarios", "/fxml/user_view.fxml");
    }

    @FXML
    void showIssueBook(ActionEvent event) {
        switchView("Gestión de Préstamos", "/fxml/issue_book_view.fxml");
    }

    @FXML
    private void showReturnBook(ActionEvent event) {
        switchView("Gestión de Devoluciones", "/fxml/return_book_view.fxml");
    }

    @FXML
    private void showReports(ActionEvent event) {
        switchView("Reportes del Sistema", "/fxml/reports_view.fxml");
    }

    @FXML
    private void showSettings(ActionEvent event) {
        switchView("Configuración del Sistema", "/fxml/settings_view.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Cerrando sesión...");
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        stage.close();
    }

    public void showBookRegistrationForm() {
        switchView("Registro de Libro", "/fxml/book_registration_form.fxml");
    }

    // --- CAMBIO DE TEMA ---
    @FXML
    private void toggleTheme(ActionEvent event) {
        String css = darkMode ? "/css/light-theme.css" : "/css/dark-theme.css";
        mainRoot.getScene().getStylesheets().clear();
        mainRoot.getScene().getStylesheets().add(getClass().getResource(css).toExternalForm());
        darkMode = !darkMode;
    }

    // --- CAMBIO DE IDIOMA ---
    public void setIdioma(Locale locale) {
        bundle = ResourceBundle.getBundle("i18n.messages", locale);
        lblDashboardTitle.setText(bundle.getString("menu.libros"));
    }

    @FXML
    void changeLanguageToEnglish(ActionEvent e) {
        setIdioma(Locale.ENGLISH);
    }
}
