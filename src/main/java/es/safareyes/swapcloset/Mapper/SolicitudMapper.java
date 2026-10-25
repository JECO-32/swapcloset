package es.safareyes.swapcloset.Mapper;

import es.safareyes.swapcloset.Modelos.Solicitudes;
import es.safareyes.swapcloset.dto.Response.SolicitudResponse;

public interface SolicitudMapper {

    static SolicitudResponse toResponse(Solicitudes solicitud) {
        return new SolicitudResponse(
                solicitud.getId(),
                solicitud.getSolicitante().getId(),
                solicitud.getPrendaSolicitada().getId(),
                solicitud.getPrendaOfrecida() != null
                        ? solicitud.getPrendaOfrecida().getId()
                        : null,
                solicitud.getTipoSolicitud(),
                solicitud.getEstado(),
                solicitud.getFechaInicio(),
                solicitud.getFechaFin(),
                solicitud.getFechaCreacion(),
                solicitud.getFechaRespuesta(),
                solicitud.getFechaFinalizacion()
        );
    }
}