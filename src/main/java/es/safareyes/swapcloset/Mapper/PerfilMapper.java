package es.safareyes.swapcloset.Mapper;

import es.safareyes.swapcloset.Modelos.Estilos;
import es.safareyes.swapcloset.Modelos.Perfil;
import es.safareyes.swapcloset.dto.Request.PerfilActualizarRequest;
import es.safareyes.swapcloset.dto.Response.PerfilResponse;

import java.util.Set;
import java.util.stream.Collectors;

public interface PerfilMapper {

    static PerfilResponse toResponse(Perfil perfil) {

        Set<String> estilos = perfil.getEstilos()
                .stream()
                .map(Estilos::getNombre)
                .collect(Collectors.toSet());

        return new PerfilResponse(
                perfil.getId(),
                perfil.getUsuario().getUsername(),
                perfil.getNombre(),
                perfil.getApellido1(),
                perfil.getApellido2(),
                perfil.getBiografia(),
                perfil.getCiudad(),
                perfil.getEmail(),
                perfil.getTallaCamiseta(),
                perfil.getTallaPantalon(),
                perfil.getTallaZapatos(),
                estilos
        );
    }

    static void actualizar(
            PerfilActualizarRequest request,
            Perfil perfil) {

        if (request.nombre() != null) {
            perfil.setNombre(request.nombre());
        }

        if (request.apellido1() != null) {
            perfil.setApellido1(request.apellido1());
        }

        if (request.apellido2() != null) {
            perfil.setApellido2(request.apellido2());
        }

        if (request.biografia() != null) {
            perfil.setBiografia(request.biografia());
        }

        if (request.ciudad() != null) {
            perfil.setCiudad(request.ciudad());
        }

        if (request.tallaCamiseta() != null) {
            perfil.setTallaCamiseta(request.tallaCamiseta());
        }

        if (request.tallaPantalon() != null) {
            perfil.setTallaPantalon(request.tallaPantalon());
        }

        if (request.tallaZapatos() != null) {
            perfil.setTallaZapatos(request.tallaZapatos());
        }
    }
}