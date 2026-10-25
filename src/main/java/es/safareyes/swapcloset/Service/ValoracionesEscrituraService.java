package es.safareyes.swapcloset.Service;

import es.safareyes.swapcloset.Mapper.ValoracionMapper;
import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.dto.Request.ValoracionCrearRequest;
import es.safareyes.swapcloset.dto.Response.ValoracionResponse;
import es.safareyes.swapcloset.exception.*;
import es.safareyes.swapcloset.repositorios.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValoracionesEscrituraService {

    private final SolicitudesRepository solicitudesRepository;
    private final PerfilRepository perfilRepository;
    private final ValoracionesRepository valoracionesRepository;

    @Transactional
    public ValoracionResponse valorar(
            Integer solicitudId,
            Integer perfilId,
            ValoracionCrearRequest request) {

        Solicitudes solicitud = solicitudesRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la solicitud"));

        if (solicitud.getEstado() != EstadoSolicitud.FINALIZADA) {
            throw new ReglaNegocioException(
                    "La solicitud debe estar finalizada");
        }

        Perfil autor = perfilRepository.findById(perfilId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el perfil"));

        if (valoracionesRepository.existsBySolicitudIdAndAutorId(
                solicitudId, perfilId)) {
            throw new ConflictoException(
                    "Ya has valorado esta solicitud");
        }

        Perfil solicitante = solicitud.getSolicitante();
        Perfil otraPersona;

        if (solicitud.getTipoSolicitud() == TipoSolicitud.INTERCAMBIO) {

            Prenda ofrecida = solicitud.getPrendaOfrecida();

            if (ofrecida == null) {
                throw new ReglaNegocioException(
                        "No existe la prenda ofrecida");
            }

            otraPersona = ofrecida.getPerfil();

        } else if (solicitud.getTipoSolicitud() == TipoSolicitud.PRESTAMO) {

            otraPersona = solicitud.getPrendaSolicitada().getPerfil();

        } else {
            throw new ReglaNegocioException(
                    "Tipo de solicitud no valido");
        }

        Perfil valorado;

        if (solicitante.getId().equals(perfilId)) {
            valorado = otraPersona;
        } else if (otraPersona.getId().equals(perfilId)) {
            valorado = solicitante;
        } else {
            throw new ReglaNegocioException(
                    "No participas en esta solicitud");
        }

        if (valorado.getId().equals(perfilId)) {
            throw new ReglaNegocioException(
                    "No puedes valorarte a ti mismo");
        }

        Valoraciones valoracion = Valoraciones.builder()
                .solicitud(solicitud)
                .autor(autor)
                .valorado(valorado)
                .puntuacion(request.puntuacion())
                .comentario(request.comentario())
                .build();

        Valoraciones guardada = valoracionesRepository.save(valoracion);

        log.info("Valoracion {} creada", guardada.getId());

        return ValoracionMapper.toResponse(guardada);
    }
}