package pe.edu.upeu.syslibrary.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.RequiredArgsConstructor;
import org.controlsfx.glyphfont.Glyph;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.model.Categoria;
import pe.edu.upeu.syslibrary.model.Libro;
import pe.edu.upeu.syslibrary.service.ICategoriaService;
import pe.edu.upeu.syslibrary.service.ILibroService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.controlsfx.glyphfont.GlyphFontRegistry;
import javafx.scene.text.Font;


@Controller
@RequiredArgsConstructor
public class LibroController {

    private final ILibroService libroService;
    private final ICategoriaService categoriaService;
    private final ApplicationContext applicationContext;

    @FXML private TableView<Libro> libroTable;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<Categoria> cmbFiltroCategoria;
    @FXML private Pagination pagination;

    // --- DEFINICIÓN DE COLUMNAS ---
    @FXML private TableColumn<Libro, Void> colNum;
    @FXML private TableColumn<Libro, String> colPortada;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colCategoria;
    @FXML private TableColumn<Libro, Integer> colAnio;
    @FXML private TableColumn<Libro, Libro> colInventario;
    @FXML private TableColumn<Libro, String> colEstado;
    @FXML private TableColumn<Libro, String> colUbicacion;
    @FXML private TableColumn<Libro, Libro> colActions;

    private static final int ROWS_PER_PAGE = 10;
    // Esta lista almacenará los libros que coinciden con los filtros actuales
    private List<Libro> listaLibrosFiltrados = new ArrayList<>();

    // --- VARIABLES PARA ALMACENAR LOS FILTROS AVANZADOS ACTIVOS ---
    private String filtroEstadoFisico = null;
    private Integer filtroAnioDesde = null;
    private Integer filtroAnioHasta = null;
    private String filtroUbicacion = null;
    private Boolean filtroSoloDisponibles = null;
    //private Boolean filtroSoloDisponibles = null;

    // -------------------------------------------------------------

    @FXML
    public void initialize() {
        // 1. FORZAR CARGA DE FUENTE FONTAWESOME (Solución a los puntos)
        try {
            // Intentamos cargar la fuente directamente desde la librería ControlsFX
            Font.loadFont(GlyphFontRegistry.class.getResourceAsStream("/org/controlsfx/glyphfont/fontawesome-webfont.ttf"), 10);
        } catch (Exception e) {
            System.out.println("Advertencia: No se pudo cargar FontAwesome automáticamente. " + e.getMessage());
        }
        setupTableColumns();
        setupCategoryFilter(); // Configurar el ComboBox de categorías
        applyFilters();        // Cargar los datos iniciales (sin filtros)
    }

    // --- 1. CONFIGURACIÓN DEL FILTRO DE CATEGORÍAS ---
    // Se eliminó la versión duplicada de este método que tenías.
    private void setupCategoryFilter() {
        try {
            List<Categoria> categorias = categoriaService.findAll();
            Categoria todas = new Categoria();
            todas.setIdCategoria(-1L); // Usamos -1 como ID especial
            todas.setNombre("Todas las categorías");
            cmbFiltroCategoria.getItems().add(todas);
            cmbFiltroCategoria.getItems().addAll(categorias);
            cmbFiltroCategoria.getSelectionModel().selectFirst(); // Seleccionar "Todas" por defecto
            // Configurar cómo se muestra el texto en el ComboBox
            cmbFiltroCategoria.setCellFactory(lv -> new ListCell<>() {
                @Override protected void updateItem(Categoria item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getNombre());
                }
            });
            cmbFiltroCategoria.setButtonCell(cmbFiltroCategoria.getCellFactory().call(null));
            // Agregar listener: cuando cambie la selección, aplicar filtros automáticamente
            cmbFiltroCategoria.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    applyFilters();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al cargar categorías.", Alert.AlertType.ERROR);
        }
    }

    // --- MÉTODO PÚBLICO LLAMADO DESDE LA VENTANA MODAL DE FILTROS ---
    // Actualiza las variables de estado y reaplica los filtros
    public void aplicarFiltrosAvanzados(String estado, Integer anioDesde, Integer anioHasta, String ubicacion, Boolean soloDisponibles) {
        this.filtroEstadoFisico = estado;
        this.filtroAnioDesde = anioDesde;
        this.filtroAnioHasta = anioHasta;
        this.filtroUbicacion = ubicacion;
        this.filtroSoloDisponibles = soloDisponibles;

        applyFilters(); // Re-aplicar todos los filtros
    }

    // --- 2. LÓGICA CENTRAL DE FILTRADO ---
    // Se fusionaron las dos versiones que tenías. Ahora usa listaLibrosFiltrados.
    private void applyFilters() {
        String searchText = txtSearch.getText().trim();
        Categoria selectedCategory = cmbFiltroCategoria.getSelectionModel().getSelectedItem();
        Long categoryId = (selectedCategory == null || selectedCategory.getIdCategoria() == -1L) ? null : selectedCategory.getIdCategoria();

        try {
            // --- LLAMADA AL NUEVO MÉTODO COMPLETO DEL SERVICIO ---
            // ¡IMPORTANTE! Este método debe existir en ILibroService
            listaLibrosFiltrados = libroService.filtrarLibrosAvanzado(
                    searchText, categoryId,
                    filtroEstadoFisico, filtroAnioDesde, filtroAnioHasta,
                    filtroUbicacion, filtroSoloDisponibles
            );
            // -----------------------------------------------------

            updatePagination(); // Actualizar paginación con los nuevos resultados

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Error al filtrar libros: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // --- MÉTODOS DE PAGINACIÓN ---
    // Se eliminó la versión duplicada y se corrigió el uso de listaLibrosVisibles por listaLibrosFiltrados
    private void updatePagination() {
        int totalPages = (int) Math.ceil((double) listaLibrosFiltrados.size() / ROWS_PER_PAGE);
        pagination.setPageCount(totalPages == 0 ? 1 : totalPages);
        // Reseteamos a la página 1 si la página actual ya no existe
        if (pagination.getCurrentPageIndex() >= totalPages) {
            pagination.setCurrentPageIndex(0);
        }
        pagination.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, listaLibrosFiltrados.size());

        // 1. Solo actualizamos los DATOS de la tabla existente
        libroTable.setItems(FXCollections.observableArrayList(listaLibrosFiltrados.subList(fromIndex, toIndex)));

        // 2. TRUCO: Devolvemos un nodo vacío.
        // Esto le dice al componente Pagination: "No dibujes nada dentro de ti,
        // yo ya actualicé la tabla que está afuera".
        return new VBox();
    }

    // --- MANEJADORES DE EVENTOS (HANDLERS) ---

    @FXML private void handleSearch(ActionEvent event) { applyFilters(); }
    // Este método ya no se usa directamente por el listener, pero se deja por si el FXML lo referencia.
    @FXML private void handleFiltrarPorCategoria(ActionEvent event) {}

    @FXML
    private void handleAbrirFiltrosAvanzados(ActionEvent event) {
        try {
            // CORRECCIÓN: Nombre del archivo FXML en minúsculas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/filtrosLibro.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            // --- PASAMOS LA REFERENCIA DE ESTE CONTROLADOR AL SECUNDARIO ---
            FiltrosLibroController controller = loader.getController();
            controller.setMainController(this);
            // ---------------------------------------------------------------

            Stage stage = new Stage();
            stage.setTitle("Filtros Avanzados");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node)event.getSource()).getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();
            // No necesitamos llamar a applyFilters() aquí porque el controlador secundario lo llama al aplicar.
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de filtros: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    @FXML
    private void Refrescar(ActionEvent event) {
        // 1. Limpiar el campo de búsqueda de texto
        txtSearch.setText("");

        // 2. Reiniciar el ComboBox de categorías (Seleccionar "Todas")
        if (!cmbFiltroCategoria.getItems().isEmpty()) {
            cmbFiltroCategoria.getSelectionModel().selectFirst();
        }

        // 3. IMPORTANTE: Limpiar las variables de los filtros avanzados
        // (Estas variables las definimos anteriormente en tu clase)
        this.filtroEstadoFisico = null;
        this.filtroAnioDesde = null;
        this.filtroAnioHasta = null;
        this.filtroUbicacion = null;
        this.filtroSoloDisponibles = null;

        // 4. Volver a cargar la tabla con los filtros limpios
        applyFilters();

        // Opcional: Imprimir en consola para verificar
        System.out.println("Filtros limpiados y tabla refrescada.");
    }

    private void abrirDetalleLibro(Libro libro) {
        try {
            // CORRECCIÓN: Nombre del archivo FXML en minúsculas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/libroDetalle.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            LibroDetalleController controller = loader.getController();
            controller.setLibroData(libro); // Pasar los datos del libro

            Stage stage = new Stage();
            stage.setTitle("Detalles del Libro");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(libroTable.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron abrir los detalles: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNewBook(ActionEvent event) {
        abrirFormularioLibro(null, "Registrar Nuevo Libro", ((Node)event.getSource()).getScene().getWindow());
    }

    private void handleDelete(Libro libro) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Eliminar '" + libro.getTitulo() + "'?");
        alert.setContentText("Esta acción no se puede deshacer.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                libroService.deleteById(libro.getIdLibro());
                mostrarAlerta("Éxito", "Libro eliminado.", Alert.AlertType.INFORMATION);
                applyFilters(); // Recargar aplicando filtros actuales
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo eliminar el libro.", Alert.AlertType.ERROR);
            }
        }
    }

    private void abrirFormularioLibro(Libro libro, String titulo, Window owner) {
        try {
            // CORRECCIÓN: Nombre del archivo FXML en minúsculas
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/bookRegistration.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            if (libro != null) {
                loader.<BookRegistrationController>getController().setLibroParaEditar(libro);
            }
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(owner);
            stage.setResizable(false);
            stage.showAndWait();
            System.out.println("Formulario cerrado. Refrescando tabla..."); // (Opcional, para depurar)
            applyFilters();       // Vuelve a buscar los datos a la BD
            libroTable.refresh(); // Fuerza a la tabla a redibujar las celdas (importante para imágenes)


        } catch (Exception e) {
            mostrarAlerta("Error", "Error al abrir formulario: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo); alert.setTitle(titulo);
        alert.setHeaderText(null); alert.setContentText(mensaje); alert.showAndWait();
    }

    // --- CONFIGURACIÓN VISUAL DE LAS COLUMNAS (Igual que antes) ---
    private void setupTableColumns() {
        // --- 1. CONFIGURACIÓN DEL NUMERADOR (#) ---
        colNum.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    // Calcula el número: (Página actual * filas por página) + índice + 1
                    int pageIndex = pagination.getCurrentPageIndex();
                    int index = getIndex() + 1 + (pageIndex * ROWS_PER_PAGE);
                    setText(String.valueOf(index));
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
                }
            }
        });

        // --- 2. PORTADA (Igual que antes) ---
        colPortada.setCellValueFactory(new PropertyValueFactory<>("urlPortada"));
        colPortada.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(50); // Un poco más pequeño para ser elegante
                imageView.setFitWidth(35);
                imageView.setPreserveRatio(true);
                imageView.getStyleClass().add("image-view-portada");
                setAlignment(Pos.CENTER);
            }
            @Override protected void updateItem(String rutaRelativa, boolean empty) {
                super.updateItem(rutaRelativa, empty);
                if (empty || rutaRelativa == null || rutaRelativa.isBlank()) {
                    setGraphic(null);
                }else {
                    try {
                        // TRUCO DE PORTABILIDAD:
                        // Si la ruta empieza con "http" (internet) o "file:" (absoluta antigua), la dejamos igual.
                        // Si no, asumimos que es un archivo local relativo y construimos la ruta completa.
                        String urlParaMostrar;

                        if (rutaRelativa.startsWith("http") || rutaRelativa.startsWith("file:")) {
                            urlParaMostrar = rutaRelativa;
                        } else {
                            // Convertimos "imagenes_libros/foto.jpg" -> "file:/C:/Proyecto/.../imagenes_libros/foto.jpg"
                            // Esto funciona en CUALQUIER computadora automáticamente.
                            urlParaMostrar = java.nio.file.Paths.get(rutaRelativa).toAbsolutePath().toUri().toString();
                        }

                        imageView.setImage(new Image(urlParaMostrar, true));
                        setGraphic(imageView);
                    } catch (Exception e) {
                        // Si falla (borraron la foto), mostramos vacío o una imagen por defecto
                        setGraphic(null);
                    }
                }
            }
        });

        // Columnas de texto estándar
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoFisico"));
        colUbicacion.setCellValueFactory(new PropertyValueFactory<>("ubicacion"));
        colCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCategoria() != null ? cellData.getValue().getCategoria().getNombre() : "N/A"
        ));

        // --- 3. INVENTARIO MEJORADO (Estilo Badge/Pastilla) ---
        colInventario.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        colInventario.setCellFactory(col -> new TableCell<>() {
            private final Label lblBadge = new Label();

            {
                setAlignment(Pos.CENTER);
                lblBadge.getStyleClass().add("stock-badge"); // Clase base CSS
            }

            @Override
            protected void updateItem(Libro libro, boolean empty) {
                super.updateItem(libro, empty);
                if (empty || libro == null) {
                    setGraphic(null);
                    return;
                }

                int total = libro.getNumeroEjemplares() != null ? libro.getNumeroEjemplares() : 0;
                int disponibles = libro.getDisponibles() != null ? libro.getDisponibles() : 0;

                // Texto simple: Ej "5 / 10"
                lblBadge.setText(disponibles + " / " + total);

                // Limpiar estilos anteriores de color
                lblBadge.getStyleClass().removeAll("stock-alto", "stock-bajo", "stock-agotado");

                // Lógica de colores simple
                if (disponibles == 0 && total > 0) {
                    lblBadge.getStyleClass().add("stock-agotado"); // Rojo
                    lblBadge.setText("Agotado");
                } else if (disponibles <= 2) { // Puedes ajustar este umbral
                    lblBadge.getStyleClass().add("stock-bajo");    // Amarillo
                } else {
                    lblBadge.getStyleClass().add("stock-alto");    // Verde
                }

                setGraphic(lblBadge);
            }
        });

        // --- 4. ACCIONES (Igual que corregimos antes) ---
        colActions.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnView = new Button("", new Glyph("FontAwesome", "EYE"));
            private final Button btnEdit = new Button("", new Glyph("FontAwesome", "PENCIL"));
            private final Button btnDelete = new Button("", new Glyph("FontAwesome", "TRASH"));
            private final HBox pane = new HBox(8, btnView, btnEdit, btnDelete);

            {
                pane.setAlignment(Pos.CENTER);
                btnView.getStyleClass().addAll("action-button", "btn-ver");
                btnEdit.getStyleClass().addAll("action-button", "btn-editar");
                btnDelete.getStyleClass().addAll("action-button", "btn-eliminar");

                btnView.setTooltip(new Tooltip("Ver Detalles"));
                btnEdit.setTooltip(new Tooltip("Editar"));
                btnDelete.setTooltip(new Tooltip("Eliminar"));

                btnView.setOnAction(event -> abrirDetalleLibro(getItem()));
                btnEdit.setOnAction(event -> abrirFormularioLibro(getItem(), "Editar Libro", btnEdit.getScene().getWindow()));
                btnDelete.setOnAction(event -> handleDelete(getItem()));
            }

            @Override
            protected void updateItem(Libro libro, boolean empty) {
                super.updateItem(libro, empty);
                setGraphic(empty || libro == null ? null : pane);
            }
        });
    }
}