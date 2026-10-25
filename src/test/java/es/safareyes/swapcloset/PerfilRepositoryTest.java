package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PerfilRepositoryTest extends BaseRepositoryTest {

    @Test
    void getPerfilUsuario() {

        Perfil perfil = crearPerfil("perfil_get");

        perfil.setCiudad("Sevilla");
        perfil.setTallaCamiseta("M");
        perfil.setTallaPantalon("40");
        perfil.setTallaZapatos("42");

        perfilRepository.save(perfil);

        Integer idPerfil = perfil.getId();
        String username = perfil.getUsuario().getUsername();

        em.flush();
        em.clear();

        Perfil resultado = perfilRepository
                .findByUsuarioUsername(username)
                .orElseThrow();

        assertThat(resultado.getId()).isEqualTo(idPerfil);
        assertThat(resultado.getCiudad()).isEqualTo("Sevilla");
        assertThat(resultado.getTallaCamiseta()).isEqualTo("M");
        assertThat(resultado.getTallaPantalon()).isEqualTo("40");
        assertThat(resultado.getTallaZapatos()).isEqualTo("42");
    }

    @Test
    void putActualizarPerfil() {

        Perfil perfil = crearPerfil("perfil_put");
        Integer id = perfil.getId();

        perfil.setNombre("Mariana");
        perfil.setCiudad("Cadiz");
        perfil.setBiografia("Me gusta intercambiar ropa");
        perfil.setTallaCamiseta("S");

        perfilRepository.save(perfil);

        em.flush();
        em.clear();

        Perfil resultado = perfilRepository.findById(id).orElseThrow();

        assertThat(resultado.getNombre()).isEqualTo("Mariana");
        assertThat(resultado.getCiudad()).isEqualTo("Cadiz");
        assertThat(resultado.getBiografia())
                .isEqualTo("Me gusta intercambiar ropa");
        assertThat(resultado.getTallaCamiseta()).isEqualTo("S");
        assertThat(resultado.getActualizadoEn()).isNotNull();
    }

    @Test
    void getPerfilesAfines() {

        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Estilos casual = estilosRepository.save(
                Estilos.builder().nombre("Casual_" + sufijo).build()
        );

        Estilos deportivo = estilosRepository.save(
                Estilos.builder().nombre("Deportivo_" + sufijo).build()
        );

        Perfil yo = crearPerfil("perfil_yo");
        yo.getEstilos().add(casual);
        yo.getEstilos().add(deportivo);
        perfilRepository.save(yo);

        Perfil afin = crearPerfil("perfil_afin");
        afin.getEstilos().add(casual);
        perfilRepository.save(afin);

        Perfil noAfin = crearPerfil("perfil_no_afin");

        em.flush();

        List<Perfil> resultado =
                perfilRepository.buscarPerfilesAfines(yo.getId());

        assertThat(resultado)
                .extracting(Perfil::getId)
                .contains(afin.getId())
                .doesNotContain(noAfin.getId(), yo.getId());
    }
}