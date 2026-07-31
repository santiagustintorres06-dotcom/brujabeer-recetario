package com.brujabeer.recetario.service.impl;

import com.brujabeer.recetario.model.*;
import com.brujabeer.recetario.repository.*;
import com.brujabeer.recetario.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Implementación concreta de RecetaService.
 *
 * @Transactional a nivel de clase: TODOS los métodos corren dentro
 * de una transacción de BD. Si algo falla, se hace ROLLBACK automático.
 *
 * Los métodos de solo lectura usan readOnly=true, que es un hint
 * para Hibernate que optimiza el uso de la sesión (no dirty-checking).
 */
@Service
@Transactional
public class RecetaServiceImpl implements RecetaService {

    /**
     * Constante cervecera: rendimiento máximo teórico de extracto
     * expresado en puntos de gravedad por kilogramo por litro.
     *
     * Equivale a ~46 PPG (points per pound per gallon) convertido a métrico.
     * 1 kg de azúcar puro disuelta en 1 litro produce ~384 puntos de gravedad.
     * Esta constante se usa como denominador al calcular la eficiencia real.
     */
    private static final double PUNTOS_MAXIMO_POR_KG_LITRO = 384.0;

    /** Rango óptimo de pH de macerado para actividad enzimática. */
    private static final double PH_OPTIMO_MIN = 5.2;
    private static final double PH_OPTIMO_MAX = 5.6;

    @Autowired
    private RecetaRepository recetaRepository;

    @Autowired
    private MaltaRepository maltaRepository;

    @Autowired
    private LupuloRepository lupuloRepository;

    @Autowired
    private LevaduraRepository levaduraRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private AguaPerfilRepository aguaPerfilRepository;

    // ── CRUD de Recetas ──────────────────────────────────────────────────────

    @Override
    public Receta guardar(Receta receta) {
        return recetaRepository.save(receta);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Receta> buscarPorId(Long id) {
        return recetaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Receta> buscarConIngredientes(Long id) {
        Optional<Receta> opt = recetaRepository.findById(id);
        opt.ifPresent(receta -> {
            Hibernate.initialize(receta.getMaltas());
            Hibernate.initialize(receta.getLupulos());
            Hibernate.initialize(receta.getLevaduras());
            Hibernate.initialize(receta.getPasosMacerado());
            Hibernate.initialize(receta.getMiscelaneos());
        });
        return opt;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receta> listarTodas() {
        return recetaRepository.findAllByOrderByFechaCreacionDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receta> listarTodasConIngredientes() {
        List<Receta> recetas = recetaRepository.findAllByOrderByFechaCreacionDesc();
        recetas.forEach(receta -> {
            Hibernate.initialize(receta.getMaltas());
            Hibernate.initialize(receta.getLupulos());
            Hibernate.initialize(receta.getLevaduras());
        });
        return recetas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receta> buscarPorNombre(String nombre) {
        return recetaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public void eliminar(Long id) {
        recetaRepository.deleteById(id);
    }

    // ── Catálogos ────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<Malta> listarMaltas() {
        return maltaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lupulo> listarLupulos() {
        return lupuloRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Levadura> listarLevaduras() {
        return levaduraRepository.findAll();
    }

    // ── Módulo de Lotes de Cocción ──────────────────────────────────────────

    /**
     * Registra un lote de cocción con cálculos automáticos:
     *
     * 1. CÁLCULO DE EFICIENCIA REAL:
     *    La eficiencia mide qué tan bien el equipo extrae azúcares de las maltas.
     *
     *    Fórmula (puntos de gravedad por litro):
     *      puntosReales = (densidadInicialReal - 1.000) × 1000
     *      puntosExtraidos = puntosReales × litrosFinalesReal
     *      puntosPotenciales = totalKgMaltas × PUNTOS_MAXIMO_POR_KG_LITRO
     *      eficiencia(%) = (puntosExtraidos / puntosPotenciales) × 100
     *
     *    Ejemplo: OG real = 1.052, litros finales = 20L, maltas = 5kg
     *      puntos = 52 × 20 = 1040
     *      potencial = 5 × 384 = 1920
     *      eficiencia = (1040/1920) × 100 = 54.17%
     *
     * 2. ALERTA DE pH:
     *    Si phMacerado < 5.2 o > 5.6 → agrega advertencia a comentarios.
     *
     * @param lote el lote con datos de control ingresados por el cervecero
     * @return el lote persistido con eficiencia calculada y alertas
     */
    @Override
    public Lote registrarCoccion(Lote lote) {
        if (lote.getReceta() != null && lote.getReceta().getId() != null) {
            buscarConIngredientes(lote.getReceta().getId()).ifPresent(lote::setReceta);
        }

        if (lote.getFechaCoccion() == null) {
            lote.setFechaCoccion(LocalDate.now());
        }

        if (lote.getNroLote() == null) {
            lote.setNroLote(Integer.parseInt(
                    lote.getFechaCoccion().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            ));
        }

        calcularEficienciaReal(lote);

        calcularAbvReal(lote);

        verificarPhMacerado(lote);

        verificarPhLavado(lote);

        return loteRepository.save(lote);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lote> listarLotes() {
        return loteRepository.findAllByOrderByFechaCoccionDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lote> listarLotesPorReceta(Receta receta) {
        return loteRepository.findByRecetaOrderByFechaCoccionDesc(receta);
    }

    // ── Métodos privados de cálculo cervecero ───────────────────────────────

    /**
     * Calcula la eficiencia real del equipo usando la fórmula de
     * puntos de gravedad.
     *
     * Solo calcula si existen los tres datos necesarios:
     *  - densidadInicialReal (OG medida)
     *  - litrosFinalesReal (volumen en fermentador)
     *  - maltas de la receta asociada (para totalizar kg)
     */
    private void calcularEficienciaReal(Lote lote) {
        if (lote.getDensidadInicialReal() == null
                || lote.getLitrosFinalesReal() == null
                || lote.getReceta() == null) {
            return;
        }

        Receta receta = lote.getReceta();
        if (receta.getMaltas() == null || receta.getMaltas().isEmpty()) {
            agregarComentario(lote,
                    "⚠️ ALERTA: La receta no tiene maltas registradas. " +
                    "No se pudo calcular la eficiencia real.");
            lote.setEficienciaEquipoReal(0.0);
            return;
        }

        double puntosExtraidosTotal = (lote.getDensidadInicialReal() - 1.0) * 1000.0 * lote.getLitrosFinalesReal();

        double puntosPotencialesTotal = 0.0;
        for (RecetaMalta rm : receta.getMaltas()) {
            double kg = (rm.getCantidadGramos() != null ? rm.getCantidadGramos() : 0.0) / 1000.0;
            double rendimiento = (rm.getMalta() != null && rm.getMalta().getRendimientoPorcentaje() != null)
                    ? rm.getMalta().getRendimientoPorcentaje() : 75.0;
            puntosPotencialesTotal += kg * (rendimiento / 100.0) * PUNTOS_MAXIMO_POR_KG_LITRO;
        }

        if (puntosPotencialesTotal <= 0.0) {
            lote.setEficienciaEquipoReal(0.0);
            return;
        }

        double eficiencia = (puntosExtraidosTotal / puntosPotencialesTotal) * 100.0;
        eficiencia = Math.round(eficiencia * 100.0) / 100.0;

        lote.setEficienciaEquipoReal(eficiencia);
    }

    /**
     * Calcula el ABV real (%) si el cervecero ingresó OG real y FG real.
     */
    private void calcularAbvReal(Lote lote) {
        if (lote.getDensidadInicialReal() != null && lote.getDensidadFinalReal() != null) {
            double og = lote.getDensidadInicialReal();
            double fg = lote.getDensidadFinalReal();
            if (og > fg && fg >= 0.990) {
                double abv = (og - fg) * 131.25;
                lote.setAbvReal(Math.round(abv * 100.0) / 100.0);
            }
        }
    }

    /**
     * Suma el total de kilogramos de maltas de una receta.
     * Las cantidades en RecetaMalta están en gramos, por lo que se dividen por 1000.
     */
    private double calcularTotalKgMaltas(Receta receta) {
        if (receta.getMaltas() == null || receta.getMaltas().isEmpty()) {
            return 0.0;
        }

        double totalGramos = 0.0;
        for (RecetaMalta rm : receta.getMaltas()) {
            if (rm.getCantidadGramos() != null) {
                totalGramos += rm.getCantidadGramos();
            }
        }
        return totalGramos / 1000.0;
    }

    /**
     * Verifica si el pH de macerado está fuera del rango óptimo (5.2 – 5.6)
     * y, en caso positivo, agrega una alerta descriptiva en los comentarios.
     */
    private void verificarPhMacerado(Lote lote) {
        if (lote.getPhMacerado() == null) {
            return;
        }

        double ph = lote.getPhMacerado();

        if (ph < PH_OPTIMO_MIN) {
            agregarComentario(lote,
                    "⚠️ ALERTA pH MACERADO: Valor medido " + String.format("%.2f", ph) +
                    " está POR DEBAJO del rango óptimo (" + PH_OPTIMO_MIN + " – " + PH_OPTIMO_MAX + "). " +
                    "Un pH bajo puede reducir la actividad de la beta-amilasa y generar un mosto menos fermentable. " +
                    "Considerar agregar sales de calcio o bicarbonato de sodio en futuras cocciones.");
        } else if (ph > PH_OPTIMO_MAX) {
            agregarComentario(lote,
                    "⚠️ ALERTA pH MACERADO: Valor medido " + String.format("%.2f", ph) +
                    " está POR ENCIMA del rango óptimo (" + PH_OPTIMO_MIN + " – " + PH_OPTIMO_MAX + "). " +
                    "Un pH alto puede extraer taninos y polifenoles, causando astringencia. " +
                    "Considerar agregar ácido láctico o ácido fosfórico al agua de macerado.");
        }
    }

    /**
     * Verifica si el pH de lavado supera 6.0 (umbral de extracción de taninos)
     * y agrega alerta correspondiente.
     */
    private void verificarPhLavado(Lote lote) {
        if (lote.getPhLavado() == null) {
            return;
        }

        if (lote.getPhLavado() > 6.0) {
            agregarComentario(lote,
                    "⚠️ ALERTA pH LAVADO: Valor medido " + String.format("%.2f", lote.getPhLavado()) +
                    " supera 6.0. Riesgo de extracción de taninos durante el sparging. " +
                    "Considerar acidificar el agua de lavado.");
        }
    }

    /**
     * Agrega un mensaje a los comentarios del lote, preservando los
     * comentarios previos ingresados por el cervecero.
     */
    private void agregarComentario(Lote lote, String mensaje) {
        String existente = lote.getComentarios();
        if (existente == null || existente.isBlank()) {
            lote.setComentarios(mensaje);
        } else {
            lote.setComentarios(existente + "\n" + mensaje);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    // SIMULADOR FÍSICO-QUÍMICO CERVECERO
    // ══════════════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<Equipo> listarEquipos() {
        return equipoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AguaPerfil> listarPerfilesAgua() {
        return aguaPerfilRepository.findAll();
    }

    /**
     * Calcula la Densidad Inicial (OG) estimada.
     *
     * Fórmula de puntos de gravedad con eficiencia de equipo:
     *
     *   Para cada malta:
     *     puntos = kg × (rendimiento/100) × 384 × (eficiencia/100)
     *
     *   OG = 1 + Σ(puntos) / litros / 1000
     *
     * Donde 384 = 46 PPG × 2.20462 lb/kg × 3.78541 gal/L
     * (conversión de Points Per Pound Per Gallon a sistema métrico)
     *
     * Ejemplo: 5kg Pilsner (80%), 20L, equipo 72%:
     *   puntos = 5 × 0.80 × 384 × 0.72 = 1105.92
     *   OG = 1 + 1105.92 / 20 / 1000 = 1.055
     */
    @Override
    public double calcularOG(List<RecetaMalta> maltas, double volumenLitros, double eficienciaBrewhouse) {
        if (maltas == null || maltas.isEmpty() || volumenLitros <= 0) {
            return 1.000;
        }

        double eficiencia = (eficienciaBrewhouse > 0) ? eficienciaBrewhouse : 72.0;
        double totalPuntos = 0.0;

        for (RecetaMalta rm : maltas) {
            double kg = (rm.getCantidadGramos() != null ? rm.getCantidadGramos() : 0.0) / 1000.0;
            double rendimiento = (rm.getMalta() != null && rm.getMalta().getRendimientoPorcentaje() != null)
                    ? rm.getMalta().getRendimientoPorcentaje() : 75.0;

            totalPuntos += kg * (rendimiento / 100.0) * PUNTOS_MAXIMO_POR_KG_LITRO * (eficiencia / 100.0);
        }

        return 1.0 + (totalPuntos / volumenLitros / 1000.0);
    }

    /**
     * Calcula IBU totales usando la fórmula de Tinseth (1997).
     *
     * Para cada adición de lúpulo (excepto DRY_HOPPING):
     *
     *   bignessFactor = 1.65 × 0.000125^(OG − 1)
     *   boilTimeFactor = (1 − e^(−0.04 × minutos)) / 4.15
     *   utilización = bignessFactor × boilTimeFactor
     *
     *   IBU_adición = (gramos × alpha%/100 × utilización × 1000) / litros
     *   IBU_total = Σ IBU_adición
     *
     * El factor de bigness penaliza mostos de alta gravedad (más azúcar
     * = menor isomerización de ácidos alfa). El boilTimeFactor sigue una
     * curva asintótica donde más tiempo = más utilización, con rendimientos
     * decrecientes después de ~60 minutos.
     */
    @Override
    public double calcularIBUTinseth(List<RecetaLupulo> lupulos, double volumenLitros, double og) {
        if (lupulos == null || lupulos.isEmpty() || volumenLitros <= 0) {
            return 0.0;
        }

        double ogEfectiva = (og > 1.0) ? og : 1.050;
        double totalIBU = 0.0;

        for (RecetaLupulo rl : lupulos) {
            if (rl.getUso() == RecetaLupulo.UsoLupulo.DRY_HOPPING) {
                continue;
            }

            double gramos = (rl.getCantidadGramos() != null) ? rl.getCantidadGramos() : 0.0;
            double alpha = (rl.getLupulo() != null && rl.getLupulo().getPorcentajeAlpha() != null)
                    ? rl.getLupulo().getPorcentajeAlpha() : 5.0;
            int minutos = (rl.getTiempoMinutos() != null) ? rl.getTiempoMinutos() : 0;

            double bignessFactor = 1.65 * Math.pow(0.000125, ogEfectiva - 1.0);
            double boilTimeFactor = (1.0 - Math.exp(-0.04 * minutos)) / 4.15;
            double utilizacion = bignessFactor * boilTimeFactor;

            double ibuAdicion = (gramos * (alpha / 100.0) * utilizacion * 1000.0) / volumenLitros;
            totalIBU += ibuAdicion;
        }

        return Math.round(totalIBU * 10.0) / 10.0;
    }

    /**
     * Calcula el color estimado en EBC usando la fórmula de Morey (1996).
     *
     * Paso 1 — Malt Color Units (MCU) para cada malta:
     *   lbs = kgMalta × 2.20462
     *   lovibond = colorEBC / 1.97
     *   gallons = litros / 3.78541
     *   MCU_malta = (lbs × lovibond) / gallons
     *
     * Paso 2 — SRM por fórmula de Morey:
     *   SRM = 1.4922 × MCU_total^0.6859
     *
     * Paso 3 — Conversión a EBC:
     *   EBC = SRM × 1.97
     */
    @Override
    public double calcularColorMorey(List<RecetaMalta> maltas, double volumenLitros) {
        if (maltas == null || maltas.isEmpty() || volumenLitros <= 0) {
            return 0.0;
        }

        double gallons = volumenLitros / 3.78541;
        double totalMCU = 0.0;

        for (RecetaMalta rm : maltas) {
            double gramos = rm.getCantidadGramos() != null ? rm.getCantidadGramos() : 0.0;
            double colorEbc = (rm.getMalta() != null && rm.getMalta().getColorEbc() != null)
                    ? rm.getMalta().getColorEbc() : 5.0;

            double lbs = gramos / 453.592;
            totalMCU += (lbs * colorEbc) / gallons;
        }

        if (totalMCU <= 0) {
            return 0.0;
        }

        double srm = 1.4922 * Math.pow(totalMCU, 0.6859);
        double ebc = srm * 1.97;
        return Math.round(ebc * 10.0) / 10.0;
    }

    /**
     * Calcula el ABV (Alcohol By Volume) usando la atenuación de la levadura.
     *
     * Fórmulas:
     *   atenuaciónPromedio = (atenuaciónMin + atenuaciónMax) / 2
     *   FG = OG − (OG − 1) × (atenuaciónPromedio / 100)
     *   ABV = (OG − FG) × 131.25
     *
     * Ejemplo: OG 1.055, atenuación 73–77% (promedio 75%):
     *   FG = 1.055 − 0.055 × 0.75 = 1.01375
     *   ABV = (1.055 − 1.01375) × 131.25 = 5.41%
     */
    @Override
    public double calcularABV(double og, Integer atenuacionMin, Integer atenuacionMax) {
        if (og <= 1.0) {
            return 0.0;
        }
        int min = (atenuacionMin != null) ? atenuacionMin : 73;
        int max = (atenuacionMax != null) ? atenuacionMax : 77;
        double atenuacionPromedio = (min + max) / 2.0;

        double fg = og - (og - 1.0) * (atenuacionPromedio / 100.0);
        double abv = (og - fg) * 131.25;
        return Math.round(abv * 100.0) / 100.0;
    }

    /**
     * Calcula los gramos de dextrosa (azúcar de maíz) necesarios para
     * carbonatación natural en botella (priming).
     *
     * Fórmulas:
     *   CO₂ residual (volúmenes) = 3.0378 − 0.050062×T + 0.00026555×T²
     *     donde T = temperatura máxima de fermentación en °C
     *
     *   gramos dextrosa = litros × (volCO₂_deseado − CO₂_residual) × 4.0
     *     donde 4.0 g/L/vol es el factor de carbonatación para dextrosa
     *
     * Ejemplo: 2.4 vol CO₂, fermentado a 20°C, 19L:
     *   residual = 3.0378 − 1.00124 + 0.10622 = 2.143
     *   gramos = 19 × (2.4 − 2.143) × 4.0 = 19.53 g
     */
    @Override
    public double calcularPrimingAzucar(double volumenesCO2, double tempMaxFermentacion, double litrosAEmbotellar) {
        if (litrosAEmbotellar <= 0 || volumenesCO2 <= 0) {
            return 0.0;
        }

        // La fórmula empírica del CO2 residual asume la temperatura en Fahrenheit
        double tempF = (tempMaxFermentacion * 9.0 / 5.0) + 32.0;
        double co2Residual = 3.0378 - (0.050062 * tempF) + (0.00026555 * Math.pow(tempF, 2));

        double gramos = litrosAEmbotellar * (volumenesCO2 - co2Residual) * 4.15;
        return Math.max(0.0, Math.round(gramos * 10.0) / 10.0);
    }
}
