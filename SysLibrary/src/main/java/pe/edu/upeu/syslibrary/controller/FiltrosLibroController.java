package pe.edu.upeu.syslibrary.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;

@Controller
public class FiltrosLibroController {

    @FXML private ComboBox<String> cmbEstadoFisico;
    @FXML private TextField txtAnioDesde;
    @FXML private TextField txtAnioHasta;
    @FXML private TextField txtUbicacion;
    @FXML private ComboBox<String> cmbDisponibilidad;

    private LibroController mainController;

    public void setMainController(LibroController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        // Cargar opciones iniciales
        cmbEstadoFisico.getItems().addAll("Todos los estados", "Bueno", "Regular", "Malo", "En Reparación");
        cmbEstadoFisico.getSelectionModel().selectFirst();
        cmbDisponibilidad.getItems().addAll("Todos", "Disponible", "Agotado");
        cmbDisponibilidad.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleLimpiarFiltros(ActionEvent event) {
        txtAnioDesde.clear();
        txtAnioHasta.clear();
        txtUbicacion.clear();
        cmbEstadoFisico.getSelectionModel().selectFirst();
        cmbDisponibilidad.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleAplicarFiltros(ActionEvent event) {
        try {
            //obtener valores del controlador
            String estadoFisico = cmbEstadoFisico.getSelectionModel().getSelectedItem();
            if ("Todos los estados".equals(estadoFisico)) estadoFisico = null;
            Integer anioDesde = parseInteger(txtAnioDesde.getText());
            Integer anioHasta = parseInteger(txtAnioHasta.getText());
            String ubicacion = txtUbicacion.getText();
            if (ubicacion != null && ubicacion.trim().isEmpty()) ubicacion = null;

            String disponibilidad = cmbDisponibilidad.getSelectionModel().getSelectedItem();
            Boolean soloDisponibles = null;
            if ("Disponible".equals(disponibilidad)) soloDisponibles = true;
            else if ("Agotado".equals(disponibilidad)) soloDisponibles = false;

            // Validar rango de años
            if (anioDesde != null && anioHasta != null && anioDesde > anioHasta) {
                mostrarAlerta("Error en Filtros", "El 'Año Desde' no puede ser mayor que 'Año Hasta'.", Alert.AlertType.WARNING);
                return;
            }

            // 2. Enviar los filtros al controlador principal
            if (mainController != null) {
                mainController.aplicarFiltrosAvanzados(estadoFisico, anioDesde, anioHasta, ubicacion, soloDisponibles);
            }

            // 3. Cerrar la ventana
            handleCerrar(event);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "Por favor, ingrese años válidos (solo números).", Alert.AlertType.ERROR);

        }
        System.out.println("Aplicando filtros avanzados... (Pendiente de implementación)");
        handleCerrar(event);
    }

    @FXML
    private void handleCerrar(ActionEvent event) {
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
    private Integer parseInteger(String text) throws NumberFormatException {
        if (text == null || text.trim().isEmpty()) return null;
        return Integer.parseInt(text.trim());
    }
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}