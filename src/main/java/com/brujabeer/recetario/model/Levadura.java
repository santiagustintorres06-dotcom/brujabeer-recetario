package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Catálogo de Levaduras disponibles.
 *
 * La atenuación (min/max) es fundamental para estimar la FG y por tanto el ABV.
 * El rango de temperatura define el perfil de fermentación de la receta.
 */
@Entity
@Table(name = "levaduras")
@Getter
@Setter
@NoArgsConstructor
public class Levadura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoLevadura tipo;

    /** Marca/laboratorio. Ej: Fermentis, Lallemand, Wyeast, White Labs */
    @Column(length = 100)
    private String marca;

    /**
     * Código de cepa del fabricante.
     * Ej: US-05 (Fermentis), WY1056 (Wyeast), BE-256 (Fermentis belga)
     */
    @Column(name = "codigo_cepa", length = 20)
    private String codigoCepa;

    /** Atenuación aparente mínima (%). Ej: 73 */
    @Column(name = "atenuacion_min")
    private Integer atenuacionMin;

    /** Atenuación aparente máxima (%). Ej: 77 */
    @Column(name = "atenuacion_max")
    private Integer atenuacionMax;

    /** Temperatura mínima de fermentación recomendada (°C). */
    @Column(name = "temp_min_celsius")
    private Integer tempMinCelsius;

    /** Temperatura máxima de fermentación recomendada (°C). */
    @Column(name = "temp_max_celsius")
    private Integer tempMaxCelsius;

    /** Tolerancia máxima al alcohol (%ABV). */
    @Column(name = "tolerancia_alcohol")
    private Double toleranciaAlcohol;

    /** Perfil de sabor, floculación, claridad, etc. */
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // ────────────────────────────────────────────────────────────────────────

    /**
     * Clasificación de levaduras según su temperatura y estilo de fermentación.
     * PERSISTIDO como String en BD (EnumType.STRING) para mayor legibilidad.
     */
    public enum TipoLevadura {
        ALE,       // Alta fermentación, 15-24°C
        LAGER,     // Baja fermentación, 7-13°C
        BELGA,     // Alta fermentación con ésteres y fenoles característicos
        SALVAJE,   // Brettanomyces y similares, para estilos silvestres
        MEAD,      // Para hidromiel
        CIDER      // Para sidra
    }

    @Override
    public String toString() {
        String codigo = (codigoCepa != null) ? " [" + codigoCepa + "]" : "";
        String marca_ = (marca != null) ? " - " + marca : "";
        return nombre + codigo + marca_;
    }
}
