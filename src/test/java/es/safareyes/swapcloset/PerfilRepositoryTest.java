package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PerfilRepositoryTest extends BaseRepositoryTest {

    private Perfil perfilConEstilos(String username, Estilos... estilos) {
        Perfil perfil = crearPerfil(username);
        perfil.getEstilos().addAll(List.of(estilos));
        return perfilRepository.save(perfil);
    }

    private Estilos estilo(String nombre) {
        return estilosRepository.save(Estilos.builder().nombre(nombre).build());
    }

    // Q1 - derivadas
    @Test
    void consultasDerivadasDePerfil() {
        Perfil perfil = crearPerfil("test_perfil");
        perfil.setCiudad("Ciudad-zz");
        perfilRepository.save(perfil);

        assertThat(perfilRepository.findByUsuarioUsername("test_perfil")).isPresent();
        assertThat(perfilRepository.findByEmailIgnoreCase("TEST_PERFIL@TEST.ES")).isPresent();
        assertThat(perfilRepository.existsByEmailIgnoreCase("test_perfil@test.es")).isTrue();
        assertThat(perfilRepository.existsByEmailIgnoreCase("no_existe@test.es")).isFalse();
        assertThat(perfilRepository.findByCiudadIgnoreCase("CIUDAD-ZZ"))
                .extracting(Perfil::getId).contains(perfil.getId());
    }

    // Q2 - JPQL con JOIN, COUNT, GROUP BY y ORDER BY
    @Test
    void perfilesAfinesSeOrdenanPorEstilosEnComun() {
        Estilos a = estilo("Estilo A-zz");
        Estilos b = estilo("Estilo B-zz");
        Estilos c = estilo("Estilo C-zz");

        Perfil yo = perfilConEstilos("test_yo", a, b, c);
        Perfil dosEnComun = perfilConEstilos("test_afin2", a, b);
        Perfil unoEnComun = perfilConEstilos("test_afin1", a);
        perfilConEstilos("test_ninguno", estilo("Estilo D-zz"));
        em.flush();

        List<Perfil> afines = perfilRepository.buscarPerfilesAfines(yo.getId());

        assertThat(afines).extracting(Perfil::getId)
                .containsExactly(dosEnComun.getId(), unoEnComun.getId());
    }
}