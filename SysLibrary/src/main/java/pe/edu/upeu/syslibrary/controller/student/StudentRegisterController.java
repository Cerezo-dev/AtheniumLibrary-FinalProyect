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
import pe.edu.upeu.syslibrary.service.IUsuarioService;

import java.io.IOException;

@Controller
public class StudentRegisterController {

    @Autowired private ApplicationContext context;
    @Autowired private IUsuarioService usuarioService;

    @FXML private TextField txtEmailReg;
    @FXML private TextField txtCodigoReg;
    @FXML private Label lblStatus;

    @FXML
    private void handleRegister(ActionEvent event) {
        String email = txtEmailReg.getText().trim();
        String codigo = txtCodigoReg.getText().trim();

        // 1. Validar campos vacíos
        if (email.isEmpty() || codigo.isEmpty()) {
            mostrarError("Por favor ingrese Email y Código.");
            return;
        }

        // 2. Validar Dominio
        if (!email.endsWith("@gmail.com") && !email.endsWith("@upeu.edu.pe") && !email.endsWith("@unaj.edu.pe")) {
            mostrarError("Solo se permiten correos @gmail.com o @upeu.edu.pe o @unaj.edu.pe");
            return;
        }

        try {
            // 3. Llamada al nuevo método del servicio
            usuarioService.registrarEstudiante(email, codigo);

            mostrarExito("Cuenta creada. Tu contraseña es tu código.");

            // Opcional: Redirigir automáticamente tras unos segundos o dejar que el usuario haga clic
            // backToLogin(event);

        } catch (Exception e) {
            e.printStackTrace();
            // Muestra el error que lanzamos en el Service (ej: "El correo ya existe")
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void backToLogin(ActionEvent event) {
        redirectToView("/fxml/student/login_student.fxml", event);
    }

    private void redirectToView(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(context::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Portal Estudiante");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar la vista.");
        }
    }

    private void mostrarError(String msg) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: #ff6b6b; -fx-effect: dropshadow(one-pass-box, black, 8, 0.0, 2, 0);");
    }

    private void mostrarExito(String msg) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: #2ecc71; -fx-effect: dropshadow(one-pass-box, black, 8, 0.0, 2, 0);");
    }
}