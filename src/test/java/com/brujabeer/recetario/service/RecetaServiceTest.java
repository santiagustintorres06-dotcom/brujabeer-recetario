package com.brujabeer.recetario.service;

import com.brujabeer.recetario.model.Malta;
import com.brujabeer.recetario.model.RecetaLupulo;
import com.brujabeer.recetario.model.RecetaMalta;
import com.brujabeer.recetario.model.Lupulo;
import com.brujabeer.recetario.service.impl.RecetaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RecetaServiceTest {

    private RecetaService recetaService;

    @BeforeEach
    void setUp() {
        recetaService = new RecetaServiceImpl();
    }

    @Test
    void testCalcularOG() {
        // Arrange
        Malta maltaBase = new Malta();
        maltaBase.setRendimientoPorcentaje(80.0);

        RecetaMalta rm = new RecetaMalta();
        rm.setMalta(maltaBase);
        rm.setCantidadGramos(5000.0);

        List<RecetaMalta> maltas = Collections.singletonList(rm);
        double volumen = 20.0;
        double eficiencia = 72.0;

        // Act
        // Formula points = (5.0 * 80/100 * 384 * 72/100) = 1105.92
        // totalPoints = 1105.92 / 20 = 55.296
        // OG = 1.0553 (approx)
        double og = recetaService.calcularOG(maltas, volumen, eficiencia);

        // Assert
        assertThat(og).isBetween(1.054, 1.056);
    }

    @Test
    void testCalcularIBUTinseth() {
        // Arrange
        Lupulo lupulo = new Lupulo();
        lupulo.setPorcentajeAlpha(5.0);

        RecetaLupulo rl = new RecetaLupulo();
        rl.setLupulo(lupulo);
        rl.setCantidadGramos(50.0);
        rl.setTiempoMinutos(60);
        rl.setUso(RecetaLupulo.UsoLupulo.HERVOR);

        List<RecetaLupulo> lupulos = Collections.singletonList(rl);
        double volumen = 20.0;
        double og = 1.050;

        // Act
        double ibu = recetaService.calcularIBUTinseth(lupulos, volumen, og);

        // Assert
        // Calculado por Tinseth debe dar alrededor de 28.5 IBUs
        assertThat(ibu).isBetween(27.0, 30.0);
    }

    @Test
    void testCalcularColorMorey() {
        // Arrange
        Malta malta1 = new Malta();
        malta1.setColorEbc(5.0); // Clara

        Malta malta2 = new Malta();
        malta2.setColorEbc(450.0); // Oscura

        RecetaMalta rm1 = new RecetaMalta();
        rm1.setMalta(malta1);
        rm1.setCantidadGramos(4000.0);

        RecetaMalta rm2 = new RecetaMalta();
        rm2.setMalta(malta2);
        rm2.setCantidadGramos(200.0); // 200g chocolate

        List<RecetaMalta> maltas = Arrays.asList(rm1, rm2);
        double volumen = 20.0;

        // Act
        double ebc = recetaService.calcularColorMorey(maltas, volumen);

        // Assert
        // Debe ser un ámbar oscuro a marrón claro (ej: 25 - 35 EBC)
        assertThat(ebc).isGreaterThan(20.0);
        assertThat(ebc).isLessThan(45.0);
    }

    @Test
    void testCalcularABV() {
        // Arrange
        double og = 1.060;
        int atenuacionMin = 70;
        int atenuacionMax = 80;

        // Act
        // FG = 1.060 - (0.060 * 0.75) = 1.015
        // ABV = (1.060 - 1.015) * 131.25 = 5.90625
        double abv = recetaService.calcularABV(og, atenuacionMin, atenuacionMax);

        // Assert
        assertThat(abv).isBetween(5.8, 6.0);
    }

    @Test
    void testCalcularPrimingAzucar() {
        // Arrange
        double volCO2 = 2.4;
        double temp = 20.0;
        double litros = 20.0;

        // Act
        // co2Residual = 3.0378 - (0.050062 * 68F) + (0.00026555 * 4624) = 0.86
        // gramos = 20.0 * (2.4 - 0.86) * 4.15 = 127.8 g
        double gramos = recetaService.calcularPrimingAzucar(volCO2, temp, litros);

        // Assert
        assertThat(gramos).isBetween(126.0, 129.0);
    }

    // ── Pruebas de Límites y Edge Cases ─────────────────────────────────────

    @Test
    void testOGLimites() {
        // Volúmenes cero o negativos no deben lanzar excepción sino retornar un valor por defecto seguro (1.000)
        double ogCero = recetaService.calcularOG(Collections.emptyList(), 0.0, 72.0);
        assertThat(ogCero).isEqualTo(1.000);

        // Maltas nulas
        double ogNulo = recetaService.calcularOG(null, 20.0, 72.0);
        assertThat(ogNulo).isEqualTo(1.000);

        // Cantidad de grano extrema (Ej. 1 tonelada de malta en 20 litros, simulación de error de tipeo)
        Malta maltaBase = new Malta(); maltaBase.setRendimientoPorcentaje(80.0);
        RecetaMalta rmEx = new RecetaMalta(); rmEx.setMalta(maltaBase); rmEx.setCantidadGramos(1_000_000.0); // 1000 kg
        double ogExtremo = recetaService.calcularOG(Collections.singletonList(rmEx), 20.0, 72.0);
        // Debe procesarse matemáticamente sin romper (OG masiva > 11.000)
        assertThat(ogExtremo).isGreaterThan(10.0);
    }

    @Test
    void testIBULimites() {
        // Volumen cero no debe causar ArithmeticException / divide by zero, debe retornar 0
        double ibuCero = recetaService.calcularIBUTinseth(Collections.emptyList(), 0.0, 1.050);
        assertThat(ibuCero).isEqualTo(0.0);

        // Tiempo de hervor 0 minutos -> factor de ebullición 0 -> 0 IBUs
        Lupulo lup = new Lupulo(); lup.setPorcentajeAlpha(10.0);
        RecetaLupulo rl = new RecetaLupulo(); rl.setLupulo(lup); rl.setCantidadGramos(100.0); rl.setTiempoMinutos(0); rl.setUso(RecetaLupulo.UsoLupulo.HERVOR);
        double ibuSinHervor = recetaService.calcularIBUTinseth(Collections.singletonList(rl), 20.0, 1.050);
        assertThat(ibuSinHervor).isEqualTo(0.0);

        // Lúpulo Dry Hopping -> 0 IBUs por convención en Tinseth
        rl.setTiempoMinutos(7); rl.setUso(RecetaLupulo.UsoLupulo.DRY_HOPPING);
        double ibuDryHop = recetaService.calcularIBUTinseth(Collections.singletonList(rl), 20.0, 1.050);
        assertThat(ibuDryHop).isEqualTo(0.0);

        // Lúpulo extremo (50kg de lúpulo a 20% alpha en 20L)
        lup.setPorcentajeAlpha(20.0);
        rl.setCantidadGramos(50_000.0); rl.setTiempoMinutos(60); rl.setUso(RecetaLupulo.UsoLupulo.HERVOR);
        double ibuExtremo = recetaService.calcularIBUTinseth(Collections.singletonList(rl), 20.0, 1.050);
        assertThat(ibuExtremo).isGreaterThan(1000.0);
    }

    @Test
    void testColorLimites() {
        // Sin maltas -> EBC 0
        assertThat(recetaService.calcularColorMorey(null, 20.0)).isEqualTo(0.0);
        // Volumen cero o negativo -> EBC 0
        RecetaMalta rm = new RecetaMalta(); rm.setCantidadGramos(1000.0);
        assertThat(recetaService.calcularColorMorey(Collections.singletonList(rm), 0.0)).isEqualTo(0.0);
        
        // Malta extrema -> 1 tonelada de malta ultra oscura
        Malta negra = new Malta(); negra.setColorEbc(1500.0);
        rm.setMalta(negra); rm.setCantidadGramos(1_000_000.0);
        double ebcExtremo = recetaService.calcularColorMorey(Collections.singletonList(rm), 20.0);
        assertThat(ebcExtremo).isGreaterThan(1000.0); // No debe devolver infinito ni romper
    }

    @Test
    void testABVLimites() {
        // OG menor a 1 (error de input) -> ABV 0
        assertThat(recetaService.calcularABV(0.990, 75, 80)).isEqualTo(0.0);
        
        // Atenuación Nula -> fallback a promedios por defecto (73-77)
        double abvDef = recetaService.calcularABV(1.050, null, null);
        assertThat(abvDef).isGreaterThan(0.0);

        // Cerveza hiper alcohólica (OG 1.300)
        double abvFuerte = recetaService.calcularABV(1.300, 90, 100);
        assertThat(abvFuerte).isGreaterThan(35.0);
    }

    @Test
    void testPrimingLimites() {
        // Cero litros
        assertThat(recetaService.calcularPrimingAzucar(2.4, 20.0, 0.0)).isEqualTo(0.0);
        // Cero Volúmenes esperados
        assertThat(recetaService.calcularPrimingAzucar(0.0, 20.0, 20.0)).isEqualTo(0.0);
        
        // Temperatura bajo cero (Simulando un cold crash agresivo a 0°C)
        // A 0°C el CO2 residual es muy alto, así que se requiere menos azúcar.
        double azucarFrio = recetaService.calcularPrimingAzucar(2.4, 0.0, 20.0);
        assertThat(azucarFrio).isLessThan(70.0);

        // Temperatura alta (Simulando fermentación Kveik a 40°C)
        // A 40°C casi no hay CO2 residual, se requiere muchísima más azúcar.
        double azucarCalor = recetaService.calcularPrimingAzucar(2.4, 40.0, 20.0);
        assertThat(azucarCalor).isGreaterThan(140.0);
    }
}
