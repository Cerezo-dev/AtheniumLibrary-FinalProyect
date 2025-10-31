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

    //-----------------------------------
    @Autowired
    PerfilRepository perfilRepository; // Inyección de dependecia del repositorio necesario para registrar usuarios


    // para lofin ----------
    @FXML
    TextField txtEmail; //Renombrado de txtUsuario a txtEmail para mayor claridad
    @FXML
    PasswordField txtClave;
    @FXML
    Button btnIngresar;
    @FXML
    private VBox loginPane;
    @FXML
    private Label lblStatus; // Etiqueta para mostrar mensajes de estado

    // ahora para register -----
    @FXML
    private VBox registerPane;

    @FXML
    private TextField txtNombreReg, txtApellidoReg, txtEmailReg;

    @FXML
    private PasswordField txtClaveReg, txtConfirmClaveReg;

    @FXML
    private void showRegisterPane(ActionEvent event) {
        //if (loginPane != null && registerPane != null) {
            loginPane.setVisible(false);
            loginPane.setManaged(false);
            registerPane.setVisible(true);
            registerPane.setManaged(true);
            lblStatus.setText("");
            lblStatus.setStyle("-fx-background-color: #f0f0f0;");// Restablecer el estilo predeterminado
            //if (lblStatus != null) lblStatus.setText("");
            //test->
        //} else {
        //    System.out.println("⚠️ Pane references are null; check FXML fx:id bindings.");
        //}
    }

    @FXML
    private void showLoginPane(ActionEvent event) {
        //if (loginPane != null && registerPane != null) {
            registerPane.setVisible(false);
            registerPane.setManaged(false);
            loginPane.setVisible(true);
            loginPane.setManaged(true);
            lblStatus.setText("");
            lblStatus.setStyle("-fx-background-color: #f0f0f0;"); // Restablecer el estilo predeterminado
            //if (lblStatus != null) lblStatus.setText("");
        //} else {
            //System.out.println("⚠️ Pane references are null; check FXML fx:id bindings.");
        //}
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
            // 3. Buscar el perfil por defecto (Asumimos que existe un perfil "ESTUDIANTE")
            // Esto es crucial porque tu entidad Usuario tiene perfil como NO OPCIONAL.
            Perfil perfilDefecto = perfilRepository.findByNombre("ESTUDIANTE");
            if (perfilDefecto == null) {
                setStatusMessage("Error de configuración: Perfil 'ESTUDIANTE' no encontrado.", true);
                return;
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setApellido(apellido);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setPassword(clave); // La clave será encriptada en el servicio
            nuevoUsuario.setEstado("ACTIVO");
            nuevoUsuario.setPerfil(perfilDefecto); // Asignar el perfil por defecto

            // Guardar el nuevo usuario
            us.save(nuevoUsuario);

            setStatusMessage("Registro exitoso. Ahora puede iniciar sesión.", false);
            limpiarCamposRegistro();
            showLoginPane(null); // Volver al pane de login
        } catch (DataIntegrityViolationException e) {
            setStatusMessage("El email ya está registrado.", true);
        } catch (Exception e) {
            setStatusMessage("Error inesperado: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }


    @FXML
    public void login(ActionEvent event) throws IOException {
        try {
            Usuario usu = us.loginUsuario(txtEmail.getText(), txtClave.getText());

            if (usu != null) {
                SessionManager.getInstance().setUserId(usu.getId());
                SessionManager.getInstance().setUserName(usu.getEmail());

                String perfilNombre = (usu.getPerfil() != null && usu.getPerfil().getNombre() != null)
                        ? usu.getPerfil().getNombre()
                        : "";
                SessionManager.getInstance().setUserPerfil(perfilNombre);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/mDashboard.fxml"));
                loader.setControllerFactory(context::getBean);
                Parent mainRoot = loader.load();
                Screen screen = Screen.getPrimary();
                Rectangle2D bounds = screen.getBounds();
                Scene mainScene = new Scene(mainRoot, bounds.getWidth(), bounds.getHeight() - 30);
                mainScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.getIcons().add(new Image(getClass().getResource("/img/store.png").toExternalForm()));
                stage.setScene(mainScene);
                stage.setTitle("Athenium Athenium - Bienvenido " + usu.getNombre() + " " + usu.getApellido());
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setResizable(true);
                StageManager.setPrimaryStage(stage);
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());
                stage.show();
            } else {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                double with = stage.getWidth() * 2;
                double h = stage.getHeight() / 2;
                Toast.showToast(stage, "Credencial invalido!! intente nuevamente", 2000, with, h);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void cerrar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        Platform.exit();
        System.exit(0);
    }

    //Metodo para mostrar mensajes de estado
    //Complementos
    private void setStatusMessage(String message, boolean isError) {
        if (lblStatus != null) {
            lblStatus.setText(message);
            if (isError) {
                lblStatus.setStyle("-fx-text-fill: #e80808;"); // Rojo para error
            } else {
                lblStatus.setStyle("-fx-text-fill: #44ff1f;"); // Verde para éxito
            }
        }
    }
    //Metodo para limpiar campos despues de registro exitoso
    private void limpiarCamposRegistro() {
        txtNombreReg.clear();
        txtApellidoReg.clear();
        txtEmailReg.clear();
        txtClaveReg.clear();
        txtConfirmClaveReg.clear();
    }
}
