package es.safareyes.swapcloset.Service;

import es.safareyes.swapcloset.Mapper.PrendaMapper;
import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.dto.Request.*;
import es.safareyes.swapcloset.dto.Response.PrendaResponse;
import es.safareyes.swapcloset.exception.*;
import es.safareyes.swapcloset.repositorios.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrendaEscrituraService {

    private final PrendaRepository prendaRepository;
    private final PerfilRepository perfilRepository;
    private final CategoriaRepository categoriaRepository;
    private final SolicitudesRepository solicitudesRepository;

    @Transactional
    public PrendaResponse crear(Integer perfilId, PrendaCrearRequest request) {

        Perfil perfil = perfilRepository.findById(perfilId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el perfil " + perfilId));

        Categoria categoria = categoriaRepository.findById(request.idCategoria())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la categoria " + request.idCategoria()));

        validarModalidad(request.permitePrestamo(), request.permiteIntercambio());

        Prenda prenda = PrendaMapper.toEntity(request);
        prenda.setPerfil(perfil);
        prenda.setCategoria(categoria);
        prenda.setEstadoPrenda(EstadoPrenda.BORRADOR);

        Prenda guardada = prendaRepository.save(prenda);

        log.info("Prenda {} creada", guardada.getId());

        return PrendaMapper.toResponse(guardada);
    }

    @Transactional
    public PrendaResponse actualizar(
            Integer id,
            Integer perfilId,
            PrendaActualizarRequest request) {

        Prenda prenda = buscarPropia(id, perfilId);

        if (prenda.getEstadoPrenda() == EstadoPrenda.RETIRADA) {
            throw new ReglaNegocioException("La prenda esta retirada");
        }

        if (prenda.getEstadoPrenda() == EstadoPrenda.RESERVADA
                || prenda.getEstadoPrenda() == EstadoPrenda.PRESTADA) {
            throw new ReglaNegocioException(
                    "No puedes modificar una prenda reservada o prestada");
        }

        Boolean prestamo = request.permitePrestamo() != null
                ? request.permitePrestamo()
                : prenda.getPermitePrestamo();

        Boolean intercambio = request.permiteIntercambio() != null
                ? request.permiteIntercambio()
                : prenda.getPermiteIntercambio();

        validarModalidad(prestamo, intercambio);

        if (request.idCategoria() != null) {
            Categoria categoria = categoriaRepository
                    .findById(request.idCategoria())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe la categoria"));
            prenda.setCategoria(categoria);
        }

        PrendaMapper.actualizar(request, prenda);

        Prenda guardada = prendaRepository.save(prenda);

        return PrendaMapper.toResponse(guardada);
    }

    @Transactional
    public void retirar(Integer id, Integer perfilId) {

        Prenda prenda = buscarPropia(id, perfilId);

        if (prenda.getEstadoPrenda() == EstadoPrenda.RESERVADA
                || prenda.getEstadoPrenda() == EstadoPrenda.PRESTADA) {
            throw new ReglaNegocioException(
                    "No puedes retirar una prenda con operaciones activas");
        }

        prenda.setEstadoPrenda(EstadoPrenda.RETIRADA);
        prendaRepository.save(prenda);

        solicitudesRepository.cancelarPendientesDePrenda(
                id,
                EstadoSolicitud.PENDIENTE,
                EstadoSolicitud.CANCELADA
        );
    }

    private Prenda buscarPropia(Integer id, Integer perfilId) {

        Prenda prenda = prendaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la prenda " + id));

        if (!prenda.getPerfil().getId().equals(perfilId)) {
            throw new ReglaNegocioException(
                    "Solo puedes modificar tus propias prendas");
        }

        return prenda;
    }

    private void validarModalidad(Boolean prestamo, Boolean intercambio) {

        if (!Boolean.TRUE.equals(prestamo)
                && !Boolean.TRUE.equals(intercambio)) {
            throw new ReglaNegocioException(
                    "Debes permitir prestamo o intercambio");
        }
    }
}