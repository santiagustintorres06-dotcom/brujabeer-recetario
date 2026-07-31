package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RecetaLupulo {

    @EqualsAndHashCode.Include
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

    /**
     * Tipo de adición del lúpulo según el momento del proceso.
     * Determina tanto el rol organoléptico como el cálculo de IBU.
     */
    public enum UsoLupulo {
        HERVOR,       // En el hervor. Tiempo > 0. Aporta amargor (isomerización de α-ácidos).
        WHIRLPOOL,    // Post-hervor / flameout. Aprovecha el calor residual (≥ 70°C).
                      // Aporta aroma y sabor con mínima isomerización.
        DRY_HOPPING   // En frío, durante o después de fermentación. tiempoMinutos = null.
                      // Solo aroma y sabor. No aporta IBU.
    }
}
