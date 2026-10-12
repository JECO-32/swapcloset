package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.dto.ActividadCategoriaDto;
import es.safareyes.swapcloset.dto.PrendaSpecs;
import es.safareyes.swapcloset.repositorios.*;

import jakarta.persistence.EntityManager;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SwapclosetApplicationTests {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PerfilRepository perfilRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private EstilosRepository estilosRepository;
    @Autowired private PrendaRepository prendaRepository;
    @Autowired private FotoRepository fotoRepository;
    @Autowired private SolicitudesRepository solicitudesRepository;
    @Autowired private MensajesRepository mensajesRepository;
    @Autowired private ValoracionesRepository valoracionesRepository;
    @Autowired private EntityManager em;

    @Test
    void getPrendasConFiltrosYPaginacion() {
        Perfil perfil = crearPerfil("test_get_prendas");
        Categoria categoria = crearCategoria("Categoria GET prendas");

        crearPrenda(perfil, categoria, "GET Prenda A", EstadoPrenda.DISPONIBLE, "M");
        crearPrenda(perfil, categoria, "GET Prenda B", EstadoPrenda.DISPONIBLE, "M");
        crearPrenda(perfil, categoria, "GET Prenda C", EstadoPrenda.BORRADOR, "M");

        Page<Prenda> pagina = prendaRepository.findAll(
                PrendaSpecs.conFiltros(categoria.getId(), "M", null, null, null, null, null),
                PageRequest.of(0, 2)
        );

        assertThat(pagina.getContent()).hasSize(2);
        assertThat(pagina.getContent()).allMatch(p -> p.getEstadoPrenda() == EstadoPrenda.DISPONIBLE);
    }

    @Test
    void getDetallePrendaConFotosYPropietario() {
        Perfil perfil = crearPerfil("test_get_detalle");
        Categoria categoria = crearCategoria("Categoria GET detalle");
        Prenda prenda = crearPrenda(perfil, categoria, "Chaqueta GET detalle", EstadoPrenda.DISPONIBLE);

        fotoRepository.save(Foto.builder().prenda(prenda).nombreOriginal("chaqueta.jpg").claveFichero(UUID.randomUUID() + ".jpg").tipoMime("image/jpeg").tamano(1000L).orden(1).build());

        em.flush();
        em.clear();

        Prenda resultado = prendaRepository.findConRelacionesById(prenda.getId()).orElseThrow();

        assertThat(resultado.getTitulo()).isEqualTo("Chaqueta GET detalle");
        assertThat(Hibernate.isInitialized(resultado.getFotos())).isTrue();
        assertThat(resultado.getFotos()).hasSize(1);
        assertThat(resultado.getPerfil()).isNotNull();
        assertThat(resultado.getCategoria()).isNotNull();
    }

    @Test
    void getCategoriasConNumeroDePrendasDisponibles() {
        Perfil perfil = crearPerfil("test_get_categorias");
        Categoria categoria = crearCategoria("Categoria GET disponibles");

        crearPrenda(perfil, categoria, "Disponible GET", EstadoPrenda.DISPONIBLE);
        crearPrenda(perfil, categoria, "Borrador GET", EstadoPrenda.BORRADOR);

        em.flush();

        List<Object[]> resultado = categoriaRepository.categoriasConNumeroDisponibles();

        Object[] fila = resultado.stream()
                .filter(r -> "Categoria GET disponibles".equals(r[1]))
                .findFirst()
                .orElseThrow();

        assertThat(((Number) fila[2]).longValue()).isEqualTo(1L);
    }

    @Test
    void getPerfilUsuario() {
        Perfil perfil = crearPerfil("test_get_usuario");

        perfil.setCiudad("Sevilla");
        perfil.setTallaCamiseta("M");
        perfil.setTallaPantalon("40");
        perfil.setTallaZapatos("42");

        perfilRepository.save(perfil);

        Perfil resultado = perfilRepository.findByUsuarioUsername("test_get_usuario").orElseThrow();

        assertThat(resultado.getUsuario().getUsername()).isEqualTo("test_get_usuario");
        assertThat(resultado.getCiudad()).isEqualTo("Sevilla");
        assertThat(resultado.getTallaCamiseta()).isEqualTo("M");
    }

    @Test
    void getPerfilesAfines() {
        Estilos casual = estilosRepository.save(Estilos.builder().nombre("Casual GET").build());
        Estilos deportivo = estilosRepository.save(Estilos.builder().nombre("Deportivo GET").build());

        Perfil yo = crearPerfil("test_get_yo");
        yo.getEstilos().add(casual);
        yo.getEstilos().add(deportivo);
        perfilRepository.save(yo);

        Perfil afin = crearPerfil("test_get_afin");
        afin.getEstilos().add(casual);
        perfilRepository.save(afin);

        Perfil noAfin = crearPerfil("test_get_no_afin");

        em.flush();

        List<Perfil> resultado = perfilRepository.buscarPerfilesAfines(yo.getId());

        assertThat(resultado).extracting(Perfil::getId).contains(afin.getId());
        assertThat(resultado).extracting(Perfil::getId).doesNotContain(noAfin.getId());
    }

    @Test
    void getSolicitudesEnviadasYRecibidas() {
        Perfil duenio = crearPerfil("test_get_duenio");
        Perfil solicitante = crearPerfil("test_get_solicitante");
        Categoria categoria = crearCategoria("Categoria GET solicitudes");
        Prenda prenda = crearPrenda(duenio, categoria, "Prenda GET solicitudes", EstadoPrenda.DISPONIBLE);

        crearSolicitud(solicitante, prenda, TipoSolicitud.INTERCAMBIO, EstadoSolicitud.PENDIENTE);
        crearSolicitud(solicitante, prenda, TipoSolicitud.PRESTAMO, EstadoSolicitud.ACEPTADA);

        em.flush();

        List<Solicitudes> enviadas = solicitudesRepository.findBySolicitanteId(solicitante.getId());
        List<Solicitudes> recibidas = solicitudesRepository.buscarSolicitudesRecibidas(duenio.getId());
        List<Solicitudes> pendientes = solicitudesRepository.buscarSolicitudesRecibidasPorEstado(duenio.getId(), EstadoSolicitud.PENDIENTE);

        assertThat(enviadas).hasSize(2);
        assertThat(recibidas).hasSize(2);
        assertThat(pendientes).hasSize(1);
    }

    @Test
    void getMensajesPaginadosYOrdenados() {
        Perfil duenio = crearPerfil("test_get_mensajes_a");
        Perfil solicitante = crearPerfil("test_get_mensajes_b");
        Categoria categoria = crearCategoria("Categoria GET mensajes");
        Prenda prenda = crearPrenda(duenio, categoria, "Prenda GET mensajes", EstadoPrenda.DISPONIBLE);
        Solicitudes solicitud = crearSolicitud(solicitante, prenda, TipoSolicitud.INTERCAMBIO, EstadoSolicitud.PENDIENTE);

        mensajesRepository.save(Mensajes.builder().solicitud(solicitud).autor(solicitante).contenido("Segundo").enviadoEn(LocalDateTime.now()).build());
        mensajesRepository.save(Mensajes.builder().solicitud(solicitud).autor(solicitante).contenido("Primero").enviadoEn(LocalDateTime.now().minusMinutes(5)).build());

        Page<Mensajes> pagina = mensajesRepository.findBySolicitudIdOrderByEnviadoEnAsc(solicitud.getId(), PageRequest.of(0, 10));

        assertThat(pagina.getContent()).extracting(Mensajes::getContenido).containsExactly("Primero", "Segundo");
    }

    @Test
    void getInformeActividad() {
        Perfil perfil = crearPerfil("test_get_informe");
        Categoria categoria = crearCategoria("Categoria GET informe");

        crearPrenda(perfil, categoria, "Prenda informe 1", EstadoPrenda.DISPONIBLE);
        crearPrenda(perfil, categoria, "Prenda informe 2", EstadoPrenda.DISPONIBLE);

        em.flush();

        LocalDateTime desde = LocalDateTime.now().minusDays(1);
        LocalDateTime hasta = LocalDateTime.now().plusDays(1);

        List<ActividadCategoriaDto> resumen = prendaRepository.actividadPorCategoria(desde, hasta);
        List<Object[]> informe = prendaRepository.actividadPorCategoriaYMes(desde, hasta);

        assertThat(resumen).anyMatch(r -> "Categoria GET informe".equals(r.categoria()));
        assertThat(informe).anyMatch(r -> "Categoria GET informe".equals(r[1]));
    }

    @Test
    void getReputacionMediaUsuario() {
        Perfil valorado = crearPerfil("test_get_valorado");
        Perfil autor = crearPerfil("test_get_autor");
        Categoria categoria = crearCategoria("Categoria GET valoraciones");
        Prenda prenda = crearPrenda(valorado, categoria, "Prenda valorada GET", EstadoPrenda.DISPONIBLE);

        Solicitudes solicitud1 = crearSolicitud(autor, prenda, TipoSolicitud.INTERCAMBIO, EstadoSolicitud.FINALIZADA);
        Solicitudes solicitud2 = crearSolicitud(autor, prenda, TipoSolicitud.PRESTAMO, EstadoSolicitud.FINALIZADA);

        valoracionesRepository.save(Valoraciones.builder().solicitud(solicitud1).autor(autor).valorado(valorado).puntuacion(5).build());
        valoracionesRepository.save(Valoraciones.builder().solicitud(solicitud2).autor(autor).valorado(valorado).puntuacion(3).build());

        em.flush();

        Double media = valoracionesRepository.calcularMediaValoraciones(valorado.getId());

        assertThat(media).isEqualTo(4.0);
    }

    private Perfil crearPerfil(String username) {
        Usuario usuario = usuarioRepository.save(Usuario.builder().username(username).contrasena("1234").rol(RolUsuario.USUARIO).build());
        return perfilRepository.save(Perfil.builder().usuario(usuario).nombre("Test").apellido1("Test").email(username + "@test.es").fechaNacimiento(LocalDate.of(2005, 5, 10)).build());
    }

    private Categoria crearCategoria(String nombre) {
        return categoriaRepository.save(Categoria.builder().nombre(nombre).build());
    }

    private Prenda crearPrenda(Perfil perfil, Categoria categoria, String titulo, EstadoPrenda estado) {
        return crearPrenda(perfil, categoria, titulo, estado, "M");
    }

    private Prenda crearPrenda(Perfil perfil, Categoria categoria, String titulo, EstadoPrenda estado, String talla) {
        return prendaRepository.save(Prenda.builder().perfil(perfil).categoria(categoria).titulo(titulo).talla(talla).estadoConservacion(EstadoConservacion.BUEN_ESTADO).estadoPrenda(estado).permitePrestamo(true).permiteIntercambio(true).build());
    }

    private Solicitudes crearSolicitud(Perfil solicitante, Prenda prenda, TipoSolicitud tipo, EstadoSolicitud estado) {
        return solicitudesRepository.save(Solicitudes.builder().solicitante(solicitante).prendaSolicitada(prenda).tipoSolicitud(tipo).estado(estado).build());
    }
}