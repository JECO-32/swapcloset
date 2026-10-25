package es.safareyes.swapcloset.dto.Request;

import es.safareyes.swapcloset.Modelos.EstadoConservacion;
import jakarta.validation.constraints.*;

public record PrendaCrearRequest(
        @NotNull Integer idCategoria,
        @NotBlank @Size(max = 120) String titulo,
        String descripcion,
        @Size(max = 80) String marca,
        @Size(max = 30) String talla,
        @NotNull EstadoConservacion estadoConservacion,
        @NotNull Boolean permitePrestamo,
        @NotNull Boolean permiteIntercambio
) {}