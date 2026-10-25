package es.safareyes.swapcloset.dto.Request;

import es.safareyes.swapcloset.Modelos.TipoSolicitud;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record SolicitudCrearRequest(
        @NotNull Integer idPrendaSolicitada,
        Integer idPrendaOfrecida,
        @NotNull TipoSolicitud tipoSolicitud,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {}