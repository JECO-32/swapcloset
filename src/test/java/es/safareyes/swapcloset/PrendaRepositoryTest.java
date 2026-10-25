package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.dto.ActividadCategoriaDto;
import es.safareyes.swapcloset.repositorios.FotoRepository;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

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
        perfil = crearPerfil("prenda");
        categoria = crearCategoria("Categoria prendas");
    }

    @Test
    void buscarPrendasConFiltrosYPaginacion() {

        crearPrenda(perfil, categoria, "A", EstadoPrenda.DISPONIBLE, "M");
        crearPrenda(perfil, categoria, "B", EstadoPrenda.DISPONIBLE, "M");
        crearPrenda(perfil, categoria, "C", EstadoPrenda.BORRADOR, "M");

        em.flush();

        Specification<Prenda> filtros = (root, query, cb) -> cb.and(
                cb.equal(root.get("categoria").get("id"), categoria.getId()),
                cb.equal(root.get("talla"), "M"),
                cb.equal(root.get("estadoPrenda"), EstadoPrenda.DISPONIBLE)
        );

        Page<Prenda> pagina = prendaRepository.findAll(
                filtros, PageRequest.of(0, 2));

        assertThat(pagina.getContent()).hasSize(2);
        assertThat(pagina.getTotalElements()).isEqualTo(2L);
    }

    @Test
    void detalleCargaFotosPerfilYCategoria() {

        Prenda prenda = crearPrenda(perfil, categoria,
                "Chaqueta", EstadoPrenda.DISPONIBLE);

        fotoRepository.save(Foto.builder()
                .prenda(prenda)
                .nombreOriginal("foto.jpg")
                .claveFichero(UUID.randomUUID() + ".jpg")
                .tipoMime("image/jpeg")
                .tamano(1000L)
                .orden(1)
                .build());

        Integer id = prenda.getId();

        em.flush();
        em.clear();

        Prenda resultado = prendaRepository.findConRelacionesById(id).orElseThrow();

        assertThat(resultado.getFotos()).hasSize(1);
        assertThat(Hibernate.isInitialized(resultado.getFotos())).isTrue();
        assertThat(resultado.getPerfil()).isNotNull();
        assertThat(resultado.getCategoria()).isNotNull();
    }

    @Test
    void postCrearPrenda() {

        Prenda prenda = prendaRepository.save(Prenda.builder()
                .perfil(perfil)
                .categoria(categoria)
                .titulo("Chaqueta nueva")
                .estadoConservacion(EstadoConservacion.BUEN_ESTADO)
                .permitePrestamo(true)
                .permiteIntercambio(true)
                .build());

        Integer id = prenda.getId();

        em.flush();
        em.clear();

        Prenda resultado = prendaRepository.findById(id).orElseThrow();

        assertThat(resultado.getTitulo()).isEqualTo("Chaqueta nueva");
        assertThat(resultado.getEstadoPrenda()).isEqualTo(EstadoPrenda.BORRADOR);
        assertThat(resultado.getFechaPublicacion()).isNotNull();
    }

    @Test
    void putModificarPrenda() {

        Prenda prenda = crearPrenda(perfil, categoria,
                "Camiseta", EstadoPrenda.BORRADOR);

        Integer id = prenda.getId();

        prenda.setTitulo("Camiseta modificada");
        prenda.setTalla("L");

        prendaRepository.save(prenda);

        em.flush();
        em.clear();

        Prenda resultado = prendaRepository.findById(id).orElseThrow();

        assertThat(resultado.getTitulo()).isEqualTo("Camiseta modificada");
        assertThat(resultado.getTalla()).isEqualTo("L");
    }

    @Test
    void deleteRetirarPrenda() {

        Prenda prenda = crearPrenda(perfil, categoria,
                "Pantalon", EstadoPrenda.DISPONIBLE);

        Integer id = prenda.getId();

        prenda.setEstadoPrenda(EstadoPrenda.RETIRADA);
        prendaRepository.save(prenda);

        em.flush();
        em.clear();

        Prenda resultado = prendaRepository.findById(id).orElseThrow();

        assertThat(resultado.getEstadoPrenda()).isEqualTo(EstadoPrenda.RETIRADA);
        assertThat(prendaRepository.existsById(id)).isTrue();
    }

    @Test
    void informeActividad() {

        crearPrenda(perfil, categoria, "Informe A", EstadoPrenda.DISPONIBLE);
        crearPrenda(perfil, categoria, "Informe B", EstadoPrenda.DISPONIBLE);

        em.flush();

        LocalDateTime desde = LocalDateTime.now().minusDays(1);
        LocalDateTime hasta = LocalDateTime.now().plusDays(1);

        List<ActividadCategoriaDto> resumen =
                prendaRepository.actividadPorCategoria(desde, hasta);

        List<Object[]> mensual =
                prendaRepository.actividadPorCategoriaYMes(desde, hasta);

        assertThat(resumen).anyMatch(r ->
                categoria.getNombre().equals(r.categoria())
                        && r.total() == 2L);

        assertThat(mensual).anyMatch(r ->
                categoria.getNombre().equals(r[1])
                        && ((Number) r[2]).longValue() == 2L);
    }
}