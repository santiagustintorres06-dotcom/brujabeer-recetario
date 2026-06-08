package com.brujabeer.recetario.service;

import com.brujabeer.recetario.model.*;

import java.util.List;
import java.util.Optional;

/**
 * Contrato del servicio de Recetas.
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
    List<Receta> listarTodas();
    List<Receta> buscarPorNombre(String nombre);
    void eliminar(Long id);

    // ── Catálogos de ingredientes (para poblar ComboBoxes) ───────────────────
    List<Malta>    listarMaltas();
    List<Lupulo>   listarLupulos();
    List<Levadura> listarLevaduras();
}
