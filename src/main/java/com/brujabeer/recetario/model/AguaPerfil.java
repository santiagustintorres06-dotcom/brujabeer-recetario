package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * Perfil Químico del Agua de Elaboración (Water Chemistry Profile).
 *
 * El agua constituye ~95% de la cerveza. La concentración de iones minerales
 * afecta directamente el pH del macerado, la percepción del amargor y la
 * claridad del producto final.
 *
 * Ejemplos clásicos:
 *  - Pilsen (Checa): agua muy blanda → lagers delicadas
 *  - Burton-on-Trent: alta en sulfatos → IPAs con amargor seco y cortante
 *  - Dublín: alta en bicarbonatos → stouts con cuerpo y suavidad
 *
 * Todos los valores se expresan en ppm (partes por millón = mg/L).
 */
@Entity
@Table(name = "agua_perfiles")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AguaPerfil {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del perfil de agua es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Calcio (Ca²⁺) en ppm. Rango cervecero: 50–150 ppm. Clave para la salud de la levadura y claridad. */
    @Column
    private Double calcio;

    /** Magnesio (Mg²⁺) en ppm. Cofactor enzimático. Exceso >30 ppm puede dar sabor amargo/astringente. */
    @Column
    private Double magnesio;

    /** Sodio (Na⁺) en ppm. Realza el cuerpo en baja concentración. >150 ppm da sabor salado/metálico. */
    @Column
    private Double sodio;

    /** Cloruro (Cl⁻) en ppm. Redondea el sabor a malta y aporta plenitud. Ratio Cl/SO₄ define el balance. */
    @Column
    private Double cloruro;

    /** Sulfato (SO₄²⁻) en ppm. Resalta el amargor del lúpulo, haciéndolo más seco y cortante. */
    @Column
    private Double sulfato;

    /** Bicarbonato (HCO₃⁻) en ppm. Alcalinidad residual. Sube el pH del macerado. */
    @Column
    private Double bicarbonato;

    @Override
    public String toString() {
        return nombre + " (Ca:" + (calcio != null ? calcio.intValue() : 0)
                + " SO₄:" + (sulfato != null ? sulfato.intValue() : 0) + ")";
    }
}
