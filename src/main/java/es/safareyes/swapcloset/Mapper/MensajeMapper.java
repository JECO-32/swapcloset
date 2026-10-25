package es.safareyes.swapcloset.Mapper;

import es.safareyes.swapcloset.Modelos.Mensajes;
import es.safareyes.swapcloset.dto.Response.MensajeResponse;

public interface MensajeMapper {

    static MensajeResponse toResponse(Mensajes mensaje) {

        if (mensaje == null) {
            return null;
        }

        return new MensajeResponse(
                mensaje.getId(),
                mensaje.getSolicitud().getId(),
                mensaje.getAutor().getId(),
                mensaje.getContenido(),
                mensaje.getEnviadoEn()
        );
    }
}