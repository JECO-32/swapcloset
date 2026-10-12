package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.repositorios.MensajesRepository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MensajesRepositoryTest extends BaseRepositoryTest {

    @Autowired private MensajesRepository mensajesRepository;

    @Test
    void conversacionPaginadaYOrdenada() {

        Perfil duenio = crearPerfil("test_mensaje_a");
        Perfil solicitante = crearPerfil("test_mensaje_b");

        Categoria categoria = crearCategoria("Categoria mensajes ZZ");

        Prenda prenda = crearPrenda(duenio, categoria, "Prenda mensajes", EstadoPrenda.DISPONIBLE);

        Solicitudes solicitud = crearSolicitud(
                solicitante,
                prenda,
                TipoSolicitud.INTERCAMBIO,
                EstadoSolicitud.PENDIENTE
        );

        mensajesRepository.save(Mensajes.builder()
                .solicitud(solicitud)
                .autor(solicitante)
                .contenido("Segundo")
                .enviadoEn(LocalDateTime.now())
                .build());

        mensajesRepository.save(Mensajes.builder()
                .solicitud(solicitud)
                .autor(solicitante)
                .contenido("Primero")
                .enviadoEn(LocalDateTime.now().minusMinutes(5))
                .build());

        Page<Mensajes> pagina = mensajesRepository.findBySolicitudIdOrderByEnviadoEnAsc(
                solicitud.getId(),
                PageRequest.of(0, 10)
        );

        assertThat(pagina.getContent())
                .extracting(Mensajes::getContenido)
                .containsExactly("Primero", "Segundo");
    }
}