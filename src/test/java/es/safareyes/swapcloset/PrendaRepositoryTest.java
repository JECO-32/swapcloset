package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.dto.*;
import es.safareyes.swapcloset.repositorios.FotoRepository;

import org.hibernate.Hibernate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PrendaRepositoryTest extends BaseRepositoryTest {

    @Autowired private FotoRepository fotoRepository;

    private Perfil perfil;
    private Categoria categoria;

    @BeforeEach
    void preparar() {
        perfil = crearPerfil("test_prenda_get");
        categoria = crearCategoria("Categoria prendas ZZ");
    }

    @Test
    void buscarPrendasConFiltrosYPaginacion() {

        crearPrenda(perfil, categoria, "Prenda A", EstadoPrenda.DISPONIBLE, "M");
        crearPrenda(perfil, categoria, "Prenda B", EstadoPrenda.DISPONIBLE, "M");
        crearPrenda(perfil, categoria, "Prenda C", EstadoPrenda.BORRADOR, "M");

        Page<Prenda> pagina = prendaRepository.findAll(
                PrendaSpecs.conFiltros(categoria.getId(), "M", null, null, null, null, null),
                PageRequest.of(0, 2)
        );

        assertThat(pagina.getContent()).hasSize(2);
        assertThat(pagina.getContent()).allMatch(p -> p.getEstadoPrenda() == EstadoPrenda.DISPONIBLE);
    }

    @Test
    void detalleCargaFotosPerfilYCategoria() {

        Prenda prenda = crearPrenda(perfil, categoria, "Detalle ZZ", EstadoPrenda.DISPONIBLE);

        fotoRepository.save(Foto.builder()
                .prenda(prenda)
                .nombreOriginal("foto.jpg")
                .claveFichero(UUID.randomUUID() + ".jpg")
                .tipoMime("image/jpeg")
                .tamano(1000L)
                .orden(1)
                .build());

        em.flush();
        em.clear();

        Prenda resultado = prendaRepository.findConRelacionesById(prenda.getId()).orElseThrow();

        assertThat(Hibernate.isInitialized(resultado.getFotos())).isTrue();
        assertThat(resultado.getFotos()).hasSize(1);
        assertThat(resultado.getPerfil()).isNotNull();
        assertThat(resultado.getCategoria()).isNotNull();
    }

    @Test
    void informeActividadFunciona() {

        crearPrenda(perfil, categoria, "Informe ZZ", EstadoPrenda.DISPONIBLE);

        em.flush();

        LocalDateTime desde = LocalDateTime.now().minusDays(1);
        LocalDateTime hasta = LocalDateTime.now().plusDays(1);

        List<ActividadCategoriaDto> resumen = prendaRepository.actividadPorCategoria(desde, hasta);

        assertThat(resumen).anyMatch(r -> "Categoria prendas ZZ".equals(r.categoria()));

        List<Object[]> informe = prendaRepository.actividadPorCategoriaYMes(desde, hasta);

        assertThat(informe).anyMatch(r -> "Categoria prendas ZZ".equals(r[1]));
    }
}