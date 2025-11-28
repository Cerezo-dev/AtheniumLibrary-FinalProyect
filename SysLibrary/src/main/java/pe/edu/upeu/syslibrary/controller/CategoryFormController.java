package pe.edu.upeu.syslibrary.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.model.Categoria;
import pe.edu.upeu.syslibrary.service.ICategoriaService;

@Controller
public class CategoryFormController {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextArea txtDescripcion;

    @Autowired
    private ICategoriaService categoriaService;

    private Categoria categoriaActual;

    // 1. Variable para almacenar la acción a ejecutar (el callback)
    private Runnable onSaveSuccess;

    /**
     * Acción del botón "Guardar" en el FXML
     */
    @FXML
    public void saveCategory() {
        // Validacion simple
        if (txtNombre.getText().isEmpty()) {
            mostrarAlerta("Validación", "El nombre es obligatorio.");
            return;
        }

        // Preparar objeto (Si es null es nuevo, si no, actualizamos los datos)
        if (categoriaActual == null) {
            categoriaActual = new Categoria();
        }
        categoriaActual.setNombre(txtNombre.getText());
        categoriaActual.setDescripcion(txtDescripcion.getText());

        try {
            // Guardar en Base de Datos
            categoriaService.save(categoriaActual);

            // 2. EJECUTAR EL CALLBACK: Avisamos al padre que ya terminamos
            if (onSaveSuccess != null) {
                onSaveSuccess.run();
            }

            closeForm();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo guardar la categoría: " + e.getMessage());
        }
    }

    /**
     * Acción del botón "Cancelar"
     */
    @FXML
    public void closeForm() {
        // Obtener el Stage (ventana) actual y cerrarlo
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

    /**
     * Método para recibir la categoría a editar (o null si es nueva)
     */
    public void setCategoria(Categoria categoria) {
        this.categoriaActual = categoria;
        if (categoria != null) {
            txtNombre.setText(categoria.getNombre());
            txtDescripcion.setText(categoria.getDescripcion());
        } else {
            txtNombre.clear();
            txtDescripcion.clear();
        }
    }

    // 3. EL MÉTODO QUE FALTABA: Asigna la acción a realizar tras guardar
    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}