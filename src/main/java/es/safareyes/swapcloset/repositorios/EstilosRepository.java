package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Estilos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstilosRepository extends JpaRepository<Estilos, Integer> {

    List<Estilos> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}