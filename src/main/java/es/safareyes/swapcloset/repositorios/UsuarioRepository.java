package es.safareyes.swapcloset.repositorios;

import es.safareyes.swapcloset.Modelos.RolUsuario;
import es.safareyes.swapcloset.Modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    boolean existsByUsername(String username);

    List<Usuario> findByRol(RolUsuario rol);
}
