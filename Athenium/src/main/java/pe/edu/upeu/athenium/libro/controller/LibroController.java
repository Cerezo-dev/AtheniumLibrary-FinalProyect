package pe.edu.upeu.athenium.libro.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.athenium.common.components.ColumnInfo;
import pe.edu.upeu.athenium.common.components.ComboBoxAutoComplete;
import pe.edu.upeu.athenium.common.components.TableViewHelper;
import pe.edu.upeu.athenium.common.dto.ComboBoxOption;
import pe.edu.upeu.athenium.genero.entity.Genero;
import pe.edu.upeu.athenium.libro.entity.Libro;
import pe.edu.upeu.athenium.genero.service.IGeneroService;
import pe.edu.upeu.athenium.libro.service.ILibroService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Controller
public class LibroController {

    @FXML private TextField txtTitulo, txtAutor, txtIsbn, txtAnio, txtFiltroDato;
    @FXML private ComboBox<ComboBoxOption> cbxGenero;
    @FXML private TableView<Libro> tableViewBook;
    @FXML private Label lbnMsg;
    @FXML private AnchorPane miContenedor;

    @Autowired private IGeneroService gs;
    @Autowired private ILibroService ls;

    private Validator validator;
    private ObservableList<Libro> observableLibros;

    @FXML
    public void initialize() {
        System.out.println("=== INICIANDO LIBRO CONTROLLER ===");

        // Configurar validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Inicializar ObservableList
        observableLibros = FXCollections.observableArrayList();
        tableViewBook.setItems(observableLibros);

        // Cargar géneros
        cargarGeneros();

        // Configurar tabla
        configurarTabla();

        // Cargar datos iniciales
        cargarDatosIniciales();

        verificarTabla();

        System.out.println("=== CONTROLLER INICIALIZADO ===");
    }

    private void cargarGeneros() {
        try {
            List<ComboBoxOption> generos = gs.listarCombobox();
            cbxGenero.getItems().clear();
            cbxGenero.getItems().addAll(generos);
            new ComboBoxAutoComplete<>(cbxGenero);
            System.out.println("Géneros cargados: " + generos.size());
        } catch (Exception e) {
            System.err.println("Error cargando géneros: " + e.getMessage());
        }
    }

    private void configurarTabla() {
        try {
            // Limpiar columnas existentes
            tableViewBook.getColumns().clear();

            TableViewHelper<Libro> tableViewHelper = new TableViewHelper<>();
            LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
            columns.put("ID", new ColumnInfo("id", 80.0));
            columns.put("Título", new ColumnInfo("titulo", 300.0));
            columns.put("Autor", new ColumnInfo("autor", 200.0));
            columns.put("ISBN", new ColumnInfo("isbn", 150.0));
            columns.put("Género", new ColumnInfo("genero.nombre", 150.0));
            columns.put("Año", new ColumnInfo("anioPublicacion", 100.0));

            Consumer<Libro> updateAction = this::editarLibro;
            Consumer<Libro> deleteAction = this::eliminarLibro;

            tableViewHelper.addColumnsInOrderWithSize(tableViewBook, columns, updateAction, deleteAction);
            tableViewBook.setTableMenuButtonVisible(true);

            System.out.println("Tabla configurada con " + tableViewBook.getColumns().size() + " columnas");

        } catch (Exception e) {
            System.err.println("Error configurando tabla: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarDatosIniciales() {
        try {
            List<Libro> libros = ls.findAll();
            observableLibros.clear();
            observableLibros.addAll(libros);

            System.out.println("Datos cargados: " + libros.size() + " libros");
            System.out.println("Items en tabla: " + tableViewBook.getItems().size());

            // Debug: mostrar los primeros 3 libros
            if (!libros.isEmpty()) {
                for (int i = 0; i < Math.min(3, libros.size()); i++) {
                    Libro libro = libros.get(i);
                    System.out.println("Libro " + (i+1) + ": " + libro.getTitulo() + " - " + libro.getAutor());
                }
            }

        } catch (Exception e) {
            System.err.println("Error cargando datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void validarFormulario() {
        System.out.println("=== INICIANDO GUARDADO ===");

        try {
            limpiarError();

            // Crear libro
            Libro libro = new Libro();
            libro.setTitulo(txtTitulo.getText().trim());
            libro.setAutor(txtAutor.getText().trim());
            libro.setIsbn(txtIsbn.getText().trim());

            try {
                libro.setAnioPublicacion(Integer.parseInt(txtAnio.getText().trim()));
            } catch (NumberFormatException e) {
                libro.setAnioPublicacion(null);
            }

            // Obtener género
            ComboBoxOption generoSeleccionado = cbxGenero.getSelectionModel().getSelectedItem();
            if (generoSeleccionado != null && !"0".equals(generoSeleccionado.getKey())) {
                Long generoId = Long.parseLong(generoSeleccionado.getKey());
                Genero genero = gs.findById(generoId);
                libro.setGenero(genero);
            }

            // Validar
            Set<ConstraintViolation<Libro>> violaciones = validator.validate(libro);
            if (!violaciones.isEmpty()) {
                mostrarErroresValidacion(violaciones);
                return;
            }

            // Guardar
            guardarLibro(libro);

        } catch (Exception e) {
            System.err.println("Error en validarFormulario: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error: " + e.getMessage());
        }
    }

    private void guardarLibro(Libro libro) {
        try {
            Libro libroGuardado = ls.save(libro);
            System.out.println("✅ LIBRO GUARDADO - ID: " + libroGuardado.getId());

            // ACTUALIZAR LA TABLA INMEDIATAMENTE
            actualizarTabla();

            mostrarExito("Libro guardado: " + libroGuardado.getTitulo());
            clearForm();

        } catch (Exception e) {
            System.err.println("❌ ERROR GUARDANDO: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    /**
     * MÉTODO CLAVE: ACTUALIZAR LA TABLA
     */
    private void actualizarTabla() {
        try {
            // 1. Obtener todos los libros de la base de datos
            List<Libro> librosActualizados = ls.findAll();
            System.out.println("🔄 Actualizando tabla... Libros en BD: " + librosActualizados.size());

            // 2. Limpiar y actualizar la ObservableList
            observableLibros.clear();
            observableLibros.addAll(librosActualizados);

            // 3. Forzar refresh de la tabla
            tableViewBook.refresh();

            // 4. Verificar que se actualizó
            System.out.println("✅ Tabla actualizada. Items visibles: " + tableViewBook.getItems().size());

            // 5. Debug adicional
            if (!librosActualizados.isEmpty()) {
                System.out.println("Último libro guardado: " +
                        librosActualizados.get(librosActualizados.size() - 1).getTitulo());
            }

        } catch (Exception e) {
            System.err.println("❌ Error actualizando tabla: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editarLibro(Libro libro) {
        System.out.println("Editando libro: " + libro.getTitulo());

        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtIsbn.setText(libro.getIsbn() != null ? libro.getIsbn() : "");
        txtAnio.setText(libro.getAnioPublicacion() != null ? libro.getAnioPublicacion().toString() : "");

        if (libro.getGenero() != null) {
            cbxGenero.getItems().stream()
                    .filter(opt -> opt.getKey().equals(libro.getGenero().getId().toString()))
                    .findFirst()
                    .ifPresent(opt -> cbxGenero.getSelectionModel().select(opt));
        }

        limpiarError();
    }

    private void eliminarLibro(Libro libro) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Eliminar el libro: " + libro.getTitulo() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                ls.deleteById(libro.getId());
                System.out.println("✅ Libro eliminado: " + libro.getId());
                actualizarTabla();
            } catch (Exception e) {
                System.err.println("❌ Error eliminando: " + e.getMessage());
                mostrarError("Error al eliminar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void clearForm() {
        txtTitulo.clear();
        txtAutor.clear();
        txtIsbn.clear();
        txtAnio.clear();
        cbxGenero.getSelectionModel().clearSelection();
        limpiarError();
        System.out.println("Formulario limpiado");
    }

    @FXML
    private void limpiarError() {
        lbnMsg.setText("");
    }

    private void mostrarError(String mensaje) {
        lbnMsg.setText(mensaje);
        lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
    }

    private void mostrarExito(String mensaje) {
        lbnMsg.setText(mensaje);
        lbnMsg.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
    }

    private void mostrarErroresValidacion(Set<ConstraintViolation<Libro>> violaciones) {
        limpiarError();
        if (!violaciones.isEmpty()) {
            String primerError = violaciones.iterator().next().getMessage();
            mostrarError(primerError);
        }
    }
    private void verificarTabla() {
        System.out.println("=== VERIFICANDO TABLA ===");
        System.out.println("TableView: " + (tableViewBook != null ? "NO NULL" : "NULL"));
        System.out.println("Columnas: " + tableViewBook.getColumns().size());
        System.out.println("Items: " + tableViewBook.getItems().size());
        System.out.println("ObservableList: " + (observableLibros != null ? observableLibros.size() : "NULL"));

        if (!observableLibros.isEmpty()) {
            System.out.println("Primer libro en observable: " + observableLibros.get(0).getTitulo());
        }
    }
}