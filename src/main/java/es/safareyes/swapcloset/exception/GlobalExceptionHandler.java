package es.safareyes.swapcloset.exception;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ProblemDetail> noEncontrado(
            RecursoNoEncontradoException ex,
            HttpServletRequest request) {

        return respuesta(HttpStatus.NOT_FOUND,
                "Recurso no encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<ProblemDetail> conflicto(
            ConflictoException ex,
            HttpServletRequest request) {

        return respuesta(HttpStatus.CONFLICT,
                "Conflicto", ex.getMessage(), request);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ProblemDetail> reglaNegocio(
            ReglaNegocioException ex,
            HttpServletRequest request) {

        return respuesta(HttpStatusCode.valueOf(422),
                "Regla de negocio", ex.getMessage(), request);
    }

    @ExceptionHandler(FicheroNoValidoException.class)
    public ResponseEntity<ProblemDetail> ficheroInvalido(
            FicheroNoValidoException ex,
            HttpServletRequest request) {

        return respuesta(HttpStatus.BAD_REQUEST,
                "Fichero no valido", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> validacion(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Los datos enviados no son validos");

        problema.setTitle("Error de validacion");
        problema.setInstance(URI.create(request.getRequestURI()));
        problema.setProperty("timestamp", LocalDateTime.now());

        Map<String, String> campos = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                campos.put(error.getField(), error.getDefaultMessage())
        );

        problema.setProperty("campos", campos);

        return ResponseEntity.badRequest().body(problema);
    }

    private ResponseEntity<ProblemDetail> respuesta(
            HttpStatusCode estado,
            String titulo,
            String detalle,
            HttpServletRequest request) {

        ProblemDetail problema =
                ProblemDetail.forStatusAndDetail(estado, detalle);

        problema.setTitle(titulo);
        problema.setInstance(URI.create(request.getRequestURI()));
        problema.setProperty("timestamp", LocalDateTime.now());

        return ResponseEntity.status(estado).body(problema);
    }
}