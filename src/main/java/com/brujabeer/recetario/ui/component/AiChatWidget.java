package com.brujabeer.recetario.ui.component;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * Widget de chat flotante del Asistente Tecnico de La Bruja Beer.
 *
 * - Burbuja circular (FAB) en la esquina inferior derecha de la ventana.
 * - Al pulsarla, despliega una tarjeta de conversacion de 380x520 px.
 * - Se comunica con el microservicio Python (FastAPI en puerto 8000)
 *   mediante java.net.http.HttpClient en hilos secundarios (sin bloquear la UI).
 * - Codificacion UTF-8 estricta para soporte completo del castellano.
 * - Sin emojis de color para compatibilidad con fuentes de JavaFX en Windows.
 */
public class AiChatWidget {

    private static final String API_URL = "http://127.0.0.1:8000/api/query";

    // Content-Type con charset explícito: evita el error HTTP 422
    private static final String CONTENT_TYPE = "application/json; charset=utf-8";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(java.time.Duration.ofSeconds(6))
            .build();

    private final Button fabButton;
    private final VBox chatCard;
    private final VBox messagesBox;
    private final ScrollPane scrollPane;
    private final TextField inputField;
    private final Button btnSend;
    private final Label statusDot;

    private boolean isOpen   = false;
    private boolean isSending = false;

    public AiChatWidget() {
        // ── 1. Boton Flotante Circular (FAB) ──────────────────────────────
        fabButton = new Button("IA");          // Texto puro: compatible con todas las fuentes
        fabButton.getStyleClass().add("bb-chat-fab");
        fabButton.setFocusTraversable(false);
        StackPane.setAlignment(fabButton, Pos.BOTTOM_RIGHT);
        fabButton.setTranslateX(-24);
        fabButton.setTranslateY(-24);

        // ── 2. Tarjeta Ventana de Chat ─────────────────────────────────────
        chatCard = new VBox();
        chatCard.getStyleClass().add("bb-chat-card");
        chatCard.setPrefSize(390, 530);
        chatCard.setMaxSize(390, 530);
        chatCard.setVisible(false);
        chatCard.setManaged(false);
        chatCard.setOpacity(0.0);
        StackPane.setAlignment(chatCard, Pos.BOTTOM_RIGHT);
        chatCard.setTranslateX(-24);
        chatCard.setTranslateY(-90);

        // ── 3. Encabezado ─────────────────────────────────────────────────
        HBox header = new HBox(10);
        header.getStyleClass().add("bb-chat-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(11, 14, 11, 14));

        // Icono de marca (texto ASCII — compatible con Segoe UI)
        Label brandIcon = new Label("[BB]");
        brandIcon.getStyleClass().add("bb-chat-brand-icon");

        VBox titleBox = new VBox(2);
        Label title = new Label("Asistente BrujaBeer");
        title.getStyleClass().add("bb-chat-title");

        HBox statusBox = new HBox(5);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusDot = new Label("●");
        statusDot.getStyleClass().add("bb-chat-status-dot");
        Label statusText = new Label("Servicio activo");
        statusText.getStyleClass().add("bb-chat-status-text");
        statusBox.getChildren().addAll(statusDot, statusText);
        titleBox.getChildren().addAll(title, statusBox);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        Button btnClose = new Button("✕");
        btnClose.getStyleClass().add("bb-chat-close-btn");
        btnClose.setOnAction(e -> toggleChat());

        header.getChildren().addAll(brandIcon, titleBox, btnClose);

        // ── 4. Area de Mensajes ───────────────────────────────────────────
        messagesBox = new VBox(10);
        messagesBox.setPadding(new Insets(12));

        scrollPane = new ScrollPane(messagesBox);
        scrollPane.getStyleClass().add("bb-chat-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Mensaje de bienvenida
        agregarMensajeAsistente(
            "Hola. Soy el asistente tecnico de La Bruja Beer.\n\n" +
            "Podes consultarme sobre:\n" +
            "- Eficiencia del equipo y metricas de produccion\n" +
            "- Alertas de pH en macerado o lavado\n" +
            "- Historial de lotes elaborados\n" +
            "- Procedimientos de macerado, fermentacion y maduracion\n" +
            "- Manuales tecnicos de equipos (bomba, intercambiador)\n" +
            "- Sanitizacion CIP y POEs de limpieza\n\n" +
            "Usá los botones de abajo para consultas frecuentes."
        );

        // ── 5. Chips de Consultas Rapidas ──────────────────────────────────
        // Fila 1
        HBox chipsRow1 = new HBox(6);
        chipsRow1.setAlignment(Pos.CENTER_LEFT);
        chipsRow1.getChildren().addAll(
            crearChip("[Eficiencia]",    "Cual es la eficiencia promedio del equipo?"),
            crearChip("[Alertas pH]",    "Hay lotes con alertas o desvios de pH en macerado?"),
            crearChip("[Lotes]",         "Listar el historial de todos los lotes elaborados")
        );

        // Fila 2
        HBox chipsRow2 = new HBox(6);
        chipsRow2.setAlignment(Pos.CENTER_LEFT);
        chipsRow2.getChildren().addAll(
            crearChip("[Macerado]",      "Como se hace el macerado de una cerveza?"),
            crearChip("[Fermentacion]",  "Como controlar la fermentacion de la cerveza?"),
            crearChip("[CIP]",           "Como se realiza la sanitizacion CIP con acido peracetico?")
        );

        VBox chipsContainer = new VBox(5, chipsRow1, chipsRow2);
        chipsContainer.getStyleClass().add("bb-chat-chips");
        chipsContainer.setPadding(new Insets(8, 12, 8, 12));

        // ── 6. Barra de Entrada ───────────────────────────────────────────
        HBox inputBar = new HBox(8);
        inputBar.getStyleClass().add("bb-chat-input-bar");
        inputBar.setPadding(new Insets(10, 12, 10, 12));
        inputBar.setAlignment(Pos.CENTER);

        inputField = new TextField();
        inputField.getStyleClass().add("bb-chat-input");
        inputField.setPromptText("Escribe tu consulta aqui...");
        HBox.setHgrow(inputField, Priority.ALWAYS);
        inputField.setOnAction(e -> enviarConsulta());

        btnSend = new Button(">");
        btnSend.getStyleClass().add("bb-chat-send-btn");
        btnSend.setOnAction(e -> enviarConsulta());

        inputBar.getChildren().addAll(inputField, btnSend);

        // Armar tarjeta completa
        chatCard.getChildren().addAll(header, scrollPane, chipsContainer, inputBar);

        // Accion del FAB
        fabButton.setOnAction(e -> toggleChat());
    }

    // ── Helpers de construccion ───────────────────────────────────────────────

    private Button crearChip(String label, String query) {
        Button chip = new Button(label);
        chip.getStyleClass().add("bb-chat-chip");
        chip.setOnAction(e -> enviarDesdeChip(query));
        return chip;
    }

    private void enviarDesdeChip(String query) {
        if (isSending) return;
        inputField.setText(query);
        enviarConsulta();
    }

    // ── Toggle del panel de chat ──────────────────────────────────────────────

    public void toggleChat() {
        isOpen = !isOpen;
        if (isOpen) {
            chatCard.setVisible(true);
            chatCard.setManaged(true);
            FadeTransition ft = new FadeTransition(Duration.millis(200), chatCard);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
            fabButton.setText("X");
            Platform.runLater(inputField::requestFocus);
        } else {
            FadeTransition ft = new FadeTransition(Duration.millis(150), chatCard);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(e -> {
                chatCard.setVisible(false);
                chatCard.setManaged(false);
            });
            ft.play();
            fabButton.setText("IA");
        }
    }

    // ── Envio de consulta al microservicio ────────────────────────────────────

    private void enviarConsulta() {
        String texto = inputField.getText().trim();
        if (texto.isEmpty() || isSending) return;

        isSending = true;
        btnSend.setDisable(true);
        inputField.setDisable(true);
        inputField.clear();

        agregarMensajeUsuario(texto);
        HBox bubbleLoading = agregarMensajeCargando();

        CompletableFuture.runAsync(() -> {
            try {
                // Serializar a JSON y enviar con charset UTF-8 explícito
                String jsonBody = "{\"query\": \"" + escapeJson(texto) + "\"}";
                byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .timeout(java.time.Duration.ofSeconds(50))
                        .header("Content-Type", CONTENT_TYPE)
                        .POST(HttpRequest.BodyPublishers.ofByteArray(bodyBytes))
                        .build();

                HttpResponse<String> response = httpClient.send(
                        request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

                Platform.runLater(() -> {
                    messagesBox.getChildren().remove(bubbleLoading);
                    if (response.statusCode() == 200) {
                        try {
                            agregarMensajeAsistente(extractAnswer(response.body()));
                        } catch (Exception ex) {
                            agregarMensajeAsistente(
                                "Recibi la respuesta del servidor pero no pude interpretarla. " +
                                "Por favor intenta de nuevo."
                            );
                        }
                    } else if (response.statusCode() == 422) {
                        agregarMensajeAsistente(
                            "El servicio no pudo interpretar la consulta " +
                            "(verifica que no tenga caracteres especiales inusuales)."
                        );
                    } else if (response.statusCode() == 500) {
                        agregarMensajeAsistente(
                            "Ocurrio un error interno en el motor de IA. " +
                            "Espera unos segundos e intenta de nuevo."
                        );
                    } else {
                        agregarMensajeAsistente(
                            "El servicio respondio con un estado inesperado (HTTP " +
                            response.statusCode() + "). Intenta de nuevo en unos segundos."
                        );
                    }
                    finalizarEnvio();
                });

            } catch (java.net.ConnectException ce) {
                Platform.runLater(() -> {
                    messagesBox.getChildren().remove(bubbleLoading);
                    agregarMensajeAsistente(
                        "El asistente no esta disponible en este momento.\n\n" +
                        "El servicio de IA se inicia automaticamente en segundo plano. " +
                        "Espera unos 30 segundos y vuelve a intentarlo."
                    );
                    finalizarEnvio();
                });
            } catch (java.net.http.HttpTimeoutException te) {
                Platform.runLater(() -> {
                    messagesBox.getChildren().remove(bubbleLoading);
                    agregarMensajeAsistente(
                        "La consulta tomo demasiado tiempo. " +
                        "El modelo de IA puede estar procesando una solicitud previa. " +
                        "Intenta de nuevo en un momento."
                    );
                    finalizarEnvio();
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    messagesBox.getChildren().remove(bubbleLoading);
                    agregarMensajeAsistente(
                        "Ocurrio un error inesperado al comunicarse con el asistente. " +
                        "Verifica que la aplicacion este completamente iniciada."
                    );
                    finalizarEnvio();
                });
            }
        });
    }

    private void finalizarEnvio() {
        isSending = false;
        btnSend.setDisable(false);
        inputField.setDisable(false);
        inputField.requestFocus();
        scrollBottom();
    }

    // ── Construccion de burbujas de mensaje ───────────────────────────────────

    private void agregarMensajeUsuario(String texto) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_RIGHT);
        row.setPadding(new Insets(0, 0, 0, 60));

        VBox bubble = new VBox();
        bubble.getStyleClass().add("chat-bubble-user");

        Label label = new Label(texto);
        label.setWrapText(true);
        label.getStyleClass().add("chat-text-user");

        bubble.getChildren().add(label);
        row.getChildren().add(bubble);
        messagesBox.getChildren().add(row);
        scrollBottom();
    }

    private void agregarMensajeAsistente(String markdownText) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.TOP_LEFT);

        // Avatar textual (compatible con todas las fuentes de Windows)
        Label avatar = new Label("BB");
        avatar.getStyleClass().add("bb-chat-avatar");

        VBox bubble = new VBox(4);
        bubble.getStyleClass().add("chat-bubble-ai");
        bubble.setMaxWidth(285);

        TextFlow textFlow = renderSimpleMarkdown(markdownText);
        bubble.getChildren().add(textFlow);

        row.getChildren().addAll(avatar, bubble);
        messagesBox.getChildren().add(row);
        scrollBottom();
    }

    private HBox agregarMensajeCargando() {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        Label avatar = new Label("BB");
        avatar.getStyleClass().add("bb-chat-avatar");

        HBox bubble = new HBox();
        bubble.getStyleClass().add("chat-bubble-ai");
        Label label = new Label("Analizando consulta...");
        label.getStyleClass().add("bb-chat-loading-text");
        bubble.getChildren().add(label);

        row.getChildren().addAll(avatar, bubble);
        messagesBox.getChildren().add(row);
        scrollBottom();
        return row;
    }

    // ── Render simple de Markdown (negritas y saltos de linea) ────────────────

    private TextFlow renderSimpleMarkdown(String text) {
        TextFlow textFlow = new TextFlow();
        textFlow.setMaxWidth(275);

        // Procesar saltos de linea y **negritas**
        String[] lines = text.split("\n", -1);
        boolean firstLine = true;

        for (String line : lines) {
            if (!firstLine) {
                textFlow.getChildren().add(new Text("\n"));
            }
            firstLine = false;

            // Dentro de cada linea procesar **negrita**
            String[] parts = line.split("(?=\\*\\*)|(?<=\\*\\*)");
            boolean bold = false;
            for (String part : parts) {
                if ("**".equals(part)) {
                    bold = !bold;
                    continue;
                }
                Text t = new Text(part);
                t.getStyleClass().add("chat-text-ai");
                if (bold) {
                    t.getStyleClass().add("chat-text-bold");
                }
                textFlow.getChildren().add(t);
            }
        }
        return textFlow;
    }

    private void scrollBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    // ── Parser JSON manual (sin dependencia externa) ──────────────────────────

    private static String extractAnswer(String json) {
        int idx = json.indexOf("\"answer\":");
        if (idx == -1) return json;
        int start = json.indexOf("\"", idx + 9);
        if (start == -1) return json;
        StringBuilder sb = new StringBuilder();
        boolean escape = false;
        for (int i = start + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (escape) {
                switch (c) {
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case 'r' -> sb.append('\r');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case 'u' -> {
                        if (i + 4 < json.length()) {
                            try {
                                int code = Integer.parseInt(json.substring(i + 1, i + 5), 16);
                                sb.appendCodePoint(code);
                                i += 4;
                            } catch (NumberFormatException ignored) {
                                sb.append(c);
                            }
                        }
                    }
                    default -> sb.append(c);
                }
                escape = false;
            } else if (c == '\\') {
                escape = true;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ── Getters para el Launcher ──────────────────────────────────────────────

    public Button getFabButton() { return fabButton; }
    public VBox   getChatCard()  { return chatCard;  }
}
