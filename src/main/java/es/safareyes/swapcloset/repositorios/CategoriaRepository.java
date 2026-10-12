package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    // Q1 - DERIVADAS

    List<Categoria> findByNombreContainingIgnoreCase(String nombre);

    Optional<Categoria> findByNombre(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}