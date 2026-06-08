package com.brujabeer.recetario.repository;

import com.brujabeer.recetario.model.Malta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaltaRepository extends JpaRepository<Malta, Long> {
    List<Malta> findByTipo(String tipo);
    List<Malta> findByNombreContainingIgnoreCase(String nombre);
}
