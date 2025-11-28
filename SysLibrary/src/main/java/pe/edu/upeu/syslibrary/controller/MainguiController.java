package pe.edu.upeu.syslibrary.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MainguiController {

    // --- ELEMENTOS ESTRUCTURALES ---
    @FXML private BorderPane mainRoot;
    @FXML private VBox sidebar;
    @FXML private VBox contentArea;
    @FXML private Label lblPageTitle;
    @FXML private Label lblUserGreeting;
    @FXML private ImageView sidebarToggleIcon;

    // 🔵 NUEVOS IDs INYECTADOS (Fusionados con la versión de tu compañero)
    // Se necesitan para controlar quien ve qué botón
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

    private boolean isSidebarExpanded = true;
    private final double SIDEBAR_EXPANDED_WIDTH = 255.0;
    private final double SIDEBAR_COLLAPSED_WIDTH = 60.0;
    private final Map<Button, String> originalButtonTexts = new HashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    public void initialize() {
        // Carga nombre de usuario (Manejo de null por seguridad)
        String userName = (SessionManager.getInstance() != null) ? SessionManager.getInstance().getUserName() : "Admin";
        lblUserGreeting.setText(userName);

        storeOriginalButtonTexts();

        // 🔵 NUEVO: Ejecutar lógica de permisos RBAC
        aplicarPermisosRol();

        // Cargar dashboard inicial al arrancar
        Platform.runLater(() -> {
            showDashboard(null);
        });
    }

    // 🔵 LOGICA DE PERMISOS
    private void aplicarPermisosRol() {
        String rol = (SessionManager.getInstance() != null) ? SessionManager.getInstance().getNombrePerfil() : "INVITADO";
        System.out.println("✅ Login detectado. Rol: " + rol);

        switch (rol) {
            case "ESTUDIANTE":
                // Estudiante no ve administración, config, ni reportes
                ocultarBoton(btnUsuariosAdmin);
                ocultarBoton(btnConfiguracion);
                ocultarBoton(btnReportes);
                ocultarBoton(btnEstudiantes);
                ocultarBoton(btnDevoluciones);
                ocultarBoton(btnCategorias);
                ocultarBoton(btnEjemplares);
                break;

            case "DOCENTE":
                ocultarBoton(btnUsuariosAdmin);
                ocultarBoton(btnConfiguracion);
                ocultarBoton(btnReportes);
                ocultarBoton(btnDevoluciones);
                break;

            case "BIBLIOTECARIO":
                // Bibliotecario ve casi todo menos crear Admins
                ocultarBoton(btnUsuariosAdmin);
                ocultarBoton(btnConfiguracion);
                break;

            case "ADMINISTRADOR":
                // Ve todo
                break;

            default:
                // Seguridad por defecto: bloquear cosas sensibles
                ocultarBoton(btnUsuariosAdmin);
                ocultarBoton(btnConfiguracion);
                ocultarBoton(btnReportes);
                break;
        }
    }

    // 🔵 Helper para ocultar botones y colapsar el espacio
    private void ocultarBoton(Button btn) {
        if (btn != null) {
            btn.setVisible(false);
            btn.setManaged(false); // Importante: Elimina el hueco blanco
        }
    }

    private void storeOriginalButtonTexts() {
        for (Node node : sidebar.getChildren()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                if (button.getGraphic() != null && !button.getText().trim().isEmpty()) {
                    originalButtonTexts.put(button, button.getText());
                }
            }
        }
    }

    // Método genérico para cargar FXML
    private Parent loadFxml(String fxmlPath) {
        try {
            Resource resource = applicationContext.getResource("classpath:" + fxmlPath);
            if (!resource.exists()) {
                System.err.println("❌ ERROR CRÍTICO: No se encontró el archivo FXML: " + fxmlPath);
                return new Label("Error: Archivo no encontrado (" + fxmlPath + ")");
            }

            FXMLLoader loader = new FXMLLoader(resource.getURL());
            loader.setControllerFactory(applicationContext::getBean);
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Error de carga: " + e.getMessage());
        }
    }

    // Método central para cambiar vistas
    private void switchView(String title, String fxmlPath) {
        if (lblPageTitle != null) {
            lblPageTitle.setText(title);
        }

        Parent view = loadFxml(fxmlPath);

        if (contentArea.getChildren().size() > 1) {
            contentArea.getChildren().remove(1);
        }

        contentArea.getChildren().add(view);
        VBox.setVgrow(view, Priority.ALWAYS);
    }

    // --- ACCIONES DEL MENÚ ---

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
    private void showIssueBook(ActionEvent event) {
        switchView("Nuevo Préstamo", "/fxml/prestamoLibro.fxml");
    }

    @FXML
    private void showReturnBooks(ActionEvent event) {
        switchView("Devolución de Libros", "/fxml/devolucion_libro.fxml");
    }

    @FXML
    private void showStudents(ActionEvent event) {
        switchView("Gestión de Estudiantes", "/fxml/usuarioMain.fxml");
    }

    @FXML
    void showAddUser(ActionEvent event) {
        switchView("Gestión de Usuarios", "/fxml/user_view.fxml");
    }

    @FXML
    private void showReports(ActionEvent event) {
        switchView("Reportes", "/fxml/dashboard_view.fxml");
    }

    @FXML
    private void showSettings(ActionEvent event) {
        switchView("Configuración", "/fxml/dashboard_view.fxml");
    }

    // --- FUNCIONALIDAD BARRA LATERAL ---

    @FXML
    public void toggleSidebar() {
        if (isSidebarExpanded) {
            sidebar.getStyleClass().add("sidebar-collapsed");
            sidebar.setPrefWidth(SIDEBAR_COLLAPSED_WIDTH);
            sidebar.setMinWidth(SIDEBAR_COLLAPSED_WIDTH);
            sidebar.setMaxWidth(SIDEBAR_COLLAPSED_WIDTH);
            setButtonsTextVisibility(false);
            try {
                // Iconos opcionales
            } catch (Exception e) {}
        } else {
            sidebar.getStyleClass().remove("sidebar-collapsed");
            sidebar.setPrefWidth(SIDEBAR_EXPANDED_WIDTH);
            sidebar.setMinWidth(SIDEBAR_EXPANDED_WIDTH);
            sidebar.setMaxWidth(SIDEBAR_EXPANDED_WIDTH);
            setButtonsTextVisibility(true);
            try {
                // Iconos opcionales
            } catch (Exception e) {}
        }
        isSidebarExpanded = !isSidebarExpanded;
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

    @FXML
    private void handleLogout(ActionEvent event) {
        Stage stage = (Stage) mainRoot.getScene().getWindow();
        stage.close();
    }
}