package es.safareyes.swapcloset.dto.Request;

import jakarta.validation.constraints.*;

public record ValoracionCrearRequest(
        @NotNull @Min(1) @Max(5) Integer puntuacion,
        @Size(max = 1000) String comentario
) {}