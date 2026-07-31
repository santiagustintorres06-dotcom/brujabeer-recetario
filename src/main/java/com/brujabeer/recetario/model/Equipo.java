package com.brujabeer.recetario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.EqualsAndHashCode;

/**
 * Perfil de Equipo Cervecero (Equipment Profile).
 *
 * Modela las características físicas del equipo de elaboración del cervecero.
 * La eficiencia del brewhouse es el dato más crítico: define qué porcentaje
 * del extracto potencial de las maltas se extrae realmente en el mosto.
 *
 * Un equipo casero típico tiene 65-75% de eficiencia.
 * Un sistema HERMS/RIMS profesional puede alcanzar 78-85%.
 */
@Entity
@Table(name = "equipos")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Equipo {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    /**
     * Eficiencia global del brewhouse (%).
     * Combina: eficiencia de conversión + eficiencia de lavado + pérdidas.
     * Rango típico homebrewing: 65% – 80%.
     */
    @Column(name = "eficiencia_brewhouse")
    private Double eficienciaBrewhouse;

    /**
     * Tasa de evaporación durante el hervor (litros/hora).
     * Depende del diámetro de la olla y la potencia del quemador.
     * Valor típico: 3.5 – 5.0 L/h.
     */
    @Column(name = "tasa_evaporacion")
    private Double tasaEvaporacion;

    /**
     * Litros perdidos en el trub (sedimento post-hervor).
     * Incluye el cono de lúpulos, proteínas coaguladas y levadura muerta.
     */
    @Column(name = "perdida_trub")
    private Double perdidaTrub;

    /**
     * Litros de espacio muerto en el macerador (dead space).
     * Volumen de mosto que queda debajo del falso fondo y no se recupera.
     */
    @Column(name = "perdida_macerador")
    private Double perdidaMacerador;

    @Override
    public String toString() {
        return nombre + " (" + (eficienciaBrewhouse != null ? eficienciaBrewhouse + "%" : "s/e") + ")";
    }
}
