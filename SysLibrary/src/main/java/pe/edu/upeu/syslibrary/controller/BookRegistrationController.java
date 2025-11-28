package pe.edu.upeu.syslibrary.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.model.Categoria;
import pe.edu.upeu.syslibrary.model.Libro;
import pe.edu.upeu.syslibrary.service.GoogleBooksService;
import pe.edu.upeu.syslibrary.service.ICategoriaService;
import pe.edu.upeu.syslibrary.service.ILibroService;

import java.util.List;

@Controller
public class BookRegistrationController {

    @Autowired private ILibroService libroService;
    @Autowired private ICategoriaService categoriaService;
    @Autowired private GoogleBooksService googleBooksService;

    // --- Elementos del FXML ---
    @FXML private TextField txtTitulo;
    @FXML private TextField txtAutor;
    @FXML private TextField txtIsbn;
    @FXML private TextField txtEditorial;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtAnio;
    @FXML private TextField txtUbicacion;
    @FXML private TextField txtCantidad; // Cantidad de ejemplares (Manual)
    @FXML private TextField txtUrlPortada;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private TextField txtBusquedaGoogle;

    private Libro libroEdicion; // Para saber si estamos editando o creando

    @FXML
    public void initialize() {
        cargarCategorias();
    }

    private void cargarCategorias() {
        cmbCategoria.getItems().clear();
        cmbCategoria.getItems().addAll(categoriaService.findAll());

        // Mostrar solo el nombre en el ComboBox
        cmbCategoria.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        cmbCategoria.setButtonCell(cmbCategoria.getCellFactory().call(null));
    }

    // --- LÓGICA GOOGLE BOOKS (Solución del Error) ---
    @FXML
    private void handleSearchISBN(ActionEvent event) {
        // Usamos el texto del Título o del ISBN para buscar
        String query = txtBusquedaGoogle.getText().trim();
        if (query.isEmpty()) {
            query = txtTitulo.getText().trim();
        }

        if (query.isEmpty()) {
            mostrarAlerta("Advertencia", "Escriba un Título o ISBN para buscar en Google.");
            return;
        }

        final String busquedaFinal = query;

        // Ejecutar en hilo secundario para no congelar la interfaz
        new Thread(() -> {
            try {
                List<GoogleBooksService.GoogleBookDto> resultados = googleBooksService.buscarLibros(busquedaFinal);

                Platform.runLater(() -> {
                    if (!resultados.isEmpty()) {
                        // Tomamos el primer resultado (el más relevante)
                        GoogleBooksService.GoogleBookDto libroGoogle = resultados.get(0);

                        // Llenar campos automáticamente
                        txtTitulo.setText(libroGoogle.getTitulo());
                        txtAutor.setText(libroGoogle.getAutor());

                        if (libroGoogle.getIsbn() != null && !libroGoogle.getIsbn().isEmpty()) {
                            txtIsbn.setText(libroGoogle.getIsbn());
                        }

                        txtUrlPortada.setText(libroGoogle.getImagenUrl());

                        mostrarAlerta("¡Encontrado!", "Datos cargados desde Google Books.\n\nComplete la Categoría, Cantidad y Ubicación manualmente.");
                    } else {
                        mostrarAlerta("Sin resultados", "No se encontró el libro en Google Books.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> mostrarAlerta("Error", "Fallo al conectar con Google Books."));
            }
        }).start();
    }

    @FXML
    private void handleGuardar(ActionEvent event) {
        try {
            // Validaciones básicas
            if (txtTitulo.getText().isEmpty() || cmbCategoria.getValue() == null) {
                mostrarAlerta("Error", "El Título y la Categoría son obligatorios.");
                return;
            }

            Libro libro = (libroEdicion != null) ? libroEdicion : new Libro();
            libro.setTitulo(txtTitulo.getText());
            libro.setAutor(txtAutor.getText());
            libro.setIsbn(txtIsbn.getText());
            libro.setEditorial(txtEditorial.getText());
            libro.setDescripcion(txtDescripcion.getText());
            libro.setCategoria(cmbCategoria.getValue());
            libro.setUbicacion(txtUbicacion.getText());
            libro.setUrlPortada(txtUrlPortada.getText());

            // Manejo de números con valores por defecto
            try {
                libro.setAnio(txtAnio.getText().isEmpty() ? 2025 : Integer.parseInt(txtAnio.getText()));
                int cant = txtCantidad.getText().isEmpty() ? 1 : Integer.parseInt(txtCantidad.getText());
                libro.setNumeroEjemplares(cant);

                // Si es nuevo, disponibles = total. Si es edición, se debería calcular diferente (aquí simplificado)
                if (libroEdicion == null) {
                    libro.setDisponibles(cant);
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Error de Formato", "Año y Cantidad deben ser números.");
                return;
            }

            if (libroEdicion == null) {
                libro.setEstadoFisico("Bueno");
                libroService.save(libro);
            } else {
                libroService.update(libro.getIdLibro(), libro);
            }

            mostrarAlerta("Éxito", "Libro guardado correctamente.");
            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtTitulo.getScene().getWindow();
        stage.close();
    }

    public void setLibroParaEditar(Libro libro) {
        this.libroEdicion = libro;
        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtIsbn.setText(libro.getIsbn());
        txtEditorial.setText(libro.getEditorial());
        txtDescripcion.setText(libro.getDescripcion());
        txtAnio.setText(libro.getAnio() != null ? libro.getAnio().toString() : "");
        txtUbicacion.setText(libro.getUbicacion());
        txtCantidad.setText(libro.getNumeroEjemplares() != null ? libro.getNumeroEjemplares().toString() : "1");
        txtUrlPortada.setText(libro.getUrlPortada());

        // Seleccionar categoría en el combo
        if (libro.getCategoria() != null) {
            for (Categoria cat : cmbCategoria.getItems()) {
                if (cat.getIdCategoria().equals(libro.getCategoria().getIdCategoria())) {
                    cmbCategoria.getSelectionModel().select(cat);
                    break;
                }
            }
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}