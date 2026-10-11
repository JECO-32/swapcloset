package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.EstadoConservacion;
import es.safareyes.swapcloset.Modelos.EstadoPrenda;
import es.safareyes.swapcloset.Modelos.Prenda;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrendaRepository extends
        JpaRepository<Prenda, Integer>,
        JpaSpecificationExecutor<Prenda> {

    List<Prenda> findByPerfilId(Integer idPerfil);

    List<Prenda> findByCategoriaId(Integer idCategoria);

    List<Prenda> findByTallaIgnoreCase(String talla);

    List<Prenda> findByEstadoPrenda(
            EstadoPrenda estadoPrenda
    );

    List<Prenda> findByEstadoConservacion(
            EstadoConservacion estadoConservacion
    );

    List<Prenda> findByPermiteIntercambioTrue();

    List<Prenda> findByPermitePrestamoTrue();

    List<Prenda> findByTituloContainingIgnoreCase(
            String titulo
    );

    List<Prenda> findByCategoriaIdAndEstadoPrenda(
            Integer idCategoria,
            EstadoPrenda estadoPrenda
    );


    // Q6 - PAGINACION

    Page<Prenda> findByTituloContainingIgnoreCase(
            String titulo,
            Pageable pageable
    );


    // Q8 - EVITAR N+1

    @EntityGraph(attributePaths = {
            "perfil",
            "categoria"
    })
    Optional<Prenda> findConRelacionesById(
            Integer id
    );


    // Q3 - SQL NATIVO

    @Query(
            value = """
            SELECT
                DATE_TRUNC(
                    'month',
                    p.fecha_publicacion
                ) AS mes,
                c.nombre AS categoria,
                COUNT(*) AS total
            FROM swapcloset.prenda p
            JOIN swapcloset.categoria c
                ON c.id_categoria = p.id_categoria
            WHERE p.fecha_publicacion
                BETWEEN :desde AND :hasta
            GROUP BY
                DATE_TRUNC(
                    'month',
                    p.fecha_publicacion
                ),
                c.nombre
            ORDER BY mes
        """,
            nativeQuery = true
    )
    List<Object[]> actividadPorCategoriaYMes(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}