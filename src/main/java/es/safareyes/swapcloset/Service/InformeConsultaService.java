package es.safareyes.swapcloset.Service;

import es.safareyes.swapcloset.dto.Response.ActividadMesResponse;
import es.safareyes.swapcloset.exception.ReglaNegocioException;
import es.safareyes.swapcloset.repositorios.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InformeConsultaService {

    private final PrendaRepository prendaRepository;
    private final SolicitudesRepository solicitudesRepository;

    @Transactional(readOnly = true)
    public List<ActividadMesResponse> actividad(
            LocalDateTime desde,
            LocalDateTime hasta) {

        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new ReglaNegocioException(
                    "El rango de fechas no es valido");
        }

        Map<Clave, Contador> resumen = new HashMap<>();

        for (Object[] fila :
                prendaRepository.actividadPorCategoriaYMes(desde, hasta)) {

            Clave clave = new Clave(
                    convertirFecha(fila[0]),
                    (String) fila[1]
            );

            Contador contador = resumen.computeIfAbsent(
                    clave, k -> new Contador()
            );

            contador.publicaciones =
                    ((Number) fila[2]).longValue();
        }

        for (Object[] fila :
                solicitudesRepository.actividadSolicitudesPorMes(desde, hasta)) {

            Clave clave = new Clave(
                    convertirFecha(fila[0]),
                    (String) fila[1]
            );

            Contador contador = resumen.computeIfAbsent(
                    clave, k -> new Contador()
            );

            contador.intercambios =
                    ((Number) fila[2]).longValue();

            contador.prestamos =
                    ((Number) fila[3]).longValue();
        }

        return resumen.entrySet()
                .stream()
                .sorted(
                        Comparator
                                .comparing((Map.Entry<Clave, Contador> e) ->
                                        e.getKey().mes())
                                .thenComparing(e ->
                                        e.getKey().categoria())
                )
                .map(e -> new ActividadMesResponse(
                        e.getKey().mes(),
                        e.getKey().categoria(),
                        e.getValue().publicaciones,
                        e.getValue().intercambios,
                        e.getValue().prestamos
                ))
                .toList();
    }

    private LocalDateTime convertirFecha(Object valor) {

        if (valor instanceof LocalDateTime fecha) {
            return fecha;
        }

        if (valor instanceof Timestamp fecha) {
            return fecha.toLocalDateTime();
        }

        if (valor instanceof java.sql.Date fecha) {
            return fecha.toLocalDate().atStartOfDay();
        }

        throw new IllegalStateException(
                "Formato de fecha no reconocido: " + valor
        );
    }

    private record Clave(
            LocalDateTime mes,
            String categoria
    ) {}

    private static class Contador {
        long publicaciones;
        long intercambios;
        long prestamos;
    }
}