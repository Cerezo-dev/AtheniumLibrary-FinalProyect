package pe.edu.upeu.syslibrary.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.RequiredArgsConstructor;
import org.controlsfx.glyphfont.Glyph;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.model.Usuario;
import pe.edu.upeu.syslibrary.service.IUsuarioService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UsuarioMainController {

    private final IUsuarioService usuarioService;
    private final ApplicationContext applicationContext;

    @FXML private TableView<Usuario> studentTable;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFiltroCarrera;

    // --- PAGINACIÓN ---
    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private Label lblPaginacion;

    // --- COLUMNAS ---
    @FXML private TableColumn<Usuario, Void> colNum;
    @FXML private TableColumn<Usuario, String> colCodigo;
    @FXML private TableColumn<Usuario, String> colDNI;
    @FXML private TableColumn<Usuario, String> colNombres;
    @FXML private TableColumn<Usuario, String> colApellidos;
    @FXML private TableColumn<Usuario, String> colCarrera;
    @FXML private TableColumn<Usuario, Usuario> colActions;

    private static final int ROWS_PER_PAGE = 10;
    private List<Usuario> masterList = new ArrayList<>();
    private List<Usuario> filteredList = new ArrayList<>();
    private int paginaActual = 0;
    private int totalPaginas = 0;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilters();
        loadData();
    }

    private void loadData() {
        // Cargar SOLO estudiantes
        masterList = usuarioService.findAll().stream()
                .filter(u -> "ESTUDIANTE".equals(u.getPerfil().getNombre()))
                .collect(Collectors.toList());
        applyFilters();
    }

    private void setupFilters() {
        cmbFiltroCarrera.getItems().clear();
        cmbFiltroCarrera.getItems().add("Todas las carreras");
        // Aquí podrías cargar carreras dinámicamente si tuvieras una tabla
        cmbFiltroCarrera.getItems().addAll("Ing. Sistemas", "Enfermería", "Psicología", "Contabilidad", "Teología");
        cmbFiltroCarrera.getSelectionModel().selectFirst();

        cmbFiltroCarrera.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void applyFilters() {
        String searchText = txtSearch.getText().toLowerCase().trim();
        String carreraFilter = cmbFiltroCarrera.getValue();
        boolean filtrarCarrera = carreraFilter != null && !carreraFilter.equals("Todas las carreras");

        filteredList = masterList.stream()
                .filter(u -> {
                    boolean matchText = (u.getCodigoEstudiante() != null && u.getCodigoEstudiante().toLowerCase().contains(searchText)) ||
                            u.getNombre().toLowerCase().contains(searchText) ||
                            (u.getApellidos() != null && u.getApellidos().toLowerCase().contains(searchText)) ||
                            (u.getDni() != null && u.getDni().contains(searchText));

                    boolean matchCarrera = !filtrarCarrera || (u.getCarrera() != null && u.getCarrera().equals(carreraFilter));
                    return matchText && matchCarrera;
                })
                .collect(Collectors.toList());

        paginaActual = 0;
        actualizarVista();
    }

    private void actualizarVista() {
        int totalItems = filteredList.size();
        totalPaginas = (int) Math.ceil((double) totalItems / ROWS_PER_PAGE);

        if (totalPaginas == 0) paginaActual = 0;
        else if (paginaActual >= totalPaginas) paginaActual = totalPaginas - 1;

        int fromIndex = paginaActual * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, totalItems);

        if (fromIndex > toIndex) fromIndex = toIndex;

        List<Usuario> itemsPagina = filteredList.subList(fromIndex, toIndex);
        studentTable.setItems(FXCollections.observableArrayList(itemsPagina));

        lblPaginacion.setText("Página " + (totalPaginas == 0 ? 0 : paginaActual + 1) + " de " + (totalPaginas == 0 ? 1 : totalPaginas));
        btnPrev.setDisable(paginaActual == 0);
        btnNext.setDisable(paginaActual >= totalPaginas - 1);
    }

    @FXML public void handlePrevPage() { if (paginaActual > 0) { paginaActual--; actualizarVista(); } }
    @FXML public void handleNextPage() { if (paginaActual < totalPaginas - 1) { paginaActual++; actualizarVista(); } }

    @FXML
    private void handleNewStudent(ActionEvent event) {
        // Asegúrate de que el nombre del FXML coincida con tu archivo: usuarioFrom.fxml (o usuarioForm.fxml si lo renombraste)
        abrirFormulario(null, "Registrar Estudiante", ((Node)event.getSource()).getScene().getWindow());
    }

    @FXML
    private void handleRefrescar(ActionEvent event) {
        txtSearch.clear();
        cmbFiltroCarrera.getSelectionModel().selectFirst();
        loadData();
    }

    private void abrirFormulario(Usuario usuario, String titulo, Window owner) {
        try {
            // Nombre del archivo FXML del formulario
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/usuarioFrom.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            UsuarioFormController controller = loader.getController();
            // controller.setUsuario(usuario); // Si implementas edición

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(owner);
            stage.setResizable(false);
            stage.showAndWait();

            loadData(); // Recargar al cerrar
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        colNum.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText(String.valueOf(getIndex() + 1 + (paginaActual * ROWS_PER_PAGE)));
            }
        });

        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoEstudiante"));
        colDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colCarrera.setCellValueFactory(new PropertyValueFactory<>("carrera"));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnDel = new Button("", new Glyph("FontAwesome", "TRASH"));
            {
                btnDel.getStyleClass().add("danger-button-light"); // Usando tu clase CSS
                btnDel.setOnAction(e -> {
                    Usuario u = getItem();
                    if(u != null) {
                        usuarioService.deleteById(u.getIdUsuario());
                        loadData();
                    }
                });
            }
            @Override protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(btnDel));
                setAlignment(Pos.CENTER);
            }
        });
        // Necesario para que la celda de acciones se renderice correctamente aunque no tenga valor propio
        colActions.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue()));
    }
}