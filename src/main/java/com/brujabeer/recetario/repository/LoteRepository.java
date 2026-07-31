package com.brujabeer.recetario.repository;

import com.brujabeer.recetario.model.Lote;
import com.brujabeer.recetario.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad Lote.
 *
 * Hereda de JpaRepository todos los métodos CRUD estándar:
 *   save(), findById(), findAll(), deleteById(), count(), etc.
 *
 * Los métodos personalizados usan la convención de nombres de Spring Data
 * para que el framework genere automáticamente las queries SQL.
 */
@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    /**
     * Busca todos los lotes asociados a una receta específica,
     * ordenados por fecha de cocción descendente (más recientes primero).
     */
    List<Lote> findByRecetaOrderByFechaCoccionDesc(Receta receta);

    /**
     * Lista todos los lotes del sistema, ordenados por fecha descendente.
     * Útil para la vista principal del historial de cocciones.
     */
    List<Lote> findAllByOrderByFechaCoccionDesc();

    /**
     * Cuenta cuántos lotes existen para una receta dada.
     * Útil para generar números de lote secuenciales.
     */
    long countByReceta(Receta receta);
}
