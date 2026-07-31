package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * Escalón de Macerado (Mash Step).
 *
 * Cada paso representa un descanso a una temperatura específica durante
 * el macerado. La combinación de temperatura y duración controla qué
 * enzimas están activas y qué tipo de azúcares se producen:
 *
 *  - 62–65°C: Beta-amilasa activa → más maltosa → cerveza seca y atenuada
 *  - 66–68°C: Balance alfa/beta → perfil medio
 *  - 68–72°C: Alfa-amilasa activa → más dextrinas → cuerpo y dulzor
 *  - 76–78°C: Mash-out → detiene actividad enzimática para el lavado
 *
 * Relación: Muchos pasos pertenecen a una Receta (@ManyToOne).
 */
@Entity
@Table(name = "pasos_macerado")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PasoMacerado {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre descriptivo del paso. Ej: "Sacarificación", "Mash Out", "Descanso Proteico" */
    @Column(length = 80)
    private String nombre;

    /** Temperatura objetivo en grados Celsius. Rango válido: 30–100°C */
    @Column
    private Double temperatura;

    /** Duración del descanso en minutos. Rango típico: 5–90 min */
    @Column
    private Integer duracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    @Override
    public String toString() {
        return nombre + " (" + temperatura + "°C × " + duracion + " min)";
    }
}
