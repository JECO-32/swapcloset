package es.safareyes.swapcloset.Service;

import es.safareyes.swapcloset.Mapper.MensajeMapper;
import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.dto.Request.MensajeCrearRequest;
import es.safareyes.swapcloset.dto.Response.MensajeResponse;
import es.safareyes.swapcloset.exception.*;
import es.safareyes.swapcloset.repositorios.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MensajesEscrituraService {

    private final SolicitudesRepository solicitudesRepository;
    private final PerfilRepository perfilRepository;
    private final MensajesRepository mensajesRepository;

    @Transactional
    public MensajeResponse enviar(
            Integer solicitudId,
            Integer perfilId,
            MensajeCrearRequest request) {

        Solicitudes solicitud = solicitudesRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la solicitud"));

        Perfil autor = perfilRepository.findById(perfilId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el perfil"));

        boolean solicitante = solicitud.getSolicitante()
                .getId().equals(perfilId);

        boolean propietario = solicitud.getPrendaSolicitada()
                .getPerfil().getId().equals(perfilId);

        boolean propietarioOriginal =
                solicitud.getTipoSolicitud() == TipoSolicitud.INTERCAMBIO
                        && solicitud.getEstado() == EstadoSolicitud.FINALIZADA
                        && solicitud.getPrendaOfrecida() != null
                        && solicitud.getPrendaOfrecida().getPerfil()
                        .getId().equals(perfilId);

        if (!solicitante && !propietario && !propietarioOriginal) {
            throw new ReglaNegocioException(
                    "No participas en esta solicitud");
        }

        if (Set.of(
                EstadoSolicitud.RECHAZADA,
                EstadoSolicitud.CANCELADA,
                EstadoSolicitud.CADUCADA
        ).contains(solicitud.getEstado())) {
            throw new ReglaNegocioException(
                    "No puedes escribir en esta solicitud");
        }

        Mensajes mensaje = Mensajes.builder()
                .solicitud(solicitud)
                .autor(autor)
                .contenido(request.contenido())
                .build();

        Mensajes guardado = mensajesRepository.save(mensaje);

        log.info("Mensaje {} enviado", guardado.getId());

        return MensajeMapper.toResponse(guardado);
    }
}