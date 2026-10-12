package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Mensajes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajesRepository extends JpaRepository<Mensajes, Integer> {

    Page<Mensajes> findBySolicitudIdOrderByEnviadoEnAsc(Integer idSolicitud, Pageable pageable);
}