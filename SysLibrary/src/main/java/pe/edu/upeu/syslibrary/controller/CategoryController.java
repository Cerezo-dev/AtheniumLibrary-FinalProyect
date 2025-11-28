package pe.edu.upeu.syslibrary.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.glyphfont.Glyph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.model.Categoria;
import pe.edu.upeu.syslibrary.service.ICategoriaService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CategoryController {

    // --- COMPONENTES FXML ---
    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private Label lblPaginacion;
    @FXML private FlowPane categoriesContainer;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cmbOrden;

    // --- DEPENDENCIAS ---
    @Autowired private ApplicationContext applicationContext;
    @Autowired private ICategoriaService categoriaService;

    // --- VARIABLES DE DATOS ---
    private List<Categoria> masterList = new ArrayList<>();   // Lista completa (BD)
    private List<Categoria> filteredList = new ArrayList<>(); // Lista filtrada/ordenada

    // --- PAGINACIÓN ---
    private static final int ITEMS_POR_PAGINA = 9;
    private int paginaActual = 0;
    private int totalPaginas = 0;

    @FXML
    public void initialize() {
        System.out.println("Inicializando controlador de categorías...");

        setupSorting();

        // Listener del buscador: Al escribir, llamamos a filtrarDatos
        txtBuscar.textProperty().addListener((obs, old, newVal) -> {
            filtrarDatos();
        });

        loadCategoryData();
    }

    private void setupSorting() {
        cmbOrden.getItems().addAll(
                "Nombre (A - Z)",
                "Nombre (Z - A)",
                "Más recientes primero",
                "Más antiguos primero"
        );
        cmbOrden.getSelectionModel().selectFirst();

        // CORRECCIÓN AQUÍ: Llamamos a filtrarDatos(), NO a applyFilters()
        cmbOrden.setOnAction(e -> filtrarDatos());
    }

    public void loadCategoryData() {
        try {
            masterList = categoriaService.findAll();
            filtrarDatos(); // Esto a su vez llamará a actualizarVista()
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lógica 1: Prepara los datos (Filtra y Ordena)
     * NO DIBUJA NADA. Solo prepara la lista filteredList.
     */
    private void filtrarDatos() {
        String busqueda = txtBuscar.getText().toLowerCase();
        String orden = cmbOrden.getValue();

        // 1. Filtrar desde masterList hacia filteredList
        filteredList = masterList.stream()
                .filter(c -> c.getNombre().toLowerCase().contains(busqueda) ||
                        (c.getDescripcion() != null && c.getDescripcion().toLowerCase().contains(busqueda)))
                .collect(Collectors.toList());

        // 2. Ordenar filteredList
        if (orden != null) {
            switch (orden) {
                case "Nombre (A - Z)":
                    filteredList.sort(Comparator.comparing(Categoria::getNombre, String.CASE_INSENSITIVE_ORDER));
                    break;
                case "Nombre (Z - A)":
                    filteredList.sort(Comparator.comparing(Categoria::getNombre, String.CASE_INSENSITIVE_ORDER).reversed());
                    break;
                case "Más recientes primero":
                    filteredList.sort(Comparator.comparing(Categoria::getIdCategoria).reversed());
                    break;
                case "Más antiguos primero":
                    filteredList.sort(Comparator.comparing(Categoria::getIdCategoria));
                    break;
            }
        }

        // 3. Resetear paginación y mandar a dibujar
        paginaActual = 0;
        actualizarVista();
    }

    /**
     * Lógica 2: Dibuja la pantalla (Paginación)
     * Toma filteredList y recorta solo los 9 items que tocan.
     */
    private void actualizarVista() {
        // 1. Calcular total páginas
        int totalItems = filteredList.size();
        totalPaginas = (int) Math.ceil((double) totalItems / ITEMS_POR_PAGINA);

        // 2. Validar índices
        if (totalPaginas == 0) {
            paginaActual = 0;
        } else if (paginaActual >= totalPaginas) {
            paginaActual = totalPaginas - 1;
        }

        // 3. Recortar la sublista (Paginación)
        int fromIndex = paginaActual * ITEMS_POR_PAGINA;
        int toIndex = Math.min(fromIndex + ITEMS_POR_PAGINA, totalItems);

        if (fromIndex > toIndex) fromIndex = toIndex; // Protección extra

        List<Categoria> itemsPaginaActual = filteredList.subList(fromIndex, toIndex);

        // 4. Dibujar en el FlowPane
        categoriesContainer.getChildren().clear();

        if (totalItems == 0) {
            categoriesContainer.getChildren().add(new Label("No se encontraron resultados."));
        } else {
            for (Categoria c : itemsPaginaActual) {
                categoriesContainer.getChildren().add(createCategoryCard(c));
            }
        }

        // 5. Actualizar botones
        actualizarControlesPaginacion();
    }

    private void actualizarControlesPaginacion() {
        lblPaginacion.setText("Página " + (totalPaginas == 0 ? 0 : paginaActual + 1) + " de " + (totalPaginas == 0 ? 1 : totalPaginas));
        btnPrev.setDisable(paginaActual == 0);
        btnNext.setDisable(paginaActual >= totalPaginas - 1);
    }

    @FXML
    public void handlePrevPage() {
        if (paginaActual > 0) {
            paginaActual--;
            actualizarVista();
        }
    }

    @FXML
    public void handleNextPage() {
        if (paginaActual < totalPaginas - 1) {
            paginaActual++;
            actualizarVista();
        }
    }

    // --- MÉTODOS DE CREACIÓN DE UI Y ACCIONES ---

    private VBox createCategoryCard(Categoria categoria) {
        VBox card = new VBox();
        card.getStyleClass().add("category-card");
        card.setPrefWidth(300);

        Glyph icon = new Glyph("FontAwesome", "BOOKMARK");
        icon.getStyleClass().add("category-icon-violet");

        Label lblNombre = new Label(categoria.getNombre());
        lblNombre.getStyleClass().add("card-category-title");

        Label lblDescripcion = new Label(
                categoria.getDescripcion() != null && !categoria.getDescripcion().isEmpty()
                        ? categoria.getDescripcion()
                        : "Sin descripción disponible"
        );
        lblDescripcion.getStyleClass().add("card-category-description");
        lblDescripcion.setWrapText(true);

        HBox header = new HBox(10, icon, new VBox(lblNombre, lblDescripcion));

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Button btnEdit = new Button("Editar");
        btnEdit.getStyleClass().add("secondary-button-light");
        btnEdit.setOnAction(e -> handleEditCategory(categoria));

        Button btnDelete = new Button("Eliminar");
        btnDelete.getStyleClass().add("danger-button-light");
        btnDelete.setOnAction(e -> handleDeleteCategory(categoria));

        HBox actions = new HBox(10, btnEdit, btnDelete);
        actions.setAlignment(javafx.geometry.Pos.CENTER);

        card.getChildren().addAll(header, spacer, actions);
        return card;
    }

    @FXML
    private void handleNewCategory(ActionEvent event) {
        openForm(null);
    }

    private void handleEditCategory(Categoria categoria) {
        openForm(categoria);
    }

    private void handleDeleteCategory(Categoria categoria) {
        try {
            categoriaService.deleteById(categoria.getIdCategoria());
            loadCategoryData();
        } catch (Exception e) {
            System.err.println("Error al eliminar: " + e.getMessage());
        }
    }

    private void openForm(Categoria categoria) {
        try {
            // NOTA: Asegúrate que la carpeta en resources sea 'fxml' o 'view'.
            // En tu último código usaste "/fxml/category_form.fxml".
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/category_form.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            CategoryFormController formController = loader.getController();
            formController.setCategoria(categoria);
            formController.setOnSaveSuccess(this::loadCategoryData);

            Stage stage = new Stage();
            stage.setTitle(categoria == null ? "Nueva Categoría" : "Editar Categoría");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}