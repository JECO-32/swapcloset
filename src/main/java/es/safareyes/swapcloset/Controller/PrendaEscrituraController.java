package es.safareyes.swapcloset.Controller;

import es.safareyes.swapcloset.Service.PrendaEscrituraService;
import es.safareyes.swapcloset.dto.Request.*;
import es.safareyes.swapcloset.dto.Response.PrendaResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/prendas")
@RequiredArgsConstructor
public class PrendaEscrituraController {

    private final PrendaEscrituraService service;

    @PostMapping
    public ResponseEntity<PrendaResponse> crear(
            @RequestParam Integer perfilId,
            @Valid @RequestBody PrendaCrearRequest request) {

        PrendaResponse respuesta = service.crear(perfilId, request);

        return ResponseEntity
                .created(URI.create("/api/v1/prendas/" + respuesta.id()))
                .body(respuesta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrendaResponse> actualizar(
            @PathVariable Integer id,
            @RequestParam Integer perfilId,
            @Valid @RequestBody PrendaActualizarRequest request) {

        return ResponseEntity.ok(
                service.actualizar(id, perfilId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> retirar(
            @PathVariable Integer id,
            @RequestParam Integer perfilId) {

        service.retirar(id, perfilId);

        return ResponseEntity.noContent().build();
    }
}