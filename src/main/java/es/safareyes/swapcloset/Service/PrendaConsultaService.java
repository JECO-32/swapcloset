package es.safareyes.swapcloset.Service;

import es.safareyes.swapcloset.Mapper.PrendaMapper;
import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.dto.Response.*;
import es.safareyes.swapcloset.exception.RecursoNoEncontradoException;
import es.safareyes.swapcloset.repositorios.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrendaConsultaService {

    private final PrendaRepository prendaRepository;
    private final CategoriaRepository categoriaRepository;
    private final ValoracionesRepository valoracionesRepository;

    @Transactional(readOnly = true)
    public Page<PrendaResponse> buscar(
            Integer idCategoria,
            String talla,
            String marca,
            EstadoConservacion estadoConservacion,
            Boolean permitePrestamo,
            Boolean permiteIntercambio,
            String texto,
            Pageable pageable) {

        Specification<Prenda> filtros = (root, query, cb) -> {

            List<Predicate> condiciones = new ArrayList<>();

            condiciones.add(cb.equal(
                    root.get("estadoPrenda"), EstadoPrenda.DISPONIBLE
            ));

            if (idCategoria != null) {
                condiciones.add(cb.equal(
                        root.get("categoria").get("id"), idCategoria
                ));
            }

            if (talla != null && !talla.isBlank()) {
                condiciones.add(cb.equal(
                        cb.lower(root.get("talla")),
                        talla.toLowerCase()
                ));
            }

            if (marca != null && !marca.isBlank()) {
                condiciones.add(cb.like(
                        cb.lower(root.get("marca")),
                        "%" + marca.toLowerCase() + "%"
                ));
            }

            if (estadoConservacion != null) {
                condiciones.add(cb.equal(
                        root.get("estadoConservacion"),
                        estadoConservacion
                ));
            }

            if (permitePrestamo != null) {
                condiciones.add(cb.equal(
                        root.get("permitePrestamo"), permitePrestamo
                ));
            }

            if (permiteIntercambio != null) {
                condiciones.add(cb.equal(
                        root.get("permiteIntercambio"), permiteIntercambio
                ));
            }

            if (texto != null && !texto.isBlank()) {

                String busqueda = "%" + texto.toLowerCase() + "%";

                condiciones.add(cb.or(
                        cb.like(cb.lower(root.get("titulo")), busqueda),
                        cb.like(cb.lower(root.get("descripcion")), busqueda)
                ));
            }

            return cb.and(condiciones.toArray(new Predicate[0]));
        };

        Pageable paginacion = PageRequest.of(
                pageable.getPageNumber(),
                Math.min(Math.max(pageable.getPageSize(), 1), 100),
                pageable.getSort()
        );

        return prendaRepository.findAll(filtros, paginacion)
                .map(PrendaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PrendaDetalleResponse detalle(Integer id) {

        Prenda prenda = prendaRepository.findConRelacionesById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la prenda " + id));

        if (prenda.getEstadoPrenda() != EstadoPrenda.DISPONIBLE) {
            throw new RecursoNoEncontradoException(
                    "La prenda no esta publicada");
        }

        Perfil propietario = prenda.getPerfil();

        Set<String> estilos = propietario.getEstilos()
                .stream()
                .map(Estilos::getNombre)
                .collect(Collectors.toSet());

        Double reputacion = valoracionesRepository
                .calcularMediaValoraciones(propietario.getId());

        PerfilPublicoResumenResponse propietarioResponse =
                new PerfilPublicoResumenResponse(
                        propietario.getId(),
                        propietario.getNombre(),
                        propietario.getCiudad(),
                        estilos,
                        reputacion == null ? 0.0 : reputacion
                );

        List<FotoResponse> fotos = prenda.getFotos()
                .stream()
                .sorted(Comparator.comparing(
                        Foto::getOrden,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(foto -> new FotoResponse(
                        foto.getId(),
                        foto.getNombreOriginal(),
                        foto.getTipoMime(),
                        foto.getTamano(),
                        foto.getOrden()
                ))
                .toList();

        return new PrendaDetalleResponse(
                PrendaMapper.toResponse(prenda),
                propietarioResponse,
                fotos
        );
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> categorias() {

        return categoriaRepository.categoriasConNumeroDisponibles()
                .stream()
                .map(fila -> new CategoriaResponse(
                        ((Number) fila[0]).intValue(),
                        (String) fila[1],
                        ((Number) fila[2]).longValue()
                ))
                .toList();
    }
}