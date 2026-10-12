package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Valoraciones;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ValoracionesRepository extends JpaRepository<Valoraciones, Integer> {

    @Query("""
        SELECT AVG(v.puntuacion)
        FROM Valoraciones v
        WHERE v.valorado.id = :idPerfil
        """)
    Double calcularMediaValoraciones(@Param("idPerfil") Integer idPerfil);
}