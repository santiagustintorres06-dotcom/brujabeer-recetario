package com.brujabeer.recetario.util;

/**
 * Constantes cerveceras documentadas.
 *
 * Centraliza los valores numéricos ("magic numbers") usados en los
 * cálculos físico-químicos del motor de BrujaBeer.
 */
public final class BrewConstants {

    private BrewConstants() { /* No instanciable */ }

    // ── Conversión de unidades ──────────────────────────────────────────

    /** Libras por kilogramo. */
    public static final double LBS_PER_KG = 2.20462;

    /** Galones US por litro. */
    public static final double GAL_PER_LITER = 1.0 / 3.78541;

    /** Gramos por libra. */
    public static final double GRAMS_PER_LB = 453.592;

    // ── Extracto y eficiencia ──────────────────────────────────────────

    /**
     * Puntos de gravedad máximos por kg por litro.
     * Equivale a ~46 PPG convertido a métrico:
     * 46 PPG × 2.20462 lb/kg × 3.78541 gal/L ≈ 384
     */
    public static final double PUNTOS_MAX_POR_KG_LITRO = 384.0;

    /** Eficiencia de brewhouse por defecto si no se especifica (%). */
    public static final double EFICIENCIA_DEFAULT = 72.0;

    /** Rendimiento de malta por defecto si no se especifica (%). */
    public static final double RENDIMIENTO_MALTA_DEFAULT = 75.0;

    // ── pH ──────────────────────────────────────────────────────────────

    /** pH de macerado óptimo mínimo. */
    public static final double PH_MACERADO_MIN = 5.2;

    /** pH de macerado óptimo máximo. */
    public static final double PH_MACERADO_MAX = 5.6;

    /** pH de lavado máximo antes de riesgo de taninos. */
    public static final double PH_LAVADO_MAX = 6.0;

    // ── Fórmulas de cálculo ────────────────────────────────────────────

    /** Factor ABV: ABV = (OG - FG) × 131.25 */
    public static final double FACTOR_ABV = 131.25;

    /** Constante de Morey para conversión MCU → SRM. */
    public static final double MOREY_COEF = 1.4922;
    public static final double MOREY_EXP = 0.6859;

    /** Factor de conversión SRM → EBC. */
    public static final double SRM_TO_EBC = 1.97;

    // ── Tinseth IBU ────────────────────────────────────────────────────

    /** Constantes de la fórmula de Tinseth para bigness factor. */
    public static final double TINSETH_BIGNESS_COEF = 1.65;
    public static final double TINSETH_BIGNESS_BASE = 0.000125;

    /** Constante de decay del boil time factor. */
    public static final double TINSETH_BOIL_DECAY = -0.04;
    public static final double TINSETH_BOIL_DIVISOR = 4.15;

    // ── Carbonatación (Priming) ─────────────────────────────────────────

    /** Factor de carbonatación para dextrosa (g/L/vol). */
    public static final double PRIMING_FACTOR_DEXTROSA = 4.15;

    /** Coeficientes de CO2 residual (fórmula empírica con temp en °F). */
    public static final double CO2_RESIDUAL_A = 3.0378;
    public static final double CO2_RESIDUAL_B = 0.050062;
    public static final double CO2_RESIDUAL_C = 0.00026555;

    // ── Atenuación por defecto ──────────────────────────────────────────

    /** Atenuación mínima por defecto (%) si la levadura no tiene datos. */
    public static final int ATENUACION_MIN_DEFAULT = 73;
    public static final int ATENUACION_MAX_DEFAULT = 77;

    /** Alpha ácido por defecto si el lúpulo no tiene datos (%). */
    public static final double ALPHA_ACIDO_DEFAULT = 5.0;

    /** OG por defecto para cálculos de IBU si no se calcula previamente. */
    public static final double OG_DEFAULT = 1.050;

    /** Color EBC por defecto si la malta no tiene datos. */
    public static final double COLOR_EBC_DEFAULT = 5.0;
}
