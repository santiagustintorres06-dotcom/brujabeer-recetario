package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TABLA INTERMEDIA: receta_lupulos
 *
 * El tiempo de adición y el tipo de uso son esenciales para
 * el cálculo de IBU. Los lúpulos añadidos al inicio del hervor
 * contribuyen más amargor; los añadidos al final, más aroma.
 */
@Entity
@Table(name = "receta_lupulos")
@Getter
@Setter
@NoArgsConstructor
public class RecetaLupulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lupulo_id", nullable = false)
    private Lupulo lupulo;

    /** Gramos de este lúpulo en la adición. */
    @Column(name = "cantidad_gramos", nullable = false)
    private Double cantidadGramos;

    /**
     * Minutos antes del fin del hervor en que se agrega.
     * 60 min = amargante, 15 min = sabor, 0 min = aroma/whirlpool.
     * Nulo si es DRY_HOP (se añade en fermentación, no en hervor).
     */
    @Column(name = "tiempo_minutos")
    private Integer tiempoMinutos;

    /**
     * Tipo de adición. Determina el rol del lúpulo en la receta.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "uso", length = 20)
    private UsoLupulo uso;

    // ────────────────────────────────────────────────────────────────────────

    public enum UsoLupulo {
        BITTERING,   // Amargante: 30-90 min antes del fin del hervor
        FLAVOR,      // Sabor: 15-30 min
        AROMA,       // Aroma: 0-15 min
        WHIRLPOOL,   // Adición al flameout, aprovecha el calor residual
        DRY_HOP      // En frío, durante/después de la fermentación. tiempoMinutos = null
    }
}
