package com.kala.challenge.dominio;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class Batalla {

    private UUID id;

    private UUID idOponente;

    private Civilizacion civilizacionOponente;

    private ResultadoBatalla resultado;

    private List<Unidad> unidadesPerdidas;

    static Batalla crear(UUID id, Ejercito oponente, ResultadoBatalla resultado, List<Unidad> unidadesPerdidas) {
        return new Batalla(id, oponente.getId(), oponente.getCivilizacion(), resultado, unidadesPerdidas);
    }

}
