package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudesRepository extends JpaRepository<Solicitudes, Integer> {

    List<Solicitudes> findBySolicitanteId(Integer idPerfil);

    List<Solicitudes> findByEstado(EstadoSolicitud estado);

    @Query("""
        SELECT s FROM Solicitudes s
        JOIN s.prendaSolicitada p
        WHERE p.perfil.id = :idPerfil
        ORDER BY s.fechaCreacion DESC
        """)
    List<Solicitudes> buscarSolicitudesRecibidas(@Param("idPerfil") Integer idPerfil);

    @Query("""
        SELECT s FROM Solicitudes s
        JOIN s.prendaSolicitada p
        WHERE p.perfil.id = :idPerfil
        AND s.estado = :estado
        ORDER BY s.fechaCreacion DESC
        """)
    List<Solicitudes> buscarSolicitudesRecibidasPorEstado(@Param("idPerfil") Integer idPerfil, @Param("estado") EstadoSolicitud estado);
}