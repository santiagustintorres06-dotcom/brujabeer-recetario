package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * Catálogo de Maltas disponibles.
 *
 * Esta entidad representa los ingredientes de malta que pueden ser
 * seleccionados al construir una receta. Es independiente de la receta
 * (es un maestro de datos, no una línea de receta).
 *
 * La conexión con Receta se hace a través de RecetaMalta,
 * donde se especifica la cantidad utilizada.
 */
@Entity
@Table(name = "maltas")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Malta {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    /**
     * Tipo de malta según su procesamiento.
     * Ej: Base, Caramelo/Crystal, Tostada, Ahumada, Ácida, Especial
     */
    @Column(length = 50)
    private String tipo;

    /** Marca/proveedor. Ej: Weyermann, Patagonia Maltera, Crisp, Bestmalz */
    @Column(length = 100)
    private String marca;

    /** Color en EBC. Mayor valor = malta más oscura. */
    @Column(name = "color_ebc")
    private Double colorEbc;

    /**
     * Rendimiento/extracto potencial en porcentaje (%).
     * Típicamente entre 70% y 85%.
     * Se usa para calcular la OG estimada.
     */
    @Column(name = "rendimiento_porcentaje")
    private Double rendimientoPorcentaje;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Override
    public String toString() {
        return nombre + (marca != null ? " (" + marca + ")" : "");
    }
}
