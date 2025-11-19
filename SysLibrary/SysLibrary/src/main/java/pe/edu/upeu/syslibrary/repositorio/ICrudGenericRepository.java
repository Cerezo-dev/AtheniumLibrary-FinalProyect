package pe.edu.upeu.syslibrary.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean // <- evita que Spring cree una tabla o bean de esta clase
public interface ICrudGenericRepository<T, ID> extends JpaRepository<T, ID> {
// Aquí podrías agregar métodos genéricos si quieres (ej: búsqueda paginada)
}
