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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Necesitas spring-security-crypto

@Controller
@RequiredArgsConstructor
public class UsuarioFormController {

    private final IUsuarioService usuarioService;
    private final PerfilRepository perfilRepository; // Para buscar el rol

    @FXML private TextField txtCodigo;
    @FXML private TextField txtDni;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEmail; // ¡IMPORTANTE! Agregado
    @FXML private ComboBox<String> cmbCarrera;
    @FXML private TextField txtTelefono;

    @FXML
    public void initialize() {
        // Cargar carreras en el combo
        cmbCarrera.getItems().addAll("Ing. Sistemas", "Enfermería", "Psicología", "Contabilidad", "Teología");
    }

    @FXML
    private void saveStudent() {
        try {
            // 1. Validaciones
            if (txtCodigo.getText().isEmpty() || txtDni.getText().isEmpty() || txtNombre.getText().isEmpty() || txtEmail.getText().isEmpty()) {
                mostrarAlerta("Error", "Complete los campos obligatorios (Código, DNI, Nombre, Email).");
                return;
            }

            // 2. Crear Objeto Usuario
            Usuario u = new Usuario();
            u.setCodigoEstudiante(txtCodigo.getText());
            u.setDni(txtDni.getText());
            u.setNombre(txtNombre.getText());
            u.setApellidos(txtApellidos.getText());
            u.setEmail(txtEmail.getText());
            u.setTelefono(txtTelefono.getText());
            u.setCarrera(cmbCarrera.getValue());
            u.setEstado("Activo");

            // 3. Asignar Perfil "ESTUDIANTE"
            Perfil perfilEst = perfilRepository.findByNombre("ESTUDIANTE");
            // Si no usas findByNombre, usa findById(2L) o lo que corresponda en tu BD
            if(perfilEst == null) {
                mostrarAlerta("Error Crítico", "El rol 'ESTUDIANTE' no existe en la BD.");
                return;
            }
            u.setPerfil(perfilEst);

            // 4. Contraseña por defecto = DNI (Encriptada)
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            u.setPassword(encoder.encode(txtDni.getText()));

            // 5. Guardar
            usuarioService.save(u);

            mostrarAlerta("Éxito", "Estudiante registrado correctamente.");
            closeForm();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar: " + e.getMessage());
        }
    }

    @FXML
    private void closeForm() {
        Stage stage = (Stage) txtDni.getScene().getWindow();
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