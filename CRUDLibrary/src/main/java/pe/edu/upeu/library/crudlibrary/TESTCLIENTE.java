package pe.edu.upeu.library.crudlibrary;

import pe.edu.upeu.library.crudlibrary.modelo.UsuarioCliente;
import pe.edu.upeu.library.crudlibrary.repositorio.UsuarioClienteRepository;

public class TESTCLIENTE {
    public static void main(String[] args) {
        UsuarioClienteRepository repo = new UsuarioClienteRepository();

        // Crear usuario
        UsuarioCliente cliente = new UsuarioCliente();
        cliente.setDireccion("Av. Siempre Viva 123");
        cliente.setCiudad("juliaca");
        cliente.setHistorialPrestamos("ni idea");
        cliente.setDni("12345678");
        cliente.setNombre("Juan");
        repo.create(cliente);

        // Leer usuario
        UsuarioCliente encontrado = repo.read("12345678");
        System.out.println("Encontrado: " + encontrado);

        // Actualizar usuario
        cliente.setNombre("Juan Actualizado");
        repo.update("12345678", cliente);
        System.out.println("Actualizado: " + repo.read("12345678"));

        // Eliminar usuario por DNI
        repo.delete("12345678");
        System.out.println("Lista después de eliminar: " + repo.listar());
    }
}
