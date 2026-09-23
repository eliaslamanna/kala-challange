package com.kala.challenge.exception;

import com.kala.challenge.dominio.Unidad;
import lombok.Getter;

@Getter
public class TransformacionNoPermitidaException extends RuntimeException {

    private Unidad unidad;

    public TransformacionNoPermitidaException(Unidad unidad) {
        super("El " + unidad.getClass().getSimpleName().toLowerCase() + " no se puede transformar");
        this.unidad = unidad;
    }
}
