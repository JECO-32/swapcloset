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

        Perfil duenio = crearPerfil("mensaje_duenio");
        Perfil autor = crearPerfil("mensaje_autor");
        Categoria categoria = crearCategoria("Mensajes");
        Prenda prenda = crearPrenda(duenio, categoria, "Camisa", EstadoPrenda.DISPONIBLE);
        Solicitudes solicitud = crearSolicitud(autor, prenda,
                TipoSolicitud.INTERCAMBIO, EstadoSolicitud.PENDIENTE);

        LocalDateTime ahora = LocalDateTime.now();

        mensajesRepository.save(Mensajes.builder()
                .solicitud(solicitud).autor(autor)
                .contenido("Segundo").enviadoEn(ahora).build());

        mensajesRepository.save(Mensajes.builder()
                .solicitud(solicitud).autor(autor)
                .contenido("Primero").enviadoEn(ahora.minusMinutes(5)).build());

        em.flush();

        Page<Mensajes> pagina = mensajesRepository.findBySolicitudIdOrderByEnviadoEnAsc(
                solicitud.getId(), PageRequest.of(0, 10));

        assertThat(pagina.getContent())
                .extracting(Mensajes::getContenido)
                .containsExactly("Primero", "Segundo");
    }

    @Test
    void postEnviarMensaje() {

        Perfil duenio = crearPerfil("mensaje_duenio");
        Perfil autor = crearPerfil("mensaje_autor");
        Categoria categoria = crearCategoria("Mensajes");
        Prenda prenda = crearPrenda(duenio, categoria, "Chaqueta", EstadoPrenda.DISPONIBLE);
        Solicitudes solicitud = crearSolicitud(autor, prenda,
                TipoSolicitud.INTERCAMBIO, EstadoSolicitud.PENDIENTE);

        Mensajes mensaje = mensajesRepository.save(Mensajes.builder()
                .solicitud(solicitud)
                .autor(autor)
                .contenido("Hola, me interesa tu chaqueta")
                .build());

        Integer id = mensaje.getId();

        em.flush();
        em.clear();

        Mensajes resultado = mensajesRepository.findById(id).orElseThrow();

        assertThat(resultado.getContenido()).isEqualTo("Hola, me interesa tu chaqueta");
        assertThat(resultado.getEnviadoEn()).isNotNull();
        assertThat(resultado.getId()).isNotNull();
    }
}