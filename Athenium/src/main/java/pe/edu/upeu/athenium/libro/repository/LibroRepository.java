package pe.edu.upeu.athenium.libro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.upeu.athenium.libro.entity.Libro;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    // Aquí puedes agregar métodos personalizados si necesitas realizar consultas específicas
    @Query(value = "SELECT l.* FROM athenium_libro p WHERE l.titulo like :filter", nativeQuery = true)
    List<Libro> listAutoCompletProducto(@Param("filter") String filter);

    @Query("SELECT l FROM Libro l WHERE l.titulo LIKE :filter")
    List<Libro> listAutoCompletProductoJ(@Param("filter") String filter);


    @Query(value = "SELECT l.* FROM athenium_libro l WHERE l.id=:filter", nativeQuery = true)
    List<Libro> listProductoMarca(@Param("filter") Integer filter);

    @Query("SELECT l FROM Libro l WHERE l.autor = :filter")
    List<Libro> listProductoMarcaJ(@Param("filter") Integer filter);
}