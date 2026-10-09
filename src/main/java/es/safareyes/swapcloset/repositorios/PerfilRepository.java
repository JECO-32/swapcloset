package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Integer> {

    Optional<Perfil> findByUsuarioUsername(String username);

    Optional<Perfil> findByEmail(String email);

    List<Perfil> findByCiudadIgnoreCase(String ciudad);

    boolean existsByEmail(String email);
}
