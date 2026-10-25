package es.safareyes.swapcloset.dto.Response;

import java.time.LocalDateTime;

public record MensajeResponse(
        Integer id,
        Integer idSolicitud,
        Integer idAutor,
        String contenido,
        LocalDateTime enviadoEn
) {}