package pe.edu.upeu.athenium.libro.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.beans.property.SimpleStringProperty; // Importación necesaria
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable; // Importación necesaria
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory; // Importación necesaria
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
// Se eliminan imports de TableViewHelper y ColumnInfo
import pe.edu.upeu.athenium.common.components.ComboBoxAutoComplete;
import pe.edu.upeu.athenium.common.dto.ComboBoxOption;
import pe.edu.upeu.athenium.genero.entity.Genero;
import pe.edu.upeu.athenium.libro.entity.Libro;
import pe.edu.upeu.athenium.genero.service.IGeneroService;
import pe.edu.upeu.athenium.libro.service.ILibroService;

import java.net.URL; // Importación necesaria
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional; // Importación necesaria
import java.util.ResourceBundle; // Importación necesaria
import java.util.Set;
import java.util.function.Consumer;

@Controller
public class LibroController implements Initializable { // Implementar Initializable

    @FXML private TextField txtTitulo, txtAutor, txtIsbn, txtAnio, txtFiltroDato;
    @FXML private ComboBox<ComboBoxOption> cbxGenero;
    @FXML private TableView<Libro> tableViewBook;
    @FXML private Label lbnMsg;
    @FXML private AnchorPane miContenedor;

    // (Asegúrate de que tu FXML tenga fx:id para estos botones si los mantienes)
    @FXML private Button btnGuardar, btnCancelar, btnEditar, btnEliminar;
    @FXML private TextField txtIdLibro; // Agregado: para el ID del libro

    @Autowired private IGeneroService gs;
    @Autowired private ILibroService ls;

    private Validator validator;
    private ObservableList<Libro> observableLibros;
    private Libro libroSeleccionadoParaEditar = null; // Para saber si estamos editando o guardando

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        configurarTabla(); // <-- Este método ha cambiado

        // Cargar datos iniciales
        cargarDatosIniciales();

        // Configurar filtro de búsqueda
        txtFiltroDato.textProperty().addListener((obs, oldText, newText) -> filtrarLibrosPorTexto(newText));

        // Listener para la selección de la tabla (para habilitar/deshabilitar botones)
        tableViewBook.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean selected = (newSelection != null);
            if (btnEditar != null) btnEditar.setDisable(!selected);
            if (btnEliminar != null) btnEliminar.setDisable(!selected);
        });

        // Inicialmente, deshabilitar botones
        if (btnEditar != null) btnEditar.setDisable(true);
        if (btnEliminar != null) btnEliminar.setDisable(true);
        if (txtIdLibro != null) {
            txtIdLibro.setEditable(false);
            txtIdLibro.setDisable(true);
        }

        System.out.println("=== CONTROLLER INICIALIZADO ===");
    }

    private void cargarGeneros() {
        try {
            List<ComboBoxOption> generos = gs.listarCombobox();
            cbxGenero.getItems().clear();
            cbxGenero.getItems().addAll(generos);
            new ComboBoxAutoComplete<>(cbxGenero); // Asumiendo que este componente funciona
            System.out.println("Géneros cargados: " + generos.size());
        } catch (Exception e) {
            System.err.println("Error cargando géneros: " + e.getMessage());
            mostrarError("Error cargando géneros.");
        }
    }

    // ==========================================================
    // MÉTODO 'configurarTabla' ACTUALIZADO (Sin TableViewHelper)
    // ==========================================================
    private void configurarTabla() {
        try {
            tableViewBook.getColumns().clear();

            // Columna ID (Propiedad simple)
            TableColumn<Libro, Long> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colId.setPrefWidth(80);

            // Columna Título (Propiedad simple)
            TableColumn<Libro, String> colTitulo = new TableColumn<>("Título");
            colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
            colTitulo.setPrefWidth(250);

            // Columna Autor (Propiedad simple)
            TableColumn<Libro, String> colAutor = new TableColumn<>("Autor");
            colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
            colAutor.setPrefWidth(180);

            // Columna ISBN (Propiedad simple)
            TableColumn<Libro, String> colIsbn = new TableColumn<>("ISBN");
            colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
            colIsbn.setPrefWidth(120);

            // Columna Año (Propiedad simple)
            TableColumn<Libro, Integer> colAnio = new TableColumn<>("Año");
            colAnio.setCellValueFactory(new PropertyValueFactory<>("anioPublicacion"));
            colAnio.setPrefWidth(80);

            // Columna Género (Propiedad ANIDADA - "genero.nombre")
            // Se usa un CellValueFactory personalizado
            TableColumn<Libro, String> colGenero = new TableColumn<>("Género");
            colGenero.setCellValueFactory(cellData -> {
                Libro libro = cellData.getValue();
                if (libro != null && libro.getGenero() != null) {
                    return new SimpleStringProperty(libro.getGenero().getNombre());
                } else {
                    return new SimpleStringProperty("N/A");
                }
            });
            colGenero.setPrefWidth(120);

            // Añadir las columnas de datos
            tableViewBook.getColumns().addAll(colId, colTitulo, colAutor, colIsbn, colGenero, colAnio);

            // --- Replicar los botones de acción que hacía tu TableViewHelper ---

            // Columna Editar (Botón)
            TableColumn<Libro, Void> colEditar = new TableColumn<>("Editar");
            colEditar.setCellFactory(param -> new TableCell<>() {
                private final Button btn = new Button("Editar");
                {
                    btn.getStyleClass().add("btn-outline-info-table"); // Clase CSS para el botón
                    btn.setOnAction(event -> {
                        Libro libro = getTableView().getItems().get(getIndex());
                        editarLibro(libro); // Llama a tu método existente
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btn);
                    }
                }
            });
            colEditar.setPrefWidth(100);

            // Columna Eliminar (Botón)
            TableColumn<Libro, Void> colEliminar = new TableColumn<>("Eliminar");
            colEliminar.setCellFactory(param -> new TableCell<>() {
                private final Button btn = new Button("Eliminar");
                {
                    btn.getStyleClass().add("btn-outline-danger-table"); // Clase CSS para el botón
                    btn.setOnAction(event -> {
                        Libro libro = getTableView().getItems().get(getIndex());
                        eliminarLibro(libro); // Llama a tu método existente
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btn);
                    }
                }
            });
            colEliminar.setPrefWidth(100);

            // Añadir las columnas de acción
            tableViewBook.getColumns().addAll(colEditar, colEliminar);

            tableViewBook.setTableMenuButtonVisible(true);
            System.out.println("Tabla configurada MANUALMENTE con " + tableViewBook.getColumns().size() + " columnas");

        } catch (Exception e) {
            System.err.println("Error configurando tabla: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error crítico al configurar la tabla.");
        }
    }
    // ==========================================================
    // FIN DEL MÉTODO ACTUALIZADO
    // ==========================================================


    private void cargarDatosIniciales() {
        try {
            List<Libro> libros = ls.findAll();
            observableLibros.clear();
            observableLibros.addAll(libros);
            System.out.println("Datos iniciales cargados: " + libros.size() + " libros");
            tableViewBook.refresh();
        } catch (Exception e) {
            System.err.println("Error cargando datos iniciales: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error cargando datos iniciales.");
        }
    }

    // Método para el campo de filtro
    private void filtrarLibrosPorTexto(String filtro) {
        try {
            List<Libro> librosFiltrados = ls.filtrarLibros(filtro);
            observableLibros.clear();
            observableLibros.addAll(librosFiltrados);
            tableViewBook.refresh();
        } catch (Exception e) {
            System.err.println("Error filtrando libros: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al filtrar libros.");
        }
    }

    @FXML
    private void validarFormulario() {
        System.out.println("=== INICIANDO GUARDADO/ACTUALIZACIÓN ===");
        limpiarError();

        try {
            Libro libro = (libroSeleccionadoParaEditar != null) ? libroSeleccionadoParaEditar : new Libro();

            libro.setTitulo(txtTitulo.getText().trim());
            libro.setAutor(txtAutor.getText().trim());
            libro.setIsbn(txtIsbn.getText().trim());

            try {
                libro.setAnioPublicacion(Integer.parseInt(txtAnio.getText().trim()));
            } catch (NumberFormatException e) {
                libro.setAnioPublicacion(null);
            }

            ComboBoxOption generoSeleccionado = cbxGenero.getSelectionModel().getSelectedItem();
            if (generoSeleccionado != null && !"0".equals(generoSeleccionado.getKey())) {
                Long generoId = Long.parseLong(generoSeleccionado.getKey());
                Genero genero = gs.findById(generoId);
                libro.setGenero(genero);
            } else {
                libro.setGenero(null);
            }

            // Validar
            Set<ConstraintViolation<Libro>> violaciones = validator.validate(libro);
            if (!violaciones.isEmpty()) {
                mostrarErroresValidacion(violaciones);
                return;
            }

            // Guardar o Actualizar
            if (libro.getId() == null) {
                // Guardar nuevo libro
                Libro libroGuardado = ls.save(libro);
                mostrarExito("Libro guardado: " + libroGuardado.getTitulo());
                System.out.println("✅ LIBRO GUARDADO - ID: " + libroGuardado.getId());
            } else {
                // Actualizar libro existente
                Libro libroActualizado = ls.update(libro.getId(), libro);
                mostrarExito("Libro actualizado: " + libroActualizado.getTitulo());
                System.out.println("✅ LIBRO ACTUALIZADO - ID: " + libroActualizado.getId());
            }

            actualizarTabla();
            clearForm();
            btnGuardar.setText("Guardar");

        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
            System.err.println("❌ Error de negocio: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error en validarFormulario: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al guardar/actualizar: " + e.getMessage());
        }
    }

    /**
     * Actualiza la tabla con los últimos datos de la base de datos.
     */
    private void actualizarTabla() {
        try {
            String filtroActual = txtFiltroDato.getText();
            List<Libro> librosActualizados = (filtroActual != null && !filtroActual.trim().isEmpty()) ?
                    ls.filtrarLibros(filtroActual) : ls.findAll();

            observableLibros.clear();
            observableLibros.addAll(librosActualizados);
            tableViewBook.refresh();
            System.out.println("✅ Tabla actualizada. Items visibles: " + tableViewBook.getItems().size());
        } catch (Exception e) {
            System.err.println("❌ Error actualizando tabla: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al actualizar la tabla.");
        }
    }

    /**
     * Carga los datos de un libro en el formulario para su edición.
     * Este método es llamado por el botón "Editar" de las filas de la tabla.
     * @param libro El libro a editar.
     */
    private void editarLibro(Libro libro) {
        if (libro != null) {
            libroSeleccionadoParaEditar = libro; // Guardamos el libro para saber que estamos editando
            txtIdLibro.setText(libro.getId().toString()); // Mostrar el ID (aunque sea deshabilitado)
            txtTitulo.setText(libro.getTitulo());
            txtAutor.setText(libro.getAutor());
            txtIsbn.setText(libro.getIsbn() != null ? libro.getIsbn() : "");
            txtAnio.setText(libro.getAnioPublicacion() != null ? libro.getAnioPublicacion().toString() : "");

            if (libro.getGenero() != null) {
                cbxGenero.getItems().stream()
                        .filter(opt -> opt.getKey().equals(libro.getGenero().getId().toString()))
                        .findFirst()
                        .ifPresent(opt -> cbxGenero.getSelectionModel().select(opt));
            } else {
                cbxGenero.getSelectionModel().clearSelection(); // Limpiar si no hay género
            }
            limpiarError();
            btnGuardar.setText("Actualizar"); // Cambiar texto del botón para indicar edición
            mostrarExito("Libro '" + libro.getTitulo() + "' cargado para edición.");
            System.out.println("Libro cargado para edición: " + libro.getTitulo());
        }
    }

    /**
     * Confirma y elimina un libro.
     * Este método es llamado por el botón "Eliminar" de las filas de la tabla.
     * @param libro El libro a eliminar.
     */
    private void eliminarLibro(Libro libro) {
        if (libro == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Estás seguro de que quieres eliminar este libro?");
        alert.setContentText("Libro: " + libro.getTitulo() + " (ID: " + libro.getId() + ")");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ls.deleteById(libro.getId());
                System.out.println("✅ Libro eliminado: " + libro.getTitulo() + " (ID: " + libro.getId() + ")");
                actualizarTabla(); // Refrescar la tabla después de la eliminación
                clearForm(); // Limpiar formulario si el libro eliminado estaba cargado
                mostrarExito("Libro '" + libro.getTitulo() + "' eliminado exitosamente.");
            } catch (Exception e) {
                System.err.println("❌ Error eliminando: " + e.getMessage());
                e.printStackTrace();
                mostrarError("Error al eliminar el libro: " + e.getMessage());
            }
        }
    }


    // ==========================================================
    // Métodos para los botones del formulario (edit, delete, clearForm)
    // ==========================================================

    @FXML
    private void edit() {
        Libro selectedLibro = tableViewBook.getSelectionModel().getSelectedItem();
        if (selectedLibro != null) {
            editarLibro(selectedLibro); // Reutiliza la lógica de carga
        } else {
            mostrarError("Selecciona un libro de la tabla para editar.");
        }
    }

    @FXML
    private void delete() {
        Libro selectedLibro = tableViewBook.getSelectionModel().getSelectedItem();
        if (selectedLibro != null) {
            eliminarLibro(selectedLibro); // Reutiliza la lógica de eliminación
        } else {
            mostrarError("Selecciona un libro de la tabla para eliminar.");
        }
    }

    @FXML
    private void clearForm() {
        txtIdLibro.clear(); // Limpiar el ID también
        txtTitulo.clear();
        txtAutor.clear();
        txtIsbn.clear();
        txtAnio.clear();
        cbxGenero.getSelectionModel().clearSelection();
        limpiarError();
        libroSeleccionadoParaEditar = null; // Resetear estado de edición
        btnGuardar.setText("Guardar"); // Restaurar texto del botón
        System.out.println("Formulario limpiado");
        tableViewBook.getSelectionModel().clearSelection(); // Deseleccionar de la tabla
    }

    // ==========================================================
    // Métodos de UI para mensajes
    // ==========================================================

    @FXML
    private void limpiarError() {
        lbnMsg.setText("");
        lbnMsg.setStyle("-fx-text-fill: -text-primary-white;"); // O el color por defecto de tus labels
    }

    private void mostrarError(String mensaje) {
        lbnMsg.setText(mensaje);
        lbnMsg.setStyle("-fx-text-fill: #EF5350; -fx-font-size: 14px; -fx-font-weight: bold;"); // Rojo
    }

    private void mostrarExito(String mensaje) {
        lbnMsg.setText(mensaje);
        lbnMsg.setStyle("-fx-text-fill: #66BB6A; -fx-font-size: 14px; -fx-font-weight: bold;"); // Verde
    }

    private void mostrarErroresValidacion(Set<ConstraintViolation<Libro>> violaciones) {
        limpiarError();
        if (!violaciones.isEmpty()) {
            StringBuilder sb = new StringBuilder("Errores de validación:\n");
            violaciones.forEach(v -> sb.append("- ").append(v.getMessage()).append("\n"));
            mostrarError(sb.toString());
        }
    }
}