package es.safareyes.swapcloset.Controller;

import es.safareyes.swapcloset.Service.InformeConsultaService;
import es.safareyes.swapcloset.dto.Response.ActividadMesResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/informes")
@RequiredArgsConstructor
public class InformeConsultaController {

    private final InformeConsultaService service;

    @GetMapping("/actividad")
    public List<ActividadMesResponse> actividad(
            @RequestParam LocalDateTime desde,
            @RequestParam LocalDateTime hasta) {

        return service.actividad(desde, hasta);
    }
}