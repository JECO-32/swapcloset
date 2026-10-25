package es.safareyes.swapcloset.Controller;

import es.safareyes.swapcloset.Modelos.EstadoSolicitud;
import es.safareyes.swapcloset.Service.SolicitudesConsultaService;
import es.safareyes.swapcloset.dto.Response.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/solicitudes")
@RequiredArgsConstructor
public class SolicitudesConsultaController {

    private final SolicitudesConsultaService service;

    @GetMapping
    public Page<SolicitudResponse> bandeja(
            @RequestParam Integer perfilId,
            @RequestParam(defaultValue = "enviadas") String bandeja,
            @RequestParam(required = false) EstadoSolicitud estado,
            @PageableDefault(
                    size = 10,
                    sort = "fechaCreacion",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        return service.bandeja(perfilId, bandeja, estado, pageable);
    }

    @GetMapping("/{id}/mensajes")
    public Page<MensajeResponse> mensajes(
            @PathVariable Integer id,
            @RequestParam Integer perfilId,
            @PageableDefault(size = 20) Pageable pageable) {

        return service.mensajes(id, perfilId, pageable);
    }
}