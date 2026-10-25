
package es.safareyes.swapcloset;

import es.safareyes.swapcloset.Controller.*;
import es.safareyes.swapcloset.Service.*;
import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.dto.Request.*;
import es.safareyes.swapcloset.dto.Response.*;
import es.safareyes.swapcloset.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.SpringDataJackson3Configuration;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EndpointsTest {

    private MockMvc mvc;

    private PrendaEscrituraService prendaEscritura;
    private PerfilEscrituraService perfilEscritura;
    private SolicitudesEscrituraService solicitudesEscritura;
    private MensajesEscrituraService mensajesEscritura;
    private ValoracionesEscrituraService valoracionesEscritura;

    private PrendaConsultaService prendaConsulta;
    private UsuarioConsultaService usuarioConsulta;
    private SolicitudesConsultaService solicitudesConsulta;
    private InformeConsultaService informeConsulta;

    @BeforeEach
    void preparar() {

        prendaEscritura = mock(PrendaEscrituraService.class);
        perfilEscritura = mock(PerfilEscrituraService.class);
        solicitudesEscritura = mock(SolicitudesEscrituraService.class);
        mensajesEscritura = mock(MensajesEscrituraService.class);
        valoracionesEscritura = mock(ValoracionesEscrituraService.class);

        prendaConsulta = mock(PrendaConsultaService.class);
        usuarioConsulta = mock(UsuarioConsultaService.class);
        solicitudesConsulta = mock(SolicitudesConsultaService.class);
        informeConsulta = mock(InformeConsultaService.class);

        JsonMapper jsonMapper = JsonMapper.builder()
                .findAndAddModules()
                .addModule(
                        new SpringDataJackson3Configuration.PageModule(null)
                )
                .build();

        mvc = MockMvcBuilders.standaloneSetup(
                        new PrendaEscrituraController(prendaEscritura),
                        new UsuarioEscrituraController(perfilEscritura),
                        new SolicitudesEscrituraController(
                                solicitudesEscritura,
                                mensajesEscritura,
                                valoracionesEscritura
                        ),
                        new PrendaConsultaController(prendaConsulta),
                        new CategoriaConsultaController(prendaConsulta),
                        new UsuarioConsultaController(usuarioConsulta),
                        new SolicitudesConsultaController(solicitudesConsulta),
                        new InformeConsultaController(informeConsulta)
                )
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver()
                )
                .setMessageConverters(
                        new JacksonJsonHttpMessageConverter(jsonMapper)
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private PrendaResponse prendaResponse() {

        return new PrendaResponse(
                20, 1, 10,
                "Chaqueta",
                "Chaqueta en buen estado",
                "Zara", "M",
                EstadoConservacion.BUEN_ESTADO,
                EstadoPrenda.BORRADOR,
                true, true,
                LocalDateTime.now()
        );
    }

    private PerfilResponse perfilResponse() {

        return new PerfilResponse(
                1, "mariana", "Mariana",
                "Garcia", "Lopez",
                "Me gusta la moda",
                "Sevilla", "mariana@test.es",
                "M", "40", "42",
                Set.of("Casual")
        );
    }

    private SolicitudResponse solicitudResponse(
            EstadoSolicitud estado) {

        return new SolicitudResponse(
                30, 2, 20, null,
                TipoSolicitud.PRESTAMO,
                estado,
                LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 8),
                LocalDateTime.now(),
                null, null
        );
    }

    private String jsonPrenda() {

        return """
                {
                    "idCategoria": 10,
                    "titulo": "Chaqueta",
                    "descripcion": "Chaqueta en buen estado",
                    "marca": "Zara",
                    "talla": "M",
                    "estadoConservacion": "BUEN_ESTADO",
                    "permitePrestamo": true,
                    "permiteIntercambio": true
                }
                """;
    }

    private String jsonSolicitud() {

        return """
                {
                    "idPrendaSolicitada": 20,
                    "tipoSolicitud": "PRESTAMO",
                    "fechaInicio": "2026-11-01",
                    "fechaFin": "2026-11-08"
                }
                """;
    }

    // TEST 1 - Buscar prendas
    @Test
    void test01BuscarPrendas() throws Exception {

        Page<PrendaResponse> pagina = new PageImpl<>(
                List.of(prendaResponse()),
                PageRequest.of(0, 10),
                1
        );

        when(prendaConsulta.buscar(
                isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(),
                any(Pageable.class)
        )).thenReturn(pagina);

        mvc.perform(get("/api/v1/prendas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(20))
                .andExpect(jsonPath("$.content[0].titulo")
                        .value("Chaqueta"));

        verify(prendaConsulta).buscar(
                isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(),
                any(Pageable.class)
        );
    }

    // TEST 2 - Ver detalle de una prenda
    @Test
    void test02DetallePrenda() throws Exception {

        PrendaDetalleResponse detalle = new PrendaDetalleResponse(
                prendaResponse(),
                new PerfilPublicoResumenResponse(
                        1, "Mariana", "Sevilla",
                        Set.of("Casual"), 4.5
                ),
                List.of(new FotoResponse(
                        5, "chaqueta.jpg",
                        "image/jpeg", 1000L, 1
                ))
        );

        when(prendaConsulta.detalle(20)).thenReturn(detalle);

        mvc.perform(get("/api/v1/prendas/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenda.id").value(20))
                .andExpect(jsonPath("$.propietario.nombre")
                        .value("Mariana"))
                .andExpect(jsonPath("$.fotos[0].id").value(5));

        verify(prendaConsulta).detalle(20);
    }

    // TEST 3 - Crear una prenda
    @Test
    void test03CrearPrenda() throws Exception {

        when(prendaEscritura.crear(
                eq(1), any(PrendaCrearRequest.class)
        )).thenReturn(prendaResponse());

        mvc.perform(post("/api/v1/prendas")
                        .param("perfilId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPrenda()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.titulo").value("Chaqueta"));

        verify(prendaEscritura).crear(
                eq(1), any(PrendaCrearRequest.class)
        );
    }

    // TEST 4 - Actualizar una prenda
    @Test
    void test04ActualizarPrenda() throws Exception {

        when(prendaEscritura.actualizar(
                eq(20), eq(1),
                any(PrendaActualizarRequest.class)
        )).thenReturn(prendaResponse());

        mvc.perform(put("/api/v1/prendas/20")
                        .param("perfilId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "titulo": "Chaqueta",
                                    "talla": "M"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20));

        verify(prendaEscritura).actualizar(
                eq(20), eq(1),
                any(PrendaActualizarRequest.class)
        );
    }

    // TEST 5 - Retirar una prenda
    @Test
    void test05RetirarPrenda() throws Exception {

        mvc.perform(delete("/api/v1/prendas/20")
                        .param("perfilId", "1"))
                .andExpect(status().isNoContent());

        verify(prendaEscritura).retirar(20, 1);
    }

    // TEST 6 - Listar categorías
    @Test
    void test06ListarCategorias() throws Exception {

        when(prendaConsulta.categorias()).thenReturn(
                List.of(new CategoriaResponse(
                        10, "Casual", 5L
                ))
        );

        mvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].nombre").value("Casual"))
                .andExpect(jsonPath("$[0].prendasDisponibles").value(5));

        verify(prendaConsulta).categorias();
    }

    // TEST 7 - Consultar perfil de usuario
    @Test
    void test07ConsultarPerfil() throws Exception {

        PerfilPublicoResponse respuesta = new PerfilPublicoResponse(
                perfilResponse(),
                4.5,
                10L,
                List.of()
        );

        when(usuarioConsulta.detalle(1))
                .thenReturn(respuesta);

        mvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil.nombre")
                        .value("Mariana"))
                .andExpect(jsonPath("$.reputacion")
                        .value(4.5));

        verify(usuarioConsulta).detalle(1);
    }

    // TEST 8 - Actualizar perfil de usuario
    @Test
    void test08ActualizarPerfil() throws Exception {

        when(perfilEscritura.actualizarMiPerfil(
                eq(1), any(PerfilActualizarRequest.class)
        )).thenReturn(perfilResponse());

        mvc.perform(put("/api/v1/usuarios/me")
                        .param("perfilId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Mariana",
                                    "apellido1": "Garcia",
                                    "ciudad": "Sevilla",
                                    "tallaCamiseta": "M",
                                    "tallaPantalon": "40",
                                    "tallaZapatos": "42"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre")
                        .value("Mariana"))
                .andExpect(jsonPath("$.ciudad")
                        .value("Sevilla"));

        verify(perfilEscritura).actualizarMiPerfil(
                eq(1), any(PerfilActualizarRequest.class)
        );
    }

    // TEST 9 - Buscar perfiles afines
    @Test
    void test09PerfilesAfines() throws Exception {

        PerfilPublicoResponse perfilAfin = new PerfilPublicoResponse(
                perfilResponse(),
                4.5,
                10L,
                List.of()
        );

        when(usuarioConsulta.afines(2))
                .thenReturn(List.of(perfilAfin));

        mvc.perform(get("/api/v1/usuarios/afines")
                        .param("perfilId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].perfil.nombre")
                        .value("Mariana"));

        verify(usuarioConsulta).afines(2);
    }

    // TEST 10 - Crear solicitud
    @Test
    void test10CrearSolicitud() throws Exception {

        when(solicitudesEscritura.crear(
                eq(2), any(SolicitudCrearRequest.class)
        )).thenReturn(
                solicitudResponse(EstadoSolicitud.PENDIENTE)
        );

        mvc.perform(post("/api/v1/solicitudes")
                        .param("perfilId", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonSolicitud()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(30))
                .andExpect(jsonPath("$.estado")
                        .value("PENDIENTE"));

        verify(solicitudesEscritura).crear(
                eq(2), any(SolicitudCrearRequest.class)
        );
    }

    // TEST 11 - Consultar bandeja de solicitudes
    @Test
    void test11BandejaSolicitudes() throws Exception {

        Page<SolicitudResponse> pagina = new PageImpl<>(
                List.of(
                        solicitudResponse(EstadoSolicitud.PENDIENTE)
                ),
                PageRequest.of(0, 10),
                1
        );

        when(solicitudesConsulta.bandeja(
                eq(2), eq("enviadas"), isNull(),
                any(Pageable.class)
        )).thenReturn(pagina);

        mvc.perform(get("/api/v1/solicitudes")
                        .param("perfilId", "2")
                        .param("bandeja", "enviadas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id")
                        .value(30))
                .andExpect(jsonPath("$.content[0].estado")
                        .value("PENDIENTE"));

        verify(solicitudesConsulta).bandeja(
                eq(2), eq("enviadas"), isNull(),
                any(Pageable.class)
        );
    }

    // TEST 12 - Cambiar estado de solicitud
    @Test
    void test12CambiarEstadoSolicitud() throws Exception {

        when(solicitudesEscritura.cambiarEstado(
                eq(30), eq(1),
                any(SolicitudEstadoRequest.class)
        )).thenReturn(
                solicitudResponse(EstadoSolicitud.ACEPTADA)
        );

        mvc.perform(patch("/api/v1/solicitudes/30/estado")
                        .param("perfilId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "estado": "ACEPTADA"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado")
                        .value("ACEPTADA"));

        verify(solicitudesEscritura).cambiarEstado(
                eq(30), eq(1),
                any(SolicitudEstadoRequest.class)
        );
    }

    // TEST 13 - Enviar mensaje
    @Test
    void test13EnviarMensaje() throws Exception {

        when(mensajesEscritura.enviar(
                eq(30), eq(2),
                any(MensajeCrearRequest.class)
        )).thenReturn(new MensajeResponse(
                40, 30, 2,
                "Hola, me interesa la chaqueta",
                LocalDateTime.now()
        ));

        mvc.perform(post("/api/v1/solicitudes/30/mensajes")
                        .param("perfilId", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "contenido": "Hola, me interesa la chaqueta"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(40))
                .andExpect(jsonPath("$.contenido")
                        .value("Hola, me interesa la chaqueta"));

        verify(mensajesEscritura).enviar(
                eq(30), eq(2),
                any(MensajeCrearRequest.class)
        );
    }

    // TEST 14 - Consultar conversación de mensajes
    @Test
    void test14ConversacionMensajes() throws Exception {

        MensajeResponse mensaje = new MensajeResponse(
                40, 30, 2, "Hola",
                LocalDateTime.now()
        );

        Page<MensajeResponse> pagina = new PageImpl<>(
                List.of(mensaje),
                PageRequest.of(0, 20),
                1
        );

        when(solicitudesConsulta.mensajes(
                eq(30), eq(2),
                any(Pageable.class)
        )).thenReturn(pagina);

        mvc.perform(get("/api/v1/solicitudes/30/mensajes")
                        .param("perfilId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id")
                        .value(40))
                .andExpect(jsonPath("$.content[0].contenido")
                        .value("Hola"));

        verify(solicitudesConsulta).mensajes(
                eq(30), eq(2),
                any(Pageable.class)
        );
    }

    // TEST 15 - Crear valoración
    @Test
    void test15CrearValoracion() throws Exception {

        ValoracionResponse respuesta = new ValoracionResponse(
                50, 30, 2, 1, 5,
                "Todo perfecto",
                LocalDateTime.now()
        );

        when(valoracionesEscritura.valorar(
                eq(30), eq(2),
                any(ValoracionCrearRequest.class)
        )).thenReturn(respuesta);

        mvc.perform(post("/api/v1/solicitudes/30/valoraciones")
                        .param("perfilId", "2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "puntuacion": 5,
                                    "comentario": "Todo perfecto"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.puntuacion").value(5));

        verify(valoracionesEscritura).valorar(
                eq(30), eq(2),
                any(ValoracionCrearRequest.class)
        );
    }

    // TEST 16 - Consultar informe de actividad
    @Test
    void test16InformeActividad() throws Exception {

        LocalDateTime mes =
                LocalDateTime.of(2026, 10, 1, 0, 0);

        when(informeConsulta.actividad(
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(List.of(
                new ActividadMesResponse(
                        mes, "Casual", 10L, 5L, 3L
                )
        ));

        mvc.perform(get("/api/v1/informes/actividad")
                        .param("desde", "2026-10-01T00:00:00")
                        .param("hasta", "2026-10-31T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria")
                        .value("Casual"))
                .andExpect(jsonPath("$[0].publicaciones")
                        .value(10))
                .andExpect(jsonPath("$[0].intercambios")
                        .value(5))
                .andExpect(jsonPath("$[0].prestamos")
                        .value(3));

        verify(informeConsulta).actividad(
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        );
    }
}
