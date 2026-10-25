package es.safareyes.swapcloset.dto.Request;

import jakarta.validation.constraints.*;

public record MensajeCrearRequest(
        @NotBlank
        @Size(min = 1, max = 1000)
        String contenido
) {}