package pe.edu.upeu.athenium.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.dto.ModeloDataAutocomplet;
import pe.edu.upeu.athenium.model.Libro;
import pe.edu.upeu.athenium.repository.LibroRepository;
import pe.edu.upeu.athenium.service.ILibroService;

import java.util.ArrayList;
import java.util.List;

@Service
public class LibroServiceImp implements ILibroService {
    private static final Logger logger = LoggerFactory.getLogger(LibroServiceImp.class);

    @Autowired
    private LibroRepository libroRepo;

    @Override
    public Libro save(Libro libro) {
        return libroRepo.save(libro);
    }

    @Override
    public List<Libro> findAll() {
        return libroRepo.findAll();
    }

    @Override
    public Libro update(Libro libro) {
        return libroRepo.save(libro);
    }

    @Override
    public void delete(Long id) {
        libroRepo.deleteById(id);
    }

    @Override
    public Libro findById(Long id) {
        return libroRepo.findById(id).orElse(null);
    }

    @Override
    public List<ModeloDataAutocomplet> listAutoCompletProducto(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return listAutoCompletProducto();
        }
        String lower = nombre.toLowerCase();
        List<ModeloDataAutocomplet> resultado = new ArrayList<>();
        for (Libro l : libroRepo.findAll()) {
            boolean match = (l.getTitulo() != null && l.getTitulo().toLowerCase().contains(lower)) ||
                    (l.getAutor() != null && l.getAutor().toLowerCase().contains(lower)) ||
                    (l.getIsbn() != null && l.getIsbn().toLowerCase().contains(lower));
            if (match) {
                ModeloDataAutocomplet dto = new ModeloDataAutocomplet();
                dto.setIdx(l.getId() == null ? "" : String.valueOf(l.getId()));
                dto.setNameDysplay(l.getTitulo() == null ? "" : l.getTitulo());
                String other = (l.getAutor() == null ? "" : l.getAutor());
                if (l.getIsbn() != null && !l.getIsbn().isEmpty()) {
                    other = other.isEmpty() ? "ISBN: " + l.getIsbn() : other + " | ISBN: " + l.getIsbn();
                }
                dto.setOtherData(other);
                resultado.add(dto);
            }
        }
        return resultado;
    }

    @Override
    public List<ModeloDataAutocomplet> listAutoCompletProducto() {
        List<ModeloDataAutocomplet> resultado = new ArrayList<>();
        for (Libro l : libroRepo.findAll()) {
            ModeloDataAutocomplet dto = new ModeloDataAutocomplet();
            dto.setIdx(l.getId() == null ? "" : String.valueOf(l.getId()));
            dto.setNameDysplay(l.getTitulo() == null ? "" : l.getTitulo());
            String other = (l.getAutor() == null ? "" : l.getAutor());
            if (l.getIsbn() != null && !l.getIsbn().isEmpty()) {
                other = other.isEmpty() ? "ISBN: " + l.getIsbn() : other + " | ISBN: " + l.getIsbn();
            }
            dto.setOtherData(other);
            resultado.add(dto);
        }
        return resultado;
    }
}