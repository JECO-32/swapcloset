package es.safareyes.swapcloset.dto.Response;

import java.util.Set;

public record PerfilPublicoResumenResponse(
        Integer id,
        String nombre,
        String ciudad,
        Set<String> estilos,
        Double reputacion
) {}