package es.safareyes.swapcloset.dto.Response;

import java.time.LocalDateTime;

public record ActividadMesResponse(
        LocalDateTime mes,
        String categoria,
        Long publicaciones,
        Long intercambios,
        Long prestamos
) {}