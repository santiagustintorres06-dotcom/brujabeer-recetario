package com.brujabeer.recetario.repository;

import com.brujabeer.recetario.model.Lupulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LupuloRepository extends JpaRepository<Lupulo, Long> {
    List<Lupulo> findByOrigen(String origen);
    List<Lupulo> findByNombreContainingIgnoreCase(String nombre);
}
