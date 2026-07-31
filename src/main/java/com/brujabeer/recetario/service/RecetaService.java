package com.brujabeer.recetario.service;

import com.brujabeer.recetario.model.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrato del servicio de Recetas y Lotes de Cocción.
 *
 * En la arquitectura MVC:
 *  - El Controller (JavaFX) SOLO habla con esta interfaz.
 *  - El Controller NUNCA habla directamente con un Repository.
 *
 * Esto respeta la Separación de Responsabilidades (SoC) y facilita
 * el testing: se puede mockear RecetaService sin tocar la BD.
 */
public interface RecetaService {

    // ── CRUD de Recetas ──────────────────────────────────────────────────────
    Receta guardar(Receta receta);
    Optional<Receta> buscarPorId(Long id);
    
    /**
     * Busca una receta por ID inicializando todas sus colecciones LAZY.
     * Usar este método desde el controlador JavaFX para evitar LazyInitializationException.
     */
    Optional<Receta> buscarConIngredientes(Long id);

    List<Receta> listarTodas();

    /**
     * Lista todas las recetas inicializando colecciones de ingredientes.
     * Para uso en la UI donde se necesitan los detalles de cada receta.
     */
    List<Receta> listarTodasConIngredientes();
    List<Receta> buscarPorNombre(String nombre);
    void eliminar(Long id);

    // ── Catálogos de ingredientes (para poblar ComboBoxes) ───────────────────
    List<Malta>    listarMaltas();
    List<Lupulo>   listarLupulos();
    List<Levadura> listarLevaduras();

    // ── Módulo de Lotes de Cocción ──────────────────────────────────────────

    /**
     * Registra una cocción calculando automáticamente:
     *  1. La eficiencia real del equipo usando la fórmula de puntos de gravedad.
     *  2. Alertas de pH si el valor medido está fuera del rango óptimo (5.2–5.6).
     *
     * @param lote el lote con los datos de control ingresados por el cervecero
     * @return el lote persistido con la eficiencia calculada y alertas agregadas
     */
    Lote registrarCoccion(Lote lote);

    /**
     * Lista todos los lotes de cocción registrados, ordenados por fecha descendente.
     */
    List<Lote> listarLotes();

    /**
     * Lista los lotes de cocción asociados a una receta específica.
     */
    List<Lote> listarLotesPorReceta(Receta receta);

    // ── Simulador Físico-Químico ────────────────────────────────────────────

    /** Lista todos los equipos registrados. */
    List<Equipo> listarEquipos();

    /** Lista todos los perfiles de agua registrados. */
    List<AguaPerfil> listarPerfilesAgua();

    /**
     * Calcula la OG estimada usando la eficiencia del equipo.
     * Fórmula: OG = 1 + Σ(kgMalta × rendimiento% × eficiencia% × 384) / litros / 1000
     */
    double calcularOG(List<RecetaMalta> maltas, double volumenLitros, double eficienciaBrewhouse);

    /**
     * Calcula IBU totales usando la fórmula de Tinseth.
     * Incluye factores de utilización por gravedad del mosto y tiempo de hervor.
     */
    double calcularIBUTinseth(List<RecetaLupulo> lupulos, double volumenLitros, double og);

    /**
     * Calcula el color en EBC usando la fórmula de Morey.
     * Convierte MCU → SRM → EBC.
     */
    double calcularColorMorey(List<RecetaMalta> maltas, double volumenLitros);

    /**
     * Calcula ABV usando la atenuación de la levadura.
     * ABV = (OG - FG) × 131.25, donde FG se deriva de la atenuación promedio.
     */
    double calcularABV(double og, Integer atenuacionMin, Integer atenuacionMax);

    /**
     * Calcula los gramos de dextrosa para carbonatación natural (priming).
     * Usa el CO₂ residual basado en la temperatura máxima de fermentación.
     */
    double calcularPrimingAzucar(double volumenesCO2, double tempMaxFermentacion, double litrosAEmbotellar);
}
