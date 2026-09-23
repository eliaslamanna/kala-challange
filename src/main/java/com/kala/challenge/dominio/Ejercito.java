package com.kala.challenge.dominio;

import com.kala.challenge.exception.OroInsuficienteException;
import com.kala.challenge.exception.TransformacionNoPermitidaException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.kala.challenge.dominio.ResultadoBatalla.*;
import static java.util.Comparator.comparingInt;
import static java.util.UUID.randomUUID;
import static java.util.stream.Stream.generate;

public final class Ejercito {

    static final int ORO_INICIAL = 1000;

    static final int ORO_DEL_GANADOR = 100;

    static final int UNIDADES_QUE_PIERDE_EL_PERDEDOR = 2;

    @Getter
    private UUID id;

    @Getter
    private Civilizacion civilizacion;

    @Getter
    private int oro = ORO_INICIAL;

    @Getter
    private List<Unidad> unidades;

    @Getter
    private List<Batalla> historial = new ArrayList<>();

    private Ejercito(UUID id, Civilizacion civilizacion, List<Unidad> unidades) {
        this.id = id;
        this.civilizacion = civilizacion;
        this.unidades = unidades;
    }

    public static Ejercito crear(TipoCivilizacion tipo) {
        Civilizacion civilizacion = Civilizacion.crear(tipo);
        List<Unidad> unidades = new ArrayList<>();
        generate(Piquero::new).limit(civilizacion.getCantidadPiqueros()).forEach(unidades::add);
        generate(Arquero::new).limit(civilizacion.getCantidadArqueros()).forEach(unidades::add);
        generate(Caballero::new).limit(civilizacion.getCantidadCaballeros()).forEach(unidades::add);
        return new Ejercito(randomUUID(), civilizacion, unidades);
    }

    public int puntos() {
        return unidades.stream().mapToInt(Unidad::getPuntos).sum();
    }

    public void atacar(Ejercito defensor) {
        if (id.equals(defensor.getId())) {
            throw new IllegalArgumentException("Un ejército no puede atacarse a sí mismo");
        }

        UUID idBatalla = randomUUID();
        if (puntos() == defensor.puntos()) {
            empatar(idBatalla, defensor);
            return;
        }

        Ejercito ganador = puntos() > defensor.puntos() ? this : defensor;
        Ejercito perdedor = ganador == this ? defensor : this;
        List<Unidad> unidadesPerdidas = perdedor.perderUnidadesConMayorPuntaje();
        ganador.recibirOro(ORO_DEL_GANADOR);
        ganador.registrar(Batalla.crear(idBatalla, perdedor, VICTORIA, List.of()));
        perdedor.registrar(Batalla.crear(idBatalla, ganador, DERROTA, unidadesPerdidas));
    }

    private void empatar(UUID idBatalla, Ejercito defensor) {
        List<Unidad> unidadesPerdidasPropias = perderUnidadAleatoria();
        List<Unidad> unidadesPerdidasDelDefensor = defensor.perderUnidadAleatoria();
        registrar(Batalla.crear(idBatalla, defensor, EMPATE, unidadesPerdidasPropias));
        defensor.registrar(Batalla.crear(idBatalla, this, EMPATE, unidadesPerdidasDelDefensor));
    }

    public void entrenar(Unidad unidad) {
        exigirQuePertenezca(unidad);
        pagar(unidad.costoDelEntrenamiento());
        unidad.entrenar();
    }

    public void transformar(Unidad unidad) {
        exigirQuePertenezca(unidad);
        Transformacion transformacion = unidad.transformacion().orElseThrow(() -> new TransformacionNoPermitidaException(unidad));
        pagar(transformacion.getCosto());
        unidades.set(unidades.indexOf(unidad), transformacion.getUnidadResultante());
    }

    private void recibirOro(int cantidad) {
        oro += cantidad;
    }

    private List<Unidad> perderUnidadesConMayorPuntaje() {
        List<Unidad> bajas = unidades.stream()
                .sorted(comparingInt(Unidad::getPuntos).reversed())
                .limit(UNIDADES_QUE_PIERDE_EL_PERDEDOR)
                .toList();
        unidades.removeAll(bajas);
        return bajas;
    }

   private List<Unidad> perderUnidadAleatoria() {
        if (unidades.isEmpty()) {
            return List.of();
        }
        return List.of(unidades.remove(new Random().nextInt(unidades.size())));
    }

    private void registrar(Batalla batalla) {
        historial.add(batalla);
    }

    private void pagar(int costo) {
        if (oro < costo) {
            throw new OroInsuficienteException(
                    "Oro insuficiente: se necesitan %d monedas y hay %d".formatted(costo, oro));
        }
        oro -= costo;
    }

    private void exigirQuePertenezca(Unidad unidad) {
        if (!unidades.contains(unidad)) {
            throw new IllegalArgumentException("La unidad no pertenece a este ejército");
        }
    }

}
