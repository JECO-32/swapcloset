package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface SolicitudesRepository
        extends JpaRepository<Solicitudes, Integer> {

    boolean existsBySolicitanteIdAndPrendaSolicitadaIdAndEstado(
            Integer idPerfil,
            Integer idPrenda,
            EstadoSolicitud estado
    );

    long countBySolicitanteIdAndTipoSolicitudAndEstadoIn(
            Integer idPerfil,
            TipoSolicitud tipo,
            Collection<EstadoSolicitud> estados
    );

    Page<Solicitudes> findBySolicitanteId(
            Integer idPerfil,
            Pageable pageable
    );

    Page<Solicitudes> findBySolicitanteIdAndEstado(
            Integer idPerfil,
            EstadoSolicitud estado,
            Pageable pageable
    );

    @Query("""
            SELECT s
            FROM Solicitudes s
            JOIN s.prendaSolicitada p
            WHERE p.perfil.id = :idPerfil
            AND (:estado IS NULL OR s.estado = :estado)
            """)
    Page<Solicitudes> buscarRecibidas(
            @Param("idPerfil") Integer idPerfil,
            @Param("estado") EstadoSolicitud estado,
            Pageable pageable
    );

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Solicitudes s
            SET s.estado = :nuevoEstado,
                s.fechaRespuesta = :fecha
            WHERE s.prendaSolicitada.id = :idPrenda
            AND s.estado = :estadoActual
            AND s.id <> :idAceptada
            """)
    int rechazarOtrasPendientes(
            @Param("idPrenda") Integer idPrenda,
            @Param("idAceptada") Integer idAceptada,
            @Param("estadoActual") EstadoSolicitud estadoActual,
            @Param("nuevoEstado") EstadoSolicitud nuevoEstado,
            @Param("fecha") LocalDateTime fecha
    );

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE Solicitudes s
            SET s.estado = :nuevoEstado
            WHERE (s.prendaSolicitada.id = :idPrenda
                   OR s.prendaOfrecida.id = :idPrenda)
            AND s.estado = :estadoActual
            """)
    int cancelarPendientesDePrenda(
            @Param("idPrenda") Integer idPrenda,
            @Param("estadoActual") EstadoSolicitud estadoActual,
            @Param("nuevoEstado") EstadoSolicitud nuevoEstado
    );

    @Query(value = """
            SELECT DATE_TRUNC('month', s.fecha_creacion) AS mes,
                   c.nombre AS categoria,
                   COUNT(*) FILTER (
                       WHERE s.tipo_solicitud = 'INTERCAMBIO'
                   ) AS intercambios,
                   COUNT(*) FILTER (
                       WHERE s.tipo_solicitud = 'PRESTAMO'
                   ) AS prestamos
            FROM swapcloset.solicitudes s
            JOIN swapcloset.prenda p
              ON p.id_prenda = s.id_prenda_solicitada
            JOIN swapcloset.categoria c
              ON c.id_categoria = p.id_categoria
            WHERE s.fecha_creacion BETWEEN :desde AND :hasta
            GROUP BY DATE_TRUNC('month', s.fecha_creacion), c.nombre
            ORDER BY mes, categoria
            """, nativeQuery = true)
    List<Object[]> actividadSolicitudesPorMes(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}