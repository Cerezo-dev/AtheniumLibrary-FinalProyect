package pe.edu.upeu.syslibrary.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    @FXML
    private VBox mainUserViewRoot;

    @FXML
    private TextField txtSearch;

    @FXML
    private FlowPane usersContainer; // Contenedor donde se cargan las tarjetas de usuarios

    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    public void initialize() {
        // Lógica para cargar la lista inicial de usuarios al iniciar la vista
        System.out.println("UserController: Inicializando gestión de usuarios.");
        loadUserCards();
    }

    private void loadUserCards() {
        // Implementación para obtener datos de la BD y generar las 'user-card'
        // Simplemente añadiremos las tarjetas de ejemplo al FlowPane aquí.
    }

    // Maneja la acción de búsqueda
    @FXML
    private void handleSearch(ActionEvent event) {
        String query = txtSearch.getText();
        System.out.println("Buscando usuarios con la consulta: " + query);
        // Lógica para filtrar las tarjetas de usuarios mostradas
    }

    // Maneja el clic en el botón "+ Nuevo Usuario"
    @FXML
    private void handleNewUser(ActionEvent event) {
        System.out.println("UserController: Solicitando al controlador principal que muestre el formulario de Nuevo Usuario.");

        // La mejor práctica es que el controlador principal (MainguiController)
        // maneje la transición a un nuevo formulario de registro.
        try {
            MainguiController mainguiController = applicationContext.getBean(MainguiController.class);

            // Aquí llamarías al método del MainguiController para cargar el formulario de usuario:
            // mainguiController.showUserRegistrationForm();

        } catch (Exception e) {
            System.err.println("No se pudo obtener el MainguiController.");
        }
    }
}