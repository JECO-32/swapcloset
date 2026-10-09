package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Prenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrendaRepository extends
        JpaRepository<Prenda, Integer>,
        JpaSpecificationExecutor<Prenda> {

    List<Prenda> findByPerfilId(Integer idPerfil);

    List<Prenda> findByCategoriaId(Integer idCategoria);

    List<Prenda> findByDisponibilidad(Boolean disponibilidad);

    List<Prenda> findByTallaIgnoreCase(String talla);

    List<Prenda> findByEstadoIgnoreCase(String estado);

    List<Prenda> findByPermiteIntercambioTrue();

    List<Prenda> findByPermitePrestamoTrue();

    List<Prenda> findByTituloContainingIgnoreCase(String titulo);

    List<Prenda> findByCategoriaIdAndDisponibilidadTrue(
            Integer idCategoria
    );
}
