package pe.edu.upeu.library.crudlibrary.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.library.crudlibrary.enums.Carrera;
import pe.edu.upeu.library.crudlibrary.enums.TipoParticipante;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class Usuarios {

    private String dni;
    private String codigo;
    private String nombre;
    private String telefono;
    private String correoinstitucional;
    private String contraseña;
    private String estado;
    private Carrera carrera;
    private TipoParticipante tipoParticipante;

}
