package es.safareyes.swapcloset.Service;

import es.safareyes.swapcloset.Mapper.SolicitudMapper;
import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.dto.Request.*;
import es.safareyes.swapcloset.dto.Response.SolicitudResponse;
import es.safareyes.swapcloset.exception.*;
import es.safareyes.swapcloset.repositorios.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudesEscrituraService {

    private final SolicitudesRepository solicitudesRepository;
    private final PerfilRepository perfilRepository;
    private final PrendaRepository prendaRepository;

    @Transactional
    public SolicitudResponse crear(
            Integer perfilId,
            SolicitudCrearRequest request) {

        Perfil solicitante = perfilRepository.findById(perfilId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el perfil"));

        Prenda solicitada = prendaRepository
                .findById(request.idPrendaSolicitada())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la prenda solicitada"));

        if (solicitada.getPerfil().getId().equals(perfilId)) {
            throw new ReglaNegocioException(
                    "No puedes solicitar una prenda propia");
        }

        if (solicitada.getEstadoPrenda() != EstadoPrenda.DISPONIBLE) {
            throw new ReglaNegocioException(
                    "La prenda no esta disponible");
        }

        if (solicitudesRepository
                .existsBySolicitanteIdAndPrendaSolicitadaIdAndEstado(
                        perfilId,
                        solicitada.getId(),
                        EstadoSolicitud.PENDIENTE)) {
            throw new ConflictoException(
                    "Ya tienes una solicitud pendiente");
        }

        Prenda ofrecida = null;

        if (request.tipoSolicitud() == TipoSolicitud.INTERCAMBIO) {

            if (!Boolean.TRUE.equals(solicitada.getPermiteIntercambio())) {
                throw new ReglaNegocioException(
                        "La prenda no admite intercambios");
            }

            if (request.idPrendaOfrecida() == null) {
                throw new ReglaNegocioException(
                        "Debes ofrecer una prenda");
            }

            if (request.fechaInicio() != null || request.fechaFin() != null) {
                throw new ReglaNegocioException(
                        "Un intercambio no necesita fechas de prestamo");
            }

            ofrecida = prendaRepository.findById(request.idPrendaOfrecida())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No existe la prenda ofrecida"));

            if (!ofrecida.getPerfil().getId().equals(perfilId)) {
                throw new ReglaNegocioException(
                        "La prenda ofrecida debe ser tuya");
            }

            if (ofrecida.getEstadoPrenda() != EstadoPrenda.DISPONIBLE) {
                throw new ReglaNegocioException(
                        "La prenda ofrecida no esta disponible");
            }

        } else if (request.tipoSolicitud() == TipoSolicitud.PRESTAMO) {

            if (!Boolean.TRUE.equals(solicitada.getPermitePrestamo())) {
                throw new ReglaNegocioException(
                        "La prenda no admite prestamos");
            }

            if (request.idPrendaOfrecida() != null) {
                throw new ReglaNegocioException(
                        "Un prestamo no debe incluir prenda ofrecida");
            }

            validarPrestamo(perfilId, request.fechaInicio(), request.fechaFin());

        } else {
            throw new ReglaNegocioException(
                    "Tipo de solicitud no valido");
        }

        Solicitudes solicitud = Solicitudes.builder()
                .solicitante(solicitante)
                .prendaSolicitada(solicitada)
                .prendaOfrecida(ofrecida)
                .tipoSolicitud(request.tipoSolicitud())
                .estado(EstadoSolicitud.PENDIENTE)
                .fechaInicio(request.fechaInicio())
                .fechaFin(request.fechaFin())
                .build();

        Solicitudes guardada = solicitudesRepository.save(solicitud);

        return SolicitudMapper.toResponse(guardada);
    }

    @Transactional
    public SolicitudResponse cambiarEstado(
            Integer id,
            Integer perfilId,
            SolicitudEstadoRequest request) {

        Solicitudes solicitud = solicitudesRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la solicitud"));

        EstadoSolicitud actual = solicitud.getEstado();
        EstadoSolicitud nuevo = request.estado();

        boolean propietario = solicitud.getPrendaSolicitada()
                .getPerfil().getId().equals(perfilId);

        boolean solicitante = solicitud.getSolicitante()
                .getId().equals(perfilId);

        if (nuevo == EstadoSolicitud.ACEPTADA
                || nuevo == EstadoSolicitud.RECHAZADA) {

            validar(actual == EstadoSolicitud.PENDIENTE,
                    "La solicitud no esta pendiente");

            validar(propietario,
                    "Solo el propietario puede responder");

        } else if (nuevo == EstadoSolicitud.CANCELADA) {

            validar(actual == EstadoSolicitud.PENDIENTE,
                    "Solo puedes cancelar solicitudes pendientes");

            validar(solicitante,
                    "Solo el solicitante puede cancelar");

        } else if (nuevo == EstadoSolicitud.FINALIZADA) {

            validar(actual == EstadoSolicitud.ACEPTADA
                            || actual == EstadoSolicitud.RETRASADA,
                    "La solicitud no se puede finalizar");

            validar(propietario,
                    "Solo el propietario puede finalizar");

        } else {
            throw new ReglaNegocioException(
                    "Transicion de estado no permitida");
        }

        LocalDateTime ahora = LocalDateTime.now();

        if (nuevo == EstadoSolicitud.ACEPTADA) {

            Prenda pedida = solicitud.getPrendaSolicitada();

            if (pedida.getEstadoPrenda() != EstadoPrenda.DISPONIBLE) {
                throw new ConflictoException(
                        "La prenda ya no esta disponible");
            }

            if (solicitud.getTipoSolicitud() == TipoSolicitud.INTERCAMBIO) {

                Prenda ofrecida = solicitud.getPrendaOfrecida();

                if (ofrecida == null
                        || ofrecida.getEstadoPrenda() != EstadoPrenda.DISPONIBLE) {
                    throw new ConflictoException(
                            "La prenda ofrecida ya no esta disponible");
                }

                ofrecida.setEstadoPrenda(EstadoPrenda.RESERVADA);
                prendaRepository.save(ofrecida);

                pedida.setEstadoPrenda(EstadoPrenda.RESERVADA);

            } else {
                pedida.setEstadoPrenda(EstadoPrenda.PRESTADA);
            }

            solicitud.setEstado(EstadoSolicitud.ACEPTADA);
            solicitud.setFechaRespuesta(ahora);

            prendaRepository.save(pedida);
            solicitudesRepository.saveAndFlush(solicitud);

            solicitudesRepository.rechazarOtrasPendientes(
                    pedida.getId(),
                    solicitud.getId(),
                    EstadoSolicitud.PENDIENTE,
                    EstadoSolicitud.RECHAZADA,
                    ahora
            );

        } else if (nuevo == EstadoSolicitud.FINALIZADA) {

            Prenda pedida = solicitud.getPrendaSolicitada();

            if (solicitud.getTipoSolicitud() == TipoSolicitud.INTERCAMBIO) {

                Prenda ofrecida = solicitud.getPrendaOfrecida();

                if (ofrecida == null) {
                    throw new ReglaNegocioException(
                            "No existe la prenda ofrecida");
                }

                Perfil propietarioOriginal = pedida.getPerfil();

                pedida.setPerfil(solicitud.getSolicitante());
                ofrecida.setPerfil(propietarioOriginal);

                ofrecida.setEstadoPrenda(EstadoPrenda.DISPONIBLE);
                prendaRepository.save(ofrecida);
            }

            pedida.setEstadoPrenda(EstadoPrenda.DISPONIBLE);
            prendaRepository.save(pedida);

            solicitud.setEstado(EstadoSolicitud.FINALIZADA);
            solicitud.setFechaFinalizacion(ahora);

        } else {

            solicitud.setEstado(nuevo);
            solicitud.setFechaRespuesta(ahora);
        }

        Solicitudes guardada = solicitudesRepository.save(solicitud);

        log.info("Solicitud {} pasa de {} a {}", id, actual, nuevo);

        return SolicitudMapper.toResponse(guardada);
    }

    private void validarPrestamo(
            Integer perfilId,
            LocalDate inicio,
            LocalDate fin) {

        if (inicio == null || fin == null) {
            throw new ReglaNegocioException(
                    "Debes indicar las fechas del prestamo");
        }

        if (inicio.isBefore(LocalDate.now())) {
            throw new ReglaNegocioException(
                    "El prestamo no puede comenzar en el pasado");
        }

        if (!fin.isAfter(inicio)) {
            throw new ReglaNegocioException(
                    "La fecha final debe ser posterior a la inicial");
        }

        if (ChronoUnit.DAYS.between(inicio, fin) > 30) {
            throw new ReglaNegocioException(
                    "El prestamo no puede superar 30 dias");
        }

        long activos = solicitudesRepository
                .countBySolicitanteIdAndTipoSolicitudAndEstadoIn(
                        perfilId,
                        TipoSolicitud.PRESTAMO,
                        List.of(EstadoSolicitud.ACEPTADA, EstadoSolicitud.RETRASADA)
                );

        if (activos >= 3) {
            throw new ReglaNegocioException(
                    "Ya tienes tres prestamos activos");
        }
    }

    private void validar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new ReglaNegocioException(mensaje);
        }
    }
}