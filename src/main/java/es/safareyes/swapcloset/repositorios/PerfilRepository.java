package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Perfil;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerfilRepository
        extends JpaRepository<Perfil, Integer> {

    Optional<Perfil> findByUsuarioUsername(String username);

    Optional<Perfil> findByEmailIgnoreCase(String email);

    List<Perfil> findByCiudadIgnoreCase(String ciudad);

    boolean existsByEmailIgnoreCase(String email);


    @Query("""
        SELECT p
        FROM Perfil p
        JOIN p.estilos e
        WHERE p.id <> :idPerfil
        AND e IN (
            SELECT e2
            FROM Perfil yo
            JOIN yo.estilos e2
            WHERE yo.id = :idPerfil
        )
        GROUP BY p
        ORDER BY COUNT(e) DESC
    """)
    List<Perfil> buscarPerfilesAfines(
            @Param("idPerfil") Integer idPerfil
    );
}