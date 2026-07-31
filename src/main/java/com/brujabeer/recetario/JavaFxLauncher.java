package com.brujabeer.recetario;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
import com.brujabeer.recetario.ui.controller.LoteFormController;
import javafx.scene.image.Image;

public class JavaFxLauncher extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() throws Exception {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.springContext = new SpringApplicationBuilder(BrujaRecetarioApp.class)
                .run(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // ── Tab 1: Recetario ────────────────────────────────────────────────
        FXMLLoader recetarioLoader = new FXMLLoader(
                getClass().getResource("/fxml/RecipeForm.fxml")
        );
        recetarioLoader.setControllerFactory(springContext::getBean);
        Parent recetarioRoot = recetarioLoader.load();

        Tab tabRecetario = new Tab("🍺 Recetario");
        tabRecetario.setContent(recetarioRoot);
        tabRecetario.setClosable(false);

        // ── Tab 2: Lotes ────────────────────────────────────────────────────
        FXMLLoader lotesLoader = new FXMLLoader(
                getClass().getResource("/fxml/LoteForm.fxml")
        );
        lotesLoader.setControllerFactory(springContext::getBean);
        Parent lotesRoot = lotesLoader.load();

        Tab tabLotes = new Tab("📊 Lotes de Cocción");
        tabLotes.setContent(lotesRoot);
        tabLotes.setClosable(false);

        // ── Tab 3: Mochila ──────────────────────────────────────────────────
        FXMLLoader mochilaLoader = new FXMLLoader(
                getClass().getResource("/fxml/MochilaForm.fxml")
        );
        mochilaLoader.setControllerFactory(springContext::getBean);
        Parent mochilaRoot = mochilaLoader.load();

        Tab tabMochila = new Tab("🎒 Mochila");
        tabMochila.setContent(mochilaRoot);
        tabMochila.setClosable(false);

        LoteFormController lotesController = lotesLoader.getController();
        tabLotes.setOnSelectionChanged(e -> {
            if (tabLotes.isSelected() && lotesController != null) {
                lotesController.cargarRecetas();
            }
        });

        // ── TabPane principal ───────────────────────────────────────────────
        TabPane tabPane = new TabPane(tabRecetario, tabLotes, tabMochila);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Scene scene = new Scene(tabPane, 1200, 800);
        scene.getStylesheets().add(
                getClass().getResource("/styles/main.css").toExternalForm()
        );

        // Icono de la ventana
        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo.png")));
        } catch (Exception ignored) {}

        primaryStage.setTitle("BrujaBeer — Cervecería Artesanal");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);
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
