package com.brujabeer.recetario.ui.util;

import com.brujabeer.recetario.model.Lupulo;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

/**
 * Fila de datos para mostrar en el TableView de lúpulos.
 * Envuelve Lúpulo + cantidad (g) + tiempo (minutos) + uso (bittering, aroma, etc).
 */
@Getter
public class RecetaLupuloRow {
    private final StringProperty nombre;
    private final IntegerProperty cantidad;
    private final IntegerProperty tiempo;
    private final StringProperty uso;
    private final Lupulo lupulo; // Referencia al lúpulo original

    public RecetaLupuloRow(Lupulo lupulo, Integer cantidadGramos, Integer tiempoMinutos, String uso) {
        this.lupulo = lupulo;
        this.nombre = new SimpleStringProperty(lupulo.getNombre());
        this.cantidad = new SimpleIntegerProperty(cantidadGramos != null ? cantidadGramos : 0);
        this.tiempo = new SimpleIntegerProperty(tiempoMinutos != null ? tiempoMinutos : 0);
        this.uso = new SimpleStringProperty(uso != null ? uso : "");
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String value) {
        nombre.set(value);
    }

    public Integer getCantidad() {
        return cantidad.get();
    }

    public void setCantidad(Integer value) {
        cantidad.set(value);
    }

    public Integer getTiempo() {
        return tiempo.get();
    }

    public void setTiempo(Integer value) {
        tiempo.set(value);
    }

    public String getUso() {
        return uso.get();
    }

    public void setUso(String value) {
        uso.set(value);
    }

    @Override
    public String toString() {
        return nombre.get() + " (" + cantidad.get() + "g @ " + tiempo.get() + "min)";
    }
}

