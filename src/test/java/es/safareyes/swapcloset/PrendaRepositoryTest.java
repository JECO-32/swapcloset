package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.dto.ActividadCategoriaDto;
import es.safareyes.swapcloset.dto.PrendaTarjetaView;
import es.safareyes.swapcloset.dto.PrendaSpecs;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PrendaRepositoryTest extends BaseRepositoryTest {

    private Perfil perfil;
    private Categoria categoria;

    @BeforeEach
    void preparar() {
        perfil = crearPerfil("test_ana");
        categoria = crearCategoria("Cat test-zz");
    }

    // Q1 - derivada
    @Test
    void buscaPrendasPorEstado() {
        crearPrenda(perfil, categoria, "Vaquero test", EstadoPrenda.DISPONIBLE);
        crearPrenda(perfil, categoria, "Borrador test", EstadoPrenda.BORRADOR);

        List<Prenda> disponibles =
                prendaRepository.findByEstadoPrenda(EstadoPrenda.DISPONIBLE);

        assertThat(disponibles).extracting(Prenda::getTitulo)
                .contains("Vaquero test")
                .doesNotContain("Borrador test");
    }

    // Q6 - paginación y ordenación
    @Test
    void paginaYOrdenaResultados() {
        crearPrenda(perfil, categoria, "chaqueta-zz 1", EstadoPrenda.DISPONIBLE);
        crearPrenda(perfil, categoria, "chaqueta-zz 2", EstadoPrenda.DISPONIBLE);
        crearPrenda(perfil, categoria, "chaqueta-zz 3", EstadoPrenda.DISPONIBLE);

        Page<Prenda> pagina = prendaRepository.findByTituloContainingIgnoreCase(
                "CHAQUETA-ZZ", PageRequest.of(0, 2, Sort.by("titulo").ascending()));

        assertThat(pagina.getContent()).hasSize(2);
        assertThat(pagina.getTotalElements()).isEqualTo(3);
        assertThat(pagina.getTotalPages()).isEqualTo(2);
        assertThat(pagina.getContent().get(0).getTitulo()).isEqualTo("chaqueta-zz 1");
    }

    // Q8 - EntityGraph (evita N+1)
    @Test
    void entityGraphCargaPerfilYCategoria() {
        Prenda guardada = crearPrenda(perfil, categoria, "Abrigo test", EstadoPrenda.DISPONIBLE);
        em.flush();
        em.clear();

        Prenda leida = prendaRepository.findConRelacionesById(guardada.getId()).orElseThrow();

        assertThat(Hibernate.isInitialized(leida.getPerfil())).isTrue();
        assertThat(Hibernate.isInitialized(leida.getCategoria())).isTrue();
    }

    // Q3 - SQL nativo
    @Test
    void informeNativoDeActividadPorCategoriaYMes() {
        crearPrenda(perfil, categoria, "Informe test", EstadoPrenda.DISPONIBLE);
        em.flush();

        List<Object[]> filas = prendaRepository.actividadPorCategoriaYMes(
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        assertThat(filas).anyMatch(f -> "Cat test-zz".equals(f[1]));
    }

    // Q5 - proyección por interfaz
    @Test
    void proyeccionPorInterfazDevuelveSoloLosCamposDeLaTarjeta() {
        crearPrenda(perfil, categoria, "Tarjeta test", EstadoPrenda.DISPONIBLE, "L");

        List<PrendaTarjetaView> tarjetas =
                prendaRepository.findByEstadoPrendaOrderByFechaPublicacionDesc(
                        EstadoPrenda.DISPONIBLE);

        assertThat(tarjetas).anyMatch(t ->
                "Tarjeta test".equals(t.getTitulo()) && "L".equals(t.getTalla()));
    }

    // Q5 - proyección por record con SELECT new
    @Test
    void proyeccionPorRecordAgrupaPorCategoria() {
        crearPrenda(perfil, categoria, "Rec 1", EstadoPrenda.DISPONIBLE);
        crearPrenda(perfil, categoria, "Rec 2", EstadoPrenda.DISPONIBLE);
        em.flush();

        List<ActividadCategoriaDto> informe = prendaRepository.actividadPorCategoria(
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));

        assertThat(informe).anyMatch(d ->
                "Cat test-zz".equals(d.categoria()) && d.total() == 2L);
    }

    // Q7 - Specification con filtros combinados
    @Test
    void specificationCombinaFiltrosYSoloMuestraDisponibles() {
        crearPrenda(perfil, categoria, "Spec A", EstadoPrenda.DISPONIBLE, "M");
        crearPrenda(perfil, categoria, "Spec B", EstadoPrenda.DISPONIBLE, "L");
        crearPrenda(perfil, categoria, "Spec C", EstadoPrenda.BORRADOR, "M");

        List<Prenda> resultado = prendaRepository.findAll(
                PrendaSpecs.conFiltros(categoria.getId(), "m", null, null, null, null, null));

        assertThat(resultado).extracting(Prenda::getTitulo).containsExactly("Spec A");
    }

    // Q7 - Specification con filtros que pasan por otras tablas (ciudad y estilo)
    @Test
    void specificationFiltraPorCiudadYEstilo() {
        Estilos estilo = estilosRepository.save(Estilos.builder().nombre("Estilo test-zz").build());
        perfil.setCiudad("Ciudad-zz");
        perfil.getEstilos().add(estilo);
        perfilRepository.save(perfil);

        Perfil otro = crearPerfil("test_bea");

        crearPrenda(perfil, categoria, "De Ana", EstadoPrenda.DISPONIBLE);
        crearPrenda(otro, categoria, "De Bea", EstadoPrenda.DISPONIBLE);

        List<Prenda> resultado = prendaRepository.findAll(
                PrendaSpecs.conFiltros(categoria.getId(), null, null, null, null,
                        "ciudad-zz", estilo.getId()));

        assertThat(resultado).extracting(Prenda::getTitulo).containsExactly("De Ana");
    }
}