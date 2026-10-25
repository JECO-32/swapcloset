package es.safareyes.swapcloset.dto.Response;

import java.util.List;

public record PrendaDetalleResponse(
        PrendaResponse prenda,
        PerfilPublicoResumenResponse propietario,
        List<FotoResponse> fotos
) {}