package com.brujabeer.recetario.service.impl;

import com.brujabeer.recetario.model.*;
import com.brujabeer.recetario.repository.*;
import com.brujabeer.recetario.service.RecetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private RecetaRepository recetaRepository;

    @Autowired
    private MaltaRepository maltaRepository;

    @Autowired
    private LupuloRepository lupuloRepository;

    @Autowired
    private LevaduraRepository levaduraRepository;

    // ── CRUD de Recetas ──────────────────────────────────────────────────────

    @Override
    public Receta guardar(Receta receta) {
        // save() hace INSERT si id == null, o UPDATE si ya tiene id
        return recetaRepository.save(receta);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Receta> buscarPorId(Long id) {
        return recetaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Receta> listarTodas() {
        return recetaRepository.findAllByOrderByFechaCreacionDesc();
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
}
