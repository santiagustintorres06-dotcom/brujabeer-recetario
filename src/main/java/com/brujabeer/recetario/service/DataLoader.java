package com.brujabeer.recetario.service;

import com.brujabeer.recetario.model.Levadura;
import com.brujabeer.recetario.model.Levadura.TipoLevadura;
import com.brujabeer.recetario.model.Lupulo;
import com.brujabeer.recetario.model.Malta;
import com.brujabeer.recetario.repository.LevaduraRepository;
import com.brujabeer.recetario.repository.LupuloRepository;
import com.brujabeer.recetario.repository.MaltaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataLoader - Carga datos iniciales en la base de datos al arrancar la aplicación.
 *
 * Se ejecuta automáticamente después de que Spring Boot inicie.
 * Si las tablas están vacías, popula 15 Maltas, 15 Lúpulos y 10 Levaduras reales.
 *
 * @author Spring Boot Expert
 */
@Slf4j
@Component
public class DataLoader implements CommandLineRunner {

    private final MaltaRepository maltaRepository;
    private final LupuloRepository lupuloRepository;
    private final LevaduraRepository levaduraRepository;

    public DataLoader(MaltaRepository maltaRepository,
                      LupuloRepository lupuloRepository,
                      LevaduraRepository levaduraRepository) {
        this.maltaRepository = maltaRepository;
        this.lupuloRepository = lupuloRepository;
        this.levaduraRepository = levaduraRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("🍺 Iniciando DataLoader...");

        // Cargar datos solo si las tablas están vacías
        if (maltaRepository.count() == 0) {
            cargarMaltas();
        } else {
            log.info("Tabla 'maltas' ya contiene datos. Omitiendo carga.");
        }

        if (lupuloRepository.count() == 0) {
            cargarLupulos();
        } else {
            log.info("Tabla 'lupulos' ya contiene datos. Omitiendo carga.");
        }

        if (levaduraRepository.count() == 0) {
            cargarLevaduras();
        } else {
            log.info("Tabla 'levaduras' ya contiene datos. Omitiendo carga.");
        }

        log.info("✅ DataLoader completado.");
    }

    // ────────────────────────────────────────────────────────────────────────────────

    /**
     * Carga 15 Maltas reales con sus valores de extracto potencial y color EBC.
     */
    private void cargarMaltas() {
        log.info("📦 Cargando 15 Maltas...");

        maltaRepository.saveAll(java.util.Arrays.asList(
                // Maltas Base
                crearMalta("Pilsen", "Base", "Weyermann", 2.0, 80.0,
                        "Malta clara, suave, perfecta para lagers y cervezas claras."),
                crearMalta("Pale Ale", "Base", "Crisp", 3.0, 80.0,
                        "Malta versátil para ales británicas y norteamericanas. Aroma y cuerpo neutro."),
                crearMalta("Munich", "Base", "Weyermann", 12.0, 78.0,
                        "Malta de caramelo suave con notas de pan y cereal. Base popular en lagers bavaras."),
                crearMalta("Vienna", "Base", "Patagonia Maltera", 4.5, 79.0,
                        "Malta base con ligeros toques tostados. Ideal para cervezas balanceadas."),
                crearMalta("Maris Otter", "Base", "Crisp", 2.5, 82.0,
                        "Malta inglesa clásica, premium. Excelente para ales tradicionales."),

                // Maltas Caramelo/Crystal
                crearMalta("Caramelo 20", "Caramelo", "Crisp", 20.0, 75.0,
                        "Aroma tostado suave. Agrega dulzor y color ámbar claro."),
                crearMalta("Caramelo 60", "Caramelo", "Weyermann", 60.0, 75.0,
                        "Caramelo oscuro. Aporta cuerpo, dulce residual y color rojo-ámbar."),
                crearMalta("Caramelo 120", "Caramelo", "Bestmalz", 120.0, 75.0,
                        "Muy oscura. Notas de caramelo y melazo. Pequeñas cantidades evitan exceso de dulzor."),

                // Maltas Tostadas
                crearMalta("Chocolate", "Tostada", "Weyermann", 450.0, 77.0,
                        "Tostada suave. Notas de cacao y chocolate. Importante para stouts y porters."),
                crearMalta("Carafa III", "Tostada", "Weyermann", 1200.0, 75.0,
                        "Muy oscura, casi negra. Sabor de café/chocolate profundo. Clave en stouts."),
                crearMalta("Black Patent", "Tostada", "Crisp", 1500.0, 72.0,
                        "Negra intensísima. Sabor acido característico. Pequeñas cantidades previenen astringencia."),

                // Maltas Especiales
                crearMalta("Trigo Malteado", "Especial", "Weyermann", 1.5, 86.0,
                        "Malta de trigo. Agrega cuerpo, espuma y palatabilidad. Base para weizens y witbiers."),
                crearMalta("Cebada Malteada", "Especial", "Patagonia Maltera", 3.0, 80.0,
                        "Suave y ligera. Común en lagers de grano mixto y ales inglesas."),
                crearMalta("Centeno Malteado", "Especial", "Bestmalz", 1.5, 75.0,
                        "Aroma especiado característico. Cuerpo seco y complejo. Típico de ryes y roggenbiers."),
                crearMalta("Avena Malteada", "Especial", "Crisp", 1.5, 65.0,
                        "Textura suave y cremosa. Baja extracción. Usada para smoothness en oatmeal stouts.")
        ));

        log.info("✅ 15 Maltas cargadas correctamente.");
    }

    /**
     * Crea una instancia de Malta con los parámetros especificados.
     */
    private Malta crearMalta(String nombre, String tipo, String marca,
                             Double colorEbc, Double rendimiento, String descripcion) {
        Malta malta = new Malta();
        malta.setNombre(nombre);
        malta.setTipo(tipo);
        malta.setMarca(marca);
        malta.setColorEbc(colorEbc);
        malta.setRendimientoPorcentaje(rendimiento);
        malta.setDescripcion(descripcion);
        return malta;
    }

    // ────────────────────────────────────────────────────────────────────────────────

    /**
     * Carga 15 Lúpulos reales con su porcentaje de Alfa Ácidos.
     */
    private void cargarLupulos() {
        log.info("🌿 Cargando 15 Lúpulos...");

        lupuloRepository.saveAll(java.util.Arrays.asList(
                // Lúpulos aromáticos (bajo alpha)
                crearLupulo("Cascade", "Estados Unidos", 5.5, 7.5, 36.0,
                        "Floral, cítrico, especias. Classic American aroma hop."),
                crearLupulo("Centennial", "Estados Unidos", 9.5, 10.5, 27.0,
                        "Cítrico intenso, pino. Versátil para bittering y aroma."),
                crearLupulo("Citra", "Estados Unidos", 11.5, 13.5, 22.0,
                        "Cítrico (pomelo, lima). Notas florales. Popular en IPAs."),
                crearLupulo("Fuggles", "Reino Unido", 4.5, 5.3, 26.0,
                        "Especias suaves, madera. Clásico británico. Noble character."),
                crearLupulo("Saaz", "República Checa", 3.0, 4.5, 26.0,
                        "Floral, especias sutiles, herbal. Base noble legítima Pilsen."),
                crearLupulo("Hallertauer", "Alemania", 3.5, 5.5, 38.0,
                        "Herbal, floral, delicado. Otro noble clásico. Lagers alemanas."),

                // Lúpulos high-alpha (bittering)
                crearLupulo("Magnum", "Alemania", 12.0, 14.0, 22.0,
                        "Limpio, amargo equilibrado. Bittering versátil, bajo aroma residual."),
                crearLupulo("Columbus", "Estados Unidos", 15.0, 17.5, 35.0,
                        "Amargo limpio con toques especias/pimienta. Four-purpose hop."),
                crearLupulo("Nugget", "Estados Unidos", 12.5, 15.0, 40.0,
                        "Amargo limpio con woody notes. Excelente para stouts y porters."),
                crearLupulo("Warrior", "Estados Unidos", 16.0, 18.5, 28.0,
                        "Limpio, neutral. Bittering de bajo aroma residual. IPAs limpias."),
                crearLupulo("Target", "Reino Unido", 10.0, 12.5, 27.0,
                        "Amargo grainy, herbal. Bittering tradicional británico."),

                // Lúpulos eclécticos/modernos
                crearLupulo("Mosaic", "Estados Unidos", 10.5, 13.5, 30.0,
                        "Frutal, tropical, herbal complejo. Moderno experimental aroma hop."),
                crearLupulo("Azacca", "Estados Unidos", 14.0, 16.0, 32.0,
                        "Tropical, piña, limón, florales. Limonada tropical en IPAs."),
                crearLupulo("Strisselspalt", "Francia", 4.5, 6.5, 25.0,
                        "Herbal francés, floral suave. Noble europeo alternativo."),
                crearLupulo("Sorachi Ace", "Japón", 12.5, 15.5, 40.0,
                        "Limón intenso, anís. Aroma único. Interesante en lagers modernas.")
        ));

        log.info("✅ 15 Lúpulos cargados correctamente.");
    }

    /**
     * Crea una instancia de Lúpulo con los parámetros especificados.
     */
    private Lupulo crearLupulo(String nombre, String origen,
                             Double alphaMin, Double alphaMax, Double cohumulona,
                             String descripcion) {
        Lupulo lupulo = new Lupulo();
        lupulo.setNombre(nombre);
        lupulo.setOrigen(origen);
        lupulo.setPorcentajeAlpha((alphaMin + alphaMax) / 2); // Promedio de min y max
        lupulo.setPorcentajeBeta(alphaMax * 0.5); // Estimación beta típica
        lupulo.setPorcentajeCohumulona(cohumulona);
        lupulo.setDescripcion(descripcion);
        return lupulo;
    }

    // ────────────────────────────────────────────────────────────────────────────────

    /**
     * Carga 10 Levaduras reales con sus características de fermentación.
     */
    private void cargarLevaduras() {
        log.info("🧬 Cargando 10 Levaduras...");

        levaduraRepository.saveAll(java.util.Arrays.asList(
                // Levaduras Ale
                crearLevadura("US-05", TipoLevadura.ALE, "Fermentis", "US-05",
                        73, 77, 15, 24, 12.0,
                        "American ale versátil. Limpia, neutra. Excelente para lagers ale y cervezas claras."),
                crearLevadura("S-04", TipoLevadura.ALE, "Fermentis", "S-04",
                        72, 80, 15, 25, 12.0,
                        "English ale. Más espasmódica, notas frutales. Clásica para pale ales británicas."),
                crearLevadura("Nottingham", TipoLevadura.ALE, "Lallemand", "Nottingham",
                        73, 80, 14, 21, 10.0,
                        "Inglesa neutra. Flocculent, seca finishing. Versátil para ales tradicionales."),
                crearLevadura("Windsor", TipoLevadura.ALE, "Lallemand", "Windsor",
                        69, 76, 15, 22, 11.0,
                        "English ale clásica. Atenuación baja, mayor cuerpo residual. Milds y bitters."),
                crearLevadura("SafAle WB-06", TipoLevadura.ALE, "Fermentis", "WB-06",
                        70, 80, 15, 24, 11.5,
                        "Witbier/Belgian ale. Polifenólica, neutrocéfalo. Base para weizens belgas."),

                // Levaduras Lager
                crearLevadura("W-34/70", TipoLevadura.LAGER, "Wyeast", "WY2278",
                        77, 83, 8, 13, 11.0,
                        "Lager alemana clásica. Limpia, atenuación alta. Pilsens y lagers claras."),
                crearLevadura("34/70", TipoLevadura.LAGER, "Fermentis", "34/70",
                        77, 85, 9, 14, 11.5,
                        "Lager versátil. Bavara/pilsen. Limpia fermentación, rápida."),
                crearLevadura("Saflager S-189", TipoLevadura.LAGER, "Fermentis", "S-189",
                        77, 84, 10, 15, 12.0,
                        "Lager bavara. Malty, suave. Bock y lagers oscuras."),

                // Levaduras Belga
                crearLevadura("Saflager BE-656", TipoLevadura.BELGA, "Fermentis", "BE-656",
                        75, 82, 16, 22, 11.0,
                        "Belga multipropósito. Frutales complejos, ligeramente especiada. Cupalón o tripel-style."),
                crearLevadura("Belgian Ale Mix", TipoLevadura.BELGA, "Wyeast", "WY3942",
                        72, 80, 18, 25, 12.0,
                        "Híbrida ale/lager belga. Ésteres frutales, fenoles sutiles. Realmente cosmopolita.")
        ));

        log.info("✅ 10 Levaduras cargadas correctamente.");
    }

    /**
     * Crea una instancia de Levadura con los parámetros especificados.
     */
    private Levadura crearLevadura(String nombre, TipoLevadura tipo, String marca,
                                  String codigoCepa, Integer atenuacionMin, Integer atenuacionMax,
                                  Integer tempMin, Integer tempMax, Double toleranciaAlcohol,
                                  String descripcion) {
        Levadura levadura = new Levadura();
        levadura.setNombre(nombre);
        levadura.setTipo(tipo);
        levadura.setMarca(marca);
        levadura.setCodigoCepa(codigoCepa);
        levadura.setAtenuacionMin(atenuacionMin);
        levadura.setAtenuacionMax(atenuacionMax);
        levadura.setTempMinCelsius(tempMin);
        levadura.setTempMaxCelsius(tempMax);
        levadura.setToleranciaAlcohol(toleranciaAlcohol);
        levadura.setDescripcion(descripcion);
        return levadura;
    }
}

