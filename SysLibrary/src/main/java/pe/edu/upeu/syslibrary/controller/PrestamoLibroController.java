package pe.edu.upeu.syslibrary.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.model.Ejemplar;
import pe.edu.upeu.syslibrary.model.Usuario;
import pe.edu.upeu.syslibrary.service.EjemplarService;
import pe.edu.upeu.syslibrary.service.IUsuarioService; // Usamos la Interfaz
import pe.edu.upeu.syslibrary.service.PrestamoService;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Controller
public class PrestamoLibroController implements Initializable {

    // --- Servicios Inyectados ---
    @Autowired
    private IUsuarioService usuarioService; // CORREGIDO: Inyectamos la interfaz

    @Autowired
    private PrestamoService prestamoService;

    @Autowired
    private EjemplarService ejemplarService;


    // --- UI Elements (Deben coincidir con fx:id en tu SceneBuilder) ---
    @FXML private TextField txtUserId; // Campo para buscar por DNI
    @FXML private Label lblUserName;
    @FXML private Label lblAccountStatus;
    // Etiquetas de estadísticas (Validamos si existen para que no rompa el código)
    @FXML private Label lblOverueCount;
    @FXML private Label lblPendingFine;
    @FXML private Label lblLoanLimit;
    @FXML private Label lblTotalSelected;
    @FXML private TextField txtExemplarId; // Campo para escanear libro
    @FXML private TableView<PrestamoItemDto> tblBooksToLoan;
    @FXML private TableColumn<PrestamoItemDto, String> colCodigo;
    @FXML private TableColumn<PrestamoItemDto, String> colTitulo;
    @FXML private TableColumn<PrestamoItemDto, String> colAutor;

    @FXML private Button btnProcessLoan;

    // --- Datos en Memoria ---
    private Usuario usuarioSeleccionado; // CORREGIDO: Usamos Usuario
    private ObservableList<PrestamoItemDto> listaPrestamos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 1. Configurar columnas de la tabla
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        tblBooksToLoan.setItems(listaPrestamos);

        // 2. Evento: Al presionar ENTER en la caja de texto del usuario, busca.
        txtUserId.setOnAction(e -> buscarUsuario());
        if(colCodigo != null) {
            colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        }

        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));


    }

    private void buscarUsuario() {
        // 1. Obtenemos el texto y quitamos espacios en blanco (.trim())
        String busqueda = txtUserId.getText().trim();

        if (busqueda.isEmpty()) return;

        System.out.println("Buscando usuario con dato: " + busqueda); // Para depuración

        // 2. Usamos el NUEVO método que busca por DNI o por Código
        Optional<Usuario> optUser = usuarioService.buscarPorDniOCodigo(busqueda);

        if (optUser.isPresent()) {
            usuarioSeleccionado = optUser.get();

            // Visualización
            lblUserName.setText(usuarioSeleccionado.getNombre() + " " + usuarioSeleccionado.getApellidos());

            // Validamos visualmente si tiene el perfil correcto (Opcional pero recomendado)
            String perfil = usuarioSeleccionado.getPerfil().getNombre();
            if ("ESTUDIANTE".equals(perfil)) {
                lblAccountStatus.setText("ESTUDIANTE ACTIVO");
                lblAccountStatus.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                lblAccountStatus.setText("PERSONAL: " + perfil);
                lblAccountStatus.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
            }

            btnProcessLoan.setDisable(false);

        } else {
            // Si no lo encuentra
            usuarioSeleccionado = null;
            lblUserName.setText("Usuario no encontrado");
            lblAccountStatus.setText("---");
            lblAccountStatus.setStyle("-fx-text-fill: red;");
            btnProcessLoan.setDisable(true);

            // Alerta visual rápida para saber que falló
            System.out.println("No se encontró ningún usuario con DNI o Código: " + busqueda);
        }
    }

    @FXML
    private void handleAddBookAction(ActionEvent event) {
        String codigo = txtExemplarId.getText().trim();

        // 1. Validación básica
        if (codigo.isEmpty()) {
            mostrarAlerta("Advertencia", "Ingrese o escanee el código del libro.");
            txtExemplarId.requestFocus();
            return;
        }

        // 2. Verificar si ya está en la tabla (Evitar duplicados)
        boolean yaExiste = listaPrestamos.stream()
                .anyMatch(item -> item.getCodigo().equalsIgnoreCase(codigo));

        if (yaExiste) {
            mostrarAlerta("Duplicado", "Este libro ya está en la lista de préstamo.");
            txtExemplarId.clear();
            txtExemplarId.requestFocus();
            return;
        }

        // 3. Buscar en Base de Datos
        Optional<Ejemplar> optEjemplar = ejemplarService.buscarPorCodigo(codigo);

        if (optEjemplar.isPresent()) {
            Ejemplar ejemplar = optEjemplar.get();

            // 4. Validar Estado del Libro (Regla de Negocio)
            // Solo prestamos libros DISPONIBLES. Si está DAÑADO o PRESTADO, error.
            if (ejemplar.getEstado() != pe.edu.upeu.syslibrary.enums.EstadoEjemplar.DISPONIBLE) {
                mostrarAlerta("No Disponible",
                        "El libro '" + ejemplar.getLibro().getTitulo() + "' se encuentra: " + ejemplar.getEstado());
                return;
            }

            // 5. Agregar a la lista Observable (La tabla se actualiza sola)
            PrestamoItemDto item = new PrestamoItemDto(
                    ejemplar.getIdEjemplar(), // <--- Esto devuelve un Long. El constructor de arriba ahora lo aceptará.
                    ejemplar.getCodigo(),
                    ejemplar.getLibro().getTitulo(),
                    ejemplar.getLibro().getAutor()
            );

            listaPrestamos.add(item);

            // Actualizar contadores visuales (Opcional)
            if(lblTotalSelected != null) {
                lblTotalSelected.setText(String.valueOf(listaPrestamos.size()));
            }

            // 6. Limpiar para siguiente escaneo
            txtExemplarId.clear();
            txtExemplarId.requestFocus(); // Volver el foco para escanear seguido

        } else {
            mostrarAlerta("Error", "No se encontró ningún ejemplar con el código: " + codigo);
            txtExemplarId.selectAll();
            txtExemplarId.requestFocus();
        }
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        // Limpiar formulario
        txtUserId.clear();
        lblUserName.setText("");
        lblAccountStatus.setText("");

        // Validaciones null-safe por si quitaste labels de la vista
        if(lblOverueCount != null) lblOverueCount.setText("0");
        if(lblPendingFine != null) lblPendingFine.setText("[S/ 0.00]");
        if(lblLoanLimit != null) lblLoanLimit.setText("");
        if(lblTotalSelected != null) lblTotalSelected.setText("0");

        txtExemplarId.clear();
        listaPrestamos.clear(); // Limpia la lista observable y la tabla

        btnProcessLoan.setDisable(true);
        usuarioSeleccionado = null;
    }

    @FXML
    private void handleProcessLoanAction(ActionEvent event) {
        if (usuarioSeleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un usuario válido (Busque por DNI).");
            return;
        }
        if (listaPrestamos.isEmpty()) {
            mostrarAlerta("Error", "No hay libros en la lista para prestar.");
            return;
        }

        try {
            // Recorremos la lista de libros agregados a la tabla
            for (PrestamoItemDto item : listaPrestamos) {
                // CORREGIDO: Pasamos el ID del Usuario (Fusión)
                prestamoService.registrarPrestamo(item.getIdEjemplar(), usuarioSeleccionado.getIdUsuario());
            }

            mostrarAlerta("Éxito", "Préstamo registrado correctamente.");

            // Limpiamos todo para el siguiente usuario
            handleCancelAction(null);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al procesar", "Ocurrió un error: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    // DTO Interno para la tabla (Simple y funcional)
    @Data
    public static class PrestamoItemDto {
        // CAMBIO CLAVE: Usamos Long idEjemplar, NO "Ejemplar ejemplar"
        private Long idEjemplar;
        private String codigo;
        private String titulo;
        private String autor;

        // Constructor que coincide con lo que enviamos desde el botón
        public PrestamoItemDto(Long idEjemplar, String codigo, String titulo, String autor) {
            this.idEjemplar = idEjemplar; // Aquí guardamos el ID (número)
            this.codigo = codigo;
            this.titulo = titulo;
            this.autor = autor;
        }
    }
}