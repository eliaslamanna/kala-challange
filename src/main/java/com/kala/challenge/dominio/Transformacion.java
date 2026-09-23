package com.kala.challenge.dominio;

import lombok.Getter;

@Getter
public class Transformacion {

    private int costo;

    private Unidad unidadResultante;

    public Transformacion(int costo, Unidad unidadResultante) {
        if (costo < 0) {
            throw new IllegalArgumentException("El costo tiene que ser cero o positivo");
        }
        this.costo = costo;
        this.unidadResultante = unidadResultante;
    }

}
