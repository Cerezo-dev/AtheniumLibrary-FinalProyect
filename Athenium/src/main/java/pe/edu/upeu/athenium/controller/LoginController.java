package pe.edu.upeu.athenium.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.athenium.components.StageManager;
import pe.edu.upeu.athenium.components.Toast;
import pe.edu.upeu.athenium.dto.SessionManager;
import pe.edu.upeu.athenium.model.Perfil;
import pe.edu.upeu.athenium.model.Usuario;
import pe.edu.upeu.athenium.repository.PerfilRepository;
import pe.edu.upeu.athenium.service.IUsuarioService;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

@Controller
public class LoginController {

    @Autowired
    private ApplicationContext context;

    @Autowired
    IUsuarioService us;

    @Autowired
    PerfilRepository perfilRepository;

    @Autowired
    private Environment env;

    @FXML
    TextField txtEmail;
    @FXML
    PasswordField txtClave;
    @FXML
    Button btnIngresar;
    @FXML
    private VBox loginPane;
    @FXML
    private Label lblStatus;

    @FXML
    private VBox registerPane;

    @FXML
    private TextField txtNombreReg, txtApellidoReg, txtEmailReg;

    @FXML
    private PasswordField txtClaveReg, txtConfirmClaveReg;

    @FXML
    private void showRegisterPane(ActionEvent event) {
        loginPane.setVisible(false);
        loginPane.setManaged(false);
        registerPane.setVisible(true);
        registerPane.setManaged(true);
        lblStatus.setText("");
        lblStatus.setStyle("-fx-background-color: #f0f0f0;");
    }

    @FXML
    private void showLoginPane(ActionEvent event) {
        registerPane.setVisible(false);
        registerPane.setManaged(false);
        loginPane.setVisible(true);
        loginPane.setManaged(true);
        lblStatus.setText("");
        lblStatus.setStyle("-fx-background-color: #f0f0f0;");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String nombre = txtNombreReg.getText();
        String apellido = txtApellidoReg.getText();
        String email = txtEmailReg.getText();
        String clave = txtClaveReg.getText();
        String confirmClave = txtConfirmClaveReg.getText();

        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || clave.isEmpty()) {
            setStatusMessage("Por favor, complete todos los campos.", true);
            return;
        }

        if (!clave.equals(confirmClave)) {
            setStatusMessage("Las contraseñas no coinciden.", true);
            return;
        }

        try {
            Perfil perfilDefecto = perfilRepository.findByNombre("ESTUDIANTE");
            if (perfilDefecto == null) {
                setStatusMessage("Error de configuración: Perfil 'ESTUDIANTE' no encontrado.", true);
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

            setStatusMessage("Registro exitoso. Ahora puede iniciar sesión.", false);
            limpiarCamposRegistro();
            showLoginPane(null);
        } catch (DataIntegrityViolationException e) {
            setStatusMessage("El email ya está registrado.", true);
        } catch (Exception e) {
            setStatusMessage("Error inesperado: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    @FXML
    public void login(ActionEvent event) {
        System.out.println("[Login] Intentando autenticación para: " + txtEmail.getText());
        try {
            Usuario usu = us.loginUsuario(txtEmail.getText(), txtClave.getText());
            System.out.println("[Login] Resultado del servicio de login: " + (usu != null ? "OK id=" + usu.getId() : "NULL"));

            if (usu != null) {
                SessionManager.getInstance().setUserId(usu.getId());
                SessionManager.getInstance().setUserName(usu.getEmail());

                String perfilNombre = (usu.getPerfil() != null && usu.getPerfil().getNombre() != null) ? usu.getPerfil().getNombre() : "";
                SessionManager.getInstance().setUserPerfil(perfilNombre);

                java.net.URL dashboardUrl = getClass().getResource("/view/mDashboard.fxml");
                System.out.println("[Login] Intentando cargar FXML de dashboard en: " + dashboardUrl);

                // calcular bounds fuera del try para usar en fallback
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getBounds();

                // intento normal con controllerFactory (inyección Spring)
                boolean forceFallback = Boolean.parseBoolean(env.getProperty("athenium.ui.forceFallback", "false"));
                try {
                    java.net.URL res = getClass().getResource("/view/mDashboard.fxml");
                    if (res == null) throw new IOException("Recurso /view/mDashboard.fxml no encontrado en classpath");
                    FXMLLoader loader = new FXMLLoader(res);
                    if (!forceFallback) loader.setControllerFactory(context::getBean);
                    Parent mainRoot = loader.load();

                    Scene mainScene = new Scene(mainRoot, bounds.getWidth(), bounds.getHeight() - 30);
                    java.net.URL cssUrl = getClass().getResource("/css/styles.css");
                    if (cssUrl != null) mainScene.getStylesheets().add(cssUrl.toExternalForm()); else System.out.println("[Login] WARNING: styles.css no se encontró en classpath");

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    java.net.URL iconUrl = getClass().getResource("/img/store.png");
                    if (iconUrl != null) stage.getIcons().add(new Image(iconUrl.toExternalForm())); else System.out.println("[Login] WARNING: store.png no se encontró en classpath");

                    stage.setScene(mainScene);
                    stage.setTitle("Athenium Athenium - Bienvenido " + usu.getNombre() + " " + usu.getApellido());
                    stage.setX(bounds.getMinX());
                    stage.setY(bounds.getMinY());
                    stage.setResizable(true);
                    StageManager.setPrimaryStage(stage);
                    stage.setWidth(bounds.getWidth());
                    stage.setHeight(bounds.getHeight());
                    stage.show();
                } catch (Exception fxEx) {
                    System.out.println("[Login] Error al cargar mDashboard.fxml con controllerFactory: " + fxEx);
                    fxEx.printStackTrace();
                    // fallback: cargar sin controllerFactory para mostrar UI aunque inyección falle
                    try {
                        java.net.URL res = getClass().getResource("/view/mDashboard.fxml");
                        if (res == null) throw new IOException("Recurso /view/mDashboard.fxml no encontrado en classpath");
                        FXMLLoader fallbackLoader = new FXMLLoader(res);
                        Parent mainRootFallback = fallbackLoader.load();

                        Scene mainSceneFallback = new Scene(mainRootFallback, bounds.getWidth(), bounds.getHeight() - 30);
                        java.net.URL cssUrlFb = getClass().getResource("/css/styles.css");
                        if (cssUrlFb != null) mainSceneFallback.getStylesheets().add(cssUrlFb.toExternalForm()); else System.out.println("[Login] WARNING: styles.css no disponible para fallback");

                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        java.net.URL iconUrlFb = getClass().getResource("/img/store.png");
                        if (iconUrlFb != null) stage.getIcons().add(new Image(iconUrlFb.toExternalForm())); else System.out.println("[Login] WARNING: store.png no disponible para fallback");

                        stage.setScene(mainSceneFallback);
                        stage.setTitle("Athenium (modo fallback) - Bienvenido " + usu.getNombre());
                        StageManager.setPrimaryStage(stage);
                        stage.show();
                        System.out.println("[Login] Fallback: mDashboard cargado sin controllerFactory (sin inyección Spring). Revisa logs para corregir inyección.");
                    } catch (Exception fallbackEx) {
                        System.out.println("[Login] Fallback también falló: " + fallbackEx);
                        fallbackEx.printStackTrace();
                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        double with = stage != null ? stage.getWidth() / 2 : 400;
                        double h = stage != null ? stage.getHeight() / 2 : 200;
                        Toast.showToast(stage, "Error al cargar dashboard: " + fxEx.getMessage(), 5000, with, h);
                    }
                }
            } else {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                double with = stage.getWidth() * 2;
                double h = stage.getHeight() / 2;
                Toast.showToast(stage, "Credencial invalido!! intente nuevamente", 2000, with, h);
            }
        } catch (Exception e) {
            System.out.println("[Login] Excepción en login: " + e);
            e.printStackTrace();
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                double with = stage != null ? stage.getWidth() / 2 : 400;
                double h = stage != null ? stage.getHeight() / 2 : 200;
                Toast.showToast(stage, "Error inesperado: " + e.getMessage(), 5000, with, h);
            } catch (Exception ex) {
                System.out.println("[Login] Error al mostrar Toast: " + ex.getMessage());
            }
        }
    }

    @FXML
    public void cerrar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        Platform.exit();
        System.exit(0);
    }

    private void setStatusMessage(String message, boolean isError) {
        if (lblStatus != null) {
            lblStatus.setText(message);
            if (isError) {
                lblStatus.setStyle("-fx-text-fill: #e80808;");
            } else {
                lblStatus.setStyle("-fx-text-fill: #44ff1f;");
            }
        }
    }

    private void limpiarCamposRegistro() {
        txtNombreReg.clear();
        txtApellidoReg.clear();
        txtEmailReg.clear();
        txtClaveReg.clear();
        txtConfirmClaveReg.clear();
    }
}
