package es.safareyes.swapcloset.dto.Response;

import java.util.Set;

public record PerfilResponse(
        Integer id,
        String username,
        String nombre,
        String apellido1,
        String apellido2,
        String biografia,
        String ciudad,
        String email,
        String tallaCamiseta,
        String tallaPantalon,
        String tallaZapatos,
        Set<String> estilos
) {}