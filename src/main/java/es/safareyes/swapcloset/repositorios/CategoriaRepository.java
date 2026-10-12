package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Categoria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    List<Categoria> findByNombreContainingIgnoreCase(String nombre);

    Optional<Categoria> findByNombre(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);

    @Query(value = """
        SELECT c.id_categoria, c.nombre, COUNT(p.id_prenda)
        FROM swapcloset.categoria c
        LEFT JOIN swapcloset.prenda p ON p.id_categoria = c.id_categoria AND p.estado_prenda = 'DISPONIBLE'
        GROUP BY c.id_categoria, c.nombre
        ORDER BY c.nombre
        """, nativeQuery = true)
    List<Object[]> categoriasConNumeroDisponibles();
}