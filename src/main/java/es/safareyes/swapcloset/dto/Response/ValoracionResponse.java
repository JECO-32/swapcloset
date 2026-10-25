package es.safareyes.swapcloset.dto.Response;

import java.time.LocalDateTime;

public record ValoracionResponse(
        Integer id,
        Integer idSolicitud,
        Integer idAutor,
        Integer idValorado,
        Integer puntuacion,
        String comentario,
        LocalDateTime fecha
) {}