package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Receta {

    @EqualsAndHashCode.Include
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

    // ── Perfil de Equipo ────────────────────────────────────────────────────

    /**
     * Equipo cervecero asociado a esta receta.
     * La eficiencia del equipo se usa para calcular la OG estimada.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;

    // ── Perfil de Agua ──────────────────────────────────────────────────────

    /**
     * Perfil químico del agua usada en esta receta.
     * Los iones afectan el pH del macerado y el perfil de sabor.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agua_perfil_id")
    private AguaPerfil aguaPerfil;

    /** Mililitros de ácido láctico agregados al agua para corrección de pH. */
    @Column(name = "acido_lactico")
    private Double acidoLactico;

    /** Sales minerales agregadas al agua (texto libre o JSON). */
    @Column(name = "sales_agregadas", columnDefinition = "TEXT")
    private String salesAgregadas;

    // ── Fermentación y Carbonatación ────────────────────────────────────────

    /** Días totales de fermentación primaria. Típico: 7–14 días. */
    @Column(name = "dias_fermentacion")
    private Integer diasFermentacion;

    /** Temperatura de fermentación en °C. Depende de la cepa de levadura. */
    @Column(name = "temp_fermentacion")
    private Double tempFermentacion;

    /**
     * Volúmenes de CO₂ deseados para carbonatación.
     * Ej: 2.0–2.5 para Ales, 2.5–3.0 para Lagers, 3.0–4.5 para estilos belgas.
     */
    @Column(name = "volumenes_co2")
    private Double volumenesCO2;

    // ── Maduración (Cold Crash) ─────────────────────────────────────────────

    /** Días de maduración o cold crash. */
    @Column(name = "dias_maduracion")
    private Integer diasMaduracion;

    /** Temperatura de maduración en °C. Típicamente cerca de 0°C. */
    @Column(name = "temp_maduracion")
    private Double tempMaduracion;

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

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PasoMacerado> pasosMacerado = new ArrayList<>();

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RecetaMiscelaneo> miscelaneos = new ArrayList<>();

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

    public void agregarPasoMacerado(PasoMacerado paso) {
        pasosMacerado.add(paso);
        paso.setReceta(this);
    }

    public void agregarMiscelaneo(RecetaMiscelaneo miscelaneo) {
        miscelaneos.add(miscelaneo);
        miscelaneo.setReceta(this);
    }

    @Override
    public String toString() {
        return nombre + " (" + estilo + ")";
    }
}
