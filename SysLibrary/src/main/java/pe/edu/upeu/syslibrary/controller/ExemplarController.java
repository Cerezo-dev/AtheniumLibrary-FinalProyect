package pe.edu.upeu.syslibrary.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.controlsfx.glyphfont.Glyph;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.model.Ejemplar;
import pe.edu.upeu.syslibrary.service.EjemplarService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ExemplarController {

    // Inyectamos el servicio y el contexto
    private final EjemplarService ejemplarService;
    private final ApplicationContext applicationContext;

    @FXML private VBox mainExemplarViewRoot;
    @FXML private TextField txtSearch;

    // Tabla tipada con tu modelo Ejemplar
    @FXML private TableView<Ejemplar> exemplarTable;

    // Columnas tipadas
    @FXML private TableColumn<Ejemplar, String> colCode;
    @FXML private TableColumn<Ejemplar, String> colBook;     // Mostrará el Título del Libro
    @FXML private TableColumn<Ejemplar, String> colStatus;
    @FXML private TableColumn<Ejemplar, String> colLocation; // Mostrará la Ubicación del Libro
    @FXML private TableColumn<Ejemplar, Ejemplar> colActions;

    // Listas para manejo de datos y filtrado
    private ObservableList<Ejemplar> masterList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadExemplarData();
        setupSearchFilter();
        System.out.println("ExemplarController: Inicializado correctamente.");
    }

    private void setupTableColumns() {
        // 1. Código del Ejemplar (Directo)
        colCode.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        // 2. Título del Libro (Anidado: Ejemplar -> Libro -> Título)
        colBook.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLibro() != null) {
                return new SimpleStringProperty(cellData.getValue().getLibro().getTitulo());
            }
            return new SimpleStringProperty("Sin Libro");
        });

        // 3. Estado (Convertimos el ENUM a String)
        colStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstado().toString()));

        // 4. Ubicación (Anidado: Ejemplar -> Libro -> Ubicación)
        colLocation.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLibro() != null) {
                return new SimpleStringProperty(cellData.getValue().getLibro().getUbicacion());
            }
            return new SimpleStringProperty("-");
        });

        // 5. Botones de Acción (Editar / Eliminar)
        colActions.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("", new Glyph("FontAwesome", "PENCIL"));
            private final Button btnDelete = new Button("", new Glyph("FontAwesome", "TRASH"));
            private final HBox pane = new HBox(10, btnEdit, btnDelete);

            {
                pane.setAlignment(Pos.CENTER);
                btnEdit.getStyleClass().addAll("action-button", "btn-editar");
                btnDelete.getStyleClass().addAll("action-button", "btn-eliminar");

                // Acción Eliminar
                btnDelete.setOnAction(event -> {
                    Ejemplar ejemplar = getItem();
                    if (ejemplar != null) {
                        eliminarEjemplar(ejemplar);
                    }
                });

                // Acción Editar (Opcional, abre el formulario con datos)
                btnEdit.setOnAction(event -> {
                    // abrirFormulario(getItem()); // Implementar si deseas editar
                    System.out.println("Editar: " + getItem().getCodigo());
                });
            }

            @Override
            protected void updateItem(Ejemplar item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void loadExemplarData() {
        masterList.clear();
        masterList.addAll(ejemplarService.findAll());
        exemplarTable.setItems(masterList);
    }

    private void setupSearchFilter() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                exemplarTable.setItems(masterList);
            } else {
                String lowerCaseFilter = newValue.toLowerCase();
                List<Ejemplar> filtered = masterList.stream()
                        .filter(e ->
                                e.getCodigo().toLowerCase().contains(lowerCaseFilter) ||
                                        e.getLibro().getTitulo().toLowerCase().contains(lowerCaseFilter)
                        )
                        .collect(Collectors.toList());
                exemplarTable.setItems(FXCollections.observableArrayList(filtered));
            }
        });
    }

    @FXML
    private void handleNewExemplar(ActionEvent event) {
        abrirFormularioModal("Registrar Nuevo Ejemplar");
    }

    // Método genérico para abrir el formulario
    private void abrirFormularioModal(String titulo) {
        try {
            // Asegúrate de crear este FXML: exemplar_form.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/exemplar_form.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(mainExemplarViewRoot.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();

            // Recargar tabla al cerrar el modal
            loadExemplarData();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el formulario: " + e.getMessage());
        }
    }

    private void eliminarEjemplar(Ejemplar ejemplar) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Eliminar Ejemplar");
        alert.setHeaderText("¿Estás seguro de eliminar el ejemplar " + ejemplar.getCodigo() + "?");
        alert.setContentText("Esta acción no se puede deshacer.");

        if (alert.showAndWait().get() == ButtonType.OK) {

            // --- SOLUCIÓN AQUÍ ---
            // Usamos deleteById y le pasamos SOLO el número ID (Long), no el objeto entero.
            ejemplarService.deleteById(ejemplar.getIdEjemplar());

            // Recargamos la tabla para que desaparezca la fila borrada
            loadExemplarData();

            mostrarAlerta("Éxito", "Ejemplar eliminado correctamente.");
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}