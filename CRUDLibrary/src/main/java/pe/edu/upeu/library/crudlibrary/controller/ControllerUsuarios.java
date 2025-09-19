package pe.edu.upeu.library.crudlibrary.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.library.crudlibrary.enums.Carrera;
import pe.edu.upeu.library.crudlibrary.enums.TipoParticipante;
import pe.edu.upeu.library.crudlibrary.modelo.Usuarios;
import pe.edu.upeu.library.crudlibrary.servicios.UsuariosServicioI;

@Controller
public class ControllerUsuarios {

    @FXML
    private TextField txtNombre, txtApellidoP, txtApellidoM, txtDomicilio, txtTelefono, txtDni;

    @FXML
    private Button btnRegistrar;

    @FXML
    private TableView<Usuarios> tableUsuarios;

    private ObservableList<Usuarios> usuariosList;
    private TableColumn<Usuarios, String> dniColumn, nombreColumn, apellidoPColumn, apellidoMColumn, domicilioColumn, telefonoColumn, carreraColumn, tipoPartiColumn;
    private TableColumn<Usuarios, Void> opcColumn;
    private int indexEdit = -1;

    @Autowired
    private UsuariosServicioI usuariosServicio;

    @FXML
    public void initialize() {

        definirColumnas();
        listarUsuarios();
    }

    private void listarUsuarios() {
        dniColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDni()));
        nombreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        apellidoPColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCodigo())); // Puedes ajustar según tu modelo
        apellidoMColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado())); // Puedes ajustar según tu modelo
        domicilioColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCorreoinstitucional())); // Puedes ajustar según tu modelo
        telefonoColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTelefono()));
        carreraColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCarrera().toString()));
        tipoPartiColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipoParticipante().toString()));
        addActionButtonsToTable();
        usuariosList = FXCollections.observableArrayList(usuariosServicio.findAll());
        tableUsuarios.setItems(usuariosList);
    }

    @FXML
    public void registrar() {
        limpiarEstilosFormulario();
        Usuarios usuario = new Usuarios();
        usuario.setDni(txtDni.getText());
        usuario.setNombre(txtNombre.getText());
        usuario.setCodigo(txtApellidoP.getText()); // Ajusta según tu modelo
        usuario.setEstado(txtApellidoM.getText()); // Ajusta según tu modelo
        usuario.setCorreoinstitucional(txtDomicilio.getText()); // Ajusta según tu modelo
        usuario.setTelefono(txtTelefono.getText());


        // Validación básica
        if (usuario.getDni().isEmpty()) {
            setErrorStyle(txtDni);
            txtDni.requestFocus();
            return;
        }
        if (usuario.getNombre().isEmpty()) {
            setErrorStyle(txtNombre);
            txtNombre.requestFocus();
            return;
        }

        if (indexEdit != -1) {
            usuariosServicio.update(usuario, indexEdit);
            indexEdit = -1;
        } else {
            usuariosServicio.save(usuario);
        }
        limpiarFormulario();
        listarUsuarios();
    }

    @FXML
    public void editar(Usuarios usuario, int index) {
        txtDni.setText(usuario.getDni());
        txtNombre.setText(usuario.getNombre());
        txtApellidoP.setText(usuario.getCodigo());
        txtApellidoM.setText(usuario.getEstado());
        txtDomicilio.setText(usuario.getCorreoinstitucional());
        txtTelefono.setText(usuario.getTelefono());
        indexEdit = index;
    }

    @FXML
    public void eliminar(int index) {
        usuariosServicio.delete(index);
        listarUsuarios();
    }

    public void definirColumnas() {
        dniColumn = new TableColumn<>("DNI");
        nombreColumn = new TableColumn<>("Nombre");
        apellidoPColumn = new TableColumn<>("Apellido Paterno");
        apellidoMColumn = new TableColumn<>("Apellido Materno");
        domicilioColumn = new TableColumn<>("Domicilio");
        telefonoColumn = new TableColumn<>("Teléfono");
        carreraColumn = new TableColumn<>("Carrera");
        tipoPartiColumn = new TableColumn<>("Tipo Participante");
        opcColumn = new TableColumn<>("Opciones");
        tableUsuarios.getColumns().addAll(dniColumn, nombreColumn, apellidoPColumn, apellidoMColumn, domicilioColumn, telefonoColumn, carreraColumn, tipoPartiColumn, opcColumn);
    }

    private void addActionButtonsToTable() {
        Callback<TableColumn<Usuarios, Void>, TableCell<Usuarios, Void>> cellFactory = param -> new TableCell<>() {
            private final Button editButton = new Button("Editar");
            private final Button deleteButton = new Button("Eliminar");
            {
                editButton.setOnAction(event -> {
                    Usuarios usuario = getTableView().getItems().get(getIndex());
                    editar(usuario, getIndex());
                });
                deleteButton.setOnAction(event -> {
                    eliminar(getIndex());
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(editButton, deleteButton);
                    buttons.setSpacing(10);
                    setGraphic(buttons);
                }
            }
        };
        opcColumn.setCellFactory(cellFactory);
    }

    public void limpiarFormulario() {
        txtDni.clear();
        txtNombre.clear();
        txtApellidoP.clear();
        txtApellidoM.clear();
        txtDomicilio.clear();
        txtTelefono.clear();
    }

    public void limpiarEstilosFormulario() {
        clearErrorStyle(txtDni);
        clearErrorStyle(txtNombre);
        clearErrorStyle(txtApellidoP);
        clearErrorStyle(txtApellidoM);
        clearErrorStyle(txtDomicilio);
        clearErrorStyle(txtTelefono);
    }

    private void clearErrorStyle(TextField field) {
        field.getStyleClass().remove("error-field");
    }

    private void setErrorStyle(TextField field) {
        if (!field.getStyleClass().contains("error-field")) {
            field.getStyleClass().add("error-field");
        }
    }
}
