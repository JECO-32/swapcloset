package es.safareyes.swapcloset.Controller;

import es.safareyes.swapcloset.Service.PerfilEscrituraService;
import es.safareyes.swapcloset.dto.Request.PerfilActualizarRequest;
import es.safareyes.swapcloset.dto.Response.PerfilResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioEscrituraController {

    private final PerfilEscrituraService service;

    @PutMapping("/me")
    public ResponseEntity<PerfilResponse> actualizarMiPerfil(
            @RequestParam Integer perfilId,
            @Valid @RequestBody PerfilActualizarRequest request) {

        return ResponseEntity.ok(
                service.actualizarMiPerfil(perfilId, request));
    }
}