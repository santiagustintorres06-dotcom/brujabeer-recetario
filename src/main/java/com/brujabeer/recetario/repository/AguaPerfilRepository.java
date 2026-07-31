package com.brujabeer.recetario.repository;

import com.brujabeer.recetario.model.AguaPerfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AguaPerfilRepository extends JpaRepository<AguaPerfil, Long> {
}
