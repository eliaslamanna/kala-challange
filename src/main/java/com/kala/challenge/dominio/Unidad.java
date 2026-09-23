package com.kala.challenge.dominio;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

import static lombok.AccessLevel.PROTECTED;

@AllArgsConstructor(access = PROTECTED)
public abstract class Unidad {

    @Getter
    private int puntos;

    public abstract int costoDelEntrenamiento();

    public abstract Optional<Transformacion> transformacion();

    protected abstract int puntosEntrenamientoObtenidos();

    final void entrenar() {
        puntos += puntosEntrenamientoObtenidos();
    }

}
