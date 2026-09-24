package com.brujabeer.recetario.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Gestor del ciclo de vida del microservicio Python (FastAPI + ChromaDB + Gemini).
 * Se encarga de verificar si el servicio está activo y, si no lo está,
 * levantarlo de forma transparente y silenciosa en segundo plano sin consola.
 */
public class PythonServiceManager {

    private static final Logger log = LoggerFactory.getLogger(PythonServiceManager.class);
    private static final String HEALTH_URL = "http://127.0.0.1:8000/api/health";
    private static Process pythonProcess = null;
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    /**
     * Inicia de forma asíncrona la verificación y el arranque silencioso del servicio.
     */
    public static void startAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                if (isHealthy()) {
                    log.info("Microservicio de IA ya se encuentra activo en http://127.0.0.1:8000");
                    return;
                }

                File pythonDir = locatePythonProject();
                if (pythonDir == null || !pythonDir.exists()) {
                    log.warn("No se encontró el directorio del Asistente Python en las rutas conocidas.");
                    return;
                }

                log.info("Iniciando Asistente Python en segundo plano desde: {}", pythonDir.getAbsolutePath());
                ProcessBuilder pb = new ProcessBuilder("py", "-m", "uvicorn", "api.main:app", "--host", "127.0.0.1", "--port", "8000");
                pb.directory(pythonDir);
                pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
                pb.redirectError(ProcessBuilder.Redirect.DISCARD);

                // En Windows, ejecutar de manera desatendida sin ventana de consola
                pythonProcess = pb.start();

                // Esperar unos momentos hasta que responda el health check
                for (int i = 0; i < 15; i++) {
                    Thread.sleep(1000);
                    if (isHealthy()) {
                        log.info("Microservicio de IA iniciado y respondiendo exitosamente.");
                        break;
                    }
                }
            } catch (Exception e) {
                log.warn("No se pudo iniciar automáticamente el servicio Python: {}", e.getMessage());
            }
        });
    }

    /**
     * Verifica si el servicio está respondiendo al health check.
     */
    public static boolean isHealthy() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HEALTH_URL))
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * Busca la ruta de la carpeta del proyecto Python del Asistente en el sistema.
     */
    private static File locatePythonProject() {
        String userHome = System.getProperty("user.home");

        // Rutas candidatas
        File[] candidates = new File[] {
                new File("../Asistente LMM para recetario"),
                new File("../brujabeer-recetario-asistente"),
                new File("../../Asistente LMM para recetario"),
                new File("../../brujabeer-recetario-asistente"),
                new File(userHome, "Desktop/Facultad/PROYECTO BRUJA BEER/Asistente LMM para recetario"),
                new File(userHome, "Desktop/brujabeer-recetario-asistente"),
                new File(userHome, "Desktop/Asistente LMM para recetario")
        };

        for (File dir : candidates) {
            if (dir.exists() && new File(dir, "api/main.py").exists()) {
                return dir;
            }
        }
        return null;
    }

    /**
     * Detiene el proceso de Python si fue iniciado por esta aplicación.
     */
    public static void stop() {
        if (pythonProcess != null && pythonProcess.isAlive()) {
            try {
                log.info("Deteniendo subproceso de Asistente Python...");
                pythonProcess.destroy();
                pythonProcess = null;
            } catch (Exception e) {
                log.warn("Error al detener proceso Python: {}", e.getMessage());
            }
        }
    }
}
