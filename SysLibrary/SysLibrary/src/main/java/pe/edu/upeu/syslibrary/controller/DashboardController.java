package pe.edu.upeu.syslibrary.controller;// DashboardController.java (Fragmento Modificado)

// Importar MainguiController y ApplicationContext
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.controller.MainguiController;
import org.springframework.beans.factory.annotation.Autowired;
// ...

@Controller
public class DashboardController {

    // Inyectamos el MainguiController usando Spring
    @Autowired
    private MainguiController mainguiController;

    // ... (El resto de las variables y loadSummaryData) ...

    // --- MÉTODOS DE DELEGACIÓN ---

    // Este método es llamado por el botón "Agregar Libro" en el FXML
    @FXML
    private void showLibros(ActionEvent event) {
        // Le pedimos al controlador principal que ejecute la navegación
        mainguiController.showLibros(event);
    }

    // Este método es llamado por el botón "Registrar Usuario" en el FXML
    @FXML
    private void showAddUser(ActionEvent event) {
        // Le pedimos al controlador principal que ejecute la navegación
        mainguiController.showAddUser(event);
    }

    // Este método es llamado por el botón "Nuevo Préstamo" en el FXML
    @FXML
    private void showIssueBook(ActionEvent event) {
        // Le pedimos al controlador principal que ejecute la navegación
        mainguiController.showIssueBook(event);
    }

    // ... (Y así para cualquier otro botón de navegación que agregues al dashboard) ...
}