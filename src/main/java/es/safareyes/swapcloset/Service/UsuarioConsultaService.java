package es.safareyes.swapcloset.Service;

import es.safareyes.swapcloset.Mapper.*;
import es.safareyes.swapcloset.Modelos.*;
import es.safareyes.swapcloset.dto.Response.*;
import es.safareyes.swapcloset.exception.RecursoNoEncontradoException;
import es.safareyes.swapcloset.repositorios.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioConsultaService {

    private final PerfilRepository perfilRepository;
    private final PrendaRepository prendaRepository;
    private final ValoracionesRepository valoracionesRepository;

    @Transactional(readOnly = true)
    public PerfilPublicoResponse detalle(Integer id) {

        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el perfil " + id));

        List<PrendaResponse> prendas = prendaRepository.findByPerfilId(id)
                .stream()
                .filter(p -> p.getEstadoPrenda() == EstadoPrenda.DISPONIBLE)
                .map(PrendaMapper::toResponse)
                .toList();

        Double media = valoracionesRepository.calcularMediaValoraciones(id);

        Long total = valoracionesRepository.countByValoradoId(id);

        return new PerfilPublicoResponse(
                PerfilMapper.toResponse(perfil),
                media == null ? 0.0 : media,
                total,
                prendas
        );
    }

    @Transactional(readOnly = true)
    public List<PerfilPublicoResponse> afines(Integer perfilId) {

        if (!perfilRepository.existsById(perfilId)) {
            throw new RecursoNoEncontradoException(
                    "No existe el perfil " + perfilId);
        }

        return perfilRepository.buscarPerfilesAfines(perfilId)
                .stream()
                .map(perfil -> detalle(perfil.getId()))
                .toList();
    }
}