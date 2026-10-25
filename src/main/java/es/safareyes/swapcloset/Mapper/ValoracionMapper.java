package es.safareyes.swapcloset.Mapper;

import es.safareyes.swapcloset.Modelos.Valoraciones;
import es.safareyes.swapcloset.dto.Response.ValoracionResponse;

public interface ValoracionMapper {

    static ValoracionResponse toResponse(Valoraciones valoracion) {
        return new ValoracionResponse(
                valoracion.getId(),
                valoracion.getSolicitud().getId(),
                valoracion.getAutor().getId(),
                valoracion.getValorado().getId(),
                valoracion.getPuntuacion(),
                valoracion.getComentario(),
                valoracion.getFecha()
        );
    }
}