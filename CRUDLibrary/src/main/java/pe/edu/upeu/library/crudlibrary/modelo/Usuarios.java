package pe.edu.upeu.library.crudlibrary.modelo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class Usuarios {
    private String dni;
    private String nombre;
    private String telefono;
    private String correoinstitucional;
    private String contraseña;
    private String estado;
    private String carrera;

}
