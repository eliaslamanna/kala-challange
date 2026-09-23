package com.kala.challenge.dominio;

import java.util.Optional;

public final class Piquero extends Unidad {

    public Piquero() {
        super(5);
    }

    @Override
    public int costoDelEntrenamiento() {
        return 10;
    }

    @Override
    protected int puntosEntrenamientoObtenidos() {
        return 3;
    }

    @Override
    public Optional<Transformacion> transformacion() {
        return Optional.of(new Transformacion(30, new Arquero()));
    }

}
