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

    private final EjemplarService ejemplarService;
    private final ApplicationContext applicationContext;

    @FXML private TextField txtSearch;
    @FXML private TableView<Ejemplar> exemplarTable;
    @FXML private TableColumn<Ejemplar, String> colCode;
    @FXML private TableColumn<Ejemplar, String> colBook;
    @FXML private TableColumn<Ejemplar, String> colStatus;
    @FXML private TableColumn<Ejemplar, String> colLocation;
    @FXML private TableColumn<Ejemplar, Ejemplar> colActions;

    private ObservableList<Ejemplar> masterList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadExemplarData();

        txtSearch.textProperty().addListener((obs, old, newVal) -> filtrar(newVal));
    }

    private void setupTableColumns() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        colBook.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLibro() != null) {
                return new SimpleStringProperty(cellData.getValue().getLibro().getTitulo());
            }
            return new SimpleStringProperty("---");
        });

        colStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstado() != null ? cellData.getValue().getEstado().toString() : ""));

        colLocation.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLibro() != null) {
                return new SimpleStringProperty(cellData.getValue().getLibro().getUbicacion());
            }
            return new SimpleStringProperty("---");
        });

        colActions.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnDel = new Button("", new Glyph("FontAwesome", "TRASH"));
            {
                btnDel.getStyleClass().add("action-button");
                btnDel.setOnAction(e -> eliminar(getItem()));
            }
            @Override protected void updateItem(Ejemplar item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(btnDel));
                setAlignment(Pos.CENTER);
            }
        });
    }

    private void loadExemplarData() {
        masterList.clear();
        masterList.addAll(ejemplarService.findAll());
        exemplarTable.setItems(masterList);
    }

    private void filtrar(String texto) {
        if (texto == null || texto.isEmpty()) {
            exemplarTable.setItems(masterList);
        } else {
            String lower = texto.toLowerCase();
            List<Ejemplar> filtered = masterList.stream()
                    .filter(e -> e.getCodigo().toLowerCase().contains(lower) ||
                            (e.getLibro() != null && e.getLibro().getTitulo().toLowerCase().contains(lower)))
                    .collect(Collectors.toList());
            exemplarTable.setItems(FXCollections.observableArrayList(filtered));
        }
    }

    @FXML
    private void handleNewExemplar(ActionEvent event) {
        // Implementar formulario modal similar a UsuarioFormController si es necesario
        System.out.println("Nuevo Ejemplar solicitado");
    }

    private void eliminar(Ejemplar e) {
        if(e == null) return;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar ejemplar " + e.getCodigo() + "?");
        if(alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            ejemplarService.deleteById(e.getIdEjemplar());
            loadExemplarData();
        }
    }
}