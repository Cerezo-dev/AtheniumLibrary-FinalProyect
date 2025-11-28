package pe.edu.upeu.syslibrary.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.service.ILibroService;
// Importa aquí tus otros servicios cuando los tengas:
// import pe.edu.upeu.syslibrary.service.IUsuarioService;
// import pe.edu.upeu.syslibrary.service.IPrestamoService;

@Controller
public class DashboardController {

    // --- REFERENCIAS FXML (Coinciden con tu dashboard_view.fxml) ---
    @FXML private Label lblTotalLibros;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblPrestamosActivos;
    @FXML private Label lblEjemplaresDisp;
    @FXML
    private LineChart<String, Number> chartPuntos;

    // --- INYECCIÓN DE SERVICIOS ---
    @Autowired
    private ILibroService libroService; // Inyectamos el servicio de libros

    // @Autowired private IUsuarioService usuarioService; // Descomentar cuando exista
    // @Autowired private IPrestamoService prestamoService; // Descomentar cuando exista

    /**
     * Método que se ejecuta automáticamente al cargar la vista.
     */
    @FXML
    public void initialize() {
        System.out.println("📊 Cargando DashboardController...");
        loadSummaryData();
    }

    /**
     * Carga los datos de las tarjetas de resumen.
     */
    private void loadSummaryData() {
        // 1. Cargar Total de Libros (Datos Reales)
        if (libroService != null) {
            try {
                // Usamos .size() o un método .count() si tu repositorio lo tiene optimizado
                long cantidadLibros = libroService.findAll().size();
                lblTotalLibros.setText(String.valueOf(cantidadLibros));
            } catch (Exception e) {
                lblTotalLibros.setText("Error");
                System.err.println("Error al contar libros: " + e.getMessage());
            }
        } else {
            lblTotalLibros.setText("0");
        }

        // 2. Cargar Usuarios (Simulado / Placeholder)
        // Cuando tengas usuarioService, reemplaza esto:
        // long cantidadUsuarios = usuarioService.count();
        // lblTotalUsuarios.setText(String.valueOf(cantidadUsuarios));
        lblTotalUsuarios.setText("5"); // Dato de prueba

        // 3. Cargar Préstamos Activos (Simulado / Placeholder)
        lblPrestamosActivos.setText("2"); // Dato de prueba

        // 4. Cargar Ejemplares Disponibles (Simulado / Placeholder)
        lblEjemplaresDisp.setText("12"); // Dato de prueba
    }

    // --- LÓGICA FUTURA PARA GRÁFICOS ---
    // Aquí podrás agregar métodos para poblar los VBox de gráficos
    // usando librerías como JFreeChart o los Charts nativos de JavaFX.
}