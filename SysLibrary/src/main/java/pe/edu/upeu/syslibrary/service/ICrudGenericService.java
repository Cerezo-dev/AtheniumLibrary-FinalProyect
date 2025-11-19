package pe.edu.upeu.syslibrary.service;

import java.util.List;

public interface ICrudGenericService<T, ID> {
    T save(T t);
    T update(ID id, T t);
    List<T> findAll();
    T findById(ID id);
    void deleteById(ID id);
    void delete(T t);
}
