package com.brujabeer.recetario.ui.controller;

import com.brujabeer.recetario.service.RecetaService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FermentacionPaneController — Sub-controller para la sección de
 * Fermentación y Carbonatación.
 *
 * Gestiona los campos de fermentación (días, temperatura) y carbonatación
 * (volúmenes CO₂), calculando en tiempo real los gramos de dextrosa
 * necesarios para el priming.
 *
 * Recibe del controller padre: volumenLitros, ogActual, atenuación de levadura.
 * Integrado al RecipeFormController vía fx:include + Spring @Controller.
 */
@Slf4j
@Controller
public class FermentacionPaneController implements Initializable {

    @Autowired
    private RecetaService recetaService;

    @FXML private TextField txtDiasFermentacion;
    @FXML private TextField txtTempFermentacion;
    @FXML private TextField txtDiasMaduracion;
    @FXML private TextField txtTempMaduracion;
    @FXML private Slider    sldVolumenesCO2;
    @FXML private Label     lblVolumenesCO2Valor;
    @FXML private Label     lblPrimingResultado;
    @FXML private Label     lblFgEstimada;

    private double volumenLitros = 20.0;
    private double ogActual = 1.050;
    private Integer atenuacionMin = 73;
    private Integer atenuacionMax = 77;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("🔧 Inicializando FermentacionPaneController...");

        // Listener reactivo del Slider de CO₂
        sldVolumenesCO2.valueProperty().addListener((obs, oldVal, newVal) -> {
            double v = Math.round(newVal.doubleValue() * 10.0) / 10.0;
            lblVolumenesCO2Valor.setText(String.format("%.1f vol", v));
            recalcularPriming();
        });

        txtTempFermentacion.textProperty().addListener((obs, oldVal, newVal) -> recalcularPriming());

        // Valor inicial del label de slider
        lblVolumenesCO2Valor.setText(String.format("%.1f vol", sldVolumenesCO2.getValue()));

        log.info("✅ FermentacionPaneController inicializado.");
    }

    // ── Cálculos reactivos ──────────────────────────────────────────────────

    /**
     * Recalcula los gramos de dextrosa para priming y la FG estimada.
     * Se dispara automáticamente cuando cambian temp, volCO₂, OG o atenuación.
     */
    private void recalcularPriming() {
        try {
            Double temp = parseDouble(txtTempFermentacion.getText());
            double volCO2 = sldVolumenesCO2.getValue();

            if (temp != null && recetaService != null) {
                double gramos = recetaService.calcularPrimingAzucar(volCO2, temp, volumenLitros);
                lblPrimingResultado.setText(String.format("%.1f g", gramos));
            } else {
                lblPrimingResultado.setText("— g");
            }
        } catch (Exception ex) {
            lblPrimingResultado.setText("— g");
            log.debug("Error calculando priming: {}", ex.getMessage());
        }

        actualizarFG();
    }

    /**
     * Calcula la FG estimada a partir de la OG y la atenuación de la levadura.
     *
     * Fórmula:
     *   FG = OG − (OG − 1) × (atenuaciónPromedio / 100)
     */
    private void actualizarFG() {
        try {
            double atenuacionProm = (atenuacionMin + atenuacionMax) / 2.0;
            double fg = ogActual - (ogActual - 1.0) * (atenuacionProm / 100.0);
            lblFgEstimada.setText(String.format("%.3f", fg));
        } catch (Exception ex) {
            lblFgEstimada.setText("—");
        }
    }

    // ── API pública (llamada por el controller padre) ────────────────────────

    public void setVolumenLitros(double litros) {
        this.volumenLitros = litros;
        recalcularPriming();
    }

    public void setOgActual(double og) {
        this.ogActual = og;
        actualizarFG();
    }

    public void setAtenuacion(Integer min, Integer max) {
        this.atenuacionMin = (min != null) ? min : 73;
        this.atenuacionMax = (max != null) ? max : 77;
        actualizarFG();
    }

    public Integer getDiasFermentacion() {
        return parseInt(txtDiasFermentacion.getText());
    }

    public Double getTempFermentacion() {
        return parseDouble(txtTempFermentacion.getText());
    }

    public Integer getDiasMaduracion() {
        return parseInt(txtDiasMaduracion.getText());
    }

    public Double getTempMaduracion() {
        return parseDouble(txtTempMaduracion.getText());
    }

    public Double getVolumenesCO2() {
        double v = Math.round(sldVolumenesCO2.getValue() * 10.0) / 10.0;
        return v;
    }

    /**
     * Limpia todos los campos y labels.
     */
    public void limpiar() {
        txtDiasFermentacion.clear();
        txtTempFermentacion.clear();
        if (txtDiasMaduracion != null) txtDiasMaduracion.clear();
        if (txtTempMaduracion != null) txtTempMaduracion.clear();
        sldVolumenesCO2.setValue(2.4);
        lblVolumenesCO2Valor.setText("2.4 vol");
        lblPrimingResultado.setText("— g");
        lblFgEstimada.setText("—");
        this.ogActual = 1.050;
        this.volumenLitros = 20.0;
        this.atenuacionMin = 73;
        this.atenuacionMax = 77;
    }

    /**
     * Carga datos de fermentación existentes (modo edición de receta).
     */
    public void cargarDatos(Integer dias, Double temp, Double vols, Integer diasMad, Double tempMad) {
        if (dias != null) txtDiasFermentacion.setText(String.valueOf(dias));
        if (temp != null) txtTempFermentacion.setText(String.valueOf(temp));
        if (txtDiasMaduracion != null && diasMad != null) txtDiasMaduracion.setText(String.valueOf(diasMad));
        if (txtTempMaduracion != null && tempMad != null) txtTempMaduracion.setText(String.valueOf(tempMad));
        if (vols != null) {
            sldVolumenesCO2.setValue(vols);
            lblVolumenesCO2Valor.setText(String.format("%.1f vol", vols));
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
}
