package es.safareyes.swapcloset.Controller;

import es.safareyes.swapcloset.Service.*;
import es.safareyes.swapcloset.dto.Request.*;
import es.safareyes.swapcloset.dto.Response.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/solicitudes")
@RequiredArgsConstructor
public class SolicitudesEscrituraController {

    private final SolicitudesEscrituraService solicitudesService;
    private final MensajesEscrituraService mensajesService;
    private final ValoracionesEscrituraService valoracionesService;

    @PostMapping
    public ResponseEntity<SolicitudResponse> crear(
            @RequestParam Integer perfilId,
            @Valid @RequestBody SolicitudCrearRequest request) {

        SolicitudResponse respuesta =
                solicitudesService.crear(perfilId, request);

        return ResponseEntity
                .created(URI.create(
                        "/api/v1/solicitudes/" + respuesta.id()))
                .body(respuesta);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<SolicitudResponse> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam Integer perfilId,
            @Valid @RequestBody SolicitudEstadoRequest request) {

        return ResponseEntity.ok(
                solicitudesService.cambiarEstado(id, perfilId, request));
    }

    @PostMapping("/{id}/mensajes")
    public ResponseEntity<MensajeResponse> enviarMensaje(
            @PathVariable Integer id,
            @RequestParam Integer perfilId,
            @Valid @RequestBody MensajeCrearRequest request) {

        MensajeResponse respuesta =
                mensajesService.enviar(id, perfilId, request);

        return ResponseEntity
                .created(URI.create(
                        "/api/v1/solicitudes/" + id
                                + "/mensajes/" + respuesta.id()))
                .body(respuesta);
    }

    @PostMapping("/{id}/valoraciones")
    public ResponseEntity<ValoracionResponse> valorar(
            @PathVariable Integer id,
            @RequestParam Integer perfilId,
            @Valid @RequestBody ValoracionCrearRequest request) {

        ValoracionResponse respuesta =
                valoracionesService.valorar(id, perfilId, request);

        return ResponseEntity
                .created(URI.create(
                        "/api/v1/solicitudes/" + id
                                + "/valoraciones/" + respuesta.id()))
                .body(respuesta);
    }
}