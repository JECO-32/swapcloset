package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;

import org.junit.jupiter.api.Test;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SolicitudesRepositoryTest extends BaseRepositoryTest {

    @Test
    void obtieneSolicitudesEnviadasYRecibidas() {

        Perfil duenio = crearPerfil("test_duenio_get");
        Perfil solicitante = crearPerfil("test_solicitante_get");

        Categoria categoria = crearCategoria("Categoria solicitudes ZZ");

        Prenda prenda = crearPrenda(duenio, categoria, "Prenda solicitud", EstadoPrenda.DISPONIBLE);

        crearSolicitud(solicitante, prenda, TipoSolicitud.INTERCAMBIO, EstadoSolicitud.PENDIENTE);
        crearSolicitud(solicitante, prenda, TipoSolicitud.PRESTAMO, EstadoSolicitud.ACEPTADA);

        em.flush();

        List<Solicitudes> enviadas = solicitudesRepository.findBySolicitanteId(solicitante.getId());

        assertThat(enviadas).hasSize(2);

        List<Solicitudes> recibidas = solicitudesRepository.buscarSolicitudesRecibidas(duenio.getId());

        assertThat(recibidas).hasSize(2);

        List<Solicitudes> pendientes = solicitudesRepository.buscarSolicitudesRecibidasPorEstado(
                duenio.getId(),
                EstadoSolicitud.PENDIENTE
        );

        assertThat(pendientes).hasSize(1);
    }
}