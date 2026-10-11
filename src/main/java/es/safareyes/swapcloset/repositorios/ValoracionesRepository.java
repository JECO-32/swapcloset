package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Valoraciones;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValoracionesRepository
        extends JpaRepository<Valoraciones, Integer> {

    List<Valoraciones> findByValoradoId(Integer idPerfil);

    List<Valoraciones> findBySolicitudId(Integer idSolicitud);

    List<Valoraciones> findByPuntuacionGreaterThan(Integer puntuacion);

    boolean existsBySolicitudIdAndAutorId(Integer idSolicitud, Integer idAutor);


    // Q2 - JPQL con AVG

    @Query("""
        SELECT AVG(v.puntuacion)
        FROM Valoraciones v
        WHERE v.valorado.id = :idPerfil
    """)
    Double calcularMediaValoraciones(
            @Param("idPerfil") Integer idPerfil
    );
}