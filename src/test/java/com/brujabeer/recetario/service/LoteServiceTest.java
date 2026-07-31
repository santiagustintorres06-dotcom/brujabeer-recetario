package com.brujabeer.recetario.service;

import com.brujabeer.recetario.model.*;
import com.brujabeer.recetario.repository.LoteRepository;
import com.brujabeer.recetario.service.impl.RecetaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para la lógica de negocio del Módulo de Lotes.
 *
 * Usa Mockito para aislar RecetaServiceImpl del repositorio real.
 * NO necesita base de datos ni Spring Context → corren en milisegundos.
 *
 * Ejecutar con:
 *   mvn test -Dtest=LoteServiceTest
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("🍺 Módulo de Lotes — Lógica de Negocio")
class LoteServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private RecetaServiceImpl recetaService;

    private Receta recetaIPA;
    private Lote loteBase;

    /**
     * Prepara una receta IPA con 5 kg de maltas (3kg Pilsner + 2kg Caramelo)
     * y un lote base con datos típicos de una cocción.
     */
    @BeforeEach
    void setUp() {
        recetaIPA = crearRecetaConMaltas("IPA Argentina", 3000.0, 2000.0);

        loteBase = new Lote();
        loteBase.setReceta(recetaIPA);
        loteBase.setDensidadInicialReal(1.052);
        loteBase.setLitrosFinalesReal(20.0);
        loteBase.setPhMacerado(5.4);

        when(loteRepository.save(any(Lote.class))).thenAnswer(invocation -> {
            Lote l = invocation.getArgument(0);
            l.setId(1L);
            return l;
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // TESTS DE CÁLCULO DE EFICIENCIA
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("⚙️ Cálculo de Eficiencia Real")
    class EficienciaTests {

        @Test
        @DisplayName("Calcula correctamente la eficiencia con datos típicos")
        void calculaEficienciaConDatosTipicos() {
            // OG=1.052, 20L, 5kg maltas (con rendimiento 78%)
            // Puntos = (1.052-1.0)*1000 = 52
            // Extraídos = 52 * 20 = 1040
            // Potencial ajustado por rendimiento de maltas = 1497.6
            // Eficiencia Brewhouse = (1040/1497.6)*100 = 69.44%
            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getEficienciaEquipoReal())
                    .isNotNull()
                    .isEqualTo(69.44);
        }

        @Test
        @DisplayName("Eficiencia alta con buena extracción (OG 1.060, 22L, 5kg)")
        void eficienciaAltaConBuenaExtraccion() {
            loteBase.setDensidadInicialReal(1.060);
            loteBase.setLitrosFinalesReal(22.0);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            // Eficiencia Brewhouse ajustada por rendimiento = 88.14%
            assertThat(resultado.getEficienciaEquipoReal())
                    .isEqualTo(88.14);
        }

        @Test
        @DisplayName("Eficiencia baja con mala extracción (OG 1.035, 18L, 5kg)")
        void eficienciaBajaConMalaExtraccion() {
            loteBase.setDensidadInicialReal(1.035);
            loteBase.setLitrosFinalesReal(18.0);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            // Eficiencia Brewhouse ajustada por rendimiento = 42.07%
            assertThat(resultado.getEficienciaEquipoReal())
                    .isEqualTo(42.07);
        }

        @Test
        @DisplayName("Eficiencia con mucha malta (8kg) y OG alta")
        void eficienciaConMuchaMalta() {
            Receta recetaGrande = crearRecetaConMaltas("Imperial Stout", 6000.0, 2000.0);
            loteBase.setReceta(recetaGrande);
            loteBase.setDensidadInicialReal(1.085);
            loteBase.setLitrosFinalesReal(20.0);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            // Eficiencia Brewhouse ajustada por rendimiento = 70.27%
            assertThat(resultado.getEficienciaEquipoReal())
                    .isEqualTo(70.27);
        }

        @Test
        @DisplayName("No calcula eficiencia si falta densidadInicialReal")
        void noCalculaSinDensidad() {
            loteBase.setDensidadInicialReal(null);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getEficienciaEquipoReal()).isNull();
        }

        @Test
        @DisplayName("No calcula eficiencia si faltan litrosFinalesReal")
        void noCalculaSinLitros() {
            loteBase.setLitrosFinalesReal(null);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getEficienciaEquipoReal()).isNull();
        }

        @Test
        @DisplayName("Eficiencia 0% y alerta si la receta no tiene maltas")
        void eficienciaCeroSinMaltas() {
            Receta recetaVacia = new Receta();
            recetaVacia.setNombre("Receta Vacía");
            recetaVacia.setMaltas(new java.util.ArrayList<>());
            loteBase.setReceta(recetaVacia);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getEficienciaEquipoReal()).isEqualTo(0.0);
            assertThat(resultado.getComentarios())
                    .contains("no tiene maltas registradas");
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // TESTS DE ALERTAS DE pH
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("🧪 Alertas de pH")
    class PhAlertTests {

        @Test
        @DisplayName("Sin alerta cuando pH macerado está en rango óptimo (5.4)")
        void sinAlertaPhOptimo() {
            loteBase.setPhMacerado(5.4);
            loteBase.setComentarios(null);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getComentarios()).isNull();
        }

        @Test
        @DisplayName("Sin alerta en el límite inferior exacto (5.2)")
        void sinAlertaEnLimiteInferior() {
            loteBase.setPhMacerado(5.2);
            loteBase.setComentarios(null);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getComentarios()).isNull();
        }

        @Test
        @DisplayName("Sin alerta en el límite superior exacto (5.6)")
        void sinAlertaEnLimiteSuperior() {
            loteBase.setPhMacerado(5.6);
            loteBase.setComentarios(null);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getComentarios()).isNull();
        }

        @Test
        @DisplayName("Alerta cuando pH macerado es bajo (4.8) — sugiere bicarbonato")
        void alertaPhBajo() {
            loteBase.setPhMacerado(4.8);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getComentarios())
                    .contains("ALERTA pH MACERADO")
                    .contains("POR DEBAJO")
                    .contains("bicarbonato");
        }

        @Test
        @DisplayName("Alerta cuando pH macerado es alto (6.2) — sugiere ácido láctico")
        void alertaPhAlto() {
            loteBase.setPhMacerado(6.2);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getComentarios())
                    .contains("ALERTA pH MACERADO")
                    .contains("POR ENCIMA")
                    .contains("ácido láctico");
        }

        @Test
        @DisplayName("Alerta de pH lavado cuando supera 6.0")
        void alertaPhLavadoAlto() {
            loteBase.setPhLavado(6.5);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getComentarios())
                    .contains("ALERTA pH LAVADO");
        }

        @Test
        @DisplayName("Sin alerta de pH lavado cuando está dentro del rango (<= 6.0)")
        void sinAlertaPhLavadoBueno() {
            loteBase.setPhLavado(5.8);
            loteBase.setComentarios(null);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getComentarios()).isNull();
        }

        @Test
        @DisplayName("Preserva comentarios del cervecero al agregar alertas")
        void preservaComentariosExistentes() {
            loteBase.setPhMacerado(4.5);
            loteBase.setComentarios("Usé agua de ósmosis inversa hoy");

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getComentarios())
                    .startsWith("Usé agua de ósmosis inversa hoy")
                    .contains("ALERTA pH MACERADO");
        }

        @Test
        @DisplayName("Múltiples alertas: pH macerado bajo + pH lavado alto")
        void multiplesAlertas() {
            loteBase.setPhMacerado(4.9);
            loteBase.setPhLavado(6.8);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getComentarios())
                    .contains("ALERTA pH MACERADO")
                    .contains("ALERTA pH LAVADO");
        }

        @Test
        @DisplayName("Sin alerta si pH macerado es null")
        void sinAlertaSiPhNull() {
            loteBase.setPhMacerado(null);
            loteBase.setComentarios(null);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getComentarios()).isNull();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // TESTS DE GENERACIÓN DE NRO LOTE Y FECHA
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("🔢 Número de Lote y Fecha")
    class NroLoteTests {

        @Test
        @DisplayName("Asigna fecha de cocción automáticamente si es null")
        void asignaFechaAutomatica() {
            loteBase.setFechaCoccion(null);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getFechaCoccion())
                    .isNotNull()
                    .isEqualTo(java.time.LocalDate.now());
        }

        @Test
        @DisplayName("Genera nroLote basado en la fecha si es null")
        void generaNroLoteBasadoEnFecha() {
            loteBase.setFechaCoccion(java.time.LocalDate.of(2026, 6, 7));
            loteBase.setNroLote(null);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getNroLote()).isEqualTo(20260607);
        }

        @Test
        @DisplayName("Respeta nroLote si ya fue asignado manualmente")
        void respetaNroLoteManual() {
            loteBase.setNroLote(99999);

            Lote resultado = recetaService.registrarCoccion(loteBase);

            assertThat(resultado.getNroLote()).isEqualTo(99999);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // TESTS DE PERSISTENCIA (verificación de llamadas al repository)
    // ════════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("💾 Persistencia")
    class PersistenciaTests {

        @Test
        @DisplayName("Llama a loteRepository.save() exactamente una vez")
        void guardaEnRepositorio() {
            recetaService.registrarCoccion(loteBase);

            verify(loteRepository, times(1)).save(any(Lote.class));
        }

        @Test
        @DisplayName("El lote guardado tiene la receta asociada correctamente")
        void loteGuardadoTieneReceta() {
            ArgumentCaptor<Lote> captor = ArgumentCaptor.forClass(Lote.class);

            recetaService.registrarCoccion(loteBase);

            verify(loteRepository).save(captor.capture());
            Lote guardado = captor.getValue();
            assertThat(guardado.getReceta()).isNotNull();
            assertThat(guardado.getReceta().getNombre()).isEqualTo("IPA Argentina");
        }

        @Test
        @DisplayName("El lote guardado contiene la eficiencia calculada")
        void loteGuardadoTieneEficiencia() {
            ArgumentCaptor<Lote> captor = ArgumentCaptor.forClass(Lote.class);

            recetaService.registrarCoccion(loteBase);

            verify(loteRepository).save(captor.capture());
            assertThat(captor.getValue().getEficienciaEquipoReal())
                    .isNotNull()
                    .isGreaterThan(0.0);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Crea una Receta con dos maltas para testing.
     *
     * @param nombre        nombre de la receta
     * @param gramosMalta1  gramos de Malta Pilsner
     * @param gramosMalta2  gramos de Malta Caramelo 60
     * @return Receta con maltas cargadas
     */
    private Receta crearRecetaConMaltas(String nombre, double gramosMalta1, double gramosMalta2) {
        Malta pilsner = new Malta();
        pilsner.setNombre("Malta Pilsner");
        pilsner.setRendimientoPorcentaje(80.0);

        Malta caramelo = new Malta();
        caramelo.setNombre("Malta Caramelo 60");
        caramelo.setRendimientoPorcentaje(75.0);

        RecetaMalta rm1 = new RecetaMalta();
        rm1.setMalta(pilsner);
        rm1.setCantidadGramos(gramosMalta1);

        RecetaMalta rm2 = new RecetaMalta();
        rm2.setMalta(caramelo);
        rm2.setCantidadGramos(gramosMalta2);

        Receta receta = new Receta();
        receta.setNombre(nombre);
        receta.setEstilo("American IPA");
        receta.setVolumenLitros(20.0);
        receta.agregarMalta(rm1);
        receta.agregarMalta(rm2);

        return receta;
    }
}
