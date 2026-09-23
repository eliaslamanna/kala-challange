package com.kala.challenge.dominio;

import java.util.Optional;

import static java.util.Optional.empty;

public final class Caballero extends Unidad {

    public Caballero() {
        super(20);
    }

    @Override
    public int costoDelEntrenamiento() {
        return 30;
    }

    @Override
    protected int puntosEntrenamientoObtenidos() {
        return 10;
    }

    @Override
    public Optional<Transformacion> transformacion() {
        return empty();
    }

}
