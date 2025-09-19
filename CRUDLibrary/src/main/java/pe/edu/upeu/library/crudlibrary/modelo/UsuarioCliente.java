package pe.edu.upeu.library.crudlibrary.modelo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(callSuper = true)
public class UsuarioCliente extends Usuarios {

    private String direccion;
    private String ciudad;
    private String historialPrestamos;

}
