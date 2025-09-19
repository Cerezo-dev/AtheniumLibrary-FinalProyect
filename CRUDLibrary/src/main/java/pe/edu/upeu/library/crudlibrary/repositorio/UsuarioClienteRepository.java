package pe.edu.upeu.library.crudlibrary.repositorio;

import pe.edu.upeu.library.crudlibrary.modelo.UsuarioCliente;
import java.util.ArrayList;
import java.util.List;

public class UsuarioClienteRepository {
    /* private List<UsuarioCliente> clientes = new ArrayList<>();

    //CRUD
    //Create
    public void create(UsuarioCliente cliente) {
        clientes.add(cliente);
    }

    // Read
    public UsuarioCliente read(String dni) {
        for (UsuarioCliente cliente : clientes) {
            if (cliente.getDni().equals(dni)) {
                return cliente;
            }
        }
        return null;
    }

    // Update
    public void update(String dni, UsuarioCliente datosActualizados) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getDni().equals(dni)) {
                clientes.set(i, datosActualizados);
                break;
            }
        }
    }

    //Delete
    public void delete(UsuarioCliente usuario) {
        clientes.remove(usuario);
    }
}
*/
    private List<UsuarioCliente> clientes = new ArrayList<>();
    //CRUD
    // Create
    public void create(UsuarioCliente cliente) {
        clientes.add(cliente);
    }

    // Read
    public UsuarioCliente read(String dni) {
        for (UsuarioCliente cliente : clientes) {
            if (cliente.getDni().equals(dni)) {
                return cliente;
            }
        }
        return null;
    }

    // Update
    public void update(String dni, UsuarioCliente datosActualizados) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getDni().equals(dni)) {
                clientes.set(i, datosActualizados);
                break;
            }
        }
    }

    // Delete por dni
    public void delete(String dni) {
        clientes.removeIf(cliente -> cliente.getDni().equals(dni));
    }

    // Listar todos
    public List<UsuarioCliente> listar() {
        return clientes;
    }
}

