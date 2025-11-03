package pe.edu.upeu.athenium.genero.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.athenium.common.dto.ComboBoxOption;
import pe.edu.upeu.athenium.genero.entity.Genero;
import pe.edu.upeu.athenium.genero.repository.GeneroRepository;
import pe.edu.upeu.athenium.common.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.genero.service.IGeneroService;
import pe.edu.upeu.athenium.common.service.impl.CrudGenericoServiceImp;

import java.util.ArrayList;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class GeneroServiceImp extends CrudGenericoServiceImp<Genero,Long> implements IGeneroService {

    private final GeneroRepository generoRepository;

    @Override
    protected ICrudGenericoRepository<Genero, Long> getRepo() {
        return generoRepository;
    }

    @Override
    public List<ComboBoxOption> listarCombobox() {
        try {
            List<ComboBoxOption> listar = new ArrayList<>();

            // Agregar opción por defecto
            ComboBoxOption opcionDefault = new ComboBoxOption();
            opcionDefault.setKey("0");
            opcionDefault.setValue("-- Seleccione Género --");
            listar.add(opcionDefault);

            // Agregar géneros desde la base de datos
            for(Genero genero : generoRepository.findAll()) {
                ComboBoxOption cb = new ComboBoxOption();
                cb.setKey(String.valueOf(genero.getId()));
                cb.setValue(genero.getNombre());
                listar.add(cb);
            }

            return listar;
        } catch (Exception e) {
            System.err.println("Error al cargar géneros: " + e.getMessage());
            // Retornar lista vacía en caso de error
            List<ComboBoxOption> listaVacia = new ArrayList<>();
            ComboBoxOption errorOption = new ComboBoxOption();
            errorOption.setKey("0");
            errorOption.setValue("Error al cargar géneros");
            listaVacia.add(errorOption);
            return listaVacia;
        }
    }
}