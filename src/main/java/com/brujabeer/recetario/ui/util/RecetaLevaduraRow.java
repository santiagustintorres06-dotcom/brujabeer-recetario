package com.brujabeer.recetario.ui.util;

import com.brujabeer.recetario.model.Levadura;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

/**
 * Fila de datos para mostrar en el TableView de levaduras.
 * Envuelve Levadura + cantidad (g).
 */
@Getter
public class RecetaLevaduraRow {
    private final StringProperty nombre;
    private final IntegerProperty cantidad;
    private final Levadura levadura; // Referencia a la levadura original

    public RecetaLevaduraRow(Levadura levadura, Integer cantidadGramos) {
        this.levadura = levadura;
        this.nombre = new SimpleStringProperty(levadura.getNombre());
        this.cantidad = new SimpleIntegerProperty(cantidadGramos != null ? cantidadGramos : 0);
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

    @Override
    public String toString() {
        return nombre.get() + " (" + cantidad.get() + "g)";
    }
}

