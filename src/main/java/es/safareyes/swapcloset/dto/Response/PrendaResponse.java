package es.safareyes.swapcloset.dto.Response;

import es.safareyes.swapcloset.Modelos.*;
import java.time.LocalDateTime;

public record PrendaResponse(
        Integer id,
        Integer idPerfil,
        Integer idCategoria,
        String titulo,
        String descripcion,
        String marca,
        String talla,
        EstadoConservacion estadoConservacion,
        EstadoPrenda estadoPrenda,
        Boolean permitePrestamo,
        Boolean permiteIntercambio,
        LocalDateTime fechaPublicacion
) {}