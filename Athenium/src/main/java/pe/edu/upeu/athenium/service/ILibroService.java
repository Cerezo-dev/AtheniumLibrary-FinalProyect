package pe.edu.upeu.athenium.service;

import pe.edu.upeu.athenium.dto.ModeloDataAutocomplet;
import pe.edu.upeu.athenium.model.Libro;

import java.util.List;

public interface ILibroService {
    Libro save(Libro libro);
    List<Libro> findAll();
    Libro update(Libro producto);
    void delete(Long id);
    Libro findById(Long id);
    List<ModeloDataAutocomplet> listAutoCompletProducto(String nombre);
    public List<ModeloDataAutocomplet> listAutoCompletProducto();
}