package pe.edu.upeu.syslibrary.controller.student;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.dto.SessionManager;
import pe.edu.upeu.syslibrary.model.Prestamo;
import pe.edu.upeu.syslibrary.service.PrestamoService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class StudentLoansController {

    @Autowired
    private PrestamoService prestamoService;

    @FXML private TableView<Prestamo> tblPrestamos;
    @FXML private TableColumn<Prestamo, String> colLibro;
    @FXML private TableColumn<Prestamo, String> colFechaPrestamo;
    @FXML private TableColumn<Prestamo, String> colFechaDevolucion;
    @FXML private TableColumn<Prestamo, String> colEstado;
    @FXML private TableColumn<Prestamo, String> colMulta;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        setupColumns();
        cargarMisPrestamos();
    }

    private void setupColumns() {
        colLibro.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getEjemplar().getLibro().getTitulo()
        ));

        colFechaPrestamo.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaPrestamo().format(dtf)
        ));

        colFechaDevolucion.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getFechaDevolucion().format(dtf)
        ));

        colEstado.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getEstado().toString()
        ));

        // Colorear el estado
        colEstado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("ACTIVO")) setTextFill(Color.GREEN);
                    else if (item.equals("ATRASADO")) setTextFill(Color.RED);
                    else setTextFill(Color.GRAY);
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });

        colMulta.setCellValueFactory(data -> new SimpleStringProperty(
                // Lógica simple: Si hay sanción asociada (necesitarías esa relación en el modelo) o calcular al vuelo
                // Por ahora dejamos placeholder
                "S/ 0.00"
        ));
    }

    private void cargarMisPrestamos() {
        Long idUsuario = SessionManager.getInstance().getUserId();
        if (idUsuario != null) {
            // Nota: prestamoService.findPrestamosActivosPorUsuario actualmente solo trae ACTIVOS.
            // Si quieres historial completo, necesitarías un método findAllByUsuario en el repo.
            List<Prestamo> misPrestamos = prestamoService.findPrestamosActivosPorUsuario(idUsuario);
            tblPrestamos.setItems(FXCollections.observableArrayList(misPrestamos));
        }
    }
}