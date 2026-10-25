package es.safareyes.swapcloset.dto.Response;

public record CategoriaResponse(
        Integer id,
        String nombre,
        Long prendasDisponibles
) {}