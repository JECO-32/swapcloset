package es.safareyes.swapcloset.exception;

public class FicheroNoValidoException extends RuntimeException {

    public FicheroNoValidoException(String mensaje) {
        super(mensaje);
    }
}