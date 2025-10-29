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
import org.springframework.stereotype.Controller;
import pe.edu.upeu.athenium.components.StageManager;
import pe.edu.upeu.athenium.components.Toast;
import pe.edu.upeu.athenium.dto.SessionManager;
import pe.edu.upeu.athenium.model.Usuario;
import pe.edu.upeu.athenium.service.IUsuarioService;

import java.io.IOException;

@Controller
public class LoginController {

    @Autowired
    private ApplicationContext context;
    @Autowired
    IUsuarioService us;
    @FXML
    TextField txtUsuario;
    @FXML
    PasswordField txtClave;
    @FXML
    Button btnIngresar;

    /** ==== Métodos agregados para manejar alternancia entre Login y Registro ====*/

    @FXML
    private javafx.scene.layout.VBox loginPane;

    @FXML
    private javafx.scene.layout.VBox registerPane;

    @FXML
    private javafx.scene.control.Label lblStatus;

    @FXML
    private void showRegisterPane(ActionEvent event) {
        if (loginPane != null && registerPane != null) {
            loginPane.setVisible(false);
            loginPane.setManaged(false);
            registerPane.setVisible(true);
            registerPane.setManaged(true);
            if (lblStatus != null) lblStatus.setText("");
        } else {
            System.out.println("⚠️ Pane references are null; check FXML fx:id bindings.");
        }
    }

    @FXML
    private void showLoginPane(ActionEvent event) {
        if (loginPane != null && registerPane != null) {
            registerPane.setVisible(false);
            registerPane.setManaged(false);
            loginPane.setVisible(true);
            loginPane.setManaged(true);
            if (lblStatus != null) lblStatus.setText("");
        } else {
            System.out.println("⚠️ Pane references are null; check FXML fx:id bindings.");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        if (lblStatus != null) {
            lblStatus.setText("✅ Registro simulado correctamente.");
        }
    }
/**en pruebaaa */
    @FXML
    public void cerrar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void login(ActionEvent event) throws IOException {
        try {
            Usuario usu = us.loginUsuario(txtUsuario.getText(), txtClave.getText());

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
                stage.setTitle("SysVentas SysCenterLife");
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
}
