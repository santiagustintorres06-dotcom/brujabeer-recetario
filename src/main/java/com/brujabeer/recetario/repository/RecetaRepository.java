// ════════════════════════════════════════════════════════════════════════════
// RecetaRepository.java
// ════════════════════════════════════════════════════════════════════════════
package com.brujabeer.recetario.repository;

import com.brujabeer.recetario.model.Receta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    // Spring Data JPA genera la query automáticamente desde el nombre del método
    List<Receta> findByEstilo(String estilo);
    List<Receta> findByNombreContainingIgnoreCase(String nombre);
    List<Receta> findAllByOrderByFechaCreacionDesc();
}
