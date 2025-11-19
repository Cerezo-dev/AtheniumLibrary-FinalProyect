package pe.edu.upeu.syslibrary.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.model.Categoria;
import pe.edu.upeu.syslibrary.model.Libro;
import pe.edu.upeu.syslibrary.enums.GeneroLibro;
import pe.edu.upeu.syslibrary.service.ICategoriaService;
import pe.edu.upeu.syslibrary.service.ILibroService;

import java.time.LocalDate;

@Controller
public class BookRegistrationController {

    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtAutor;
    @FXML
    private TextField txtISBN;
    @FXML
    private TextField txtEditorial;
    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML private DatePicker dpFechaPublicacion;
    @FXML private TextField txtIdioma;
    @FXML private TextField txtNumeroPaginas;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtNumeroEjemplares;

    @Autowired
    private ILibroService libroService;

    @Autowired
    private ICategoriaService categoriaService;

    @Autowired
    private MainguiController mainguiController;

    @FXML
    public void initialize() {
        loadCategories();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void loadCategories() {
        try {
            cmbCategoria.getItems().setAll(categoriaService.findAll());
        } catch (Exception e) {
            System.err.println("Error al cargar categorías: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            if (txtTitulo.getText().isEmpty() || txtAutor.getText().isEmpty() || txtISBN.getText().isEmpty()) {
                mostrarAlerta("Error", "Título, Autor e ISBN son campos obligatorios.", Alert.AlertType.ERROR);
                return;
            }

            // ✅ Obtener la fecha directamente del DatePicker
            LocalDate fecha = dpFechaPublicacion.getValue();
            if (fecha == null) {
                mostrarAlerta("Error", "Debe seleccionar una fecha de publicación.", Alert.AlertType.ERROR);
                return;
            }

            int paginas = Integer.parseInt(txtNumeroPaginas.getText());
            int ejemplares = Integer.parseInt(txtNumeroEjemplares.getText());

            Categoria categoria = cmbCategoria.getSelectionModel().getSelectedItem();
            if (categoria == null) {
                mostrarAlerta("Error", "Debe seleccionar una categoría.", Alert.AlertType.ERROR);
                return;
            }

            Libro nuevoLibro = Libro.builder()
                    .titulo(txtTitulo.getText())
                    .autor(txtAutor.getText())
                    .isbn(txtISBN.getText())
                    .editorial(txtEditorial.getText())
                    .idioma(txtIdioma.getText())
                    .descripcion(txtDescripcion.getText())
                    .fechaPublicacion(fecha) // ✅ Usar la fecha del DatePicker
                    .numeroPaginas(paginas)
                    .numeroEjemplares(ejemplares)
                    .stock(ejemplares)
                    .disponibles(ejemplares)
                    .categoria(categoria)
                    .genero(GeneroLibro.NOVELA)
                    .build();

            libroService.save(nuevoLibro);
            mostrarAlerta("Éxito", "Libro guardado exitosamente.", Alert.AlertType.INFORMATION);
            mainguiController.showLibros(null);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Los campos de páginas y ejemplares deben ser numéricos.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al guardar el libro: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        mainguiController.showLibros(null);
    }
}
