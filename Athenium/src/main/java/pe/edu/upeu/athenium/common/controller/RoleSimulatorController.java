package pe.edu.upeu.athenium.common.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Controller;

// Usamos @Controller para la inyección de Spring
@Controller
public class RoleSimulatorController {

    // Referencia al controlador padre para la comunicación
    private mMainMenuController parentController;

    public void setParentController(mMainMenuController parentController) {
        this.parentController = parentController;
    }

    // Métodos para manejar el clic en cada rol
    @FXML
    private void handleEstudiante(ActionEvent event) {
        if (parentController != null) parentController.simularLogin("ESTUDIANTE");
    }

    @FXML
    private void handleDocente(ActionEvent event) {
        if (parentController != null) parentController.simularLogin("DOCENTE");
    }

    @FXML
    private void handleBibliotecario(ActionEvent event) {
        if (parentController != null) parentController.simularLogin("BIBLIOTECARIO");
    }

    @FXML
    private void handleAdministrador(ActionEvent event) {
        if (parentController != null) parentController.simularLogin("ADMINISTRADOR");
    }

    @FXML
    private void handlePublico(ActionEvent event) {
        if (parentController != null) parentController.simularLogin("PUBLICO");
    }
}