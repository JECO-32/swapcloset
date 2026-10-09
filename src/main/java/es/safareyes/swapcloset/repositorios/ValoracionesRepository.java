package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Valoraciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValoracionesRepository extends JpaRepository<Valoraciones, Integer> {

    List<Valoraciones> findByPerfilId(Integer idPerfil);

    Optional<Valoraciones> findBySolicitudId(Integer idSolicitud);

    List<Valoraciones> findByPuntuacionGreaterThan(Integer puntuacion);

    boolean existsBySolicitudId(Integer idSolicitud);
}
