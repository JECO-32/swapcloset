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
    void crearValoracionYCalcularMedia() {

        Perfil valorado = crearPerfil("valorado");
        Perfil autor = crearPerfil("autor");
        Categoria categoria = crearCategoria("Valoraciones");

        Prenda prenda = crearPrenda(valorado, categoria,
                "Chaqueta", EstadoPrenda.DISPONIBLE);

        Solicitudes solicitud = crearSolicitud(autor, prenda,
                TipoSolicitud.PRESTAMO, EstadoSolicitud.FINALIZADA);

        Valoraciones valoracion = valoracionesRepository.save(
                Valoraciones.builder()
                        .solicitud(solicitud)
                        .autor(autor)
                        .valorado(valorado)
                        .puntuacion(5)
                        .comentario("Todo perfecto")
                        .build()
        );

        Integer idValoracion = valoracion.getId();
        Integer idSolicitud = solicitud.getId();
        Integer idAutor = autor.getId();
        Integer idValorado = valorado.getId();

        em.flush();
        em.clear();

        Valoraciones resultado = valoracionesRepository
                .findById(idValoracion).orElseThrow();

        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getPuntuacion()).isEqualTo(5);
        assertThat(resultado.getComentario()).isEqualTo("Todo perfecto");
        assertThat(resultado.getFecha()).isNotNull();

        assertThat(valoracionesRepository.existsBySolicitudIdAndAutorId(
                idSolicitud, idAutor)).isTrue();

        assertThat(valoracionesRepository.calcularMediaValoraciones(
                idValorado)).isEqualTo(5.0);

        assertThat(valoracionesRepository.countByValoradoId(
                idValorado)).isEqualTo(1L);
    }
}