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

    @Test
    void perfilesAfines() {

        Estilos casual = estilosRepository.save(Estilos.builder().nombre("Casual ZZ").build());

        Perfil yo = crearPerfil("test_yo_get");
        yo.getEstilos().add(casual);
        perfilRepository.save(yo);

        Perfil afin = crearPerfil("test_afin_get");
        afin.getEstilos().add(casual);
        perfilRepository.save(afin);

        em.flush();

        List<Perfil> resultado = perfilRepository.buscarPerfilesAfines(yo.getId());

        assertThat(resultado).extracting(Perfil::getId).contains(afin.getId());
    }
}