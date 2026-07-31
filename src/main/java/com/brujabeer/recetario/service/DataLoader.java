package com.brujabeer.recetario.service;

import com.brujabeer.recetario.model.AguaPerfil;
import com.brujabeer.recetario.model.Equipo;
import com.brujabeer.recetario.model.Levadura;
import com.brujabeer.recetario.model.Levadura.TipoLevadura;
import com.brujabeer.recetario.model.Lupulo;
import com.brujabeer.recetario.model.Malta;
import com.brujabeer.recetario.repository.AguaPerfilRepository;
import com.brujabeer.recetario.repository.EquipoRepository;
import com.brujabeer.recetario.repository.LevaduraRepository;
import com.brujabeer.recetario.repository.LupuloRepository;
import com.brujabeer.recetario.repository.MaltaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class DataLoader implements CommandLineRunner {

    private final MaltaRepository maltaRepository;
    private final LupuloRepository lupuloRepository;
    private final LevaduraRepository levaduraRepository;
    private final EquipoRepository equipoRepository;
    private final AguaPerfilRepository aguaPerfilRepository;

    public DataLoader(MaltaRepository maltaRepository,
                      LupuloRepository lupuloRepository,
                      LevaduraRepository levaduraRepository,
                      EquipoRepository equipoRepository,
                      AguaPerfilRepository aguaPerfilRepository) {
        this.maltaRepository = maltaRepository;
        this.lupuloRepository = lupuloRepository;
        this.levaduraRepository = levaduraRepository;
        this.equipoRepository = equipoRepository;
        this.aguaPerfilRepository = aguaPerfilRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("🍺 Iniciando DataLoader...");

        if (maltaRepository.count() == 0) cargarMaltas();
        if (lupuloRepository.count() == 0) cargarLupulos();
        if (levaduraRepository.count() == 0) cargarLevaduras();
        if (equipoRepository.count() == 0) cargarEquipos();
        if (aguaPerfilRepository.count() == 0) cargarPerfilesAgua();

        log.info("✅ DataLoader completado.");
    }

    private void cargarMaltas() {
        maltaRepository.saveAll(Arrays.asList(
                crearMalta("Pilsen", "Base", "Weyermann", 2.0, 80.0, "Malta clara, suave, perfecta para lagers."),
                crearMalta("Pale Ale", "Base", "Crisp", 3.0, 80.0, "Versátil para ales. Aroma neutro."),
                crearMalta("Munich", "Base", "Weyermann", 12.0, 78.0, "Caramelo suave, pan y cereal."),
                crearMalta("Vienna", "Base", "Patagonia Maltera", 4.5, 79.0, "Ligeros toques tostados."),
                crearMalta("Maris Otter", "Base", "Crisp", 2.5, 82.0, "Malta inglesa premium."),
                crearMalta("Caramelo 20", "Caramelo", "Crisp", 20.0, 75.0, "Dulzor y color ámbar claro."),
                crearMalta("Caramelo 60", "Caramelo", "Weyermann", 60.0, 75.0, "Cuerpo y rojo-ámbar."),
                crearMalta("Caramelo 120", "Caramelo", "Bestmalz", 120.0, 75.0, "Notas de caramelo y melazo."),
                crearMalta("Chocolate", "Tostada", "Weyermann", 450.0, 77.0, "Notas de cacao y chocolate."),
                crearMalta("Carafa III", "Tostada", "Weyermann", 1200.0, 75.0, "Casi negra. Café/chocolate profundo."),
                crearMalta("Black Patent", "Tostada", "Crisp", 1500.0, 72.0, "Negra intensísima. Ácida."),
                crearMalta("Trigo Malteado", "Especial", "Weyermann", 1.5, 86.0, "Agrega cuerpo y espuma."),
                crearMalta("Cebada Malteada", "Especial", "Patagonia", 3.0, 80.0, "Común en lagers."),
                crearMalta("Centeno Malteado", "Especial", "Bestmalz", 1.5, 75.0, "Especiado. Cuerpo seco."),
                crearMalta("Avena Malteada", "Especial", "Crisp", 1.5, 65.0, "Textura suave y cremosa."),
                crearMalta("Munich II", "Base", "Weyermann", 25.0, 78.0, "Munich oscura, aromas de pan tostado."),
                crearMalta("Carapils", "Caramelo", "Weyermann", 4.0, 75.0, "Retención de espuma y cuerpo."),
                crearMalta("Melanoidin", "Especial", "Weyermann", 70.0, 75.0, "Ideal para red ales."),
                crearMalta("Special B", "Caramelo", "Dingemans", 300.0, 70.0, "Pasas, ciruela y caramelo quemado."),
                crearMalta("Roasted Barley", "Tostada", "Crisp", 1300.0, 65.0, "Café seco de la Irish Stout."),
                crearMalta("Abbey Malt", "Base", "Weyermann", 45.0, 78.0, "Maltosidad pronunciada."),
                crearMalta("Biscuit", "Tostada", "Dingemans", 50.0, 75.0, "Sabor a pan horneado."),
                crearMalta("Victory", "Tostada", "Briess", 55.0, 73.0, "Galleta tostada y pan tostado."),
                crearMalta("Pale Chocolate", "Tostada", "Crisp", 600.0, 72.0, "Cacao y café suaves."),
                crearMalta("CaraAroma", "Caramelo", "Weyermann", 400.0, 73.0, "Muy oscura, da cuerpo."),
                crearMalta("Munich I", "Base", "Weyermann", 15.0, 78.0, "Maltosidad pronunciada."),
                crearMalta("Caramunich I", "Caramelo", "Weyermann", 90.0, 73.0, "Caramelo claro."),
                crearMalta("Caramunich II", "Caramelo", "Weyermann", 120.0, 73.0, "Caramelo medio intenso."),
                crearMalta("Caramunich III", "Caramelo", "Weyermann", 150.0, 73.0, "Caramelo tostado profundo."),
                crearMalta("Carafa I", "Tostada", "Weyermann", 900.0, 65.0, "Café y chocolate suave.")
        ));
    }

    private Malta crearMalta(String n, String t, String m, Double c, Double r, String d) {
        Malta malta = new Malta(); malta.setNombre(n); malta.setTipo(t); malta.setMarca(m);
        malta.setColorEbc(c); malta.setRendimientoPorcentaje(r); malta.setDescripcion(d);
        return malta;
    }

    private void cargarLupulos() {
        lupuloRepository.saveAll(Arrays.asList(
                crearLupulo("Cascade", "EEUU", 5.5, 7.5, 36.0, "Floral, cítrico, especias."),
                crearLupulo("Centennial", "EEUU", 9.5, 10.5, 27.0, "Cítrico intenso, pino."),
                crearLupulo("Citra", "EEUU", 11.5, 13.5, 22.0, "Cítrico (pomelo, lima)."),
                crearLupulo("Fuggles", "Reino Unido", 4.5, 5.3, 26.0, "Especias suaves, madera."),
                crearLupulo("Saaz", "República Checa", 3.0, 4.5, 26.0, "Floral, especias sutiles."),
                crearLupulo("Hallertauer", "Alemania", 3.5, 5.5, 38.0, "Herbal, floral, delicado."),
                crearLupulo("Magnum", "Alemania", 12.0, 14.0, 22.0, "Amargo equilibrado."),
                crearLupulo("Columbus", "EEUU", 15.0, 17.5, 35.0, "Amargo limpio, especias."),
                crearLupulo("Nugget", "EEUU", 12.5, 15.0, 40.0, "Woody notes. Stouts."),
                crearLupulo("Warrior", "EEUU", 16.0, 18.5, 28.0, "Limpio, neutral."),
                crearLupulo("Target", "Reino Unido", 10.0, 12.5, 27.0, "Amargo grainy, herbal."),
                crearLupulo("Mosaic", "EEUU", 10.5, 13.5, 30.0, "Tropical, herbal complejo."),
                crearLupulo("Azacca", "EEUU", 14.0, 16.0, 32.0, "Tropical, piña, limón."),
                crearLupulo("Strisselspalt", "Francia", 4.5, 6.5, 25.0, "Floral suave."),
                crearLupulo("Sorachi Ace", "Japón", 12.5, 15.5, 40.0, "Limón intenso, anís."),
                crearLupulo("Amarillo", "EEUU", 8.0, 11.0, 24.0, "Naranja y durazno."),
                crearLupulo("Simcoe", "EEUU", 12.0, 14.0, 18.0, "Pino, tierra y cítricos."),
                crearLupulo("Galaxy", "Australia", 13.0, 15.0, 35.0, "Maracuyá, durazno."),
                crearLupulo("East Kent Goldings", "Reino Unido", 4.5, 6.5, 30.0, "Especiado, floral."),
                crearLupulo("Nelson Sauvin", "Nueva Zelanda", 12.0, 13.0, 24.0, "Uvas, grosella."),
                crearLupulo("El Dorado", "EEUU", 14.0, 16.0, 30.0, "Frutas de carozo."),
                crearLupulo("Sabro", "EEUU", 13.0, 16.0, 22.0, "Coco, mandarina."),
                crearLupulo("Mandarina Bavaria", "Alemania", 7.0, 10.0, 31.0, "Mandarina dulce."),
                crearLupulo("Chinook", "EEUU", 12.0, 14.0, 30.0, "Pino fuerte y especiado."),
                crearLupulo("Vic Secret", "Australia", 14.0, 17.0, 53.0, "Piña limpia y pino."),
                crearLupulo("Northern Brewer", "Alemania", 7.0, 10.0, 29.0, "Menta y pino. Clásico."),
                crearLupulo("Perle", "Alemania", 6.0, 8.5, 30.0, "Especiado floral sutil."),
                crearLupulo("Tettnang", "Alemania", 3.5, 5.5, 24.0, "Noble especiado, herbal."),
                crearLupulo("Ekuanot", "EEUU", 13.0, 16.5, 36.0, "Melón, papaya, pimentón."),
                crearLupulo("Idaho 7", "EEUU", 13.0, 15.0, 40.0, "Damasco, té negro, pino.")
        ));
    }

    private Lupulo crearLupulo(String n, String o, Double amin, Double amax, Double coh, String d) {
        Lupulo lupulo = new Lupulo(); lupulo.setNombre(n); lupulo.setOrigen(o);
        lupulo.setPorcentajeAlpha((amin + amax) / 2); lupulo.setPorcentajeBeta(amax * 0.5);
        lupulo.setPorcentajeCohumulona(coh); lupulo.setDescripcion(d);
        return lupulo;
    }

    private void cargarLevaduras() {
        levaduraRepository.saveAll(Arrays.asList(
                crearLevadura("US-05", TipoLevadura.ALE, "Fermentis", "US-05", 73, 77, 15, 24, 12.0, "Neutra."),
                crearLevadura("S-04", TipoLevadura.ALE, "Fermentis", "S-04", 72, 80, 15, 25, 12.0, "Notas frutales."),
                crearLevadura("Nottingham", TipoLevadura.ALE, "Lallemand", "Nottingham", 73, 80, 14, 21, 10.0, "Seca."),
                crearLevadura("Windsor", TipoLevadura.ALE, "Lallemand", "Windsor", 69, 76, 15, 22, 11.0, "Cuerpo residual."),
                crearLevadura("SafAle WB-06", TipoLevadura.ALE, "Fermentis", "WB-06", 70, 80, 15, 24, 11.5, "Witbier."),
                crearLevadura("W-34/70", TipoLevadura.LAGER, "Wyeast", "WY2278", 77, 83, 8, 13, 11.0, "Lager alemana."),
                crearLevadura("34/70", TipoLevadura.LAGER, "Fermentis", "34/70", 77, 85, 9, 14, 11.5, "Limpia rápida."),
                crearLevadura("Saflager S-189", TipoLevadura.LAGER, "Fermentis", "S-189", 77, 84, 10, 15, 12.0, "Malty."),
                crearLevadura("Saflager BE-656", TipoLevadura.BELGA, "Fermentis", "BE-656", 75, 82, 16, 22, 11.0, "Belga."),
                crearLevadura("Belgian Ale Mix", TipoLevadura.BELGA, "Wyeast", "WY3942", 72, 80, 18, 25, 12.0, "Híbrida."),
                crearLevadura("WLP001", TipoLevadura.ALE, "White Labs", "WLP001", 73, 80, 15, 22, 15.0, "California Ale limpia."),
                crearLevadura("WLP400", TipoLevadura.ALE, "White Labs", "WLP400", 74, 78, 19, 23, 10.0, "Belgian Wit Ale."),
                crearLevadura("M44", TipoLevadura.ALE, "Mangrove Jack's", "M44", 77, 85, 15, 23, 11.0, "US West Coast."),
                crearLevadura("Kveik Voss", TipoLevadura.ALE, "Lallemand", "Kveik", 76, 82, 25, 40, 12.0, "Alta temperatura."),
                crearLevadura("S-33", TipoLevadura.ALE, "Fermentis", "S-33", 68, 72, 15, 20, 11.0, "Baja atenuación.")
        ));
    }

    private Levadura crearLevadura(String n, TipoLevadura t, String m, String c, Integer amin, Integer amax, Integer tmin, Integer tmax, Double tol, String d) {
        Levadura levadura = new Levadura(); levadura.setNombre(n); levadura.setTipo(t); levadura.setMarca(m);
        levadura.setCodigoCepa(c); levadura.setAtenuacionMin(amin); levadura.setAtenuacionMax(amax);
        levadura.setTempMinCelsius(tmin); levadura.setTempMaxCelsius(tmax); levadura.setToleranciaAlcohol(tol);
        levadura.setDescripcion(d); return levadura;
    }

    private void cargarEquipos() {
        Equipo b20 = new Equipo(); b20.setNombre("BrujaBeer 20L"); b20.setEficienciaBrewhouse(72.0); b20.setTasaEvaporacion(4.0); b20.setPerdidaTrub(2.0); b20.setPerdidaMacerador(1.5); equipoRepository.save(b20);
        Equipo p10 = new Equipo(); p10.setNombre("Olla Principiante 10L"); p10.setEficienciaBrewhouse(65.0); p10.setTasaEvaporacion(3.0); p10.setPerdidaTrub(1.5); p10.setPerdidaMacerador(1.0); equipoRepository.save(p10);
        Equipo h50 = new Equipo(); h50.setNombre("Sistema HERMS 50L"); h50.setEficienciaBrewhouse(78.0); h50.setTasaEvaporacion(5.0); h50.setPerdidaTrub(3.0); h50.setPerdidaMacerador(2.0); equipoRepository.save(h50);
        Equipo p100 = new Equipo(); p100.setNombre("Olla Profesional 100L"); p100.setEficienciaBrewhouse(80.0); p100.setTasaEvaporacion(8.0); p100.setPerdidaTrub(5.0); p100.setPerdidaMacerador(3.0); equipoRepository.save(p100);
    }

    private void cargarPerfilesAgua() {
        aguaPerfilRepository.save(crearAgua("Buenos Aires", 40.0, 8.0, 25.0, 35.0, 30.0, 120.0));
        aguaPerfilRepository.save(crearAgua("Pilsen (Checa)", 7.0, 2.0, 2.0, 5.0, 5.0, 15.0));
        aguaPerfilRepository.save(crearAgua("Burton-on-Trent", 275.0, 40.0, 25.0, 35.0, 450.0, 260.0));
        aguaPerfilRepository.save(crearAgua("Agua Destilada", 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
    }

    private AguaPerfil crearAgua(String n, Double ca, Double mg, Double na, Double cl, Double so4, Double hco3) {
        AguaPerfil perfil = new AguaPerfil(); perfil.setNombre(n); perfil.setCalcio(ca); perfil.setMagnesio(mg);
        perfil.setSodio(na); perfil.setCloruro(cl); perfil.setSulfato(so4); perfil.setBicarbonato(hco3); return perfil;
    }
}
