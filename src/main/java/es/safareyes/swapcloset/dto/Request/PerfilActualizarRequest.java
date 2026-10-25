package es.safareyes.swapcloset.dto.Request;

import jakarta.validation.constraints.*;
import java.util.Set;

public record PerfilActualizarRequest(
        @Size(max = 80) String nombre,
        @Size(max = 80) String apellido1,
        @Size(max = 80) String apellido2,
        @Size(max = 500) String biografia,
        @Size(max = 100) String ciudad,

        @Pattern(regexp = "XS|S|M|L|XL|XXL")
        String tallaCamiseta,

        @Pattern(regexp = "3[4-9]|4[0-9]|50")
        String tallaPantalon,

        @Pattern(regexp = "3[5-9]|4[0-6]")
        String tallaZapatos,

        @Size(min = 1, max = 5)
        Set<Integer> idsEstilos
) {}