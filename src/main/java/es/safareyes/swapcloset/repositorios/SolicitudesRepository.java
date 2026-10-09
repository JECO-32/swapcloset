package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Solicitudes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SolicitudesRepository extends JpaRepository<Solicitudes, Integer> {

    List<Solicitudes> findByPerfilId(Integer idPerfil);

    List<Solicitudes> findByEstadoIgnoreCase(String estado);

    List<Solicitudes> findByTipoSolicitudIgnoreCase(String tipoSolicitud);

    List<Solicitudes> findByPerfilIdAndEstado(
            Integer idPerfil,
            String estado
    );

    List<Solicitudes> findByFechaInicioBetween(
            LocalDate inicio,
            LocalDate fin
    );

    List<Solicitudes> findByEstadoOrderByFechaCreacionDesc(
            String estado
    );
}
