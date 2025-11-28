package pe.edu.upeu.syslibrary.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import pe.edu.upeu.syslibrary.dto.SessionManager;
import pe.edu.upeu.syslibrary.model.Usuario;
import pe.edu.upeu.syslibrary.service.IUsuarioService;

import java.io.IOException;

@Controller
public class LoginController {

    @Autowired private ApplicationContext context;
    @Autowired private IUsuarioService usuarioService;

    // Campos de Login
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtClave;
    @FXML private Label lblStatus;

    // Campos de Registro
    @FXML private TextField txtNombreReg, txtApellidoReg, txtEmailReg;
    @FXML private PasswordField txtClaveReg, txtConfirmClaveReg;

    // Selector de Rol (Administrador / Bibliotecario)
    @FXML private ComboBox<String> cbxRolReg;
    @FXML private Label lblStatusReg;

    @FXML
    public void initialize() {
        if (cbxRolReg != null) {
            cbxRolReg.setItems(FXCollections.observableArrayList("ADMINISTRADOR", "BIBLIOTECARIO"));
            cbxRolReg.getSelectionModel().selectFirst();
        }
    }

    // --- NAVEGACIÓN ---

    @FXML
    private void redirectToRegister(ActionEvent event) {
        redirectToScene("/fxml/register.fxml", event);
    }

    @FXML
    private void redirectToLogin(ActionEvent event) {
        redirectToScene("/fxml/login.fxml", event);
    }

    // 🔵 MÉTODO CORREGIDO: TAMAÑO DINÁMICO
    private void redirectToScene(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();

            // Obtenemos el Stage actual
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Ocultamos brevemente para evitar parpadeos visuales al cambiar de tamaño
            stage.hide();

            stage.setScene(new Scene(root));

            if (fxmlPath.contains("maingui_view")) {
                // --- MODO DASHBOARD (Pantalla Completa) ---
                stage.setTitle("SysLibrary - Panel Principal");
                stage.setResizable(true);

                // Tamaño base "Restaurado"
                stage.setWidth(1280);
                stage.setHeight(800);
                stage.centerOnScreen();

                // Aplicar Maximizado
                stage.setMaximized(true);

            } else {
                // --- MODO LOGIN / REGISTRO (Dinámico) ---
                stage.setTitle("SysLibrary - Acceso");
                stage.setResizable(false);
                stage.setMaximized(false);

                // 🔵 CAMBIO CLAVE: sizeToScene()
                // Esto ajusta la ventana automáticamente al tamaño definido en el FXML
                // Si login.fxml dice 600px, usará 600px. Si register.fxml dice 700px, usará 700px.
                stage.sizeToScene();
                stage.centerOnScreen();
            }

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la vista: " + fxmlPath);
        }
    }

    // --- LÓGICA DE LOGIN ---

    @FXML
    public void login(ActionEvent event) {
        if (txtEmail.getText().isEmpty() || txtClave.getText().isEmpty()) {
            mostrarAlerta("Error", "Por favor ingrese sus credenciales");
            return;
        }

        try {
            Usuario usu = usuarioService.loginUsuario(txtEmail.getText(), txtClave.getText());

            if (usu != null) {
                // 1. LLENAR EL SESSION MANAGER
                SessionManager.getInstance().setUserId(usu.getIdUsuario());
                SessionManager.getInstance().setUserName(usu.getNombre() + " " + usu.getApellidos());

                String perfil = (usu.getPerfil() != null) ? usu.getPerfil().getNombre() : "INVITADO";
                SessionManager.getInstance().setUserPerfil(perfil);

                System.out.println("Login exitoso. Rol: " + perfil);

                // 2. REDIRIGIR AL DASHBOARD
                redirectToScene("/fxml/maingui_view.fxml", event);

            } else {
                mostrarAlerta("Error de Acceso", "Usuario o contraseña incorrectos");
                if(lblStatus != null) lblStatus.setText("Credenciales inválidas");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error del Sistema", e.getMessage());
        }
    }

    // --- LÓGICA DE REGISTRO ---

    @FXML
    private void handleRegister(ActionEvent event) {
        // Validar campos vacíos incluyendo el Rol
        if (txtNombreReg.getText().isEmpty() || txtEmailReg.getText().isEmpty() || cbxRolReg.getValue() == null) {
            mostrarAlerta("Error", "Complete los campos obligatorios");
            return;
        }

        if (!txtClaveReg.getText().equals(txtConfirmClaveReg.getText())) {
            mostrarAlerta("Error", "Las contraseñas no coinciden");
            return;
        }

        try {
            String rolSeleccionado = cbxRolReg.getValue();

            usuarioService.registrarNuevoUsuario(
                    txtNombreReg.getText(),
                    txtApellidoReg.getText(),
                    txtEmailReg.getText(),
                    txtClaveReg.getText(),
                    rolSeleccionado
            );

            mostrarAlerta("Éxito", "Usuario registrado como " + rolSeleccionado);
            redirectToLogin(event);

        } catch (Exception e) {
            mostrarAlerta("Error al registrar", e.getMessage());
            e.printStackTrace();
        }
    }

    // --- OTROS MÉTODOS ---

    @FXML
    private void handleForgetPassword(ActionEvent event) {
        mostrarAlerta("Información", "Opción 'Olvidé mi contraseña' aún no implementada.");
    }

    @FXML
    private void loginStudent(ActionEvent event) {
        redirectToScene("/fxml/student/login_student.fxml", event);
    }

    // --- UTILITARIOS ---

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void cerrar(ActionEvent event) {
        Platform.exit();
    }
}