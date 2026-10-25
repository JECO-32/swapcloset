package es.safareyes.swapcloset.Controller;

import es.safareyes.swapcloset.Service.PrendaConsultaService;
import es.safareyes.swapcloset.dto.Response.CategoriaResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaConsultaController {

    private final PrendaConsultaService service;

    @GetMapping
    public List<CategoriaResponse> listar() {
        return service.categorias();
    }
}