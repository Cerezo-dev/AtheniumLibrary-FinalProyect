package pe.edu.upeu.syslibrary.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

// Asumiendo una clase de modelo para el Ejemplar, por ejemplo, Exemplar.java
// import pe.edu.upeu.syslibrary.model.Exemplar;

@Controller
public class ExemplarController {

    @FXML
    private VBox mainExemplarViewRoot;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView exemplarTable; // Debe ser TableView<Exemplar>

    // Columnas
    @FXML private TableColumn colCode;
    @FXML private TableColumn colBook;
    @FXML private TableColumn colStatus;
    @FXML private TableColumn colLocation;
    @FXML private TableColumn colActions;

    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    public void initialize() {
        // 1. Configurar las celdas de las columnas (necesita el modelo de datos)
        // colCode.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        // colBook.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLibro().getTitulo()));

        // 2. Cargar datos iniciales
        loadExemplarData();

        System.out.println("ExemplarController: Inicializando gestión de ejemplares.");
    }

    private void loadExemplarData() {
        // Lógica para obtener ejemplares de un servicio/repositorio y cargarlos en la tabla
        // exemplarTable.setItems(FXCollections.observableArrayList(exemplarService.findAll()));
    }

    // Maneja la acción de búsqueda en el campo de texto
    @FXML
    private void handleSearch(ActionEvent event) {
        String query = txtSearch.getText();
        System.out.println("Buscando ejemplares con la consulta: " + query);
        // Lógica para filtrar la tabla según el query
    }

    // Maneja el clic en el botón "+ Nuevo Ejemplar"
    @FXML
    private void handleNewExemplar(ActionEvent event) {
        // Redirigir a la vista del formulario de registro de ejemplar
        System.out.println("ExemplarController: Solicitando al controlador principal que muestre el formulario de Nuevo Ejemplar.");

        // === IMPLEMENTACIÓN DE NAVEGACIÓN (Idealmente en MainguiController) ===
        try {
            // Obtenemos una referencia al MainguiController y llamamos a un nuevo método de navegación
            MainguiController mainguiController = applicationContext.getBean(MainguiController.class);

            // Suponemos que existe un método para cargar el formulario de ejemplares
            // mainguiController.showExemplarRegistrationForm();

            // Nota: Este llamado requiere que el MainguiController tenga métodos
            // específicos para cada formulario para mantener la barra de título y el
            // área de contenido actualizadas.

        } catch (Exception e) {
            System.err.println("No se pudo obtener el MainguiController. Implementar navegación alternativa.");
        }
    }

    // Se necesitarían métodos para manejar las acciones de Editar/Eliminar fila.
    // @FXML private void handleEdit(Exemplar exemplar) { ... }
    // @FXML private void handleDelete(Exemplar exemplar) { ... }
}