package com.brujabeer.recetario.ui.util;

import com.brujabeer.recetario.model.Malta;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

/**
 * Fila de datos para mostrar en el TableView de maltas.
 * Envuelve Malta + cantidad (g) + porcentaje en la receta.
 */
@Getter
public class RecetaMaltaRow {
    private final StringProperty nombre;
    private final IntegerProperty cantidad;
    private final DoubleProperty porcentaje;
    private final Malta malta; // Referencia a la malta original

    public RecetaMaltaRow(Malta malta, Integer cantidadGramos, Double porcentajeReceta) {
        this.malta = malta;
        this.nombre = new SimpleStringProperty(malta.getNombre());
        this.cantidad = new SimpleIntegerProperty(cantidadGramos != null ? cantidadGramos : 0);
        this.porcentaje = new SimpleDoubleProperty(porcentajeReceta != null ? porcentajeReceta : 0.0);
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

    public Double getPorcentaje() {
        return porcentaje.get();
    }

    public void setPorcentaje(Double value) {
        porcentaje.set(value);
    }

    @Override
    public String toString() {
        return nombre.get() + " (" + cantidad.get() + "g)";
    }
}

