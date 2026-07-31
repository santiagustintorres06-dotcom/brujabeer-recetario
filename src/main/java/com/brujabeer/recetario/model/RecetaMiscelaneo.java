package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * Entidad para registrar ingredientes misceláneos como
 * clarificantes, especias, frutas, etc.
 */
@Entity
@Table(name = "receta_miscelaneos")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RecetaMiscelaneo {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Double cantidad;

    @Column(length = 50)
    private String uso;

    @Column
    private Integer tiempo;
}
