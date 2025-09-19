package pe.edu.upeu.library.crudlibrary.repositorio;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import pe.edu.upeu.library.crudlibrary.enums.Carrera;
import pe.edu.upeu.library.crudlibrary.enums.TipoParticipante;
import pe.edu.upeu.library.crudlibrary.modelo.Usuarios;

import java.util.ArrayList;
import java.util.List;

public class UsuariosRepository {
    protected List<Usuarios> usuarios = new ArrayList<>();

    public List<Usuarios> findAll() {
        usuarios.add(new Usuarios(
                "76183244",           // dni
                "U20230001",          // codigo
                "Wilson",             // nombre
                "987654321",          // telefono
                "wilson@upeu.edu.pe", // correoinstitucional
                "password123",        // contraseña
                "activo",             // estado
                Carrera.SISTEMAS,
                TipoParticipante.CLIENTE
        ));
        return usuarios;
    }
}
