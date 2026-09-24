package com.brujabeer.recetario;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
import com.brujabeer.recetario.service.PythonServiceManager;
import com.brujabeer.recetario.ui.component.AiChatWidget;
import com.brujabeer.recetario.ui.controller.LoteFormController;
import javafx.scene.image.Image;

import java.io.*;
import java.util.Properties;

public class JavaFxLauncher extends Application {

    private ConfigurableApplicationContext springContext;
    private static final String CONFIG_FILE = "brujabeer-config.properties";
    private static final String THEME_KEY = "theme.dark-mode";

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
                lotesController.cargarHistorialLotes();
            }
        });

        // ── TabPane principal ───────────────────────────────────────────────
        TabPane tabPane = new TabPane(tabRecetario, tabLotes, tabMochila);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // ── Botón Toggle de Tema (☀️ / 🌙) ─────────────────────────────────
        Button btnThemeToggle = new Button("🌙");
        btnThemeToggle.getStyleClass().add("bb-theme-toggle");
        btnThemeToggle.setTooltip(new Tooltip("Cambiar modo claro / oscuro"));

        // ── Layout principal con botón de tema ──────────────────────────────
        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        // ── Iniciar verificación del Asistente Python en segundo plano ──────
        PythonServiceManager.startAsync();

        // ── Widget Asistente IA (Burbuja flotante en esquina inferior derecha)
        AiChatWidget chatWidget = new AiChatWidget();

        // ── Posicionar botón de tema en la esquina superior derecha ─────────
        StackPane rootStack = new StackPane();
        rootStack.getChildren().addAll(mainLayout, btnThemeToggle, chatWidget.getChatCard(), chatWidget.getFabButton());
        StackPane.setAlignment(btnThemeToggle, Pos.TOP_RIGHT);
        btnThemeToggle.setTranslateX(-15);
        btnThemeToggle.setTranslateY(6);

        Scene scene = new Scene(rootStack, 1200, 800);
        scene.getStylesheets().add(
                getClass().getResource("/styles/main.css").toExternalForm()
        );

        // ── Cargar preferencia de tema guardada ─────────────────────────────
        boolean darkMode = cargarPreferenciaTema();
        if (darkMode) {
            rootStack.getStyleClass().add("dark-mode");
            btnThemeToggle.setText("☀️");
        }

        // ── Acción del botón de tema ────────────────────────────────────────
        btnThemeToggle.setOnAction(e -> {
            boolean isDark = rootStack.getStyleClass().contains("dark-mode");
            if (isDark) {
                rootStack.getStyleClass().remove("dark-mode");
                btnThemeToggle.setText("🌙");
                guardarPreferenciaTema(false);
            } else {
                rootStack.getStyleClass().add("dark-mode");
                btnThemeToggle.setText("☀️");
                guardarPreferenciaTema(true);
            }
        });

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
     * Carga la preferencia de tema desde el archivo de configuración local.
     */
    private boolean cargarPreferenciaTema() {
        try {
            File configFile = new File(CONFIG_FILE);
            if (configFile.exists()) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    props.load(fis);
                }
                return Boolean.parseBoolean(props.getProperty(THEME_KEY, "false"));
            }
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Guarda la preferencia de tema en un archivo de configuración local.
     */
    private void guardarPreferenciaTema(boolean darkMode) {
        try {
            Properties props = new Properties();
            props.setProperty(THEME_KEY, String.valueOf(darkMode));
            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                props.store(fos, "BrujaBeer — Preferencias de Usuario");
            }
        } catch (Exception ignored) {}
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
        PythonServiceManager.stop();
        springContext.close();
        Platform.exit();
    }
}
