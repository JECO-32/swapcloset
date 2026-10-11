package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Mensajes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajesRepository
        extends JpaRepository<Mensajes, Integer> {

    List<Mensajes> findBySolicitudId(Integer idSolicitud);

    List<Mensajes> findBySolicitudIdOrderByEnviadoEnAsc(Integer idSolicitud);

    boolean existsBySolicitudIdAndAutorId(Integer idSolicitud, Integer idAutor);
}