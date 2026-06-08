package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Catálogo de Lúpulos disponibles.
 *
 * Los datos de porcentajeAlpha son los más críticos para el cálculo
 * de IBU. El porcentaje varía entre cosechas, por lo que este valor
 * representa el promedio declarado por el proveedor.
 */
@Entity
@Table(name = "lupulos")
@Getter
@Setter
@NoArgsConstructor
public class Lupulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    /** País de origen. Ej: Estados Unidos, Alemania, Nueva Zelanda, Argentina */
    @Column(length = 50)
    private String origen;

    /**
     * Porcentaje de ácidos Alpha (%).
     * CLAVE para el cálculo de IBU (amargor).
     * Rango típico: 3% (aromatic) a 18% (high-alpha bittering).
     */
    @Column(name = "porcentaje_alpha")
    private Double porcentajeAlpha;

    /**
     * Porcentaje de ácidos Beta (%).
     * Contribuye al amargor a largo plazo durante la maduración.
     */
    @Column(name = "porcentaje_beta")
    private Double porcentajeBeta;

    /**
     * Porcentaje de Cohumulona (%).
     * Menor cohumulona = amargor más suave y redondo.
     */
    @Column(name = "porcentaje_cohumulona")
    private Double porcentajeCohumulona;

    /** Notas de aroma/sabor. Ej: "Cítrico, Tropical, Pino, Floral" */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Override
    public String toString() {
        return nombre + (origen != null ? " [" + origen + "]" : "")
                + (porcentajeAlpha != null ? " α:" + porcentajeAlpha + "%" : "");
    }
}
