package es.safareyes.swapcloset.Controller;

import es.safareyes.swapcloset.Modelos.EstadoConservacion;
import es.safareyes.swapcloset.Service.PrendaConsultaService;
import es.safareyes.swapcloset.dto.Response.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/prendas")
@RequiredArgsConstructor
public class PrendaConsultaController {

    private final PrendaConsultaService service;

    @GetMapping
    public Page<PrendaResponse> buscar(
            @RequestParam(required = false) Integer idCategoria,
            @RequestParam(required = false) String talla,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) EstadoConservacion estadoConservacion,
            @RequestParam(required = false) Boolean permitePrestamo,
            @RequestParam(required = false) Boolean permiteIntercambio,
            @RequestParam(required = false) String texto,
            @PageableDefault(
                    size = 10,
                    sort = "fechaPublicacion",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        return service.buscar(
                idCategoria,
                talla,
                marca,
                estadoConservacion,
                permitePrestamo,
                permiteIntercambio,
                texto,
                pageable
        );
    }

    @GetMapping("/{id}")
    public PrendaDetalleResponse detalle(@PathVariable Integer id) {
        return service.detalle(id);
    }
}