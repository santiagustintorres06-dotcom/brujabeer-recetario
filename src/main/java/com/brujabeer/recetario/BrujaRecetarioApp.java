package com.brujabeer.recetario;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║  PUNTO DE ENTRADA PRINCIPAL — BrujaBeer Recetario                       ║
 * ║                                                                          ║
 * ║  DECISIÓN ARQUITECTÓNICA:                                                ║
 * ║  Esta clase NO extiende Application (JavaFX) directamente.               ║
 * ║  Delegamos el launch() a JavaFxLauncher para evitar conflictos entre     ║
 * ║  el Java Module System (JPMS) y el class-scanning de Spring Boot.        ║
 * ║                                                                          ║
 * ║  FLUJO DE ARRANQUE:                                                      ║
 * ║  main() → Application.launch(JavaFxLauncher)                             ║
 * ║         → JavaFxLauncher.init()  → SpringApplicationBuilder.run()        ║
 * ║         → JavaFxLauncher.start() → FXMLLoader carga la vista principal   ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
@SpringBootApplication
public class BrujaRecetarioApp {

    public static void main(String[] args) {
        /*
         * ¡IMPORTANTE! No llamamos SpringApplication.run() aquí.
         * En una desktop app, JavaFX DEBE controlar el hilo principal (EDT equivalente).
         * JavaFxLauncher.init() se encargará de arrancar el contexto de Spring.
         */
        Application.launch(JavaFxLauncher.class, args);
    }
}
