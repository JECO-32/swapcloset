package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.repositorios.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoriosSimplesTest extends BaseRepositoryTest {

    @Autowired private FotoRepository fotoRepository;
    @Autowired private MensajesRepository mensajesRepository;
    @Autowired private NotificacionRepository notificacionRepository;

    private Perfil duenia;
    private Perfil pide;
    private Prenda prenda;

    @BeforeEach
    void preparar() {
        duenia = crearPerfil("test_simple_a");
        pide = crearPerfil("test_simple_b");
        prenda = crearPrenda(duenia, crearCategoria("Cat simples-zz"),
                "Prenda simple", EstadoPrenda.DISPONIBLE);
    }

    @Test
    void usuarioRepository() {
        crearPerfil("test_usuario_x");

        assertThat(usuarioRepository.existsByUsername("test_usuario_x")).isTrue();
        assertThat(usuarioRepository.existsByUsername("no_existe_zz")).isFalse();
        assertThat(usuarioRepository.findByRol(RolUsuario.USUARIO))
                .extracting(Usuario::getUsername).contains("test_usuario_x");
    }

    @Test
    void estilosRepository() {
        estilosRepository.save(Estilos.builder().nombre("Vintage-zz").build());

        assertThat(estilosRepository.existsByNombreIgnoreCase("VINTAGE-ZZ")).isTrue();
        assertThat(estilosRepository.findByNombreContainingIgnoreCase("vintage-z")).hasSize(1);
    }

    @Test
    void fotoRepositoryDevuelveLasFotosOrdenadas() {
        fotoRepository.save(nuevaFoto(2));
        fotoRepository.save(nuevaFoto(1));

        List<Foto> fotos = fotoRepository.findByPrendaIdOrderByOrdenAsc(prenda.getId());

        assertThat(fotos).extracting(Foto::getOrden).containsExactly(1, 2);
    }

    private Foto nuevaFoto(int orden) {
        return Foto.builder()
                .prenda(prenda)
                .nombreOriginal("foto" + orden + ".jpg")
                .claveFichero(UUID.randomUUID() + ".jpg")
                .tipoMime("image/jpeg")
                .tamano(1000L)
                .orden(orden)
                .build();
    }

    @Test
    void mensajesRepository() {
        Solicitudes solicitud = crearSolicitud(pide, prenda,
                TipoSolicitud.INTERCAMBIO, EstadoSolicitud.PENDIENTE);

        mensajesRepository.save(Mensajes.builder()
                .solicitud(solicitud).autor(pide).contenido("Segundo")
                .enviadoEn(LocalDateTime.now()).build());
        mensajesRepository.save(Mensajes.builder()
                .solicitud(solicitud).autor(pide).contenido("Primero")
                .enviadoEn(LocalDateTime.now().minusMinutes(5)).build());

        List<Mensajes> chat = mensajesRepository
                .findBySolicitudIdOrderByEnviadoEnAsc(solicitud.getId());

        assertThat(chat).extracting(Mensajes::getContenido).containsExactly("Primero", "Segundo");
        assertThat(mensajesRepository
                .existsBySolicitudIdAndAutorId(solicitud.getId(), pide.getId())).isTrue();
        assertThat(mensajesRepository
                .existsBySolicitudIdAndAutorId(solicitud.getId(), duenia.getId())).isFalse();
    }

    @Test
    void notificacionRepository() {
        notificacionRepository.save(Notificacion.builder()
                .perfil(duenia).tipo(TipoNotificacion.NUEVA_SOLICITUD)
                .mensaje("Sin leer").build());
        notificacionRepository.save(Notificacion.builder()
                .perfil(duenia).tipo(TipoNotificacion.NUEVO_MENSAJE)
                .mensaje("Leída").leida(true).build());

        assertThat(notificacionRepository.findByPerfilId(duenia.getId())).hasSize(2);
        assertThat(notificacionRepository.findByPerfilIdAndLeida(duenia.getId(), false)).hasSize(1);
        assertThat(notificacionRepository.countByPerfilIdAndLeida(duenia.getId(), true)).isEqualTo(1);
    }
}