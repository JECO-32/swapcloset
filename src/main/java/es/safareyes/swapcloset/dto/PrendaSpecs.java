package es.safareyes.swapcloset.dto;

import es.safareyes.swapcloset.Modelos.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PrendaSpecs {

    private PrendaSpecs() {}

    public static Specification<Prenda> conFiltros(
            Integer idCategoria,
            String talla,
            EstadoConservacion estadoConservacion,
            Boolean permiteIntercambio,
            Boolean permitePrestamo,
            String ciudad,
            Integer idEstilo) {

        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> filtros = new ArrayList<>();

            // Solo se ven las prendas disponibles (RN-03)
            filtros.add(cb.equal(root.get("estadoPrenda"), EstadoPrenda.DISPONIBLE));

            Join<Prenda, Perfil> perfil = root.join("perfil");

            if (idCategoria != null) {
                filtros.add(cb.equal(root.get("categoria").get("id"), idCategoria));
            }
            if (talla != null && !talla.isBlank()) {
                filtros.add(cb.equal(cb.lower(root.get("talla")), talla.toLowerCase()));
            }
            if (estadoConservacion != null) {
                filtros.add(cb.equal(root.get("estadoConservacion"), estadoConservacion));
            }
            if (permiteIntercambio != null) {
                filtros.add(cb.equal(root.get("permiteIntercambio"), permiteIntercambio));
            }
            if (permitePrestamo != null) {
                filtros.add(cb.equal(root.get("permitePrestamo"), permitePrestamo));
            }
            if (ciudad != null && !ciudad.isBlank()) {
                filtros.add(cb.equal(cb.lower(perfil.get("ciudad")), ciudad.toLowerCase()));
            }
            if (idEstilo != null) {
                Join<Perfil, Estilos> estilos = perfil.join("estilos");
                filtros.add(cb.equal(estilos.get("id"), idEstilo));
            }
            return cb.and(filtros.toArray(new Predicate[0]));
        };
    }
}