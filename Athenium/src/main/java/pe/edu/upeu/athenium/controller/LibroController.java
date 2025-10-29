package pe.edu.upeu.athenium.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.athenium.components.ColumnInfo;
import pe.edu.upeu.athenium.components.ComboBoxAutoComplete;
import pe.edu.upeu.athenium.components.TableViewHelper;
import pe.edu.upeu.athenium.components.Toast;
import pe.edu.upeu.athenium.dto.ComboBoxOption;
import pe.edu.upeu.athenium.model.Genero;
import pe.edu.upeu.athenium.model.Libro;
import pe.edu.upeu.athenium.service.IGeneroService;
import pe.edu.upeu.athenium.service.ILibroService;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * FASE 2: Prototipo CRUD de 'Libro' (Esqueleto)
 * REFACTORIZACIÓN TOTAL:
 * 1. Este controlador AHORA coincide con 'main_producto.fxml' refactorizado.
 * 2. Eliminados los campos de 'Producto' (txtPUnit, txtStock, cbxMarca, etc.).
 * 3. Añadidos los campos de 'Libro' (txtTitulo, txtAutor, txtIsbn, cbxGenero).
 * 4. La lógica de 'validarFormulario', 'listar', 'editForm', 'clearForm'
 * ha sido actualizada para usar el modelo 'Libro' y sus servicios.
 * 5. Este es el prototipo de controlador CRUD para tu equipo.
 */
@Controller
public class LibroController {

    // --- CAMPOS FXML REFACTORIZADOS ---
    @FXML
    TextField txtTitulo, txtAutor, txtIsbn, txtAnio, txtFiltroDato;
    @FXML
    ComboBox<ComboBoxOption> cbxGenero;
    @FXML
    private TableView<Libro> tableView;
    @FXML
    Label lbnMsg;
    @FXML
    private AnchorPane miContenedor;

    Stage stage;

    // --- SERVICIOS INYECTADOS ---
    @Autowired
    IGeneroService gs; // Servicio para Géneros (antes CategoriaService)
    @Autowired
    ILibroService ls; // Servicio para Libros (antes ProductoService)

    private Validator validator;
    ObservableList<Libro> listarLibro;
    Libro formulario;
    Long idLibroEdicion = 0L; // Para saber si estamos editando o creando

    /**
     * Filtra la tabla en tiempo real.
     * Ahora busca en título, autor e isbn (RF-Catálogo)
     */
    private void filtrarLibros(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            tableView.getItems().clear();
            tableView.getItems().addAll(listarLibro);
            return;
        }
        String lower = filtro.toLowerCase();
        List<Libro> filtrados = listarLibro.stream()
                .filter(libro ->
                        (libro.getTitulo() != null && libro.getTitulo().toLowerCase().contains(lower)) ||
                                (libro.getAutor() != null && libro.getAutor().toLowerCase().contains(lower)) ||
                                (libro.getIsbn() != null && libro.getIsbn().toLowerCase().contains(lower))
                )
                .collect(Collectors.toList());
        tableView.getItems().clear();
        tableView.getItems().addAll(filtrados);
    }

    /**
     * Carga todos los libros del servicio en la tabla.
     */
    public void listar(){
        try {
            tableView.getItems().clear();
            listarLibro = FXCollections.observableArrayList(ls.findAll());
            tableView.getItems().addAll(listarLibro);
            // Listener para el filtro automático (RF-Catálogo)
            txtFiltroDato.textProperty().addListener((observable, oldValue, newValue) ->
                    filtrarLibros(newValue));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        // Lógica para obtener el Stage (igual que en el PDF)
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500),
                event -> {
                    stage = (Stage) miContenedor.getScene().getWindow();
                    if (stage == null) {
                        System.out.println("Stage aún no disponible.");
                    }
                }));
        timeline.setCycleCount(1);
        timeline.play();

        // Cargar Géneros en el ComboBox
        cbxGenero.getItems().addAll(gs.listarCombobox());
        new ComboBoxAutoComplete<>(cbxGenero);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // --- DEFINICIÓN DE COLUMNAS REFACTORIZADA ---
        TableViewHelper<Libro> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("id", 60.0));
        columns.put("Título", new ColumnInfo("titulo", 250.0));
        columns.put("Autor", new ColumnInfo("autor", 200.0));
        columns.put("ISBN", new ColumnInfo("isbn", 150.0));
        // ¡Magia! Accede a la propiedad anidada 'nombre' de 'genero'
        columns.put("Género", new ColumnInfo("genero.nombre", 180.0));
        columns.put("Año", new ColumnInfo("anioPublicacion", 100.0));

        // Acciones de los botones de la tabla
        Consumer<Libro> updateAction = this::editForm;
        Consumer<Libro> deleteAction = (Libro libro) -> {
            if (mostrarConfirmacion("¿Está seguro de eliminar el libro: " + libro.getTitulo() + "?")) {
                ls.delete(libro.getId());
                //ls.deleteById(libro.getId()); // ¡Corregido! Usar deleteById
                mostrarToast("Se eliminó correctamente!!");
                listar();
            }
        };

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);
        listar(); // Cargar datos al iniciar
    }

    /**
     * Limpia los estilos de error rojos de los campos.
     */
    @FXML
    public void limpiarError() {
        List<Control> controles = List.of(txtTitulo, txtAutor, txtIsbn, txtAnio, cbxGenero);
        controles.forEach(c -> c.getStyleClass().remove("text-field-error"));
        lbnMsg.setText(""); // Limpiar mensaje de error
    }

    /**
     * Limpia el formulario y resetea el ID de edición.
     */
    @FXML
    public void clearForm() {
        txtTitulo.clear();
        txtAutor.clear();
        txtIsbn.clear();
        txtAnio.clear();
        cbxGenero.getSelectionModel().clearSelection();
        idLibroEdicion = 0L;
        limpiarError();
    }

    /**
     * Carga los datos de un libro de la tabla en el formulario para editarlo.
     */
    public void editForm(Libro libro){
        if (libro == null) return;

        idLibroEdicion = libro.getId(); // Marcar que estamos editando

        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtIsbn.setText(libro.getIsbn() == null ? "" : libro.getIsbn());
        txtAnio.setText(libro.getAnioPublicacion() == 0 ? "" : String.valueOf(libro.getAnioPublicacion()));

        // Seleccionar el Género correcto en el ComboBox
        if (libro.getGenero() != null) {
            cbxGenero.getItems().stream()
                    .filter(opt -> opt.getKey().equals(String.valueOf(libro.getGenero().getId())))
                    .findFirst()
                    .ifPresent(opt -> cbxGenero.getSelectionModel().select(opt));
        } else {
            cbxGenero.getSelectionModel().clearSelection();
        }

        limpiarError();
    }

    // --- Métodos de Ayuda (Helpers) ---
    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private void mostrarToast(String message) {
        if (stage == null) return;
        double width = stage.getWidth() / 1.5;
        double h = stage.getHeight() / 2;
        Toast.showToast(stage, message, 2000, width, h);
    }

    private boolean mostrarConfirmacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    /**
     * Muestra los errores de validación (JSR 380) en la UI.
     */
    private void mostrarErroresValidacion(Set<ConstraintViolation<Libro>> violaciones) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("titulo", txtTitulo);
        campos.put("autor", txtAutor);
        campos.put("isbn", txtIsbn);
        campos.put("anioPublicacion", txtAnio);
        campos.put("genero", cbxGenero);

        LinkedHashMap<String, String> erroresOrdenados = new LinkedHashMap<>();
        violaciones.forEach(v -> erroresOrdenados.put(v.getPropertyPath().toString(), v.getMessage()));

        final Control[] primerControlConError = {null};
        for (String campo : campos.keySet()) {
            if (erroresOrdenados.containsKey(campo)) {
                Control c = campos.get(campo);
                c.getStyleClass().add("text-field-error"); // Aplica CSS de error
                if (primerControlConError[0] == null) primerControlConError[0] = c;
            }
        }
        if (!erroresOrdenados.isEmpty()) {
            lbnMsg.setText(erroresOrdenados.values().stream().findFirst().orElse("Error en formulario"));
            lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            if (primerControlConError[0] != null) primerControlConError[0].requestFocus();
        }
    }

    /**
     * Procesa el guardado o actualización después de que la validación sea exitosa.
     */
    private void procesarFormulario() {
        try {
            if (idLibroEdicion > 0L) {
                // El 'formulario' ya tiene el ID, así que 'update' funcionará
                ls.update(formulario);
                //ls.update(idLibroEdicion, formulario);
                mostrarToast("Libro actualizado correctamente");
            } else {
                ls.save(formulario);
                mostrarToast("Libro registrado correctamente");
            }
            clearForm();
            listar();
        } catch (Exception ex) {
            // Capturar errores de BD (ej. ISBN duplicado)
            lbnMsg.setText("Error al guardar: " + ex.getMessage());
            lbnMsg.setStyle("-fx-text-fill: red;");
            ex.printStackTrace();
        }
    }

    /**
     * Método principal llamado por el botón "Guardar".
     * Valida el formulario y luego llama a procesarFormulario.
     */
    @FXML
    public void validarFormulario() {
        limpiarError();

        formulario = new Libro();
        formulario.setTitulo(txtTitulo.getText());
        formulario.setAutor(txtAutor.getText());
        formulario.setIsbn(txtIsbn.getText());
        formulario.setAnioPublicacion(parseIntSafe(txtAnio.getText()));

        // Obtener el Género seleccionado del ComboBox
        ComboBoxOption selGen = cbxGenero.getSelectionModel().getSelectedItem();
        if (selGen != null && !"0".equals(selGen.getKey())) {
            try {
                Long idG = Long.parseLong(selGen.getKey());
                // Buscamos el objeto Genero completo
                Genero g = gs.findById(idG);
                formulario.setGenero(g);
            } catch (Exception ignored) {
                formulario.setGenero(null);
            }
        } else {
            formulario.setGenero(null); // No se seleccionó género
        }

        // Si estamos editando, asignar el ID al formulario
        if (idLibroEdicion > 0L) {
            formulario.setId(idLibroEdicion);
        }

        // Validar el objeto 'formulario' usando JSR 380 (jakarta.validation)
        Set<ConstraintViolation<Libro>> violaciones = validator.validate(formulario);
        if (!violaciones.isEmpty()) {
            // Si hay errores, mostrarlos y detener
            mostrarErroresValidacion(violaciones);
            return;
        }

        // Si no hay errores, procesar
        procesarFormulario();
    }
}
