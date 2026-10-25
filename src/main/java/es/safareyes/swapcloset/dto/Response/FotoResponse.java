package es.safareyes.swapcloset.dto.Response;

public record FotoResponse(
        Integer id,
        String nombreOriginal,
        String tipoMime,
        Long tamano,
        Integer orden
) {}