package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TABLA INTERMEDIA: receta_levaduras
 *
 * Generalmente una receta usa una sola levadura, pero el modelo
 * soporta co-pitching (dos cepas) que es válido en estilos mixtos.
 */
@Entity
@Table(name = "receta_levaduras")
@Getter
@Setter
@NoArgsConstructor
public class RecetaLevadura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "levadura_id", nullable = false)
    private Levadura levadura;

    /** Gramos de levadura seca (si aplica). */
    @Column(name = "cantidad_gramos")
    private Double cantidadGramos;

    /** Cantidad de sobres/paquetes (formato más común en homebrewing). */
    @Column(name = "cantidad_paquetes")
    private Integer cantidadPaquetes;
}
