package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Entidad JPA que representa un Lote de Cocción (Batch).
 *
 * Un lote es el registro real de una sesión de elaboración vinculada
 * a una receta. Almacena las variables de control que el cervecero
 * mide durante el día de cocción (pH, densidades, litros) y calcula
 * automáticamente la eficiencia real del equipo.
 *
 * Relación:
 *  - Muchos Lotes pueden pertenecer a una misma Receta (@ManyToOne).
 *    Esto permite comparar múltiples cocciones de la misma receta
 *    y evaluar la repetibilidad del proceso.
 */
@Entity
@Table(name = "lotes")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Lote {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Número de lote, generado automáticamente con formato YYYYMMDD + secuencia.
     * Ej: 20260607 para el primer lote del 7 de junio de 2026.
     * El service se encarga de asignar este valor antes de persistir.
     */
    @Column(name = "nro_lote", nullable = false)
    private Integer nroLote;

    @Column(name = "fecha_coccion", nullable = false)
    private LocalDate fechaCoccion;

    /**
     * FK hacia la Receta que se está cocinando.
     * EAGER porque siempre necesitamos la receta al operar sobre un lote
     * (para calcular la eficiencia necesitamos acceder a las maltas).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    // ── Variables de control del macerado ────────────────────────────────────

    /**
     * pH medido a los 15 minutos de comenzar el macerado.
     * Rango óptimo: 5.2 - 5.6
     * Un pH fuera de rango afecta la actividad enzimática y puede
     * producir astringencia (alto) o bajo rendimiento (bajo).
     */
    @Column(name = "ph_macerado")
    private Double phMacerado;

    /**
     * pH medido durante el lavado de granos (sparging).
     * Debe mantenerse por debajo de 6.0 para evitar extraer taninos.
     */
    @Column(name = "ph_lavado")
    private Double phLavado;

    // ── Variables de control pre-hervor ──────────────────────────────────────

    /**
     * Densidad específica medida antes de iniciar el hervor.
     * Permite estimar si la extracción fue adecuada.
     * Ej: 1.042
     */
    @Column(name = "densidad_pre_hervor")
    private Double densidadPreHervor;

    /**
     * Volumen en litros en la olla antes de iniciar el hervor.
     * Se usa junto con densidadPreHervor para verificar la extracción.
     */
    @Column(name = "litros_pre_hervor")
    private Double litrosPreHervor;

    // ── Variables de control post-hervor (OG real) ──────────────────────────

    /**
     * Densidad Inicial Real (OG real) medida después del hervor y antes
     * de inocular la levadura. Esta es la medición definitiva del lote.
     * Ej: 1.058
     */
    @Column(name = "densidad_inicial_real")
    private Double densidadInicialReal;

    /**
     * Volumen final real en litros después del hervor, medido en el
     * fermentador. Considera las pérdidas por trub, evaporación, etc.
     */
    @Column(name = "litros_finales_real")
    private Double litrosFinalesReal;

    /**
     * Densidad Final Real (FG real) medida al terminar la fermentación.
     * Ej: 1.012
     */
    @Column(name = "densidad_final_real")
    private Double densidadFinalReal;

    /**
     * Alcohol By Volume real del lote (%), calculado como (OG_real - FG_real) * 131.25.
     */
    @Column(name = "abv_real")
    private Double abvReal;

    // ── Cálculos automáticos ────────────────────────────────────────────────

    /**
     * Eficiencia real del equipo (%), calculada automáticamente por el
     * service a partir de la densidadInicialReal, litrosFinalesReal y
     * el total de kg de maltas de la receta asociada.
     *
     * Fórmula: (puntos de densidad reales × litros reales) /
     *          (kg de malta × potencial de extracto máximo) × 100
     */
    @Column(name = "eficiencia_equipo_real")
    private Double eficienciaEquipoReal;

    // ── Notas del cervecero ─────────────────────────────────────────────────

    /**
     * Campo libre para que el cervecero anote contratiempos, observaciones
     * y alertas automáticas del sistema (ej: pH fuera de rango).
     */
    @Column(columnDefinition = "TEXT")
    private String comentarios;

    // ── Hooks de ciclo de vida JPA ──────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        if (this.fechaCoccion == null) {
            this.fechaCoccion = LocalDate.now();
        }
        if (this.nroLote == null) {
            this.nroLote = Integer.parseInt(
                    this.fechaCoccion.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            );
        }
    }

    @Override
    public String toString() {
        String recetaNombre = receta != null ? receta.getNombre() : "Sin receta";
        return "Lote #" + nroLote + " — " + recetaNombre + " (" + fechaCoccion + ")";
    }
}
