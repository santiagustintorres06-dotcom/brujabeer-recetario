package com.brujabeer.recetario.ui.controller;

import com.brujabeer.recetario.model.Lote;
import com.brujabeer.recetario.model.Receta;
import com.brujabeer.recetario.service.RecetaService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * LoteFormController — Controlador JavaFX para el Módulo de Lotes de Cocción.
 *
 * Gestiona la interfaz de registro de un día de cocción (Batch), permitiendo
 * al cervecero ingresar datos de control (pH, densidades, volúmenes) y
 * persistirlos a través del servicio. Al guardar, muestra los resultados
 * calculados automáticamente (eficiencia real, alertas de pH).
 *
 * Inyectado por Spring gracias a @Controller + setControllerFactory() en JavaFxLauncher.
 */
@Slf4j
@Controller
public class LoteFormController implements Initializable {

    @Autowired
    private RecetaService recetaService;

    // ── Componentes FXML ────────────────────────────────────────────────────

    @FXML private ComboBox<Receta> cbReceta;
    @FXML private Label lblEstiloReceta;

    @FXML private TextField txtPhMacerado;
    @FXML private TextField txtPhLavado;

    @FXML private TextField txtDensidadPreHervor;

    @FXML private TextField txtDensidadInicialReal;
    @FXML private TextField txtLitrosFinalesReal;
    @FXML private TextField txtDensidadFinalReal;

    @FXML private TextArea txaComentarios;

    @FXML private Label lblEficienciaReal;
    @FXML private Label lblOgReal;
    @FXML private Label lblFgReal;
    @FXML private Label lblAbvReal;
    @FXML private Label lblLitrosFinales;
    @FXML private Label lblPhStatus;

    @FXML private TableView<Lote> tblHistorialLotes;
    @FXML private TableColumn<Lote, Integer> colNroLote;
    @FXML private TableColumn<Lote, String> colReceta;
    @FXML private TableColumn<Lote, String> colFecha;
    @FXML private TableColumn<Lote, Double> colEficiencia;

    private final ObservableList<Lote> historialObs = FXCollections.observableArrayList();

    // ── Inicialización ──────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("🔧 Inicializando LoteFormController...");

        configurarTablaHistorial();

        cargarRecetas();

        cargarHistorialLotes();

        // Listener: al seleccionar receta, mostrar su estilo
        cbReceta.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && lblEstiloReceta != null) {
                String estilo = newVal.getEstilo();
                lblEstiloReceta.setText(estilo != null && !estilo.isBlank() ? estilo : "Sin estilo definido");
            } else if (lblEstiloReceta != null) {
                lblEstiloReceta.setText("—");
            }
        });

        // Listener: al desplegar o hacer clic en cbReceta, recargar recetas de la BD
        cbReceta.setOnShowing(e -> cargarRecetas());

        // ── BUG FIX: Doble-clic en historial abre el lote seleccionado ──────
        tblHistorialLotes.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Lote seleccionado = tblHistorialLotes.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    cargarLoteEnFormulario(seleccionado);
                }
            }
        });

        log.info("✅ LoteFormController inicializado correctamente.");
    }

    /**
     * Configura las columnas del TableView de historial de lotes.
     */
    private void configurarTablaHistorial() {
        tblHistorialLotes.setItems(historialObs);

        colNroLote.setCellValueFactory(data ->
                new SimpleIntegerProperty(
                        data.getValue().getNroLote() != null ? data.getValue().getNroLote() : 0
                ).asObject());

        if (colReceta != null) {
            colReceta.setCellValueFactory(data ->
                    new SimpleStringProperty(
                            data.getValue().getReceta() != null ? data.getValue().getReceta().getNombre() : "—"
                    ));
        }

        colFecha.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getFechaCoccion() != null
                                ? data.getValue().getFechaCoccion().toString()
                                : "—"
                ));

        colEficiencia.setCellValueFactory(data ->
                new SimpleDoubleProperty(
                        data.getValue().getEficienciaEquipoReal() != null
                                ? data.getValue().getEficienciaEquipoReal()
                                : 0.0
                ).asObject());
    }

    /**
     * Carga el ComboBox de recetas desde la base de datos.
     */
    public void cargarRecetas() {
        try {
            if (recetaService != null) {
                List<Receta> recetas = recetaService.listarTodasConIngredientes();
                if (recetas != null) {
                    Receta seleccionadaPrevia = cbReceta.getValue();
                    cbReceta.setItems(FXCollections.observableArrayList(recetas));
                    if (seleccionadaPrevia != null) {
                        cbReceta.setValue(seleccionadaPrevia);
                    }
                    log.info("✅ {} Recetas cargadas en el ComboBox.", recetas.size());
                }
            }
        } catch (Exception ex) {
            log.error("❌ Error cargando recetas: {}", ex.getMessage());
        }
    }

    /**
     * Carga el historial de lotes en la tabla lateral.
     */
    public void cargarHistorialLotes() {
        try {
            if (recetaService != null) {
                List<Lote> lotes = recetaService.listarLotes();
                if (lotes != null) {
                    historialObs.clear();
                    historialObs.addAll(lotes);
                    log.info("✅ {} Lotes cargados en el historial.", lotes.size());
                }
            }
        } catch (Exception ex) {
            log.error("❌ Error cargando historial de lotes: {}", ex.getMessage());
        }
    }

    // ── Acciones FXML ───────────────────────────────────────────────────────

    /**
     * Acción principal: Registrar Cocción.
     *
     * Valida todos los campos, construye el objeto Lote, llama al servicio
     * para persistir (que calcula eficiencia y alertas de pH), y muestra
     * un Alert con los resultados.
     */
    @FXML
    public void registrarCoccion(ActionEvent event) {
        log.debug("🔧 Llamado registrarCoccion()");

        try {
            // ── Validación 1: Receta seleccionada ───────────────────────
            Receta recetaSeleccionada = cbReceta.getValue();
            if (recetaSeleccionada == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "⚠️ Validación",
                        "❌ Seleccioná una receta del ComboBox antes de registrar la cocción.");
                cbReceta.requestFocus();
                return;
            }

            // ── Validación 2: pH Macerado (obligatorio, entre 0 y 14) ──
            Double phMacerado = validarCampoDouble(txtPhMacerado, "pH Macerado", true);
            if (phMacerado == null) return;

            if (phMacerado < 0.0 || phMacerado > 14.0) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ pH Inválido",
                        "El pH de macerado debe estar entre 0 y 14.\nRecibido: " + phMacerado);
                txtPhMacerado.requestFocus();
                return;
            }

            // ── Validación 3: pH Lavado (opcional, pero si se ingresa debe ser válido) ──
            Double phLavado = validarCampoDouble(txtPhLavado, "pH Lavado", false);
            if (phLavado != null && (phLavado < 0.0 || phLavado > 14.0)) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ pH Lavado Inválido",
                        "El pH de lavado debe estar entre 0 y 14.\nRecibido: " + phLavado);
                txtPhLavado.requestFocus();
                return;
            }

            // ── Validación 4: Densidad Inicial Real (obligatoria) ──
            Double densidadInicialReal = validarCampoDouble(
                    txtDensidadInicialReal, "Densidad Inicial Real (OG)", true);
            if (densidadInicialReal == null) return;

            if (densidadInicialReal < 1.000 || densidadInicialReal > 1.200) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Densidad Inválida",
                        "La OG real debe estar entre 1.000 y 1.200.\nRecibido: " + densidadInicialReal);
                txtDensidadInicialReal.requestFocus();
                return;
            }

            // ── Validación 5: Litros Finales Real (obligatorio) ──
            Double litrosFinalesReal = validarCampoDouble(
                    txtLitrosFinalesReal, "Litros Finales", true);
            if (litrosFinalesReal == null) return;

            if (litrosFinalesReal <= 0.0) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Litros Inválidos",
                        "Los litros finales deben ser mayor a 0.\nRecibido: " + litrosFinalesReal);
                txtLitrosFinalesReal.requestFocus();
                return;
            }

            // ── Validación 6: Densidad Pre-Hervor (opcional) ──
            Double densidadPreHervor = validarCampoDouble(
                    txtDensidadPreHervor, "Densidad Pre-Hervor", false);

            // ── Validación 7: Densidad Final Real / FG (opcional) ──
            Double densidadFinalReal = validarCampoDouble(
                    txtDensidadFinalReal, "Densidad Final Real (FG)", false);

            // ── Construir el Lote ───────────────────────────────────────
            Lote lote = new Lote();
            lote.setReceta(recetaSeleccionada);
            lote.setFechaCoccion(LocalDate.now());
            lote.setPhMacerado(phMacerado);
            lote.setPhLavado(phLavado);
            lote.setDensidadPreHervor(densidadPreHervor);
            lote.setDensidadInicialReal(densidadInicialReal);
            lote.setDensidadFinalReal(densidadFinalReal);
            lote.setLitrosFinalesReal(litrosFinalesReal);

            String comentarios = txaComentarios.getText();
            if (comentarios != null && !comentarios.isBlank()) {
                lote.setComentarios(comentarios.trim());
            }

            // ── Llamar al servicio (calcula eficiencia + alertas) ───────
            Lote loteGuardado = recetaService.registrarCoccion(lote);

            log.info("✅ Lote registrado: ID={}, NroLote={}, Eficiencia={}%",
                    loteGuardado.getId(),
                    loteGuardado.getNroLote(),
                    loteGuardado.getEficienciaEquipoReal());

            // ── Actualizar panel de resultados ──────────────────────────
            actualizarPanelResultados(loteGuardado);

            // ── BUG FIX: Reflejar comentarios/alertas automáticas en la UI ──
            if (loteGuardado.getComentarios() != null && !loteGuardado.getComentarios().isBlank()) {
                txaComentarios.setText(loteGuardado.getComentarios());
            }

            // ── BUG FIX: Recargar historial completo desde la BD ────────
            cargarHistorialLotes();

            // ── Mostrar Alert de éxito ──────────────────────────────────
            String mensajeExito = construirMensajeExito(loteGuardado, recetaSeleccionada);
            mostrarAlerta(Alert.AlertType.INFORMATION, "✅ Cocción Registrada", mensajeExito);

        } catch (Exception ex) {
            log.error("❌ Error al registrar cocción: ", ex);
            mostrarAlerta(Alert.AlertType.ERROR, "❌ Error al Registrar",
                    "Ocurrió un error al registrar la cocción:\n\n" + ex.getMessage());
        }
    }

    /**
     * Limpia todos los campos del formulario para una nueva cocción.
     */
    @FXML
    public void limpiarFormulario(ActionEvent event) {
        cbReceta.getSelectionModel().clearSelection();
        txtPhMacerado.clear();
        txtPhLavado.clear();
        txtDensidadPreHervor.clear();
        txtDensidadInicialReal.clear();
        txtLitrosFinalesReal.clear();
        if (txtDensidadFinalReal != null) txtDensidadFinalReal.clear();
        txaComentarios.clear();

        lblEficienciaReal.setText("— %");
        lblOgReal.setText("—");
        if (lblFgReal != null) lblFgReal.setText("—");
        if (lblAbvReal != null) lblAbvReal.setText("— %");
        lblLitrosFinales.setText("—");
        lblPhStatus.setText("—");

        log.info("🔄 Formulario de lote limpiado.");
    }

    // ── Métodos privados auxiliares ──────────────────────────────────────────

    /**
     * Valida un campo de texto como número Double.
     *
     * @param textField  el campo a validar
     * @param nombreCampo nombre legible del campo para mensajes de error
     * @param obligatorio si es true, el campo no puede estar vacío
     * @return el valor parseado, o null si el campo está vacío (y no es obligatorio),
     *         o null si hubo error de validación (ya se mostró la alerta)
     */
    private Double validarCampoDouble(TextField textField, String nombreCampo, boolean obligatorio) {
        String texto = textField.getText();

        if (texto == null || texto.isBlank()) {
            if (obligatorio) {
                mostrarAlerta(Alert.AlertType.ERROR, "⚠️ Campo Obligatorio",
                        "❌ El campo '" + nombreCampo + "' es obligatorio.\nCompletalo antes de continuar.");
                textField.requestFocus();
                return null;
            }
            return null;
        }

        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "❌ Error de Número",
                    "El campo '" + nombreCampo + "' debe ser un número válido.\n" +
                    "Recibido: \"" + texto + "\"\n\n" +
                    "Ejemplos válidos: 5.35, 1.052, 20");
            textField.clear();
            textField.requestFocus();
            return null;
        }
    }

    /**
     * Actualiza el panel inferior de resultados con los datos del lote guardado.
     */
    private void actualizarPanelResultados(Lote lote) {
        if (lote.getEficienciaEquipoReal() != null) {
            lblEficienciaReal.setText(String.format("%.2f %%", lote.getEficienciaEquipoReal()));
        } else {
            lblEficienciaReal.setText("N/A");
        }

        if (lote.getDensidadInicialReal() != null) {
            lblOgReal.setText(String.format("%.3f", lote.getDensidadInicialReal()));
        }

        if (lblFgReal != null) {
            if (lote.getDensidadFinalReal() != null) {
                lblFgReal.setText(String.format("%.3f", lote.getDensidadFinalReal()));
            } else {
                lblFgReal.setText("—");
            }
        }

        if (lblAbvReal != null) {
            if (lote.getAbvReal() != null) {
                lblAbvReal.setText(String.format("%.1f %%", lote.getAbvReal()));
            } else {
                lblAbvReal.setText("— %");
            }
        }

        if (lote.getLitrosFinalesReal() != null) {
            lblLitrosFinales.setText(String.format("%.1f L", lote.getLitrosFinalesReal()));
        }

        if (lote.getPhMacerado() != null) {
            double ph = lote.getPhMacerado();
            if (ph >= 5.2 && ph <= 5.6) {
                lblPhStatus.setText(String.format("%.2f ✅", ph));
                lblPhStatus.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
            } else {
                lblPhStatus.setText(String.format("%.2f ⚠️", ph));
                lblPhStatus.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
            }
        }
    }

    /**
     * Construye el mensaje de éxito que se muestra en el Alert post-registro.
     */
    private String construirMensajeExito(Lote lote, Receta receta) {
        StringBuilder sb = new StringBuilder();
        sb.append("¡Cocción registrada exitosamente!\n\n");
        sb.append("📋 Receta: ").append(receta.getNombre()).append("\n");
        sb.append("🔢 Lote Nro: ").append(lote.getNroLote()).append("\n");
        sb.append("📅 Fecha: ").append(lote.getFechaCoccion()).append("\n\n");

        sb.append("── Resultados ──────────────────\n");

        if (lote.getEficienciaEquipoReal() != null) {
            sb.append("⚙️ Eficiencia Real: ")
              .append(String.format("%.2f", lote.getEficienciaEquipoReal()))
              .append(" %\n");
        }

        if (lote.getDensidadInicialReal() != null) {
            sb.append("🎯 OG Real: ")
              .append(String.format("%.3f", lote.getDensidadInicialReal()))
              .append("\n");
        }

        if (lote.getLitrosFinalesReal() != null) {
            sb.append("🍺 Litros Finales: ")
              .append(String.format("%.1f", lote.getLitrosFinalesReal()))
              .append(" L\n");
        }

        if (lote.getPhMacerado() != null) {
            double ph = lote.getPhMacerado();
            sb.append("🧪 pH Macerado: ").append(String.format("%.2f", ph));
            if (ph >= 5.2 && ph <= 5.6) {
                sb.append(" ✅ (en rango óptimo)\n");
            } else {
                sb.append(" ⚠️ (FUERA de rango óptimo)\n");
            }
        }

        if (lote.getComentarios() != null && !lote.getComentarios().isBlank()) {
            sb.append("\n── Alertas / Comentarios ───────\n");
            sb.append(lote.getComentarios());
        }

        return sb.toString();
    }

    /**
     * Carga los datos de un lote del historial en el formulario.
     * Permite ver o verificar los datos de una cocción pasada.
     */
    private void cargarLoteEnFormulario(Lote lote) {
        if (lote == null) return;

        log.info("📋 Cargando lote #{} en formulario.", lote.getNroLote());

        // Seleccionar la receta del lote en el ComboBox
        if (lote.getReceta() != null) {
            for (Receta r : cbReceta.getItems()) {
                if (r.getId() != null && r.getId().equals(lote.getReceta().getId())) {
                    cbReceta.setValue(r);
                    break;
                }
            }
        }

        // Cargar campos de control
        if (lote.getPhMacerado() != null) {
            txtPhMacerado.setText(String.format("%.2f", lote.getPhMacerado()));
        } else {
            txtPhMacerado.clear();
        }

        if (lote.getPhLavado() != null) {
            txtPhLavado.setText(String.format("%.2f", lote.getPhLavado()));
        } else {
            txtPhLavado.clear();
        }

        if (lote.getDensidadPreHervor() != null) {
            txtDensidadPreHervor.setText(String.format("%.3f", lote.getDensidadPreHervor()));
        } else {
            txtDensidadPreHervor.clear();
        }

        if (lote.getDensidadInicialReal() != null) {
            txtDensidadInicialReal.setText(String.format("%.3f", lote.getDensidadInicialReal()));
        } else {
            txtDensidadInicialReal.clear();
        }

        if (lote.getLitrosFinalesReal() != null) {
            txtLitrosFinalesReal.setText(String.format("%.1f", lote.getLitrosFinalesReal()));
        } else {
            txtLitrosFinalesReal.clear();
        }

        if (txtDensidadFinalReal != null) {
            if (lote.getDensidadFinalReal() != null) {
                txtDensidadFinalReal.setText(String.format("%.3f", lote.getDensidadFinalReal()));
            } else {
                txtDensidadFinalReal.clear();
            }
        }

        if (lote.getComentarios() != null) {
            txaComentarios.setText(lote.getComentarios());
        } else {
            txaComentarios.clear();
        }

        // Cargar panel de resultados
        actualizarPanelResultados(lote);
    }

    /**
     * Muestra un Alert de JavaFX con el tipo, título y contenido especificados.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.getDialogPane().setMinWidth(500);
        alert.showAndWait();
    }
}
