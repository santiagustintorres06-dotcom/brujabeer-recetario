package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad central del recetario.
 *
 * Relaciones:
 *  - Una Receta tiene N RecetaMalta  (tabla intermedia con cantidad en gramos)
 *  - Una Receta tiene N RecetaLupulo (tabla intermedia con cantidad + tiempo)
 *  - Una Receta tiene N RecetaLevadura (tabla intermedia con cantidad)
 *
 * NOTA: Usamos entidades intermedias (RecetaMalta, RecetaLupulo, RecetaLevadura)
 * en lugar de @ManyToMany porque necesitamos persistir datos EXTRA en la relación
 * (cantidadGramos, tiempoMinutos, uso) que @ManyToMany no soporta.
 */
@Entity
@Table(name = "recetas")
@Getter
@Setter
@NoArgsConstructor
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la receta es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    /**
     * Estilo cervecero según clasificación BJCP.
     * Ej: "American IPA", "Russian Imperial Stout", "Munich Helles", etc.
     */
    @Column(length = 80)
    private String estilo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Volumen objetivo del batch producido, en litros. */
    @NotNull
    @Positive
    @Column(name = "volumen_litros", nullable = false)
    private Double volumenLitros;

    // ── Valores estimados calculados por el motor de la app ─────────────────

    /** Original Gravity estimada. Ej: 1.060 */
    @Column(name = "og_estimada")
    private Double ogEstimada;

    /** Final Gravity estimada. Ej: 1.012 */
    @Column(name = "fg_estimada")
    private Double fgEstimada;

    /**
     * International Bitterness Units estimados.
     * Calculados por fórmula de Tinseth o Rager.
     */
    @Column(name = "ibu_estimado")
    private Integer ibuEstimado;

    /**
     * Alcohol By Volume estimado (%).
     * Fórmula simple: ABV = (OG - FG) * 131.25
     */
    @Column(name = "abv_estimado")
    private Double abvEstimado;

    /**
     * Color estimado en EBC (European Brewery Convention).
     * Más intuitivo para el mercado argentino que los grados SRM.
     * Conversión: EBC = SRM * 1.97
     */
    @Column(name = "color_ebc")
    private Integer colorEbc;

    @Column(name = "fecha_creacion")
    private LocalDate fechaCreacion;

    // ── Relaciones ──────────────────────────────────────────────────────────

    /**
     * CascadeType.ALL + orphanRemoval = true:
     * Si borro una Receta, se borran sus RecetaMaltas en cascada.
     * Si saco una RecetaMalta de la lista, también se borra de la BD.
     */
    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RecetaMalta> maltas = new ArrayList<>();

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RecetaLupulo> lupulos = new ArrayList<>();

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RecetaLevadura> levaduras = new ArrayList<>();

    // ── Hooks de ciclo de vida JPA ──────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDate.now();
    }

    // ── Métodos helper para mantener la consistencia bidireccional ──────────

    public void agregarMalta(RecetaMalta recetaMalta) {
        maltas.add(recetaMalta);
        recetaMalta.setReceta(this);
    }

    public void agregarLupulo(RecetaLupulo recetaLupulo) {
        lupulos.add(recetaLupulo);
        recetaLupulo.setReceta(this);
    }

    public void agregarLevadura(RecetaLevadura recetaLevadura) {
        levaduras.add(recetaLevadura);
        recetaLevadura.setReceta(this);
    }

    @Override
    public String toString() {
        return nombre + " (" + estilo + ")";
    }
}
