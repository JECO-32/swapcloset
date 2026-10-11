package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    List<Notificacion> findByPerfilId(Integer idPerfil);

    List<Notificacion> findByPerfilIdAndLeida(Integer idPerfil, Boolean leida);

    long countByPerfilIdAndLeida(Integer idPerfil, Boolean leida);
}
