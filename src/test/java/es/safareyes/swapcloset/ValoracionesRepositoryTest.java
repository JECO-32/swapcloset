package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.repositorios.ValoracionesRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ValoracionesRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ValoracionesRepository valoracionesRepository;

    private Perfil duenia;
    private Perfil pide;
    private Solicitudes solicitud1;
    private Valoraciones cinco;
    private Valoraciones tres;

    @BeforeEach
    void preparar() {
        duenia = crearPerfil("test_valorada");
        pide = crearPerfil("test_valora");
        Categoria categoria = crearCategoria("Cat valoraciones-zz");
        Prenda prenda = crearPrenda(duenia, categoria, "Prenda valorada", EstadoPrenda.DISPONIBLE);

        solicitud1 = crearSolicitud(pide, prenda, TipoSolicitud.INTERCAMBIO, EstadoSolicitud.FINALIZADA);
        Solicitudes solicitud2 = crearSolicitud(pide, prenda, TipoSolicitud.PRESTAMO, EstadoSolicitud.FINALIZADA);

        cinco = valoracionesRepository.save(Valoraciones.builder()
                .solicitud(solicitud1).autor(pide).valorado(duenia)
                .puntuacion(5).comentario("Genial").build());
        tres = valoracionesRepository.save(Valoraciones.builder()
                .solicitud(solicitud2).autor(pide).valorado(duenia)
                .puntuacion(3).build());
        em.flush();
    }

    // Q1 - derivadas
    @Test
    void consultasDerivadasDeValoraciones() {
        assertThat(valoracionesRepository.findByValoradoId(duenia.getId())).hasSize(2);
        assertThat(valoracionesRepository.findBySolicitudId(solicitud1.getId())).hasSize(1);

        List<Valoraciones> mayoresQueCuatro = valoracionesRepository.findByPuntuacionGreaterThan(4);
        assertThat(mayoresQueCuatro).extracting(Valoraciones::getId)
                .contains(cinco.getId())
                .doesNotContain(tres.getId());

        assertThat(valoracionesRepository
                .existsBySolicitudIdAndAutorId(solicitud1.getId(), pide.getId())).isTrue();
        assertThat(valoracionesRepository
                .existsBySolicitudIdAndAutorId(solicitud1.getId(), duenia.getId())).isFalse();
    }

    // Q2 - JPQL con AVG
    @Test
    void calculaLaMediaDeValoraciones() {
        Double media = valoracionesRepository.calcularMediaValoraciones(duenia.getId());

        assertThat(media).isEqualTo(4.0);
    }

    @Test
    void sinValoracionesLaMediaEsNull() {
        assertThat(valoracionesRepository.calcularMediaValoraciones(pide.getId())).isNull();
    }
}