package es.safareyes.swapcloset.dto;

import es.safareyes.swapcloset.Modelos.EstadoConservacion;

public interface PrendaTarjetaView {

    Integer getId();

    String getTitulo();

    String getMarca();

    String getTalla();

    EstadoConservacion getEstadoConservacion();
}