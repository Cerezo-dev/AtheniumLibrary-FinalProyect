package pe.edu.upeu.syslibrary.service.imp;

import pe.edu.upeu.syslibrary.exception.ModelNotFoundException;
import pe.edu.upeu.syslibrary.repositorio.ICrudGenericRepository;
import pe.edu.upeu.syslibrary.service.ICrudGenericService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public abstract class CrudGenericServiceImp<T,ID> implements ICrudGenericService<T,ID> {
    protected abstract ICrudGenericRepository<T,ID> getRepo();

    @Override
    public T save(T t) {
        return getRepo().save(t);
    }

    @Override
    public T update(ID id, T t) {
        getRepo().findById(id).orElseThrow(() -> new ModelNotFoundException("ID NOT FOUND"+id));
        return getRepo().save(t);
    }

    @Override
    public List<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public T findById(ID id) {
        return getRepo().findById(id).orElseThrow(() -> new ModelNotFoundException("ID NOT FOUND"+id));
    }

    @Override
    public void delete(T t) {
        getRepo().delete(t);
    }

    @Override
    public void deleteById(ID id) {
        if(!getRepo().existsById(id)){
            throw new ModelNotFoundException("ID NOT FOUND"+id);
        }
        getRepo().deleteById(id);
    }
}
