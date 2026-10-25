package es.safareyes.swapcloset.Controller;

import es.safareyes.swapcloset.Service.UsuarioConsultaService;
import es.safareyes.swapcloset.dto.Response.PerfilPublicoResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioConsultaController {

    private final UsuarioConsultaService service;

    @GetMapping("/afines")
    public List<PerfilPublicoResponse> afines(
            @RequestParam Integer perfilId) {

        return service.afines(perfilId);
    }

    @GetMapping("/{id}")
    public PerfilPublicoResponse detalle(@PathVariable Integer id) {
        return service.detalle(id);
    }
}