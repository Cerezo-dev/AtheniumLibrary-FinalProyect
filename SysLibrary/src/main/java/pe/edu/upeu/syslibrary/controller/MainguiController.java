package pe.edu.upeu.syslibrary.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
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
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainguiController {

    @Autowired
    private ApplicationContext applicationContext;

    // --- ELEMENTOS ESTRUCTURALES FXML ---
    @FXML private BorderPane mainRoot;
    @FXML private VBox sidebar;
    @FXML private VBox contentArea; // Área donde cargan las vistas hijas
    @FXML private Label lblPageTitle;
    @FXML private Label lblUserGreeting;
    @FXML private ImageView sidebarToggleIcon;

    // --- BOTONES DEL MENÚ (IDs deben coincidir con tu FXML) ---
    @FXML private Button btnDashboard;
    @FXML private Button btnLibros;
    @FXML private Button btnCategorias;
    @FXML private Button btnEjemplares;
    @FXML private Button btnPrestamos;
    @FXML private Button btnDevoluciones;
    @FXML private Button btnEstudiantes;
    @FXML private Button btnUsuariosAdmin;
    @FXML private Button btnReportes;
    @FXML private Button btnConfiguracion;

    // --- VARIABLES DE ESTADO ---
    private boolean isSidebarExpanded = true;
    private final double SIDEBAR_EXPANDED_WIDTH = 255.0;
    private final double SIDEBAR_COLLAPSED_WIDTH = 60.0;
    private final Map<Button, String> originalButtonTexts = new HashMap<>();

    @FXML
    public void initialize() {
        // 1. Cargar Usuario de la Sesión
        String userName = (SessionManager.getInstance() != null) ? SessionManager.getInstance().getUserName() : "Admin Local";
        lblUserGreeting.setText(userName);

        // 2. Guardar textos originales de los botones para poder colapsar el menú luego
        storeOriginalButtonTexts();

        // 3. Aplicar Permisos (Quién ve qué botón)
        aplicarPermisosRol();

        // 4. Cargar Dashboard inicial
        Platform.runLater(() -> showDashboard(null));
    }

    // --- LÓGICA DE ROLES (RBAC) ---
    private void aplicarPermisosRol() {
        String rol = (SessionManager.getInstance() != null) ? SessionManager.getInstance().getNombrePerfil() : "INVITADO";
        System.out.println("✅ Login detectado. Rol: " + rol);

        switch (rol) {
            case "ESTUDIANTE":
                // El estudiante solo ve sus cosas (o quizás nada del panel admin)
                ocultarBoton(btnUsuariosAdmin);
                ocultarBoton(btnConfiguracion);
                ocultarBoton(btnReportes);
                ocultarBoton(btnEstudiantes);
                ocultarBoton(btnDevoluciones);
                ocultarBoton(btnCategorias);
                ocultarBoton(btnEjemplares);
                ocultarBoton(btnPrestamos); // Un estudiante no se presta a sí mismo en el sistema admin
                break;

            case "DOCENTE":
                ocultarBoton(btnUsuariosAdmin);
                ocultarBoton(btnConfiguracion);
                ocultarBoton(btnReportes);
                ocultarBoton(btnDevoluciones);
                break;

            case "BIBLIOTECARIO":
                // Ve todo excepto gestión de super-usuarios y config del sistema
                ocultarBoton(btnUsuariosAdmin);
                ocultarBoton(btnConfiguracion);
                break;

            case "ADMINISTRADOR":
                // Ve todo
                break;

            default:
                // Por seguridad, ocultar lo sensible
                ocultarBoton(btnUsuariosAdmin);
                ocultarBoton(btnConfiguracion);
                break;
        }
    }

    private void ocultarBoton(Button btn) {
        if (btn != null) {
            btn.setVisible(false);
            btn.setManaged(false); // Importante: Elimina el espacio blanco que dejaría el botón
        }
    }

    // --- NAVEGACIÓN ---

    private void switchView(String title, String fxmlPath) {
        if (lblPageTitle != null) lblPageTitle.setText(title);

        try {
            Resource resource = applicationContext.getResource("classpath:" + fxmlPath);
            if (!resource.exists()) {
                System.err.println("❌ ERROR: No se encuentra FXML: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource.getURL());
            loader.setControllerFactory(applicationContext::getBean);
            Parent view = loader.load();

            contentArea.getChildren().clear(); // Limpia la vista anterior
            contentArea.getChildren().add(view);
            VBox.setVgrow(view, Priority.ALWAYS); // Que ocupe todo el espacio

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error cargando vista: " + e.getMessage());
        }
    }

    // --- EVENTOS DE BOTONES (Rutas FXML ajustadas) ---

    @FXML private void showDashboard(ActionEvent event) {
        // Asegúrate de tener dashboard_view.fxml o dashboard_admin.fxml
        switchView("Panel Principal", "/fxml/dashboard_view.fxml");
    }

    @FXML private void showLibros(ActionEvent event) {
        switchView("Gestión de Libros", "/fxml/libro.fxml");
    }

    @FXML private void showExemplars(ActionEvent event) {
        switchView("Gestión de Ejemplares", "/fxml/exemplar_view.fxml");
    }

    @FXML private void showCategories(ActionEvent event) {
        switchView("Categorías", "/fxml/category_view.fxml");
    }

    @FXML private void showIssueBook(ActionEvent event) {
        // Esta es la vista que usa el controlador PrestamoLibroController
        switchView("Registro de Préstamos", "/fxml/prestamoLibro.fxml");
    }

    @FXML private void showReturnBooks(ActionEvent event) {
        switchView("Devolución de Libros", "/fxml/devolucion_libro.fxml");
    }

    @FXML private void showStudents(ActionEvent event) {
        switchView("Gestión de Estudiantes", "/fxml/usuarioMain.fxml");
    }

    @FXML void showAddUser(ActionEvent event) {
        switchView("Gestión de Usuarios Admin", "/fxml/user_view.fxml");
    }

    @FXML private void showReports(ActionEvent event) {
        switchView("Reportes", "/fxml/dashboard_view.fxml"); // Placeholder
    }

    @FXML private void showSettings(ActionEvent event) {
        switchView("Configuración", "/fxml/dashboard_view.fxml"); // Placeholder
    }

    @FXML private void handleLogout(ActionEvent event) {
        // Lógica para cerrar la ventana actual y abrir Login
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        stage.close();
        // Aquí podrías reabrir el LoginController
    }

    // --- LÓGICA VISUAL DEL SIDEBAR (Colapsar/Expandir) ---

    @FXML
    public void toggleSidebar() {
        if (isSidebarExpanded) {
            sidebar.setPrefWidth(SIDEBAR_COLLAPSED_WIDTH);
            sidebar.setMinWidth(SIDEBAR_COLLAPSED_WIDTH);
            sidebar.setMaxWidth(SIDEBAR_COLLAPSED_WIDTH);
            setButtonsTextVisibility(false);
            sidebar.getStyleClass().add("sidebar-collapsed");
        } else {
            sidebar.setPrefWidth(SIDEBAR_EXPANDED_WIDTH);
            sidebar.setMinWidth(SIDEBAR_EXPANDED_WIDTH);
            sidebar.setMaxWidth(SIDEBAR_EXPANDED_WIDTH);
            setButtonsTextVisibility(true);
            sidebar.getStyleClass().remove("sidebar-collapsed");
        }
        isSidebarExpanded = !isSidebarExpanded;
    }

    private void storeOriginalButtonTexts() {
        for (Node node : sidebar.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                if (!button.getText().trim().isEmpty()) {
                    originalButtonTexts.put(button, button.getText());
                }
            }
        }
    }

    private void setButtonsTextVisibility(boolean visible) {
        for (Node node : sidebar.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                if (originalButtonTexts.containsKey(button)) {
                    button.setText(visible ? originalButtonTexts.get(button) : "");
                }
            }
        }
    }
}