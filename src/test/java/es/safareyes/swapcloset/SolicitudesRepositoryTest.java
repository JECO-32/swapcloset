package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SolicitudesRepositoryTest extends BaseRepositoryTest {

    @Test
    void crearSolicitudIntercambioYPrestamo() {

        Perfil duenio = crearPerfil("duenio");
        Perfil solicitante = crearPerfil("solicitante");
        Categoria categoria = crearCategoria("Solicitudes");

        Prenda pedida = crearPrenda(duenio, categoria,
                "Chaqueta", EstadoPrenda.DISPONIBLE);

        Prenda ofrecida = crearPrenda(solicitante, categoria,
                "Camiseta", EstadoPrenda.DISPONIBLE);

        Solicitudes intercambio = solicitudesRepository.save(Solicitudes.builder()
                .solicitante(solicitante)
                .prendaSolicitada(pedida)
                .prendaOfrecida(ofrecida)
                .tipoSolicitud(TipoSolicitud.INTERCAMBIO)
                .estado(EstadoSolicitud.PENDIENTE)
                .build());

        LocalDate inicio = LocalDate.now().plusDays(1);

        Solicitudes prestamo = solicitudesRepository.save(Solicitudes.builder()
                .solicitante(solicitante)
                .prendaSolicitada(pedida)
                .tipoSolicitud(TipoSolicitud.PRESTAMO)
                .estado(EstadoSolicitud.PENDIENTE)
                .fechaInicio(inicio)
                .fechaFin(inicio.plusDays(7))
                .build());

        Integer idIntercambio = intercambio.getId();
        Integer idPrestamo = prestamo.getId();

        em.flush();
        em.clear();

        assertThat(solicitudesRepository.findById(idIntercambio).orElseThrow()
                .getTipoSolicitud()).isEqualTo(TipoSolicitud.INTERCAMBIO);

        assertThat(solicitudesRepository.findById(idPrestamo).orElseThrow()
                .getFechaFin()).isEqualTo(inicio.plusDays(7));
    }

    @Test
    void obtenerSolicitudesEnviadasYRecibidas() {

        Perfil duenio = crearPerfil("duenio");
        Perfil solicitante = crearPerfil("solicitante");
        Categoria categoria = crearCategoria("Solicitudes");

        Prenda prenda = crearPrenda(duenio, categoria,
                "Sudadera", EstadoPrenda.DISPONIBLE);

        crearSolicitud(solicitante, prenda,
                TipoSolicitud.INTERCAMBIO, EstadoSolicitud.PENDIENTE);

        crearSolicitud(solicitante, prenda,
                TipoSolicitud.PRESTAMO, EstadoSolicitud.ACEPTADA);

        em.flush();

        Page<Solicitudes> enviadas = solicitudesRepository.findBySolicitanteId(
                solicitante.getId(), PageRequest.of(0, 10));

        Page<Solicitudes> recibidas = solicitudesRepository.buscarRecibidas(
                duenio.getId(), null, PageRequest.of(0, 10));

        Page<Solicitudes> pendientes = solicitudesRepository.buscarRecibidas(
                duenio.getId(), EstadoSolicitud.PENDIENTE, PageRequest.of(0, 10));

        assertThat(enviadas.getTotalElements()).isEqualTo(2);
        assertThat(recibidas.getTotalElements()).isEqualTo(2);
        assertThat(pendientes.getTotalElements()).isEqualTo(1);
    }

    @Test
    void aceptarYRechazarOtrasSolicitudes() {

        Perfil duenio = crearPerfil("duenio");
        Perfil persona1 = crearPerfil("persona1");
        Perfil persona2 = crearPerfil("persona2");
        Categoria categoria = crearCategoria("Solicitudes");

        Prenda prenda = crearPrenda(duenio, categoria,
                "Jersey", EstadoPrenda.DISPONIBLE);

        Solicitudes aceptada = crearSolicitud(persona1, prenda,
                TipoSolicitud.PRESTAMO, EstadoSolicitud.PENDIENTE);

        Solicitudes otra = crearSolicitud(persona2, prenda,
                TipoSolicitud.PRESTAMO, EstadoSolicitud.PENDIENTE);

        Integer idAceptada = aceptada.getId();
        Integer idOtra = otra.getId();

        aceptada.setEstado(EstadoSolicitud.ACEPTADA);
        aceptada.setFechaRespuesta(LocalDateTime.now());
        prenda.setEstadoPrenda(EstadoPrenda.PRESTADA);

        solicitudesRepository.save(aceptada);
        prendaRepository.save(prenda);

        em.flush();

        int modificadas = solicitudesRepository.rechazarOtrasPendientes(
                prenda.getId(), idAceptada,
                EstadoSolicitud.PENDIENTE,
                EstadoSolicitud.RECHAZADA,
                LocalDateTime.now());

        em.clear();

        assertThat(modificadas).isEqualTo(1);

        assertThat(solicitudesRepository.findById(idAceptada).orElseThrow()
                .getEstado()).isEqualTo(EstadoSolicitud.ACEPTADA);

        assertThat(solicitudesRepository.findById(idOtra).orElseThrow()
                .getEstado()).isEqualTo(EstadoSolicitud.RECHAZADA);
    }
}