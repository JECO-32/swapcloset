package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Notificacion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
}