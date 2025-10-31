package pe.edu.upeu.athenium.usuario.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.athenium.common.components.StageManager;
import pe.edu.upeu.athenium.common.components.Toast;
import pe.edu.upeu.athenium.common.dto.SessionManager;
import pe.edu.upeu.athenium.perfil.entity.Perfil;
import pe.edu.upeu.athenium.usuario.entity.Usuario;
import pe.edu.upeu.athenium.perfil.repository.PerfilRepository;
import pe.edu.upeu.athenium.usuario.service.IUsuarioService;

import java.io.IOException;

@Controller
public class LoginController {

    @Autowired private ApplicationContext context;
    @Autowired IUsuarioService us;
    @Autowired PerfilRepository perfilRepository;
    @Autowired private Environment env;

    // Campos de Login (Vista /view/login.fxml)
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtClave;
    @FXML private Label lblStatus;

    // Campos de Registro (Vista /view/register.fxml)
    @FXML private TextField txtNombreReg, txtApellidoReg, txtEmailReg;
    @FXML private PasswordField txtClaveReg, txtConfirmClaveReg;
    @FXML private Label lblStatusReg; // Nuevo Label para mensajes en Registro

    //---------------------------------------------------------
    //  MÉTODOS DE REDIRECCIÓN DE ESCENA (Separación Visual)
    //---------------------------------------------------------

    @FXML
    private void redirectToRegister(ActionEvent event) {
        redirectToScene("/view/pMenus/usr-authAccess/register.fxml", event);
    }

    @FXML
    private void redirectToLogin(ActionEvent event) {
        redirectToScene("/view/pMenus/usr-authAccess/login.fxml", event);
    }

    private void redirectToScene(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
            // Aseguramos la inyección de Spring
            fxmlLoader.setControllerFactory(context::getBean);
            Parent root = fxmlLoader.load();

            // Obtenemos el Stage actual
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene newScene = new Scene(root);

            // Añadimos el CSS (asumiendo que "styles.css" contiene el tema oscuro)
            java.net.URL cssUrl = getClass().getResource("/css/styles.css");
            if (cssUrl != null) newScene.getStylesheets().add(cssUrl.toExternalForm());

            stage.setScene(newScene);
            stage.sizeToScene();
            stage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Error al cargar la escena: " + fxmlPath);
            e.printStackTrace();
        }
    }


    //---------------------------------------------------------
    //  LÓGICA DE REGISTRO
    //---------------------------------------------------------

    @FXML
    private void handleRegister(ActionEvent event) {
        String nombre = txtNombreReg.getText();
        String apellido = txtApellidoReg.getText();
        String email = txtEmailReg.getText();
        String clave = txtClaveReg.getText();
        String confirmClave = txtConfirmClaveReg.getText();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || clave.isEmpty()) {
            setStatusMessageReg("Por favor, complete todos los campos.", true);
            return;
        }
        if (!clave.equals(confirmClave)) {
            setStatusMessageReg("Las contraseñas no coinciden.", true);
            return;
        }

        try {
            Perfil perfilDefecto = perfilRepository.findByNombre("ESTUDIANTE");
            if (perfilDefecto == null) {
                setStatusMessageReg("Error de configuración: Perfil 'ESTUDIANTE' no encontrado.", true);
                return;
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setApellido(apellido);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setPassword(clave);
            nuevoUsuario.setEstado("ACTIVO");
            nuevoUsuario.setPerfil(perfilDefecto);

            us.save(nuevoUsuario);

            setStatusMessageReg("Registro exitoso. Redirigiendo a Login.", false);
            limpiarCamposRegistro();

            // Redirigir automáticamente a Login después de un breve momento
            Platform.runLater(() -> redirectToLogin(event));

        } catch (DataIntegrityViolationException e) {
            setStatusMessageReg("El email ya está registrado.", true);
        } catch (Exception e) {
            setStatusMessageReg("Error inesperado: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void setStatusMessageReg(String message, boolean isError) {
        // Usa lblStatusReg para la vista de Registro
        if (lblStatusReg != null) {
            lblStatusReg.setText(message);
            // Uso de clases CSS para manejar el color (necesitas definir .label-status-error/success en tu CSS)
            lblStatusReg.getStyleClass().removeAll("label-status-error", "label-status-success");
            if (isError) {
                lblStatusReg.getStyleClass().add("label-status-error");
            } else {
                lblStatusReg.getStyleClass().add("label-status-success");
            }
        }
    }

    private void limpiarCamposRegistro() {
        if (txtNombreReg != null) txtNombreReg.clear();
        if (txtApellidoReg != null) txtApellidoReg.clear();
        if (txtEmailReg != null) txtEmailReg.clear();
        if (txtClaveReg != null) txtClaveReg.clear();
        if (txtConfirmClaveReg != null) txtConfirmClaveReg.clear();
    }


    //---------------------------------------------------------
    //  LÓGICA DE LOGIN
    //---------------------------------------------------------

    @FXML
    public void login(ActionEvent event) {
        System.out.println("[Login] Intentando autenticación para: " + txtEmail.getText());
        try {
            Usuario usu = us.loginUsuario(txtEmail.getText(), txtClave.getText());

            if (usu != null) {
                // Configurar sesión
                SessionManager.getInstance().setUserId(usu.getId());
                SessionManager.getInstance().setUserName(usu.getEmail());
                String perfilNombre = (usu.getPerfil() != null) ? usu.getPerfil().getNombre() : "";
                SessionManager.getInstance().setUserPerfil(perfilNombre);

                // Cargar Dashboard (Tu lógica existente se mantiene)
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getBounds();

                // Intento normal con controllerFactory (Inyección Spring)
                try {
                    java.net.URL res = getClass().getResource("/view/pMenus/mDashboard/mDashboard.fxml");
                    if (res == null) throw new IOException("Recurso /view/pMenus/mDashboard/mDashboard.fxml no encontrado");
                    FXMLLoader loader = new FXMLLoader(res);
                    loader.setControllerFactory(context::getBean);
                    Parent mainRoot = loader.load();

                    Scene mainScene = new Scene(mainRoot, bounds.getWidth(), bounds.getHeight() - 30);
                    java.net.URL cssUrl = getClass().getResource("/css/styles.css");
                    if (cssUrl != null) mainScene.getStylesheets().add(cssUrl.toExternalForm());

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    java.net.URL iconUrl = getClass().getResource("/img/store.png");
                    if (iconUrl != null) stage.getIcons().add(new Image(iconUrl.toExternalForm()));

                    stage.setScene(mainScene);
                    stage.setTitle("Athenium - Bienvenido " + usu.getNombre() + " " + usu.getApellido());
                    stage.setX(bounds.getMinX());
                    stage.setY(bounds.getMinY());
                    stage.setResizable(true);
                    StageManager.setPrimaryStage(stage);
                    stage.setWidth(bounds.getWidth());
                    stage.setHeight(bounds.getHeight());
                    stage.show();
                } catch (Exception fxEx) {
                    System.out.println("[Login] Error al cargar mDashboard.fxml: " + fxEx);
                    fxEx.printStackTrace();
                    // Fallback: Tu lógica de fallback simplificada
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Toast.showToast(stage, "Error al cargar dashboard: " + fxEx.getMessage(), 5000, stage.getWidth()/2, stage.getHeight()/2);
                }
            } else {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Toast.showToast(stage, "Credencial invalido!! intente nuevamente", 2000, stage.getWidth() * 2, stage.getHeight() / 2);
                setStatusMessage("Credencial inválida. Intente nuevamente.", true);
            }
        } catch (Exception e) {
            System.out.println("[Login] Excepción en login: " + e);
            e.printStackTrace();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Toast.showToast(stage, "Error inesperado: " + e.getMessage(), 5000, stage.getWidth()/2, stage.getHeight()/2);
            setStatusMessage("Error inesperado: " + e.getMessage(), true);
        }
    }

    private void setStatusMessage(String message, boolean isError) {
        // Usa lblStatus para la vista de Login
        if (lblStatus != null) {
            lblStatus.setText(message);
            lblStatus.getStyleClass().removeAll("label-status-error", "label-status-success");
            if (isError) {
                lblStatus.getStyleClass().add("label-status-error");
            } else {
                lblStatus.getStyleClass().add("label-status-success");
            }
        }
    }


    //---------------------------------------------------------
    //  NUEVOS MÉTODOS DEL DISEÑO DARK
    //---------------------------------------------------------

    @FXML
    private void handleForgetPassword(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Toast.showToast(stage, "Funcionalidad 'Olvidé mi contraseña' no implementada.", 3000, stage.getWidth()/2, stage.getHeight()/2);
    }

    @FXML
    private void loginAdmin(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Toast.showToast(stage, "Funcionalidad 'Admin Login' no implementada.", 3000, stage.getWidth()/2, stage.getHeight()/2);
    }

    @FXML
    public void cerrar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        Platform.exit();
        System.exit(0);
    }
}