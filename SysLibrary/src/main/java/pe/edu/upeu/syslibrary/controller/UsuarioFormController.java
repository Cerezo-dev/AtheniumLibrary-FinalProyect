package pe.edu.upeu.syslibrary.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.syslibrary.model.Perfil;
import pe.edu.upeu.syslibrary.model.Usuario;
import pe.edu.upeu.syslibrary.repositorio.PerfilRepository;
import pe.edu.upeu.syslibrary.service.IUsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder; // Usar el encoder inyectado

@Controller
@RequiredArgsConstructor
public class UsuarioFormController {

    private final IUsuarioService usuarioService;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder; // Inyectado desde tu SecurityConfig

    @FXML private TextField txtCodigo;
    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private ComboBox<String> cmbCarrera;

    @FXML
    public void initialize() {
        cmbCarrera.getItems().addAll("Ing. Sistemas", "Enfermería", "Psicología", "Contabilidad", "Teología");
    }

    @FXML
    private void saveStudent() {
        try {
            if (txtCodigo.getText().isEmpty() || txtDni.getText().isEmpty() ||
                    txtNombre.getText().isEmpty() || txtApellidos.getText().isEmpty() ||
                    txtEmail.getText().isEmpty() || cmbCarrera.getValue() == null) {
                mostrarAlerta("Validación", "Todos los campos con * son obligatorios.");
                return;
            }

            Usuario u = new Usuario();
            u.setCodigoEstudiante(txtCodigo.getText());
            u.setDni(txtDni.getText());
            u.setNombre(txtNombre.getText());
            u.setApellidos(txtApellidos.getText());
            u.setEmail(txtEmail.getText());
            u.setTelefono(txtTelefono.getText());
            u.setCarrera(cmbCarrera.getValue());
            u.setEstado("ACTIVO");

            Perfil perfilEst = perfilRepository.findByNombre("ESTUDIANTE");
            if (perfilEst == null) {
                mostrarAlerta("Error de Configuración", "No existe el rol 'ESTUDIANTE' en la base de datos.");
                return;
            }
            u.setPerfil(perfilEst);

            // Contraseña por defecto es el DNI
            u.setPassword(passwordEncoder.encode(txtDni.getText()));

            usuarioService.save(u);

            mostrarAlerta("Éxito", "Estudiante registrado correctamente.");
            closeForm();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    @FXML
    private void closeForm() {
        Stage stage = (Stage) txtCodigo.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}