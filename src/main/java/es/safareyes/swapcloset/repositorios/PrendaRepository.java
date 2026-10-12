package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.dto.ActividadCategoriaDto;
import es.safareyes.swapcloset.dto.PrendaTarjetaView;

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
public interface PrendaRepository extends JpaRepository<Prenda, Integer>, JpaSpecificationExecutor<Prenda> {

    List<Prenda> findByPerfilId(Integer idPerfil);

    List<Prenda> findByCategoriaId(Integer idCategoria);

    List<Prenda> findByEstadoPrenda(EstadoPrenda estadoPrenda);

    List<Prenda> findByTituloContainingIgnoreCase(String titulo);

    @EntityGraph(attributePaths = {"fotos", "perfil", "categoria"})
    Optional<Prenda> findConRelacionesById(Integer id);

    List<PrendaTarjetaView> findByEstadoPrendaOrderByFechaPublicacionDesc(EstadoPrenda estadoPrenda);

    @Query("""
        SELECT new es.safareyes.swapcloset.dto.ActividadCategoriaDto(c.nombre, COUNT(p))
        FROM Prenda p
        JOIN p.categoria c
        WHERE p.fechaPublicacion BETWEEN :desde AND :hasta
        GROUP BY c.nombre
        ORDER BY COUNT(p) DESC
        """)
    List<ActividadCategoriaDto> actividadPorCategoria(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query(value = """
        SELECT DATE_TRUNC('month', p.fecha_publicacion) AS mes, c.nombre AS categoria, COUNT(*) AS total
        FROM swapcloset.prenda p
        JOIN swapcloset.categoria c ON c.id_categoria = p.id_categoria
        WHERE p.fecha_publicacion BETWEEN :desde AND :hasta
        GROUP BY DATE_TRUNC('month', p.fecha_publicacion), c.nombre
        ORDER BY mes
        """, nativeQuery = true)
    List<Object[]> actividadPorCategoriaYMes(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}