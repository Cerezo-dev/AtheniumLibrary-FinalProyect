package pe.edu.upeu.athenium.common.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.springframework.stereotype.Controller;

@Controller
public class AdminPanelController implements Initializable {

    @FXML
    private BarChart<String, Number> prestamosChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuración inicial del gráfico (solo para mostrar datos de ejemplo)
        if (prestamosChart != null && xAxis != null && yAxis != null) {
            // Configurar etiquetas de ejes
            xAxis.setLabel("Horas del día");
            yAxis.setLabel("Cantidad de Préstamos");

            // Limpiar cualquier serie existente
            prestamosChart.getData().clear();

            // Crear datos de ejemplo para el gráfico de barras
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Préstamos"); // Nombre de la serie, no se muestra si legendVisible=false

            series.getData().add(new XYChart.Data<>("0-4h", 5));
            series.getData().add(new XYChart.Data<>("4-8h", 15));
            series.getData().add(new XYChart.Data<>("8-12h", 30));
            series.getData().add(new XYChart.Data<>("12-16h", 45));
            series.getData().add(new XYChart.Data<>("16-20h", 25));
            series.getData().add(new XYChart.Data<>("20-24h", 10));

            prestamosChart.getData().add(series);

            // Opcional: Estilo de las etiquetas del eje si no se ve bien con el CSS
            // xAxis.setTickLabelFill(javafx.scene.paint.Color.web("#A0A0A0"));
            // yAxis.setTickLabelFill(javafx.scene.paint.Color.web("#A0A0A0"));

        } else {
            System.err.println("Error: Chart components not initialized in AdminPanelController.");
        }
    }
}