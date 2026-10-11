package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.EstadoSolicitud;
import es.safareyes.swapcloset.Modelos.Solicitudes;
import es.safareyes.swapcloset.Modelos.TipoSolicitud;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Collection;
import java.util.List;

@Repository
public interface SolicitudesRepository
        extends JpaRepository<Solicitudes, Integer> {


    // Q1 - DERIVADAS

    List<Solicitudes> findBySolicitanteId(
            Integer idPerfil
    );

    List<Solicitudes> findByEstado(
            EstadoSolicitud estado
    );

    List<Solicitudes> findByTipoSolicitud(
            TipoSolicitud tipoSolicitud
    );

    boolean existsBySolicitanteIdAndPrendaSolicitadaIdAndEstado(
            Integer idPerfil,
            Integer idPrenda,
            EstadoSolicitud estado
    );

    long countBySolicitanteIdAndTipoSolicitudAndEstadoIn(
            Integer idPerfil,
            TipoSolicitud tipoSolicitud,
            Collection<EstadoSolicitud> estados
    );

    List<Solicitudes> findByFechaInicioBetween(
            LocalDate inicio,
            LocalDate fin
    );

    List<Solicitudes> findByEstadoOrderByFechaCreacionDesc(
            EstadoSolicitud estado
    );


    // Q2 - JPQL
    // Solicitudes recibidas por el propietario

    @Query("""
        SELECT s
        FROM Solicitudes s
        JOIN s.prendaSolicitada p
        WHERE p.perfil.id = :idPerfil
        AND (
            :estado IS NULL
            OR s.estado = :estado
        )
    """)
    List<Solicitudes> buscarSolicitudesRecibidas(
            @Param("idPerfil") Integer idPerfil,
            @Param("estado") EstadoSolicitud estado
    );


    // Q4 - MODIFICACION
    // Al aceptar una solicitud,
    // rechaza las demás pendientes

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE Solicitudes s
        SET s.estado = :nuevoEstado
        WHERE s.prendaSolicitada.id = :idPrenda
        AND s.estado = :estadoPendiente
        AND s.id <> :idSolicitudAceptada
    """)
    int rechazarOtrasSolicitudes(
            @Param("idPrenda") Integer idPrenda,

            @Param("idSolicitudAceptada")
            Integer idSolicitudAceptada,

            @Param("estadoPendiente")
            EstadoSolicitud estadoPendiente,

            @Param("nuevoEstado")
            EstadoSolicitud nuevoEstado
    );


    // Q3 - SEGUNDA CONSULTA NATIVA
    // Ranking de usuarios más activos

    @Query(
            value = """
            SELECT
                s.id_perfil,
                COUNT(*) AS total
            FROM swapcloset.solicitudes s
            WHERE s.fecha_creacion
                BETWEEN :desde AND :hasta
            GROUP BY s.id_perfil
            ORDER BY total DESC
        """,
            nativeQuery = true
    )
    List<Object[]> rankingUsuariosActivos(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}