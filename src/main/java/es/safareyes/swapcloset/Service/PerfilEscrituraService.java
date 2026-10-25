package es.safareyes.swapcloset.Service;

import es.safareyes.swapcloset.Mapper.PerfilMapper;
import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.dto.Request.PerfilActualizarRequest;
import es.safareyes.swapcloset.dto.Response.PerfilResponse;
import es.safareyes.swapcloset.exception.*;
import es.safareyes.swapcloset.repositorios.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerfilEscrituraService {

    private final PerfilRepository perfilRepository;
    private final EstilosRepository estilosRepository;

    @Transactional
    public PerfilResponse actualizarMiPerfil(
            Integer perfilId,
            PerfilActualizarRequest request) {

        Perfil perfil = perfilRepository.findById(perfilId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el perfil " + perfilId));

        PerfilMapper.actualizar(request, perfil);

        if (request.idsEstilos() != null) {

            List<Estilos> estilos = estilosRepository
                    .findAllById(request.idsEstilos());

            if (estilos.size() != request.idsEstilos().size()) {
                throw new RecursoNoEncontradoException(
                        "Alguno de los estilos no existe");
            }

            perfil.setEstilos(new HashSet<>(estilos));
        }

        Perfil guardado = perfilRepository.save(perfil);

        log.info("Perfil {} actualizado", perfilId);

        return PerfilMapper.toResponse(guardado);
    }
}