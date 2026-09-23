package com.kala.challenge.dominio;

import com.kala.challenge.exception.OroInsuficienteException;
import com.kala.challenge.exception.TransformacionNoPermitidaException;
import org.junit.jupiter.api.Test;

import static com.kala.challenge.dominio.ResultadoBatalla.DERROTA;
import static com.kala.challenge.dominio.ResultadoBatalla.EMPATE;
import static com.kala.challenge.dominio.ResultadoBatalla.VICTORIA;
import static com.kala.challenge.dominio.TipoCivilizacion.BIZANTINO;
import static com.kala.challenge.dominio.TipoCivilizacion.CHINO;
import static com.kala.challenge.dominio.TipoCivilizacion.INGLES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EjercitoTest {

    @Test
    void unEjercitoNaceConMilMonedasYSinBatallas() {
        Ejercito ejercito = Ejercito.crear(CHINO);

        assertEquals(1000, ejercito.getOro());
        assertTrue(ejercito.getHistorial().isEmpty());
    }

    @Test
    void laCantidadInicialDeUnidadesDependeDeLaCivilizacion() {
        Ejercito chinos = Ejercito.crear(CHINO);
        Ejercito ingleses = Ejercito.crear(INGLES);
        Ejercito bizantinos = Ejercito.crear(BIZANTINO);

        assertEquals(2, obtenerCantidad(chinos, Piquero.class));
        assertEquals(25, obtenerCantidad(chinos, Arquero.class));
        assertEquals(2, obtenerCantidad(chinos, Caballero.class));
        assertEquals(300, chinos.puntos());

        assertEquals(10, obtenerCantidad(ingleses, Piquero.class));
        assertEquals(10, obtenerCantidad(ingleses, Arquero.class));
        assertEquals(10, obtenerCantidad(ingleses, Caballero.class));
        assertEquals(350, ingleses.puntos());

        assertEquals(5, obtenerCantidad(bizantinos, Piquero.class));
        assertEquals(8, obtenerCantidad(bizantinos, Arquero.class));
        assertEquals(15, obtenerCantidad(bizantinos, Caballero.class));
        assertEquals(405, bizantinos.puntos());
    }

    @Test
    void puedenCoexistirDosEjercitosDeLaMismaCivilizacion() {
        Ejercito uno = Ejercito.crear(INGLES);
        Ejercito otro = Ejercito.crear(INGLES);

        assertEquals(INGLES, uno.getCivilizacion().getTipo());
        assertEquals(INGLES, otro.getCivilizacion().getTipo());
        assertFalse(uno.getId().equals(otro.getId()));
    }

    @Test
    void cadaUnidadAportaLosPuntosDeSuTipo() {
        assertEquals(5, new Piquero().getPuntos());
        assertEquals(10, new Arquero().getPuntos());
        assertEquals(20, new Caballero().getPuntos());
    }

    @Test
    void entrenarSumaPuntosYDescuentaElCosto() {
        Ejercito ejercito = Ejercito.crear(CHINO);
        Piquero piquero = obtenerPrimeraUnidad(ejercito, Piquero.class);
        Arquero arquero = obtenerPrimeraUnidad(ejercito, Arquero.class);
        Caballero caballero = obtenerPrimeraUnidad(ejercito, Caballero.class);

        ejercito.entrenar(piquero);
        ejercito.entrenar(arquero);
        ejercito.entrenar(caballero);

        assertEquals(8, piquero.getPuntos());
        assertEquals(17, arquero.getPuntos());
        assertEquals(30, caballero.getPuntos());
        assertEquals(940, ejercito.getOro());
    }

    @Test
    void noSePuedeEntrenarSinOroSuficiente() {
        Ejercito ejercito = Ejercito.crear(CHINO);
        Caballero caballero = obtenerPrimeraUnidad(ejercito, Caballero.class);
        for (int i = 0; i < 33; i++) {
            ejercito.entrenar(caballero);
        }

        assertThrows(OroInsuficienteException.class, () -> ejercito.entrenar(caballero));
        assertEquals(10, ejercito.getOro());
        assertEquals(350, caballero.getPuntos());
    }

    @Test
    void unPiqueroSeTransformaEnArqueroYUnArqueroEnCaballero() {
        Ejercito ejercito = Ejercito.crear(CHINO);
        Piquero piquero = obtenerPrimeraUnidad(ejercito, Piquero.class);
        ejercito.entrenar(piquero);

        ejercito.transformar(piquero);

        assertFalse(ejercito.getUnidades().contains(piquero));
        assertEquals(1, obtenerCantidad(ejercito, Piquero.class));
        assertEquals(26, obtenerCantidad(ejercito, Arquero.class));
        assertEquals(960, ejercito.getOro());
        assertInstanceOf(Arquero.class, ejercito.getUnidades().getFirst());
        assertEquals(10, ejercito.getUnidades().getFirst().getPuntos());

        Arquero arquero = obtenerPrimeraUnidad(ejercito, Arquero.class);
        ejercito.transformar(arquero);

        assertFalse(ejercito.getUnidades().contains(arquero));
        assertEquals(3, obtenerCantidad(ejercito, Caballero.class));
        assertEquals(920, ejercito.getOro());
    }

    @Test
    void unCaballeroNoSePuedeTransformar() {
        Ejercito ejercito = Ejercito.crear(CHINO);
        Caballero caballero = obtenerPrimeraUnidad(ejercito, Caballero.class);

        assertThrows(TransformacionNoPermitidaException.class, () -> ejercito.transformar(caballero));
        assertEquals(1000, ejercito.getOro());
        assertTrue(ejercito.getUnidades().contains(caballero));
    }

    @Test
    void ganaElEjercitoConMasPuntosYElPerdedorPierdeSusDosUnidadesMasFuertes() {
        Ejercito ingleses = Ejercito.crear(INGLES);
        Ejercito chinos = Ejercito.crear(CHINO);

        ingleses.atacar(chinos);

        assertEquals(1100, ingleses.getOro());
        assertEquals(1000, chinos.getOro());
        assertEquals(0, obtenerCantidad(chinos, Caballero.class));
        assertEquals(27, chinos.getUnidades().size());

        Batalla victoria = ingleses.getHistorial().getFirst();
        Batalla derrota = chinos.getHistorial().getFirst();
        assertEquals(VICTORIA, victoria.getResultado());
        assertEquals(DERROTA, derrota.getResultado());
        assertTrue(victoria.getUnidadesPerdidas().isEmpty());
        assertEquals(2, derrota.getUnidadesPerdidas().size());
        assertTrue(derrota.getUnidadesPerdidas().stream().allMatch(Caballero.class::isInstance));
        assertEquals(chinos.getId(), victoria.getIdOponente());
        assertEquals(ingleses.getId(), derrota.getIdOponente());
    }

    @Test
    void unEjercitoPuedeAtacarAOtroDeLaMismaCivilizacion() {
        Ejercito atacante = Ejercito.crear(BIZANTINO);
        Ejercito defensor = Ejercito.crear(BIZANTINO);
        atacante.entrenar(obtenerPrimeraUnidad(atacante, Caballero.class));

        atacante.atacar(defensor);

        assertEquals(VICTORIA, atacante.getHistorial().getFirst().getResultado());
        assertEquals(DERROTA, defensor.getHistorial().getFirst().getResultado());
        assertEquals(BIZANTINO, atacante.getHistorial().getFirst().getCivilizacionOponente().getTipo());
    }

    @Test
    void enUnEmpateCadaEjercitoPierdeUnaUnidadYNadieGanaOro() {
        Ejercito atacante = Ejercito.crear(INGLES);
        Ejercito defensor = Ejercito.crear(INGLES);

        atacante.atacar(defensor);

        assertEquals(29, atacante.getUnidades().size());
        assertEquals(29, defensor.getUnidades().size());
        assertEquals(1000, atacante.getOro());
        assertEquals(1000, defensor.getOro());

        Batalla batallaAtacante = atacante.getHistorial().getFirst();
        Batalla batallaDefensor = defensor.getHistorial().getFirst();
        assertEquals(EMPATE, batallaAtacante.getResultado());
        assertEquals(EMPATE, batallaDefensor.getResultado());
        assertEquals(1, batallaAtacante.getUnidadesPerdidas().size());
        assertEquals(1, batallaDefensor.getUnidadesPerdidas().size());
        assertFalse(atacante.getUnidades().contains(batallaAtacante.getUnidadesPerdidas().getFirst()));
        assertFalse(defensor.getUnidades().contains(batallaDefensor.getUnidadesPerdidas().getFirst()));
    }

    private long obtenerCantidad(Ejercito ejercito, Class<?> tipo) {
        return ejercito.getUnidades().stream().filter(tipo::isInstance).count();
    }

    private <T extends Unidad> T obtenerPrimeraUnidad(Ejercito ejercito, Class<T> tipo) {
        return ejercito.getUnidades().stream()
                .filter(tipo::isInstance)
                .map(tipo::cast)
                .findFirst()
                .orElseThrow();
    }

}
