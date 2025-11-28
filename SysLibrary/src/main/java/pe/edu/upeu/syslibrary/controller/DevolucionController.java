package pe.edu.upeu.syslibrary.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.model.Prestamo;
import pe.edu.upeu.syslibrary.service.PrestamoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Controller
public class DevolucionController {

    @Autowired
    private PrestamoService prestamoService;

    // --- Elementos de la UI (Deben coincidir con SceneBuilder) ---
    @FXML private TextField txtCodigoEjemplar; // Donde escanean el libro
    @FXML private Button btnBuscar;

    // Etiquetas para mostrar info
    @FXML private Label lblTituloLibro;
    @FXML private Label lblNombreUsuario;
    @FXML private Label lblFechaPrestamo;
    @FXML private Label lblFechaVencimiento;
    @FXML private Label lblDiasRetraso;
    @FXML private Label lblMoraEstimada; // Para mostrar cuánto debe pagar

    @FXML private Button btnConfirmarDevolucion;

    // Variable auxiliar para guardar el préstamo encontrado
    private Prestamo prestamoEncontrado;

    @FXML
    public void initialize() {
        limpiarCampos();
    }

    @FXML
    private void handleBuscarPrestamo(ActionEvent event) {
        String codigo = txtCodigoEjemplar.getText();
        if (codigo.isEmpty()) {
            mostrarAlerta("Error", "Ingrese el código del ejemplar.");
            return;
        }

        Optional<Prestamo> optPrestamo = prestamoService.buscarPrestamoPorCodigoEjemplar(codigo);

        if (optPrestamo.isPresent()) {
            prestamoEncontrado = optPrestamo.get();
            llenarDatosEnPantalla(prestamoEncontrado);
            btnConfirmarDevolucion.setDisable(false);
        } else {
            mostrarAlerta("Info", "No se encontró un préstamo activo para este libro.");
            limpiarCampos();
        }
    }

    private void llenarDatosEnPantalla(Prestamo p) {
        lblTituloLibro.setText(p.getEjemplar().getLibro().getTitulo());

        // OJO: Aquí usamos la nueva relación con Usuario
        lblNombreUsuario.setText(p.getUsuario().getNombre() + " " + p.getUsuario().getApellidos());

        lblFechaPrestamo.setText(p.getFechaPrestamo().toLocalDate().toString());
        lblFechaVencimiento.setText(p.getFechaDevolucion().toLocalDate().toString());

        // --- CÁLCULO VISUAL DE MULTA ---
        long diasRetraso = calcularDiasRetraso(p.getFechaDevolucion());
        lblDiasRetraso.setText(String.valueOf(diasRetraso));

        if (diasRetraso > 0) {
            double multa = diasRetraso * 2.00; // Asumiendo 2 soles por día
            lblMoraEstimada.setText("S/ " + String.format("%.2f", multa));
            lblMoraEstimada.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            lblMoraEstimada.setText("S/ 0.00");
            lblMoraEstimada.setStyle("-fx-text-fill: green;");
        }
    }

    private long calcularDiasRetraso(LocalDateTime fechaPactada) {
        LocalDateTime hoy = LocalDateTime.now();
        if (hoy.isAfter(fechaPactada)) {
            return ChronoUnit.DAYS.between(fechaPactada, hoy);
        }
        return 0;
    }

    @FXML
    private void handleConfirmarDevolucion(ActionEvent event) {
        if (prestamoEncontrado == null) return;

        try {
            // Llamamos al servicio que ya programamos (el que crea la sanción automáticamente)
            prestamoService.devolverPrestamo(prestamoEncontrado.getIdPrestamo());

            mostrarAlerta("Éxito", "Libro devuelto correctamente. Stock actualizado.");

            // Si había mora, podrías mostrar un aviso extra
            if (!lblMoraEstimada.getText().equals("S/ 0.00")) {
                mostrarAlerta("Atención", "Se ha generado una multa de: " + lblMoraEstimada.getText());
            }

            limpiarCampos();
        } catch (Exception e) {
            mostrarAlerta("Error", "Error al devolver: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        prestamoEncontrado = null;
        txtCodigoEjemplar.clear();
        lblTituloLibro.setText("---");
        lblNombreUsuario.setText("---");
        lblFechaPrestamo.setText("---");
        lblFechaVencimiento.setText("---");
        lblDiasRetraso.setText("0");
        lblMoraEstimada.setText("S/ 0.00");
        btnConfirmarDevolucion.setDisable(true);
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}