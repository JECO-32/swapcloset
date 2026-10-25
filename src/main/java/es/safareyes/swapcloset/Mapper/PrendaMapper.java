package es.safareyes.swapcloset.Mapper;

import es.safareyes.swapcloset.Modelos.Prenda;
import es.safareyes.swapcloset.dto.Request.PrendaCrearRequest;
import es.safareyes.swapcloset.dto.Request.PrendaActualizarRequest;
import es.safareyes.swapcloset.dto.Response.PrendaResponse;

public interface PrendaMapper {

    static Prenda toEntity(PrendaCrearRequest request) {

        Prenda prenda = new Prenda();

        prenda.setTitulo(request.titulo());
        prenda.setDescripcion(request.descripcion());
        prenda.setMarca(request.marca());
        prenda.setTalla(request.talla());
        prenda.setEstadoConservacion(request.estadoConservacion());
        prenda.setPermitePrestamo(request.permitePrestamo());
        prenda.setPermiteIntercambio(request.permiteIntercambio());

        return prenda;
    }

    static PrendaResponse toResponse(Prenda prenda) {

        return new PrendaResponse(
                prenda.getId(),
                prenda.getPerfil().getId(),
                prenda.getCategoria().getId(),
                prenda.getTitulo(),
                prenda.getDescripcion(),
                prenda.getMarca(),
                prenda.getTalla(),
                prenda.getEstadoConservacion(),
                prenda.getEstadoPrenda(),
                prenda.getPermitePrestamo(),
                prenda.getPermiteIntercambio(),
                prenda.getFechaPublicacion()
        );
    }

    static void actualizar(
            PrendaActualizarRequest request,
            Prenda prenda) {

        if (request.titulo() != null) {
            prenda.setTitulo(request.titulo());
        }

        if (request.descripcion() != null) {
            prenda.setDescripcion(request.descripcion());
        }

        if (request.marca() != null) {
            prenda.setMarca(request.marca());
        }

        if (request.talla() != null) {
            prenda.setTalla(request.talla());
        }

        if (request.estadoConservacion() != null) {
            prenda.setEstadoConservacion(request.estadoConservacion());
        }

        if (request.permitePrestamo() != null) {
            prenda.setPermitePrestamo(request.permitePrestamo());
        }

        if (request.permiteIntercambio() != null) {
            prenda.setPermiteIntercambio(request.permiteIntercambio());
        }
    }
}