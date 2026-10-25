package es.safareyes.swapcloset.Service;

import es.safareyes.swapcloset.Mapper.*;
import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.dto.Response.*;
import es.safareyes.swapcloset.exception.*;
import es.safareyes.swapcloset.repositorios.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SolicitudesConsultaService {

    private final SolicitudesRepository solicitudesRepository;
    private final MensajesRepository mensajesRepository;
    private final PerfilRepository perfilRepository;

    @Transactional(readOnly = true)
    public Page<SolicitudResponse> bandeja(
            Integer perfilId,
            String bandeja,
            EstadoSolicitud estado,
            Pageable pageable) {

        if (!perfilRepository.existsById(perfilId)) {
            throw new RecursoNoEncontradoException(
                    "No existe el perfil");
        }

        Page<Solicitudes> resultado;

        if ("enviadas".equalsIgnoreCase(bandeja)) {

            if (estado == null) {
                resultado = solicitudesRepository.findBySolicitanteId(
                        perfilId, pageable);
            } else {
                resultado = solicitudesRepository.findBySolicitanteIdAndEstado(
                        perfilId, estado, pageable);
            }

        } else if ("recibidas".equalsIgnoreCase(bandeja)) {

            resultado = solicitudesRepository.buscarRecibidas(
                    perfilId, estado, pageable);

        } else {
            throw new ReglaNegocioException(
                    "La bandeja debe ser enviadas o recibidas");
        }

        return resultado.map(SolicitudMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<MensajeResponse> mensajes(
            Integer solicitudId,
            Integer perfilId,
            Pageable pageable) {

        Solicitudes solicitud = solicitudesRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la solicitud"));

        if (!perfilRepository.existsById(perfilId)) {
            throw new RecursoNoEncontradoException(
                    "No existe el perfil");
        }

        boolean esSolicitante =
                solicitud.getSolicitante().getId().equals(perfilId);

        boolean esPropietario =
                solicitud.getPrendaSolicitada()
                        .getPerfil().getId().equals(perfilId);

        boolean esPropietarioOriginal =
                solicitud.getTipoSolicitud() == TipoSolicitud.INTERCAMBIO
                        && solicitud.getEstado() == EstadoSolicitud.FINALIZADA
                        && solicitud.getPrendaOfrecida() != null
                        && solicitud.getPrendaOfrecida()
                        .getPerfil().getId().equals(perfilId);

        if (!esSolicitante && !esPropietario && !esPropietarioOriginal) {
            throw new ReglaNegocioException(
                    "No participas en esta solicitud");
        }

        return mensajesRepository
                .findBySolicitudIdOrderByEnviadoEnAsc(
                        solicitudId, pageable
                )
                .map(MensajeMapper::toResponse);
    }
}