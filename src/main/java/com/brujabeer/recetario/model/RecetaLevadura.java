package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * TABLA INTERMEDIA: receta_levaduras
 *
 * Generalmente una receta usa una sola levadura, pero el modelo
 * soporta co-pitching (dos cepas) que es válido en estilos mixtos.
 */
@Entity
@Table(name = "receta_levaduras")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RecetaLevadura {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "levadura_id", nullable = false)
    private Levadura levadura;

    /** Gramos de levadura seca (si aplica). */
    @Column(name = "cantidad_gramos")
    private Double cantidadGramos;

    /** Formato de la levadura: SECA (sobres) o LIQUIDA (viales/smack packs). */
    @Enumerated(EnumType.STRING)
    @Column(name = "formato", length = 10)
    private FormatoLevadura formato;

    // ────────────────────────────────────────────────────────────────────────

    /**
     * Formato de presentación de la levadura.
     * Determina las unidades de dosificación y el manejo pre-inoculación.
     */
    public enum FormatoLevadura {
        SECA,     // Levadura seca en sobres. Ej: Fermentis US-05 (11.5g), SafAle S-04
        LIQUIDA   // Levadura líquida en viales o smack packs. Ej: Wyeast 1056, White Labs WLP001
    }
}
