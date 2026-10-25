package es.safareyes.swapcloset.dto.Response;

import es.safareyes.swapcloset.Modelos.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SolicitudResponse(
        Integer id,
        Integer idSolicitante,
        Integer idPrendaSolicitada,
        Integer idPrendaOfrecida,
        TipoSolicitud tipoSolicitud,
        EstadoSolicitud estado,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaRespuesta,
        LocalDateTime fechaFinalizacion
) {}