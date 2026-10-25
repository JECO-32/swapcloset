package es.safareyes.swapcloset.dto.Request;

import es.safareyes.swapcloset.Modelos.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;

public record SolicitudEstadoRequest(
        @NotNull EstadoSolicitud estado
) {}