package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SolicitudesRepositoryTest extends BaseRepositoryTest {

    private Perfil duenia;
    private Perfil pide1;
    private Perfil pide2;
    private Prenda prenda;

    @BeforeEach
    void preparar() {
        duenia = crearPerfil("test_duenia");
        pide1 = crearPerfil("test_pide1");
        pide2 = crearPerfil("test_pide2");
        Categoria categoria = crearCategoria("Cat solicitudes-zz");
        prenda = crearPrenda(duenia, categoria, "Prenda solicitada", EstadoPrenda.DISPONIBLE);
    }

    // Q1 - derivadas
    @Test
    void consultasDerivadasDeSolicitudes() {
        crearSolicitud(pide1, prenda, TipoSolicitud.PRESTAMO, EstadoSolicitud.PENDIENTE);
        crearSolicitud(pide1, prenda, TipoSolicitud.PRESTAMO, EstadoSolicitud.ACEPTADA);
        crearSolicitud(pide1, prenda, TipoSolicitud.PRESTAMO, EstadoSolicitud.RETRASADA);

        assertThat(solicitudesRepository.findBySolicitanteId(pide1.getId())).hasSize(3);

        assertThat(solicitudesRepository
                .existsBySolicitanteIdAndPrendaSolicitadaIdAndEstado(
                        pide1.getId(), prenda.getId(), EstadoSolicitud.PENDIENTE)).isTrue();
        assertThat(solicitudesRepository
                .existsBySolicitanteIdAndPrendaSolicitadaIdAndEstado(
                        pide2.getId(), prenda.getId(), EstadoSolicitud.PENDIENTE)).isFalse();

        long activos = solicitudesRepository.countBySolicitanteIdAndTipoSolicitudAndEstadoIn(
                pide1.getId(), TipoSolicitud.PRESTAMO,
                List.of(EstadoSolicitud.ACEPTADA, EstadoSolicitud.RETRASADA));
        assertThat(activos).isEqualTo(2);
    }

    // Q2 - JPQL con JOIN
    @Test
    void solicitudesRecibidasFiltradasPorEstado() {
        Solicitudes pendiente = crearSolicitud(pide1, prenda,
                TipoSolicitud.INTERCAMBIO, EstadoSolicitud.PENDIENTE);
        crearSolicitud(pide2, prenda, TipoSolicitud.INTERCAMBIO, EstadoSolicitud.ACEPTADA);

        List<Solicitudes> soloPendientes = solicitudesRepository
                .buscarSolicitudesRecibidas(duenia.getId(), EstadoSolicitud.PENDIENTE);

        assertThat(soloPendientes).extracting(Solicitudes::getId)
                .containsExactly(pendiente.getId());
    }

    // Q2 - mismo método sin filtro de estado (parámetro null)
    @Test
    void solicitudesRecibidasSinFiltroDeEstado() {
        Solicitudes s1 = crearSolicitud(pide1, prenda,
                TipoSolicitud.INTERCAMBIO, EstadoSolicitud.PENDIENTE);
        Solicitudes s2 = crearSolicitud(pide2, prenda,
                TipoSolicitud.INTERCAMBIO, EstadoSolicitud.ACEPTADA);

        List<Solicitudes> todas = solicitudesRepository
                .buscarSolicitudesRecibidas(duenia.getId(), null);

        assertThat(todas).extracting(Solicitudes::getId)
                .containsExactlyInAnyOrder(s1.getId(), s2.getId());
    }

    // Q4 - UPDATE masivo con @Modifying
    @Test
    void alAceptarUnaSolicitudSeRechazanLasDemasPendientes() {
        Solicitudes aceptada = crearSolicitud(pide1, prenda,
                TipoSolicitud.INTERCAMBIO, EstadoSolicitud.PENDIENTE);
        Solicitudes otra1 = crearSolicitud(pide2, prenda,
                TipoSolicitud.INTERCAMBIO, EstadoSolicitud.PENDIENTE);
        Solicitudes otra2 = crearSolicitud(pide2, prenda,
                TipoSolicitud.PRESTAMO, EstadoSolicitud.PENDIENTE);
        em.flush();

        int afectadas = solicitudesRepository.rechazarOtrasSolicitudes(
                prenda.getId(), aceptada.getId(),
                EstadoSolicitud.PENDIENTE, EstadoSolicitud.RECHAZADA);

        assertThat(afectadas).isEqualTo(2);

        assertThat(solicitudesRepository.findById(otra1.getId()).orElseThrow().getEstado())
                .isEqualTo(EstadoSolicitud.RECHAZADA);
        assertThat(solicitudesRepository.findById(otra2.getId()).orElseThrow().getEstado())
                .isEqualTo(EstadoSolicitud.RECHAZADA);
        assertThat(solicitudesRepository.findById(aceptada.getId()).orElseThrow().getEstado())
                .isEqualTo(EstadoSolicitud.PENDIENTE);
    }

    // Q3 - SQL nativo con GROUP BY
    @Test
    void rankingDeUsuariosMasActivos() {
        crearSolicitud(pide1, prenda, TipoSolicitud.PRESTAMO, EstadoSolicitud.PENDIENTE);
        crearSolicitud(pide1, prenda, TipoSolicitud.PRESTAMO, EstadoSolicitud.CANCELADA);
        crearSolicitud(pide2, prenda, TipoSolicitud.PRESTAMO, EstadoSolicitud.PENDIENTE);
        em.flush();

        List<Object[]> ranking = solicitudesRepository.rankingUsuariosActivos(
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        Object[] filaPide1 = ranking.stream()
                .filter(f -> ((Number) f[0]).intValue() == pide1.getId())
                .findFirst().orElseThrow();
        Object[] filaPide2 = ranking.stream()
                .filter(f -> ((Number) f[0]).intValue() == pide2.getId())
                .findFirst().orElseThrow();

        assertThat(((Number) filaPide1[1]).longValue()).isEqualTo(2);
        assertThat(((Number) filaPide2[1]).longValue()).isEqualTo(1);
    }
}