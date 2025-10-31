package pe.edu.upeu.athenium.libro.service;

import pe.edu.upeu.athenium.common.dto.ModeloDataAutocomplet;
import pe.edu.upeu.athenium.libro.entity.Libro;

import java.util.List;
/**
public interface ILibroService extends ICrudGenericoService<Libro, Long> {
    List<ModeloDataAutocomplet> listAutoCompletProducto(String nombre);
    List<ModeloDataAutocomplet> listAutoCompletProducto();
} */
// Temporarl (Creo)
public interface ILibroService {
    Libro save(Libro libro);
    List<Libro> findAll();
    Libro update(Libro producto);
    void delete(Long id);
    Libro findById(Long id);
    List<ModeloDataAutocomplet> listAutoCompletProducto(String nombre);
    public List<ModeloDataAutocomplet> listAutoCompletProducto();


}