package com.brujabeer.recetario.ui.controller;

import com.brujabeer.recetario.model.PasoMacerado;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * MaceradoTabController — Sub-controller para la pestaña de Macerado.
 *
 * Gestiona los escalones de temperatura (Mash Steps) del proceso
 * de macerado. Permite al cervecero definir un perfil de macerado
 * con múltiples descansos enzimáticos.
 *
 * Integrado al RecipeFormController vía fx:include + Spring @Controller.
 */
@Slf4j
@Controller
public class MaceradoTabController implements Initializable {

    @FXML private TextField txtNombrePaso;
    @FXML private TextField txtTemperatura;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtTiempoRecirculado;

    @FXML private TableView<PasoMacerado> tblPasosMacerado;
    @FXML private TableColumn<PasoMacerado, String> colPasoNombre;
    @FXML private TableColumn<PasoMacerado, Double> colPasoTemperatura;
    @FXML private TableColumn<PasoMacerado, Integer> colPasoDuracion;

    private final ObservableList<PasoMacerado> pasosObs = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("🔧 Inicializando MaceradoTabController...");

        configurarTabla();

        agregarPasoDefault();

        log.info("✅ MaceradoTabController inicializado.");
    }

    private void configurarTabla() {
        tblPasosMacerado.setItems(pasosObs);

        colPasoNombre.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNombre() != null ? data.getValue().getNombre() : "—"));

        colPasoTemperatura.setCellValueFactory(data ->
                new SimpleDoubleProperty(
                        data.getValue().getTemperatura() != null ? data.getValue().getTemperatura() : 0.0
                ).asObject());

        colPasoDuracion.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getDuracion() != null ? data.getValue().getDuracion() : 0
                ).asObject());
    }

    /**
     * Agrega un paso de sacarificación por defecto (67°C × 60 min),
     * que es el descanso más común en cervezas all-grain.
     */
    private void agregarPasoDefault() {
        PasoMacerado sacarificacion = new PasoMacerado();
        sacarificacion.setNombre("Sacarificación");
        sacarificacion.setTemperatura(67.0);
        sacarificacion.setDuracion(60);
        pasosObs.add(sacarificacion);
    }

    // ── Acciones FXML ───────────────────────────────────────────────────────

    @FXML
    public void agregarPaso(ActionEvent event) {
        String nombre = txtNombrePaso.getText();
        if (nombre == null || nombre.isBlank()) {
            mostrarAlerta("⚠️ Campo Obligatorio", "Ingresá un nombre para el paso (ej: Sacarificación, Mash Out).");
            txtNombrePaso.requestFocus();
            return;
        }

        Double temperatura = parseDouble(txtTemperatura.getText());
        if (temperatura == null) {
            mostrarAlerta("❌ Temperatura Inválida", "Ingresá una temperatura válida en °C.");
            txtTemperatura.requestFocus();
            return;
        }
        if (temperatura < 30.0 || temperatura > 100.0) {
            mostrarAlerta("❌ Temperatura Fuera de Rango",
                    "La temperatura debe estar entre 30°C y 100°C.\nRecibido: " + temperatura + "°C");
            txtTemperatura.requestFocus();
            return;
        }

        Integer duracion = parseInt(txtDuracion.getText());
        if (duracion == null) {
            mostrarAlerta("❌ Duración Inválida", "Ingresá una duración válida en minutos.");
            txtDuracion.requestFocus();
            return;
        }
        if (duracion < 1 || duracion > 120) {
            mostrarAlerta("❌ Duración Fuera de Rango",
                    "La duración debe estar entre 1 y 120 minutos.\nRecibido: " + duracion);
            txtDuracion.requestFocus();
            return;
        }

        PasoMacerado paso = new PasoMacerado();
        paso.setNombre(nombre.trim());
        paso.setTemperatura(temperatura);
        paso.setDuracion(duracion);
        pasosObs.add(paso);

        txtNombrePaso.clear();
        txtTemperatura.clear();
        txtDuracion.clear();
        txtNombrePaso.requestFocus();

        log.info("✅ Paso de macerado agregado: {} ({}°C × {} min)", nombre, temperatura, duracion);
    }

    @FXML
    public void eliminarPaso(ActionEvent event) {
        PasoMacerado seleccionado = tblPasosMacerado.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            pasosObs.remove(seleccionado);
            log.info("🗑 Paso de macerado eliminado: {}", seleccionado.getNombre());
        } else {
            mostrarAlerta("⚠️ Sin Selección", "Seleccioná un paso de la tabla para eliminarlo.");
        }
    }

    // ── API pública para el controller padre ─────────────────────────────────

    /**
     * Retorna la lista de pasos de macerado para que el controller padre
     * los persista al guardar la receta.
     */
    public List<PasoMacerado> getPasosMacerado() {
        return new ArrayList<>(pasosObs);
    }

    /**
     * Limpia todos los pasos y los campos de entrada.
     */
    public void limpiar() {
        pasosObs.clear();
        txtNombrePaso.clear();
        txtTemperatura.clear();
        txtDuracion.clear();
        if (txtTiempoRecirculado != null) txtTiempoRecirculado.clear();
        agregarPasoDefault();
    }

    /**
     * Retorna el tiempo de recirculado (Vorlauf) en minutos, o null si no se ingresó.
     */
    public Integer getTiempoRecirculado() {
        return txtTiempoRecirculado != null ? parseInt(txtTiempoRecirculado.getText()) : null;
    }

    /**
     * Carga pasos existentes (para modo edición de receta).
     */
    public void cargarPasos(List<PasoMacerado> pasos) {
        pasosObs.clear();
        if (pasos != null && !pasos.isEmpty()) {
            pasosObs.addAll(pasos);
        } else {
            agregarPasoDefault();
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Double parseDouble(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return Double.parseDouble(text.trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Integer parseInt(String text) {
        if (text == null || text.isBlank()) return null;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
