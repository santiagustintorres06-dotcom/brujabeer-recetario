package com.brujabeer.recetario.repository;

import com.brujabeer.recetario.model.Levadura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LevaduraRepository extends JpaRepository<Levadura, Long> {
    List<Levadura> findByTipo(Levadura.TipoLevadura tipo);
    List<Levadura> findByNombreContainingIgnoreCase(String nombre);
}
