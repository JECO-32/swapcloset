package es.safareyes.swapcloset.dto.Response;

import java.util.List;

public record PerfilPublicoResponse(
        PerfilResponse perfil,
        Double reputacion,
        Long totalValoraciones,
        List<PrendaResponse> prendas
) {}