package pe.edu.upeu.athenium.service;

import pe.edu.upeu.athenium.dto.ModeloDataAutocomplet;
import pe.edu.upeu.athenium.model.Cliente;

import java.util.List;

public interface IClienteService extends ICrudGenericoService<Cliente,String> {
    List<ModeloDataAutocomplet> listAutoCompletCliente();
}
