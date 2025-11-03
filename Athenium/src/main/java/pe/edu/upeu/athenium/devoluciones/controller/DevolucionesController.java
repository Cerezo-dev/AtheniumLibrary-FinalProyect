package pe.edu.upeu.athenium.devoluciones.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.athenium.devoluciones.entity.Devoluciones;
import pe.edu.upeu.athenium.devoluciones.service.IDevolucionesService;


@RequiredArgsConstructor
@Controller
public class DevolucionesController {

    private final ApplicationContext context;
    private final IDevolucionesService devolucionesService;

    @FXML
    private TextField folioUsuarioField;

    @FXML
    private TextField libroIdField;

    @FXML
    private Button devolverButton;

    @FXML
    private Label lblStatus;

    @FXML
    public void initialize() {
        // Configurar validadores de entrada
        configurarValidadores();

        // También puedes conectar el evento aquí como alternativa
        devolverButton.setOnAction(this::procesarDevolucion);
    }

    private void configurarValidadores() {
        // Solo permitir números en los campos
        folioUsuarioField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                folioUsuarioField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        libroIdField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                libroIdField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    private void procesarDevolucion(ActionEvent event) {
        try {
            // Validar campos vacíos
            if (folioUsuarioField.getText().isEmpty() || libroIdField.getText().isEmpty()) {
                setStatusMessage("Todos los campos son obligatorios", true);
                return;
            }

            // Obtener valores de los campos
            Integer folioUsuario = Integer.parseInt(folioUsuarioField.getText());
            Integer libroId = Integer.parseInt(libroIdField.getText());

            // Validar valores positivos
            if (folioUsuario <= 0 || libroId <= 0) {
                setStatusMessage("Los valores deben ser mayores a cero", true);
                return;
            }

            // Registrar devolución usando el servicio
            Devoluciones devolucionGuardada = devolucionesService.registrarDevolucion(folioUsuario, libroId);

            // Mostrar mensaje de éxito
            setStatusMessage(
                    "Devolución registrada exitosamente:\n" +
                            "Folio Usuario: " + devolucionGuardada.getFolioUsuario() + "\n" +
                            "Libro ID: " + devolucionGuardada.getLibroId() + "\n" +
                            "Fecha: " + devolucionGuardada.getFechaDevolucion() + "\n" +
                            "Estado: " + devolucionGuardada.getEstado(),
                    false
            );

            // Limpiar campos después de guardar
            limpiarCampos();

        } catch (NumberFormatException e) {
            setStatusMessage("Por favor ingrese valores numéricos válidos", true);
        } catch (DataIntegrityViolationException e) {
            setStatusMessage("Error de integridad de datos: " + e.getMessage(), true);
        } catch (IllegalArgumentException e) {
            setStatusMessage(e.getMessage(), true);
        } catch (Exception e) {
            setStatusMessage("Error inesperado al procesar la devolución: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }

    private void setStatusMessage(String message, boolean isError) {
        if (lblStatus != null) {
            lblStatus.setText(message);
            lblStatus.getStyleClass().removeAll("label-status-error", "label-status-success");
            if (isError) {
                lblStatus.getStyleClass().add("label-status-error");
            } else {
                lblStatus.getStyleClass().add("label-status-success");
            }
        }
    }

    private void limpiarCampos() {
        folioUsuarioField.clear();
        libroIdField.clear();
        folioUsuarioField.requestFocus();
    }

    // Métodos adicionales para operaciones CRUD desde otros controladores
    public boolean eliminarDevolucion(Long id) {
        try {
            devolucionesService.eliminarDevolucion(id);
            return true;
        } catch (Exception e) {
            setStatusMessage("Error al eliminar devolución: " + e.getMessage(), true);
            return false;
        }
    }

    public Devoluciones buscarDevolucionPorId(Long id) {
        try {
            return devolucionesService.obtenerDevolucionPorId(id).orElse(null);
        } catch (Exception e) {
            setStatusMessage("Error al buscar devolución: " + e.getMessage(), true);
            return null;
        }
    }

    public void actualizarDevolucion(Devoluciones devolucion) {
        try {
            devolucionesService.actualizarDevolucion(devolucion);
            setStatusMessage("Devolución actualizada exitosamente", false);
        } catch (Exception e) {
            setStatusMessage("Error al actualizar devolución: " + e.getMessage(), true);
        }
    }
}