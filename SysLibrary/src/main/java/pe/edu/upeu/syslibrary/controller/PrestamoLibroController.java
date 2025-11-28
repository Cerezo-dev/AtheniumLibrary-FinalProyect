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

    // --- Servicios Inyectados ---
    @Autowired private IUsuarioService usuarioService;
    @Autowired private PrestamoService prestamoService;
    @Autowired private EjemplarService ejemplarService;

    // --- Elementos FXML (Deben coincidir con SceneBuilder) ---
    @FXML private TextField txtUserId; // Input para buscar Usuario (DNI o Código)
    @FXML private Label lblUserName;
    @FXML private Label lblAccountStatus;

    // Estadísticas visuales (Opcionales, manejamos nulos por seguridad)
    @FXML private Label lblOverueCount;
    @FXML private Label lblPendingFine;
    @FXML private Label lblLoanLimit;
    @FXML private Label lblTotalSelected;

    @FXML private TextField txtExemplarId; // Input para buscar Libro (Scanner)
    @FXML private Button btnProcessLoan;

    // Tabla y Columnas
    @FXML private TableView<PrestamoItemDto> tblBooksToLoan;
    @FXML private TableColumn<PrestamoItemDto, String> colCodigo;
    @FXML private TableColumn<PrestamoItemDto, String> colTitulo;
    @FXML private TableColumn<PrestamoItemDto, String> colAutor;

    // --- Datos en Memoria ---
    private Usuario usuarioSeleccionado;
    private ObservableList<PrestamoItemDto> listaPrestamos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Configurar columnas de la tabla
        if(colCodigo != null) colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));

        tblBooksToLoan.setItems(listaPrestamos);

        // 2. Eventos al presionar ENTER
        txtUserId.setOnAction(e -> buscarUsuario());
        txtUserId.setPromptText("Escanear Código de Estudiante"); // Texto guía
        txtExemplarId.setOnAction(this::handleAddBookAction);
    }

    // --- Lógica de Usuario ---
    private void buscarUsuario() {
        String busqueda = txtUserId.getText().trim();
        if (busqueda.isEmpty()) return;

        // Busca por DNI o Código usando tu servicio (Lógica dual)
        Optional<Usuario> optUser = usuarioService.buscarPorDniOCodigo(busqueda);

        if (optUser.isPresent()) {
            usuarioSeleccionado = optUser.get();
            lblUserName.setText(usuarioSeleccionado.getNombre() + " " + usuarioSeleccionado.getApellidos());

            // Estilos visuales según perfil
            String perfil = (usuarioSeleccionado.getPerfil() != null) ? usuarioSeleccionado.getPerfil().getNombre() : "SIN PERFIL";
            lblAccountStatus.setText("Perfil: " + perfil);
            lblAccountStatus.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;"); // Verde

            btnProcessLoan.setDisable(false);
            txtExemplarId.requestFocus(); // Saltar al campo de libro automáticamente
        } else {
            usuarioSeleccionado = null;
            lblUserName.setText("Usuario no encontrado");
            lblAccountStatus.setText("---");
            lblAccountStatus.setStyle("-fx-text-fill: red;");
            btnProcessLoan.setDisable(true);
            mostrarAlerta("Error", "No se encontró usuario con el dato: " + busqueda, Alert.AlertType.ERROR);
        }
    }

    // --- Lógica de Libro (Agregar a la tabla) ---
    @FXML
    private void handleAddBookAction(ActionEvent event) {
        String codigo = txtExemplarId.getText().trim();
        if (codigo.isEmpty()) {
            txtExemplarId.requestFocus();
            return;
        }

        // 1. Evitar duplicados en la lista visual
        if (listaPrestamos.stream().anyMatch(p -> p.getCodigo().equalsIgnoreCase(codigo))) {
            mostrarAlerta("Duplicado", "El libro ya está en la lista.", Alert.AlertType.WARNING);
            txtExemplarId.clear();
            return;
        }

        // 2. Buscar Ejemplar en BD
        Optional<Ejemplar> optEjemplar = ejemplarService.buscarPorCodigo(codigo);

        if (optEjemplar.isPresent()) {
            Ejemplar ej = optEjemplar.get();

            // 3. Validar Disponibilidad (Regla de Negocio)
            if (ej.getEstado() != pe.edu.upeu.syslibrary.enums.EstadoEjemplar.DISPONIBLE) {
                mostrarAlerta("No Disponible", "El libro figura como " + ej.getEstado(), Alert.AlertType.WARNING);
                return;
            }

            // 4. Agregar a la tabla
            listaPrestamos.add(new PrestamoItemDto(
                    ej.getIdEjemplar(),
                    ej.getCodigo(),
                    ej.getLibro().getTitulo(),
                    ej.getLibro().getAutor()
            ));

            if(lblTotalSelected != null) lblTotalSelected.setText(String.valueOf(listaPrestamos.size()));

            // Limpiar y preparar para el siguiente scanner
            txtExemplarId.clear();
            txtExemplarId.requestFocus();

        } else {
            mostrarAlerta("Error", "Código de ejemplar no encontrado.", Alert.AlertType.ERROR);
            txtExemplarId.selectAll();
        }
    }

    // --- Procesar el Préstamo Final ---
    @FXML
    private void handleProcessLoanAction(ActionEvent event) {
        if (usuarioSeleccionado == null || listaPrestamos.isEmpty()) {
            mostrarAlerta("Atención", "Seleccione usuario y agregue al menos un libro.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Iterar y guardar cada préstamo
            for (PrestamoItemDto item : listaPrestamos) {
                prestamoService.registrarPrestamo(item.getIdEjemplar(), usuarioSeleccionado.getIdUsuario());
            }

            mostrarAlerta("Éxito", "Préstamo registrado correctamente.", Alert.AlertType.INFORMATION);
            handleCancelAction(null); // Limpiar formulario

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error Crítico", "Fallo al registrar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        txtUserId.clear();
        lblUserName.setText("---");
        lblAccountStatus.setText("---");
        txtExemplarId.clear();
        listaPrestamos.clear();

        if(lblTotalSelected != null) lblTotalSelected.setText("0");

        usuarioSeleccionado = null;
        btnProcessLoan.setDisable(true);
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    // --- DTO Interno para la Tabla ---
    @Data
    @AllArgsConstructor
    public static class PrestamoItemDto {
        private Long idEjemplar;
        private String codigo;
        private String titulo;
        private String autor;
    }
}