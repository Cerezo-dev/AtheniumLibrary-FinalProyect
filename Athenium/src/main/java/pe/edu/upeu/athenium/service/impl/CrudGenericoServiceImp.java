package pe.edu.upeu.athenium.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.athenium.exception.ModelNotFoundException;
import pe.edu.upeu.athenium.repository.ICrudGenericoRepository;
import pe.edu.upeu.athenium.service.ICrudGenericoService;

import java.util.List;

/**
 * CORRECCIÓN:
 * 1. Eliminado el método 'delete(T t)' que ya no está en la interfaz.
 * 2. Tu lógica de `update` es correcta, pero la de `save` y `deleteById`
 * está perfecta como estaba en el PDF.
 */
@RequiredArgsConstructor
@Service
public abstract class CrudGenericoServiceImp<T,ID> implements ICrudGenericoService<T,ID> {

    protected abstract ICrudGenericoRepository<T,ID> getRepo();

    @Override
    public T save(T t) {
        return getRepo().save(t);
    }

    @Override
    public T update(ID id, T t) {
        //comprueba si existe.
        getRepo().findById(id).orElseThrow(()->new ModelNotFoundException("ID NOT FOUND "+id));
        // Luego guarda (JPA sabe que es un update si el objeto 't' tiene un ID)
        return getRepo().save(t);
    }

    @Override
    public List<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public T findById(ID id) {
        return getRepo().findById(id).orElseThrow(()->new ModelNotFoundException("ID NOT FOUND "+id));
    }

    @Override
    public void deleteById(ID id) {
        if(!getRepo().existsById(id)){ //verifica si existe el id
            throw new ModelNotFoundException("ID NOT FOUND "+id);//si no existe lanza la excepcion
        }
        getRepo().deleteById(id);//si existe lo borra
    }

    // El método 'delete(T t)' se elimina.
}