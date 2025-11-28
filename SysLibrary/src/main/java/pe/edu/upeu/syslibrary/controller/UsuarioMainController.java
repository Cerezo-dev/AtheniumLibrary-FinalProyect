package pe.edu.upeu.syslibrary.controller;

import javafx.beans.property.SimpleObjectProperty;
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
import pe.edu.upeu.syslibrary.model.Usuario; // USAMOS USUARIO
import pe.edu.upeu.syslibrary.service.IUsuarioService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UsuarioMainController {

    private final IUsuarioService usuarioService;
    private final ApplicationContext applicationContext;

    @FXML private TableView<Usuario> studentTable; // Tabla de USUARIOS
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFiltroCarrera;

    // --- PAGINACIÓN ---
    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private Label lblPaginacion;

    // --- COLUMNAS (Apuntan a Usuario) ---
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
        // TRUCO: Obtenemos TODOS los usuarios y filtramos en memoria por el perfil "ESTUDIANTE"
        // O idealmente: usuarioService.listarPorPerfil("ESTUDIANTE");
        masterList = usuarioService.findAll().stream()
                .filter(u -> u.getPerfil().getNombre().equals("ESTUDIANTE"))
                .collect(Collectors.toList());

        applyFilters();
    }

    private void setupFilters() {
        cmbFiltroCarrera.getItems().clear();
        cmbFiltroCarrera.getItems().add("Todas las carreras");
        // Aquí podrías cargar las carreras dinámicamente si tuvieras una tabla de carreras
        cmbFiltroCarrera.getItems().addAll("Ing. Sistemas", "Enfermería", "Teología", "Arquitectura");
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
                    // Buscamos en los campos de Usuario
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
        abrirFormulario(null, "Registrar Estudiante", ((Node)event.getSource()).getScene().getWindow());
    }

    @FXML
    private void handleRefrescar(ActionEvent event) {
        txtSearch.clear();
        loadData();
    }

    private void abrirFormulario(Usuario usuario, String titulo, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/usuarioFrom.fxml")); // Asegúrate que la ruta sea correcta
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            UsuarioFormController controller = loader.getController();
            // Si estuviéramos editando, pasaríamos el usuario aquí: controller.setUsuario(usuario);

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(owner);
            stage.setResizable(false);
            stage.showAndWait();

            loadData(); // Recargar tabla al cerrar
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

        // OJO: Aquí usamos los nombres de variables de la clase USUARIO
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoEstudiante")); // variable nueva
        colDNI.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colCarrera.setCellValueFactory(new PropertyValueFactory<>("carrera")); // variable nueva

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
                btnDelete.setOnAction(e -> {
                    Usuario u = getItem();
                    if(u != null) {
                        usuarioService.deleteById(u.getIdUsuario());
                        loadData();
                    }
                });
            }

            @Override protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }
}