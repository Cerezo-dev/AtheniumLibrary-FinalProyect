package pe.edu.upeu.syslibrary.controller;

// Importaciones necesarias de JavaFX, Spring y tu propio proyecto
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.model.Categoria;
import pe.edu.upeu.syslibrary.model.Libro;
import pe.edu.upeu.syslibrary.service.ICategoriaService;
import pe.edu.upeu.syslibrary.service.ILibroService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

// La anotación @Controller permite que Spring maneje esta clase
@Controller
public class BookRegistrationController {

    // --- CONEXIONES CON EL FXML (Deben coincidir exactamente con los fx:id) ---
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtISBN;
    @FXML private Button btnBuscarISBN; // Referencia al botón para poder deshabilitarlo si es necesario
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private TextField txtAnio;
    @FXML private TextField txtEditorial;
    @FXML private TextField txtNumeroEjemplares;
    @FXML private TextField txtCantidadDisponible;
    @FXML private TextField txtUbicacion;
    @FXML private ComboBox<String> cmbEstadoFisico;
    @FXML private TextField txtCodigoBarras;
    @FXML private TextField txtUrlPortada;
    @FXML private TextArea txtDescripcion;
    //@FXML private TextField txtUrlPortada;
    @FXML private ImageView imgPreview;

    // --- INYECCIÓN DE SERVICIOS (Para hablar con la base de datos) ---
    @Autowired private ILibroService libroService;
    @Autowired private ICategoriaService categoriaService;

    // Variable para almacenar el libro que se está editando. Si es null, es un libro nuevo.
    private Libro libroEnEdicion = null;

    // --- MÉTODO DE INICIALIZACIÓN (Se ejecuta automáticamente al abrir la ventana) ---
    @FXML
    public void initialize() {
        loadCategories();      // Carga las categorías en el ComboBox
        loadEstadosFisicos();  // Carga los estados físicos en el ComboBox
        setupListeners();      // Configura comportamientos automáticos de los campos
    }

    // --- MÉTODO PARA RECIBIR UN LIBRO PARA EDITAR ---
    // Este método es llamado desde el LibroController cuando se hace clic en el botón "Editar".
    public void setLibroParaEditar(Libro libro) {
        this.libroEnEdicion = libro; // Guardamos la referencia al libro

        // Llenamos los campos de texto con los datos del libro
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtISBN.setText(libro.getIsbn());
        txtEditorial.setText(libro.getEditorial());
        txtUbicacion.setText(libro.getUbicacion());
        txtCodigoBarras.setText(libro.getCodigoBarras());
        txtUrlPortada.setText(libro.getUrlPortada());
        txtDescripcion.setText(libro.getDescripcion());

        // Llenamos los campos numéricos (manejando posibles valores nulos)
        txtAnio.setText(libro.getAnio() != null ? libro.getAnio().toString() : "");
        txtNumeroEjemplares.setText(libro.getNumeroEjemplares() != null ? libro.getNumeroEjemplares().toString() : "");
        txtCantidadDisponible.setText(libro.getDisponibles() != null ? libro.getDisponibles().toString() : "");

        // Seleccionamos los valores correspondientes en los ComboBox
        cmbCategoria.getSelectionModel().select(libro.getCategoria());
        cmbEstadoFisico.getSelectionModel().select(libro.getEstadoFisico());
    }

    // --- MÉTODOS DE ACCIÓN DE LOS BOTONES ---

    // Acción del botón "Buscar" (ISBN)
    @FXML
    private void handleSearchISBN(ActionEvent event) {
        // Por ahora, solo muestra un mensaje informativo.
        // Aquí se implementaría la lógica para buscar el ISBN en una API externa (ej. Open Library).
        mostrarAlerta("Funcionalidad en desarrollo", "La búsqueda automática por ISBN estará disponible pronto.", Alert.AlertType.INFORMATION);
    }

    // Acción del botón "Generar" (Código de Barras)
    @FXML
    private void handleGenerateBarcode(ActionEvent event) {
        // Genera un código de barras simple basado en la hora actual para asegurar unicidad.
        // En un sistema real, podrías usar una lógica más compleja o un prefijo específico.
        txtCodigoBarras.setText("LIB" + System.currentTimeMillis());
    }

    // Acción del botón "Guardar"
    @FXML
    private void handleSave(ActionEvent event) {
        try {
            // 1. Validación básica de campos obligatorios
            if (txtTitulo.getText().isEmpty() || txtAutor.getText().isEmpty()) {
                mostrarAlerta("Error de Validación", "El Título y el Autor son campos obligatorios.", Alert.AlertType.ERROR);
                return; // Detiene el proceso si falla la validación
            }
            Categoria categoria = cmbCategoria.getSelectionModel().getSelectedItem();
            if (categoria == null) {
                mostrarAlerta("Error de Validación", "Debe seleccionar una Categoría para el libro.", Alert.AlertType.ERROR);
                return;
            }

            // 2. Determinar si estamos creando un libro nuevo o editando uno existente
            // Si libroEnEdicion es null, creamos una nueva instancia. Si no, usamos la existente.
            Libro libroAGuardar = (libroEnEdicion == null) ? new Libro() : libroEnEdicion;

            // 3. Transferir los datos de los campos del formulario al objeto Libro
            libroAGuardar.setTitulo(txtTitulo.getText());
            libroAGuardar.setAutor(txtAutor.getText());
            libroAGuardar.setIsbn(txtISBN.getText());
            libroAGuardar.setEditorial(txtEditorial.getText());
            libroAGuardar.setUbicacion(txtUbicacion.getText());
            libroAGuardar.setCodigoBarras(txtCodigoBarras.getText());
            libroAGuardar.setUrlPortada(txtUrlPortada.getText());
            libroAGuardar.setDescripcion(txtDescripcion.getText());

            // Usamos un método auxiliar para convertir texto a número de forma segura
            libroAGuardar.setAnio(parseIntSafe(txtAnio.getText()));
            libroAGuardar.setNumeroEjemplares(parseIntSafe(txtNumeroEjemplares.getText()));

            // Lógica para la cantidad disponible:
            // Si es un libro nuevo, la cantidad disponible es igual a la cantidad total.
            // Si se está editando, se toma el valor que el usuario haya podido modificar en el campo (si estuviera habilitado).
            // Para este diseño, donde el campo 'disponibles' suele estar deshabilitado y se calcula, esta lógica puede refinarse.
            // Por ahora, si es nuevo, se iguala al total. Si se edita, se mantiene el valor del campo (que el listener actualiza).
            if (libroEnEdicion == null) {
                libroAGuardar.setDisponibles(parseIntSafe(txtNumeroEjemplares.getText()));
            } else {
                libroAGuardar.setDisponibles(parseIntSafe(txtCantidadDisponible.getText()));
            }


            libroAGuardar.setCategoria(categoria);
            libroAGuardar.setEstadoFisico(cmbEstadoFisico.getValue());


            // 4. Guardar el libro en la base de datos usando el servicio
            // El método save() de Spring Data JPA sirve tanto para crear (si no tiene ID) como para actualizar (si tiene ID).
            libroService.save(libroAGuardar);

            // 5. Mostrar mensaje de éxito y cerrar la ventana
            mostrarAlerta("Operación Exitosa", "El libro ha sido guardado correctamente.", Alert.AlertType.INFORMATION);
            cerrarVentana(event);

        } catch (Exception e) {
            // Manejo de errores: Muestra una alerta si algo sale mal durante el proceso de guardado.
            mostrarAlerta("Error al Guardar", "Ocurrió un error al intentar guardar el libro:\n" + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(); // Imprime el error en la consola para depuración
        }
    }

    // Acción del botón "Cancelar"
    @FXML
    private void handleCancel(ActionEvent event) {
        cerrarVentana(event); // Simplemente cierra la ventana
    }

    // --- MÉTODOS AUXILIARES (Privados) ---

    // Método para cerrar la ventana actual
    private void cerrarVentana(ActionEvent event) {
        // Obtiene la fuente del evento (el botón), luego su escena, luego su ventana (Stage) y la cierra.
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    // Método para convertir un String a Integer de forma segura (evita excepciones si el texto no es un número)
    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0; // Devuelve 0 si no se puede convertir (por ejemplo, si el campo está vacío)
        }
    }

    // Método para mostrar alertas gráficas al usuario
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null); // Sin encabezado para un diseño más limpio
        alert.setContentText(mensaje);
        alert.showAndWait(); // Muestra la alerta y espera a que el usuario la cierre
    }

    // Carga las categorías desde la base de datos y las añade al ComboBox
    private void loadCategories() {
        try {
            cmbCategoria.getItems().setAll(categoriaService.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudieron cargar las categorías.", Alert.AlertType.ERROR);
        }
    }

    // Carga una lista fija de estados físicos en el ComboBox
    private void loadEstadosFisicos() {
        cmbEstadoFisico.getItems().addAll("Bueno", "Regular", "Malo", "En Reparación");
        cmbEstadoFisico.getSelectionModel().selectFirst(); // Selecciona "Bueno" por defecto
    }

    // Configura "listeners" para comportamientos automáticos de los campos
    private void setupListeners() {
        // Listener para el campo "Cantidad Total":
        // Cuando el texto cambia, si el nuevo valor es un número válido...
        txtNumeroEjemplares.textProperty().addListener((obs, old, newVal) -> {
            if (newVal.matches("\\d*")) {
                // ... y si estamos creando un libro NUEVO, actualizamos automáticamente la "Cantidad Disponible"
                // para que coincida con la cantidad total.
                if (libroEnEdicion == null) {
                    txtCantidadDisponible.setText(newVal);
                }
            }
        });
    }
    @FXML
    private void handleSeleccionarImagen(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Portada");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

        Window window = ((Node) event.getSource()).getScene().getWindow();
        File archivoSeleccionado = fileChooser.showOpenDialog(window);

        if (archivoSeleccionado != null) {
            try {
                // 1. Definir la carpeta de destino dentro de tu proyecto
                // "imagenes_libros" se creará al lado de tu carpeta src o el ejecutable
                String nombreCarpeta = "imagenes_libros";
                Path carpetaDestino = Paths.get(nombreCarpeta);

                // Crear la carpeta si no existe
                if (!Files.exists(carpetaDestino)) {
                    Files.createDirectories(carpetaDestino);
                }

                // 2. Generar un nombre único para evitar que se repitan (Ej: libro_a1b2.jpg)
                // Usamos la extensión original del archivo
                String nombreOriginal = archivoSeleccionado.getName();
                String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
                String nuevoNombre = "libro_" + UUID.randomUUID().toString() + extension;

                // 3. Copiar el archivo
                Path destinoFinal = carpetaDestino.resolve(nuevoNombre);
                Files.copy(archivoSeleccionado.toPath(), destinoFinal, StandardCopyOption.REPLACE_EXISTING);

                // 4. Guardar LA RUTA RELATIVA en el campo de texto
                // Guardamos: imagenes_libros/libro_xyz.jpg
                txtUrlPortada.setText(destinoFinal.toString());

                // 5. Mostrar previsualización (Usamos toURI para cargarla visualmente ahora)
                imgPreview.setImage(new Image(destinoFinal.toUri().toString()));

            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo copiar la imagen: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

}