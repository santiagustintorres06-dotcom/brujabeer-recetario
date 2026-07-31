package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * TABLA INTERMEDIA: receta_maltas
 *
 * ¿Por qué no usar @ManyToMany?
 * Porque necesitamos persistir datos ADICIONALES en la relación:
 *   - cantidadGramos: cuántos gramos de esta malta usa la receta
 *   - porcentajeEnGrist: % que representa del total de granos
 *
 * @ManyToMany solo puede representar el vínculo, no datos extra.
 * Patrón: "Entidad de Asociación" o "Join Entity".
 */
@Entity
@Table(name = "receta_maltas")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RecetaMalta {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * FK hacia Receta. LAZY porque rara vez necesitamos la receta entera
     * al operar sobre una línea de ingrediente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    /**
     * FK hacia el catálogo de Maltas. EAGER porque siempre queremos
     * el nombre/tipo de la malta al mostrar la tabla de ingredientes.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "malta_id", nullable = false)
    private Malta malta;

    /** Gramos de esta malta en la receta. */
    @Column(name = "cantidad_gramos", nullable = false)
    private Double cantidadGramos;

    /**
     * Porcentaje que esta malta representa sobre el total del grist.
     * Calculado por la app, no por el usuario. Ej: 75.3
     */
    @Column(name = "porcentaje_en_grist")
    private Double porcentajeEnGrist;
}
