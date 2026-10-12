package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.repositorios.ValoracionesRepository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ValoracionesRepositoryTest extends BaseRepositoryTest {

    @Autowired private ValoracionesRepository valoracionesRepository;

    @Test
    void calculaReputacionMedia() {

        Perfil valorado = crearPerfil("test_valorado_get");
        Perfil autor = crearPerfil("test_autor_get");

        Categoria categoria = crearCategoria("Categoria valoraciones ZZ");

        Prenda prenda = crearPrenda(valorado, categoria, "Prenda valorada", EstadoPrenda.DISPONIBLE);

        Solicitudes solicitud1 = crearSolicitud(
                autor,
                prenda,
                TipoSolicitud.INTERCAMBIO,
                EstadoSolicitud.FINALIZADA
        );

        Solicitudes solicitud2 = crearSolicitud(
                autor,
                prenda,
                TipoSolicitud.PRESTAMO,
                EstadoSolicitud.FINALIZADA
        );

        valoracionesRepository.save(Valoraciones.builder()
                .solicitud(solicitud1)
                .autor(autor)
                .valorado(valorado)
                .puntuacion(5)
                .build());

        valoracionesRepository.save(Valoraciones.builder()
                .solicitud(solicitud2)
                .autor(autor)
                .valorado(valorado)
                .puntuacion(3)
                .build());

        em.flush();

        Double media = valoracionesRepository.calcularMediaValoraciones(valorado.getId());

        assertThat(media).isEqualTo(4.0);
    }
}