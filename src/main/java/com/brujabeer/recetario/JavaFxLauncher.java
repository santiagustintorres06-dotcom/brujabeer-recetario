package com.brujabeer.recetario;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║  MOTOR DE INTEGRACIÓN — Spring Boot + JavaFX                            ║
 * ║                                                                          ║
 * ║  Esta clase NO es un Spring Bean (@Component).                           ║
 * ║  JavaFX la gestiona con su propio ciclo de vida en 3 fases:             ║
 * ║                                                                          ║
 * ║  1. init()  → Hilo Launcher de JavaFX. Aquí arrancamos Spring.          ║
 * ║  2. start() → JavaFX Application Thread. Aquí mostramos la ventana.     ║
 * ║  3. stop()  → Al cerrar la app. Aquí cerramos Spring limpiamente.       ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
public class JavaFxLauncher extends Application {

    /**
     * El contexto de Spring. Lo guardamos como campo de instancia para que
     * init() lo cree y start()/stop() puedan accederlo.
     */
    private ConfigurableApplicationContext springContext;

    /**
     * FASE 1: Se ejecuta ANTES del JavaFX Application Thread.
     *
     * Arrancamos Spring aquí (y no en main()) para que el contexto
     * esté 100% listo —con todos los @Bean, @Repository, @Service
     * instanciados— antes de intentar cargar la primera vista FXML.
     */
    @Override
    public void init() throws Exception {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.springContext = new SpringApplicationBuilder(BrujaRecetarioApp.class)
                .run(args);
    }

    /**
     * FASE 2: Se ejecuta en el JavaFX Application Thread.
     *
     * LA LÍNEA CLAVE del proyecto:
     *   loader.setControllerFactory(springContext::getBean)
     *
     * Sin esta línea, FXMLLoader crea los controllers con new + reflexión,
     * y todos los @Autowired quedan como null (no-op de Spring).
     * Con esta línea, delegamos la creación al IoC de Spring, que inyecta
     * todas las dependencias antes de devolver el controller a JavaFX.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/RecipeForm.fxml")
        );

        // ← La integración real ocurre aquí: Spring inyecta en el controller
        loader.setControllerFactory(springContext::getBean);

        Parent root = loader.load();

        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(
                getClass().getResource("/styles/main.css").toExternalForm()
        );

        primaryStage.setTitle("🍺  BrujaBeer — Recetario");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    /**
     * FASE 3: Se ejecuta cuando el usuario cierra la ventana principal.
     *
     * Es CRÍTICO cerrar Spring antes de Platform.exit() para que
     * @PreDestroy, las conexiones al pool de DB y los recursos JPA
     * se liberen ordenadamente y no haya corrupción de datos.
     */
    @Override
    public void stop() throws Exception {
        springContext.close();
        Platform.exit();
    }
}
