package pe.edu.upeu.syslibrary.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.HBox;
import lombok.RequiredArgsConstructor;
import org.controlsfx.glyphfont.Glyph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.dto.SessionManager;
import pe.edu.upeu.syslibrary.model.Libro;
import pe.edu.upeu.syslibrary.service.ILibroService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class LibroController {

    private final ILibroService libroService;

    @Autowired
    private MainguiController mainguiController;

    @FXML private TextField txtSearch;
    @FXML private TableView<Libro> libroTable;

    @FXML private TableColumn<Libro, String> colISBN;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colEditorial;
    @FXML private TableColumn<Libro, String> colCategoria;
    @FXML private TableColumn<Libro, Integer> colDisponibles;
    @FXML private TableColumn<Libro, Void> colActions;
    @FXML private Pagination pagination;

    private static final int ROWS_PER_PAGE = 10;
    private List<Libro> listaLibros;
    @FXML
    public void initialize() {
        System.out.println("✅ LibroController inicializado. libroService = " + libroService);

        // Configurar columnas de la tabla
        colISBN.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colEditorial.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        colDisponibles.setCellValueFactory(new PropertyValueFactory<>("disponibles"));

        // Columna de categoría
        colCategoria.setCellValueFactory(cellData -> {
            Libro libro = cellData.getValue();
            if (libro.getCategoria() != null) {
                return new SimpleStringProperty(libro.getCategoria().getNombre());
            }
            return new SimpleStringProperty("N/A");
        });

        // Cargar datos al iniciar
        loadLibros();

        // Configurar botones de acción (editar/eliminar)
        configureActionColumn();
    }

    /** Carga los libros desde el servicio */
    private void loadLibros() {
        try {
            listaLibros = libroService.findAll();

            int totalPages = (int) Math.ceil((double) listaLibros.size() / ROWS_PER_PAGE);
            pagination.setPageCount(totalPages == 0 ? 1 : totalPages);

            pagination.setPageFactory(this::createPage);

            System.out.println("Libros cargados: " + listaLibros.size());
        } catch (Exception e) {
            System.err.println("Error al cargar los libros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Buscar libros por título */
    @FXML
    private void handleSearch(ActionEvent event) {
        String query = txtSearch.getText().trim();
        if (query.isBlank()) {
            loadLibros();
            return;
        }

        try {
            listaLibros = libroService.findByTituloContainingIgnoreCase(query);

            int totalPages = (int) Math.ceil((double) listaLibros.size() / ROWS_PER_PAGE);
            pagination.setPageCount(totalPages == 0 ? 1 : totalPages);
            pagination.setPageFactory(this::createPage);
        } catch (Exception e) {
            System.err.println("Error durante la búsqueda: " + e.getMessage());
        }
    }

    /** Navegar al formulario de registro de libros */
    @FXML
    private void handleNewBook(ActionEvent event) {
        mainguiController.showBookRegistrationForm();
    }

    /** Configura la columna de acciones (editar y eliminar) */
    private void configureActionColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("");
            private final Button btnDelete = new Button("");
            private final HBox pane = new HBox(5, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().add("secondary-button-light");
                btnDelete.getStyleClass().add("danger-button-light");

                btnEdit.setGraphic(new Glyph("FontAwesome", "PENCIL_SQUARE_O"));
                btnDelete.setGraphic(new Glyph("FontAwesome", "TRASH_O"));

                btnEdit.setOnAction(event -> {
                    Libro libro = getTableView().getItems().get(getIndex());
                    handleEdit(libro);
                });

                btnDelete.setOnAction(event -> {
                    Libro libro = getTableView().getItems().get(getIndex());
                    handleDelete(libro);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    /** Acción al hacer clic en "Editar" */
    private void handleEdit(Libro libro) {
        System.out.println("Editando libro: " + libro.getTitulo());
        // Aquí puedes cargar el formulario con los datos del libro seleccionado.
    }

    /** Acción al hacer clic en "Eliminar" */
    private void handleDelete(Libro libro) {
        try {
            libroService.deleteById(libro.getIdLibro());
            loadLibros();
            System.out.println("Libro eliminado correctamente: " + libro.getTitulo());
        } catch (Exception e) {
            System.err.println("Error al eliminar libro: " + e.getMessage());
        }
    }
    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, listaLibros.size());
        libroTable.setItems(FXCollections.observableArrayList(listaLibros.subList(fromIndex, toIndex)));
        return libroTable;
    }
}
