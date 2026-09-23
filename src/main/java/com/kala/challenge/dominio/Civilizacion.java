package com.kala.challenge.dominio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Civilizacion {

    TipoCivilizacion tipo;

    String nombre;

    int cantidadPiqueros;

    int cantidadArqueros;

    int cantidadCaballeros;

    public static Civilizacion crear(TipoCivilizacion tipo) {
        return switch (tipo) {
            case CHINO -> new Civilizacion(tipo, "Chinos", 2, 25, 2);
            case INGLES -> new Civilizacion(tipo, "Ingleses", 10, 10, 10);
            case BIZANTINO -> new Civilizacion(tipo, "Bizantinos", 5, 8, 15);
        };
    }

}
