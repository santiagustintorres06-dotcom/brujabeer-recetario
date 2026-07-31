package com.brujabeer.recetario.ui.controller;

import javafx.application.HostServices;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * MochilaFormController — Controlador JavaFX para el Módulo Mochila.
 *
 * Permite gestionar manuales, fichas técnicas, protocolos y documentos PDF
 * necesarios para la producción cervecera, organizados por carpetas.
 *
 * Almacena los archivos en la carpeta "./mochila" en la raíz de la app.
 */
@Slf4j
@Controller
public class MochilaFormController implements Initializable {

    private static final String MOCHILA_BASE_DIR = "./mochila";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    @FXML private TreeView<File> treeCarpetas;
    @FXML private TableView<FileItem> tblDocumentos;
    @FXML private TableColumn<FileItem, String> colNombre;
    @FXML private TableColumn<FileItem, String> colTamano;
    @FXML private TableColumn<FileItem, String> colFecha;

    @FXML private TextField txtBuscar;
    @FXML private Label lblCarpetaActual;
    @FXML private Label lblContadorArchivos;

    private final ObservableList<FileItem> documentosObs = FXCollections.observableArrayList();
    private File carpetaActual;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("🔧 Inicializando MochilaFormController...");

        asegurarCarpetaBase();
        configurarTabla();
        configurarArbolCarpetas();
        configurarBusqueda();

        log.info("✅ MochilaFormController inicializado.");
    }

    private void asegurarCarpetaBase() {
        File base = new File(MOCHILA_BASE_DIR);
        if (!base.exists()) {
            boolean created = base.mkdirs();
            if (created) {
                log.info("📁 Carpeta base ./mochila creada.");
                // Crear algunas subcarpetas por defecto para organizar
                new File(base, "Manuales de Equipo").mkdirs();
                new File(base, "Fichas Tecnicas Raw").mkdirs();
                new File(base, "Protocolos de Limpieza").mkdirs();
            }
        }
        this.carpetaActual = base;
    }

    private void configurarTabla() {
        tblDocumentos.setItems(documentosObs);

        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colTamano.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTamanoFormateado()));
        colFecha.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFechaFormateada()));

        // Doble clic para abrir PDF directamente
        tblDocumentos.setRowFactory(tv -> {
            TableRow<FileItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    abrirDocumentoSeleccionado();
                }
            });
            return row;
        });
    }

    private void configurarArbolCarpetas() {
        File rootDir = new File(MOCHILA_BASE_DIR);
        TreeItem<File> rootItem = crearTreeItem(rootDir, "🎒 Mochila Principal");
        rootItem.setExpanded(true);

        treeCarpetas.setCellFactory(tv -> new TreeCell<File>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (item.getPath().replace("\\", "/").endsWith("/mochila") || item.getPath().equals(MOCHILA_BASE_DIR)) {
                        setText("🎒 Mochila Principal");
                    } else {
                        setText("📁 " + item.getName());
                    }
                }
            }
        });

        treeCarpetas.setRoot(rootItem);
        treeCarpetas.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null && newItem.getValue() != null) {
                this.carpetaActual = newItem.getValue();
                actualizarListaArchivos();
            }
        });

        treeCarpetas.getSelectionModel().select(rootItem);
    }

    private TreeItem<File> crearTreeItem(File folder, String displayName) {
        TreeItem<File> item = new TreeItem<File>(folder) {
            @Override
            public String toString() {
                return displayName != null ? displayName : folder.getName();
            }
        };

        File[] subdirs = folder.listFiles(File::isDirectory);
        if (subdirs != null) {
            for (File subdir : subdirs) {
                item.getChildren().add(crearTreeItem(subdir, "📁 " + subdir.getName()));
            }
        }
        return item;
    }

    private void configurarBusqueda() {
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldText, newText) -> actualizarListaArchivos());
        }
    }

    @FXML
    public void actualizarListaArchivos() {
        documentosObs.clear();

        if (carpetaActual == null || !carpetaActual.exists()) {
            lblCarpetaActual.setText("Carpetas");
            lblContadorArchivos.setText("0 archivos");
            return;
        }

        lblCarpetaActual.setText("📁 " + (carpetaActual.getPath().equals(MOCHILA_BASE_DIR) ? "Mochila Principal" : carpetaActual.getName()));

        File[] archivos = carpetaActual.listFiles(f -> !f.isDirectory() && !f.isHidden());
        String filtro = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";

        int contador = 0;
        if (archivos != null) {
            for (File f : archivos) {
                if (filtro.isEmpty() || f.getName().toLowerCase().contains(filtro)) {
                    documentosObs.add(new FileItem(f));
                    contador++;
                }
            }
        }

        lblContadorArchivos.setText(contador + " archivo(s)");
    }

    // ── Acciones ─────────────────────────────────────────────────────────────

    @FXML
    public void crearNuevaCarpeta(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("📁 Nueva Carpeta");
        dialog.setHeaderText("Crear una subcarpeta en la Mochila");
        dialog.setContentText("Nombre de la carpeta:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nombre -> {
            String nombreLimpio = nombre.trim();
            if (nombreLimpio.isBlank()) return;

            File parentDir = carpetaActual != null ? carpetaActual : new File(MOCHILA_BASE_DIR);
            File nuevaCarpeta = new File(parentDir, nombreLimpio);

            if (nuevaCarpeta.exists()) {
                mostrarAlerta(Alert.AlertType.WARNING, "⚠️ Carpeta Existente", "Ya existe una carpeta con ese nombre.");
                return;
            }

            if (nuevaCarpeta.mkdirs()) {
                log.info("📁 Carpeta creada: {}", nuevaCarpeta.getAbsolutePath());
                configurarArbolCarpetas();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Error", "No se pudo crear la carpeta.");
            }
        });
    }

    @FXML
    public void eliminarCarpeta(ActionEvent event) {
        TreeItem<File> selectedItem = treeCarpetas.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem.getValue() == null) return;

        File folder = selectedItem.getValue();
        if (folder.getPath().equals(MOCHILA_BASE_DIR)) {
            mostrarAlerta(Alert.AlertType.WARNING, "⚠️ Operación no permitida", "No podés eliminar la carpeta raíz de la Mochila.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Borrado");
        confirm.setHeaderText("¿Eliminar carpeta '" + folder.getName() + "'?");
        confirm.setContentText("Se eliminará la carpeta y su contenido.");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            borrarDirectorio(folder);
            configurarArbolCarpetas();
        }
    }

    private void borrarDirectorio(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) borrarDirectorio(f);
                else f.delete();
            }
        }
        dir.delete();
    }

    @FXML
    public void subirDocumento(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("📄 Seleccionar Documento PDF");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documentos PDF (*.pdf)", "*.pdf"),
                new FileChooser.ExtensionFilter("Todos los archivos (*.*)", "*.*")
        );

        File archivoOrigen = fileChooser.showOpenDialog(tblDocumentos.getScene().getWindow());
        if (archivoOrigen != null) {
            File destinoDir = carpetaActual != null ? carpetaActual : new File(MOCHILA_BASE_DIR);
            File archivoDestino = new File(destinoDir, archivoOrigen.getName());

            try {
                Files.copy(archivoOrigen.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                log.info("📄 Archivo copiado a Mochila: {}", archivoDestino.getAbsolutePath());
                actualizarListaArchivos();
                mostrarAlerta(Alert.AlertType.INFORMATION, "✅ Documento Subido", "El archivo '" + archivoOrigen.getName() + "' fue agregado a la Mochila.");
            } catch (IOException ex) {
                log.error("❌ Error copiando documento: ", ex);
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Error al Subir", "No se pudo guardar el archivo: " + ex.getMessage());
            }
        }
    }

    @FXML
    public void abrirDocumento(ActionEvent event) {
        abrirDocumentoSeleccionado();
    }

    @FXML
    public void moverDocumento(ActionEvent event) {
        FileItem seleccionado = tblDocumentos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "⚠️ Sin Selección", "Seleccioná un documento de la lista para moverlo.");
            return;
        }

        File archivoOrigen = seleccionado.getFile();
        if (!archivoOrigen.exists()) {
            mostrarAlerta(Alert.AlertType.ERROR, "❌ Archivo no encontrado", "El archivo ya no existe en el disco.");
            actualizarListaArchivos();
            return;
        }

        File baseDir = new File(MOCHILA_BASE_DIR);
        java.util.List<File> carpetas = obtenerTodasLasSubcarpetas(baseDir);
        carpetas.add(0, baseDir); // Incluir la carpeta raíz

        ChoiceDialog<File> dialog = new ChoiceDialog<>(carpetaActual != null ? carpetaActual : baseDir, carpetas);
        dialog.setTitle("➡️ Mover Documento");
        dialog.setHeaderText("Mover '" + archivoOrigen.getName() + "'");
        dialog.setContentText("Seleccioná la carpeta de destino:");

        Optional<File> result = dialog.showAndWait();
        result.ifPresent(carpetaDestino -> {
            File archivoDestino = new File(carpetaDestino, archivoOrigen.getName());

            if (archivoDestino.getAbsolutePath().equals(archivoOrigen.getAbsolutePath())) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "ℹ️ Misma Carpeta", "El archivo ya se encuentra en esa carpeta.");
                return;
            }

            try {
                Files.move(archivoOrigen.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                log.info("➡️ Archivo movido: {} -> {}", archivoOrigen.getName(), carpetaDestino.getName());
                actualizarListaArchivos();
                mostrarAlerta(Alert.AlertType.INFORMATION, "✅ Documento Movido", "El archivo '" + archivoOrigen.getName() + "' fue movido con éxito.");
            } catch (IOException ex) {
                log.error("❌ Error moviendo archivo: ", ex);
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Error al Mover", "No se pudo mover el archivo: " + ex.getMessage());
            }
        });
    }

    private java.util.List<File> obtenerTodasLasSubcarpetas(File parent) {
        java.util.List<File> result = new java.util.ArrayList<>();
        File[] files = parent.listFiles(File::isDirectory);
        if (files != null) {
            for (File f : files) {
                result.add(f);
                result.addAll(obtenerTodasLasSubcarpetas(f));
            }
        }
        return result;
    }

    private void abrirDocumentoSeleccionado() {
        FileItem seleccionado = tblDocumentos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "⚠️ Sin Selección", "Seleccioná un documento de la lista para abrirlo.");
            return;
        }

        File file = seleccionado.getFile();
        if (!file.exists()) {
            mostrarAlerta(Alert.AlertType.ERROR, "❌ Archivo no encontrado", "El archivo ya no existe en el disco.");
            actualizarListaArchivos();
            return;
        }

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            } else {
                new ProcessBuilder("cmd", "/c", "start", "", file.getAbsolutePath()).start();
            }
            log.info("👁 Documento abierto: {}", file.getName());
        } catch (Exception ex) {
            log.error("❌ Error al abrir archivo: ", ex);
            mostrarAlerta(Alert.AlertType.ERROR, "❌ Error al Abrir", "No se pudo abrir el archivo:\n" + ex.getMessage());
        }
    }

    @FXML
    public void eliminarDocumento(ActionEvent event) {
        FileItem seleccionado = tblDocumentos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "⚠️ Sin Selección", "Seleccioná un documento para eliminarlo.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Borrado");
        confirm.setHeaderText("¿Eliminar '" + seleccionado.getNombre() + "'?");
        confirm.setContentText("Esta acción eliminará el archivo del almacenamiento de la Mochila.");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (seleccionado.getFile().delete()) {
                log.info("🗑 Archivo eliminado: {}", seleccionado.getNombre());
                actualizarListaArchivos();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Error", "No se pudo eliminar el archivo.");
            }
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // ── Modelo de datos helper para la tabla ─────────────────────────────────

    public static class FileItem {
        private final File file;

        public FileItem(File file) {
            this.file = file;
        }

        public File getFile() { return file; }
        public String getNombre() { return file.getName(); }

        public String getTamanoFormateado() {
            long bytes = file.length();
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }

        public String getFechaFormateada() {
            return DATE_FORMATTER.format(Instant.ofEpochMilli(file.lastModified()));
        }
    }
}
