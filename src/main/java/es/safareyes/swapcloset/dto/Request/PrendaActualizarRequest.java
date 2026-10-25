package es.safareyes.swapcloset.dto.Request;

import es.safareyes.swapcloset.Modelos.EstadoConservacion;
import jakarta.validation.constraints.Size;

public record PrendaActualizarRequest(
        Integer idCategoria,
        @Size(max = 120) String titulo,
        String descripcion,
        @Size(max = 80) String marca,
        @Size(max = 30) String talla,
        EstadoConservacion estadoConservacion,
        Boolean permitePrestamo,
        Boolean permiteIntercambio
) {}