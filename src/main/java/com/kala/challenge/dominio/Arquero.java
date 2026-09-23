package com.kala.challenge.dominio;

import java.util.Optional;

public final class Arquero extends Unidad {

    public Arquero() {
        super(10);
    }

    @Override
    public int costoDelEntrenamiento() {
        return 20;
    }

    @Override
    protected int puntosEntrenamientoObtenidos() {
        return 7;
    }

    @Override
    public Optional<Transformacion> transformacion() {
        return Optional.of(new Transformacion(40, new Caballero()));
    }

}
