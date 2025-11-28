package pe.edu.upeu.syslibrary.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.model.Ejemplar;
import pe.edu.upeu.syslibrary.model.Usuario;
import pe.edu.upeu.syslibrary.service.EjemplarService;
import pe.edu.upeu.syslibrary.service.IUsuarioService;
import pe.edu.upeu.syslibrary.service.PrestamoService;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Controller
public class PrestamoLibroController implements Initializable {

    @Autowired private IUsuarioService usuarioService;
    @Autowired private PrestamoService prestamoService;
    @Autowired private EjemplarService ejemplarService;

    // --- UI Elements ---
    @FXML private TextField txtUserId;
    @FXML private Label lblUserName;
    @FXML private Label lblAccountStatus;

    // Stats Labels
    @FXML private Label lblOverueCount;
    @FXML private Label lblPendingFine;
    @FXML private Label lblLoanLimit;
    @FXML private Label lblTotalSelected;

    @FXML private TextField txtExemplarId;
    @FXML private Button btnProcessLoan;
    @FXML private Button btnAddBook;
    @FXML private Button btnCancel;

    // Tabla
    @FXML private TableView<PrestamoItemDto> tblBooksToLoan;
    @FXML private TableColumn<PrestamoItemDto, String> colIdEjemplar; // fx:id="colIdEjemplar"
    @FXML private TableColumn<PrestamoItemDto, String> colTitulo;
    @FXML private TableColumn<PrestamoItemDto, String> colAutor;
    // Las otras columnas (FechaLimite, Accion) son visuales o futuras

    private Usuario usuarioSeleccionado;
    private ObservableList<PrestamoItemDto> listaPrestamos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTable();

        txtUserId.setOnAction(e -> buscarUsuario());
        txtExemplarId.setOnAction(this::handleAddBookAction);
    }

    private void setupTable() {
        colIdEjemplar.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        tblBooksToLoan.setItems(listaPrestamos);
    }

    private void buscarUsuario() {
        String busqueda = txtUserId.getText().trim();
        if (busqueda.isEmpty()) return;

        Optional<Usuario> optUser = usuarioService.buscarPorDniOCodigo(busqueda);

        if (optUser.isPresent()) {
            usuarioSeleccionado = optUser.get();
            lblUserName.setText(usuarioSeleccionado.getNombre() + " " + usuarioSeleccionado.getApellidos());
            lblAccountStatus.setText("ACTIVO");
            lblAccountStatus.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

            // Aquí podrías cargar estadísticas reales del usuario si tuvieras los métodos en el servicio
            // Por ahora placeholders:
            lblOverueCount.setText("0");
            lblPendingFine.setText("S/ 0.00");
            lblLoanLimit.setText("3/5");

            btnProcessLoan.setDisable(false);
            txtExemplarId.requestFocus();
        } else {
            usuarioSeleccionado = null;
            lblUserName.setText("No encontrado");
            lblAccountStatus.setText("---");
            btnProcessLoan.setDisable(true);
            mostrarAlerta("Error", "Usuario no encontrado.");
        }
    }

    @FXML
    private void handleAddBookAction(ActionEvent event) {
        String codigo = txtExemplarId.getText().trim();
        if (codigo.isEmpty()) return;

        if (listaPrestamos.stream().anyMatch(p -> p.getCodigo().equals(codigo))) {
            mostrarAlerta("Duplicado", "Este libro ya está en la lista.");
            txtExemplarId.clear();
            return;
        }

        Optional<Ejemplar> optEjemplar = ejemplarService.buscarPorCodigo(codigo);

        if (optEjemplar.isPresent()) {
            Ejemplar ej = optEjemplar.get();
            if (ej.getEstado() != pe.edu.upeu.syslibrary.enums.EstadoEjemplar.DISPONIBLE) {
                mostrarAlerta("No Disponible", "Libro en estado: " + ej.getEstado());
                return;
            }

            listaPrestamos.add(new PrestamoItemDto(
                    ej.getIdEjemplar(),
                    ej.getCodigo(),
                    ej.getLibro().getTitulo(),
                    ej.getLibro().getAutor()
            ));

            lblTotalSelected.setText(String.valueOf(listaPrestamos.size()));
            txtExemplarId.clear();
            txtExemplarId.requestFocus();
        } else {
            mostrarAlerta("Error", "Ejemplar no encontrado.");
        }
    }

    @FXML
    private void handleProcessLoanAction(ActionEvent event) {
        if (usuarioSeleccionado == null || listaPrestamos.isEmpty()) return;

        try {
            for (PrestamoItemDto item : listaPrestamos) {
                prestamoService.registrarPrestamo(item.getIdEjemplar(), usuarioSeleccionado.getIdUsuario());
            }
            mostrarAlerta("Éxito", "Préstamo registrado.");
            handleCancelAction(null);
        } catch (Exception e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        txtUserId.clear();
        lblUserName.setText("---");
        lblAccountStatus.setText("---");
        txtExemplarId.clear();
        listaPrestamos.clear();
        lblTotalSelected.setText("0");
        lblOverueCount.setText("0");
        lblPendingFine.setText("S/ 0.00");
        lblLoanLimit.setText("-/-");
        usuarioSeleccionado = null;
        btnProcessLoan.setDisable(true);
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    @Data
    @AllArgsConstructor
    public static class PrestamoItemDto {
        private Long idEjemplar;
        private String codigo;
        private String titulo;
        private String autor;
    }
}