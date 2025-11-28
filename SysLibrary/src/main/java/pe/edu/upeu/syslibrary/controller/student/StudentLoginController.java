package pe.edu.upeu.syslibrary.controller.student;

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
import java.util.Optional;

@Controller
public class StudentLoginController {

    @Autowired private ApplicationContext context;
    @Autowired private IUsuarioService usuarioService;

    @FXML private TextField txtEmailStudent;
    @FXML private PasswordField txtCodigoStudent;
    @FXML private Label lblStatus;

    @FXML
    private void handleStudentLogin(ActionEvent event) {
        // ... (Tu código existente de login) ...
        String email = txtEmailStudent.getText().trim();
        String codigo = txtCodigoStudent.getText().trim();

        if (email.isEmpty() || codigo.isEmpty()) {
            mostrarError("Ingrese su correo y código.");
            return;
        }

        try {
            Usuario user = usuarioService.findByEmail(email);
            if (user != null && "ESTUDIANTE".equals(user.getPerfil().getNombre())) {
                if (codigo.equals(user.getCodigoEstudiante())) {
                    ingresarAlDashboard(user, event);
                } else {
                    mostrarError("El código no coincide con el registrado.");
                }
            } else {
                mostrarError("Usuario no encontrado o no es estudiante.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error de conexión.");
        }
    }

    // --- NUEVO MÉTODO: REGISTRO DE ESTUDIANTE ---
    @FXML
    private void handleStudentRegister(ActionEvent event) {
        // Redirige directamente al formulario simplificado
        redirectToView("/fxml/student/student_register.fxml", event);
    }

    private void ingresarAlDashboard(Usuario user, ActionEvent event) {
        SessionManager.getInstance().setUserId(user.getIdUsuario());
        SessionManager.getInstance().setUserName(user.getNombre());
        SessionManager.getInstance().setUserPerfil("ESTUDIANTE");
        redirectToView("/fxml/student/student_dashboard.fxml", event);
    }

    @FXML
    private void goBackToAdmin(ActionEvent event) {
        // Regresa al login principal
        redirectToView("/fxml/login.fxml", event);
    }

    private void redirectToView(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.hide();
            stage.setScene(new Scene(root));

            if (fxmlPath.contains("dashboard")) {
                stage.setTitle("SysLibrary - Campus Virtual");
                stage.setResizable(true);
                stage.setMaximized(true);
            } else {
                stage.setTitle("SysLibrary - Acceso");
                stage.setResizable(false);
                stage.setMaximized(false);
                stage.sizeToScene();
                stage.centerOnScreen();
            }
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar la vista: " + fxmlPath);
        }
    }

    private void mostrarError(String msg) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: #ff6b6b;");
    }
}