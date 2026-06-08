package com.brujabeer.recetario.ui.controller;

import com.brujabeer.recetario.model.*;
import com.brujabeer.recetario.repository.LevaduraRepository;
import com.brujabeer.recetario.repository.LupuloRepository;
import com.brujabeer.recetario.repository.MaltaRepository;
import com.brujabeer.recetario.repository.RecetaRepository;
import com.brujabeer.recetario.service.RecetaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * RecipeFormController - Controlador principal de la UI JavaFX.
 * Gestiona la interfaz de creación y edición de recetas.
 *
 * Se inyectan los repositorios directamente para acceder a BD.
 */
@Slf4j
@Controller
public class RecipeFormController implements Initializable {

    @Autowired(required = false)
    private RecetaService recetaService;

    @Autowired(required = false)
    private MaltaRepository maltaRepository;

    @Autowired(required = false)
    private LupuloRepository lupuloRepository;

    @Autowired(required = false)
    private LevaduraRepository levaduraRepository;

    @Autowired(required = false)
    private RecetaRepository recetaRepository;

    // ────────────────────────────────────────────────────────────────────────
    // SECCIÓN: Datos generales de la receta
    // ────────────────────────────────────────────────────────────────────────

    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cbEstilo;
    @FXML private TextArea txaDescripcion;
    @FXML private TextField txtVolumenLitros;

    @FXML private Label lblOgEstimada;
    @FXML private Label lblFgEstimada;
    @FXML private Label lblIbuEstimado;
    @FXML private Label lblAbvEstimado;
    @FXML private Label lblColorEbc;
    @FXML private ListView<Receta> lstRecetas;

    // ────────────────────────────────────────────────────────────────────────
    // SECCIÓN: Pestaña MALTAS
    // ────────────────────────────────────────────────────────────────────────

    @FXML private ComboBox<Malta> cbMaltaSeleccionada;
    @FXML private TextField txtCantidadMalta;
    @FXML private Button btnAgregarMalta;
    @FXML private Button btnEliminarMalta;
    @FXML private TableView<RecetaMalta> tblMaltas;
    @FXML private TableColumn<RecetaMalta, String> colMaltaNombre;
    @FXML private TableColumn<RecetaMalta, Double> colMaltaCantidad;
    @FXML private TableColumn<RecetaMalta, Double> colMaltaPorcentaje;

    // ────────────────────────────────────────────────────────────────────────
    // SECCIÓN: Pestaña LÚPULOS
    // ────────────────────────────────────────────────────────────────────────

    @FXML private ComboBox<Lupulo> cbLupuloSeleccionado;
    @FXML private TextField txtCantidadLupulo;
    @FXML private TextField txtTiempoLupulo;
    @FXML private ComboBox<RecetaLupulo.UsoLupulo> cbUsoLupulo;
    @FXML private Button btnAgregarLupulo;
    @FXML private Button btnEliminarLupulo;
    @FXML private TableView<RecetaLupulo> tblLupulos;
    @FXML private TableColumn<RecetaLupulo, String> colLupuloNombre;
    @FXML private TableColumn<RecetaLupulo, Double> colLupuloCantidad;
    @FXML private TableColumn<RecetaLupulo, Integer> colLupuloTiempo;
    @FXML private TableColumn<RecetaLupulo, RecetaLupulo.UsoLupulo> colLupuloUso;

    // ────────────────────────────────────────────────────────────────────────
    // SECCIÓN: Pestaña LEVADURAS
    // ────────────────────────────────────────────────────────────────────────

    @FXML private ComboBox<Levadura> cbLevaduraSeleccionada;
    @FXML private TextField txtCantidadLevadura;
    @FXML private Button btnAgregarLevadura;
    @FXML private Button btnEliminarLevadura;
    @FXML private TableView<RecetaLevadura> tblLevaduras;
    @FXML private TableColumn<RecetaLevadura, String> colLevaduraNombre;
    @FXML private TableColumn<RecetaLevadura, Double> colLevaduraCantidad;

    // ────────────────────────────────────────────────────────────────────────
    // Modelos de datos observables para los TableView
    // ────────────────────────────────────────────────────────────────────────

    private final ObservableList<RecetaMalta> maltasObs = FXCollections.observableArrayList();
    private final ObservableList<RecetaLupulo> lupulosObs = FXCollections.observableArrayList();
    private final ObservableList<RecetaLevadura> levaduraObs = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("🔧 Inicializando RecipeFormController...");

        // Configurar TableViews
        configurarTablas();

        // Cargar ComboBoxes desde repositorios (con fallback a datos en memoria)
        cargarCombos();

        // Listener para seleccionar receta
        lstRecetas.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                cargarRecetaEnFormulario(newV);
            }
        });

        log.info("✅ RecipeFormController inicializado.");
    }

    // ────────────────────────────────────────────────────────────────────────

    /**
     * Configura los TableView con sus correspondientes TableColumns.
     */
    private void configurarTablas() {
        // --- TableView de Maltas ---
        tblMaltas.setItems(maltasObs);
        colMaltaNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getMalta() != null ? data.getValue().getMalta().getNombre() : ""));
        colMaltaCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadGramos"));
        colMaltaPorcentaje.setCellValueFactory(new PropertyValueFactory<>("porcentajeEnGrist"));

        // --- TableView de Lúpulos ---
        tblLupulos.setItems(lupulosObs);
        colLupuloNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getLupulo() != null ? data.getValue().getLupulo().getNombre() : ""));
        colLupuloCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadGramos"));
        colLupuloTiempo.setCellValueFactory(new PropertyValueFactory<>("tiempoMinutos"));
        colLupuloUso.setCellValueFactory(new PropertyValueFactory<>("uso"));

        // --- TableView de Levaduras ---
        tblLevaduras.setItems(levaduraObs);
        colLevaduraNombre.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getLevadura() != null ? data.getValue().getLevadura().getNombre() : ""));
        colLevaduraCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadGramos"));
    }

    /**
     * Carga los ComboBox con datos de los repositorios.
     * Si no hay datos, usa ejemplos en memoria como fallback.
     */
    private void cargarCombos() {
        try {
            // --- ComboBox Estilos ---
            cbEstilo.setItems(FXCollections.observableArrayList(
                    "American IPA", "Session IPA", "Stout", "Porter",
                    "Amber Ale", "Blonde Ale", "Hefeweizen", "Munich Helles",
                    "Pilsner", "Barley Wine", "Saison", "Tripel"
            ));

            // --- ComboBox Maltas ---
            if (maltaRepository != null) {
                var maltas = maltaRepository.findAll();
                if (maltas != null && !maltas.isEmpty()) {
                    cbMaltaSeleccionada.setItems(FXCollections.observableArrayList(maltas));
                    log.info("✅ {} Maltas cargadas desde BD.", maltas.size());
                } else {
                    cargarMaltasDefault();
                }
            } else {
                cargarMaltasDefault();
            }

            // --- ComboBox Lúpulos ---
            if (lupuloRepository != null) {
                var lupulos = lupuloRepository.findAll();
                if (lupulos != null && !lupulos.isEmpty()) {
                    cbLupuloSeleccionado.setItems(FXCollections.observableArrayList(lupulos));
                    log.info("✅ {} Lúpulos cargados desde BD.", lupulos.size());
                } else {
                    cargarLupulosDefault();
                }
            } else {
                cargarLupulosDefault();
            }

            // --- ComboBox Levaduras ---
            if (levaduraRepository != null) {
                var levaduras = levaduraRepository.findAll();
                if (levaduras != null && !levaduras.isEmpty()) {
                    cbLevaduraSeleccionada.setItems(FXCollections.observableArrayList(levaduras));
                    log.info("✅ {} Levaduras cargadas desde BD.", levaduras.size());
                } else {
                    cargarLevadurasDefault();
                }
            } else {
                cargarLevadurasDefault();
            }

            // --- ComboBox Uso Lúpulo ---
            cbUsoLupulo.setItems(FXCollections.observableArrayList(RecetaLupulo.UsoLupulo.values()));

            // --- ListView Recetas ---
            if (recetaRepository != null) {
                var recetas = recetaRepository.findAll();
                if (recetas != null && !recetas.isEmpty()) {
                    lstRecetas.setItems(FXCollections.observableArrayList(recetas));
                    log.info("✅ {} Recetas cargadas desde BD.", recetas.size());
                }
            }

        } catch (Exception ex) {
            log.warn("⚠️ Error cargando datos: {}", ex.getMessage());
        }
    }

    /**
     * Carga datos de ejemplo para Maltas (fallback si BD está vacía).
     */
    private void cargarMaltasDefault() {
        log.info("📦 Usando Maltas de ejemplo (fallback)");
        Malta m1 = new Malta();
        m1.setNombre("Malta Pilsner"); m1.setMarca("Genérica");
        m1.setRendimientoPorcentaje(80.0); m1.setColorEbc(4.0);

        Malta m2 = new Malta();
        m2.setNombre("Malta Caramelo 60"); m2.setMarca("Genérica");
        m2.setRendimientoPorcentaje(75.0); m2.setColorEbc(60.0);

        cbMaltaSeleccionada.setItems(FXCollections.observableArrayList(m1, m2));
    }

    /**
     * Carga datos de ejemplo para Lúpulos (fallback si BD está vacía).
     */
    private void cargarLupulosDefault() {
        log.info("🌿 Usando Lúpulos de ejemplo (fallback)");
        Lupulo l1 = new Lupulo(); l1.setNombre("Cascade"); l1.setPorcentajeAlpha(6.5);
        Lupulo l2 = new Lupulo(); l2.setNombre("Centennial"); l2.setPorcentajeAlpha(10.0);
        cbLupuloSeleccionado.setItems(FXCollections.observableArrayList(l1, l2));
    }

    /**
     * Carga datos de ejemplo para Levaduras (fallback si BD está vacía).
     */
    private void cargarLevadurasDefault() {
        log.info("🧬 Usando Levaduras de ejemplo (fallback)");
        Levadura lv1 = new Levadura();
        lv1.setNombre("US-05"); lv1.setAtenuacionMin(73); lv1.setAtenuacionMax(77);
        cbLevaduraSeleccionada.setItems(FXCollections.observableArrayList(lv1));
    }

    /**
     * Carga los datos de una receta existente en el formulario.
     */
    private void cargarRecetaEnFormulario(Receta receta) {
        if (receta == null) return;

        txtNombre.setText(receta.getNombre());
        cbEstilo.getSelectionModel().select(receta.getEstilo());
        txaDescripcion.setText(receta.getDescripcion() != null ? receta.getDescripcion() : "");
        if (receta.getVolumenLitros() != null) {
            txtVolumenLitros.setText(receta.getVolumenLitros().toString());
        }

        // Cargar ingredientes
        maltasObs.clear();
        if (receta.getMaltas() != null) {
            maltasObs.addAll(receta.getMaltas());
        }

        lupulosObs.clear();
        if (receta.getLupulos() != null) {
            lupulosObs.addAll(receta.getLupulos());
        }

        levaduraObs.clear();
        if (receta.getLevaduras() != null) {
            levaduraObs.addAll(receta.getLevaduras());
        }

        recalcularTodo();
    }

    @FXML
    public void agregarMalta(ActionEvent event) {
        log.debug("🔧 Llamado agregarMalta()");

        try {
            // Validación 1: Malta seleccionada
            if (cbMaltaSeleccionada.getValue() == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "⚠️ Validación",
                    "❌ Seleccioná una malta de la lista desplegable.");
                cbMaltaSeleccionada.requestFocus();
                return;
            }

            // Validación 2: Cantidad no vacía
            String cantidadText = txtCantidadMalta.getText();
            if (cantidadText == null || cantidadText.isBlank()) {
                mostrarAlerta(Alert.AlertType.ERROR, "⚠️ Validación",
                    "❌ Completá la cantidad de gramos de malta.");
                txtCantidadMalta.requestFocus();
                return;
            }

            // Validación 3: Cantidad es número válido
            Double cantidad;
            try {
                cantidad = Double.parseDouble(cantidadText.trim());
            } catch (NumberFormatException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Error de Número",
                    "La cantidad debe ser un número válido (ej: 500 o 250.5)\nRecibido: \"" + cantidadText + "\"");
                txtCantidadMalta.clear();
                txtCantidadMalta.requestFocus();
                return;
            }

            // Validación 4: Cantidad positiva
            if (cantidad <= 0) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Valor Inválido",
                    "La cantidad debe ser mayor a 0 gramos.\nRecibido: " + cantidad);
                txtCantidadMalta.clear();
                txtCantidadMalta.requestFocus();
                return;
            }

            // Validación 5: Cantidad razonable (máximo 100kg)
            if (cantidad > 100000) {
                mostrarAlerta(Alert.AlertType.WARNING, "⚠️ Cantidad Muy Grande",
                    "¿Seguro? Ingresaste " + cantidad + "g (" + (cantidad / 1000) + " kg).");
                return;
            }

            // Si pasa todas las validaciones: agregar
            Malta malta = cbMaltaSeleccionada.getValue();
            RecetaMalta rm = new RecetaMalta();
            rm.setMalta(malta);
            rm.setCantidadGramos(cantidad);
            maltasObs.add(rm);

            log.info("✅ Malta agregada: {} - {} g", malta.getNombre(), cantidad);
            txtCantidadMalta.clear();
            recalcularTodo();

            mostrarAlerta(Alert.AlertType.INFORMATION, "✅ Éxito",
                "Malta agregada: " + malta.getNombre() + " (" + cantidad + "g)");

        } catch (Exception ex) {
            log.error("❌ Error inesperado en agregarMalta: ", ex);
            mostrarAlerta(Alert.AlertType.ERROR, "❌ Error Inesperado",
                "Ocurrió un error: " + ex.getMessage());
        }
    }

    @FXML
    public void eliminarMalta(ActionEvent event) {
        RecetaMalta seleccionada = tblMaltas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "⚠️ Sin Selección", 
                "❌ Seleccioná una malta en la tabla primero.");
            return;
        }
        try {
            String nombre = seleccionada.getMalta() != null ? seleccionada.getMalta().getNombre() : "Desconocida";
            maltasObs.remove(seleccionada);
            log.info("✅ Malta eliminada: {}", nombre);
            recalcularTodo();
            mostrarAlerta(Alert.AlertType.INFORMATION, "✅ Eliminada", 
                "Malta eliminada: " + nombre);
        } catch (Exception ex) {
            log.error("❌ Error al eliminar malta: ", ex);
            mostrarAlerta(Alert.AlertType.ERROR, "❌ Error", 
                "No se pudo eliminar la malta: " + ex.getMessage());
        }
    }

    @FXML
    public void agregarLupulo(ActionEvent event) {
        log.debug("🔧 Llamado agregarLupulo()");
        
        try {
            // Validación 1: Lúpulo seleccionado
            if (cbLupuloSeleccionado.getValue() == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "⚠️ Validación", 
                    "❌ Seleccioná un lúpulo de la lista desplegable.");
                cbLupuloSeleccionado.requestFocus();
                return;
            }

            // Validación 2: Cantidad no vacía
            String cantidadText = txtCantidadLupulo.getText();
            if (cantidadText == null || cantidadText.isBlank()) {
                mostrarAlerta(Alert.AlertType.ERROR, "⚠️ Validación", 
                    "❌ Completá la cantidad de gramos de lúpulo.");
                txtCantidadLupulo.requestFocus();
                return;
            }

            // Validación 3: Cantidad es número válido
            Double cantidad;
            try {
                cantidad = Double.parseDouble(cantidadText.trim());
            } catch (NumberFormatException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Error de Número", 
                    "La cantidad debe ser un número válido (ej: 10 o 5.5)\nRecibido: \"" + cantidadText + "\"");
                txtCantidadLupulo.clear();
                txtCantidadLupulo.requestFocus();
                return;
            }

            // Validación 4: Cantidad positiva
            if (cantidad <= 0) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Valor Inválido", 
                    "La cantidad debe ser mayor a 0 gramos.\nRecibido: " + cantidad);
                txtCantidadLupulo.clear();
                txtCantidadLupulo.requestFocus();
                return;
            }

            // Validación 5: Tiempo de hervido (si se proporcionó)
            Integer tiempo = null;
            if (txtTiempoLupulo.getText() != null && !txtTiempoLupulo.getText().isBlank()) {
                try {
                    tiempo = Integer.parseInt(txtTiempoLupulo.getText().trim());
                    if (tiempo < 0) {
                        mostrarAlerta(Alert.AlertType.ERROR, "❌ Tiempo Inválido", 
                            "El tiempo debe ser >= 0 minutos.\nRecibido: " + tiempo);
                        txtTiempoLupulo.clear();
                        txtTiempoLupulo.requestFocus();
                        return;
                    }
                    if (tiempo > 120) {
                        mostrarAlerta(Alert.AlertType.WARNING, "⚠️ Tiempo Muy Largo", 
                            "¿Seguro? Ingresaste " + tiempo + " minutos (típicamente 0-90).");
                    }
                } catch (NumberFormatException ex) {
                    mostrarAlerta(Alert.AlertType.ERROR, "❌ Error de Número (Tiempo)", 
                        "El tiempo debe ser un número entero en minutos.\nRecibido: \"" + txtTiempoLupulo.getText() + "\"");
                    txtTiempoLupulo.clear();
                    txtTiempoLupulo.requestFocus();
                    return;
                }
            }

            // Validación 6: Uso seleccionado
            if (cbUsoLupulo.getValue() == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "⚠️ Uso No Especificado", 
                    "Estableciendo uso predeterminado a 'Bittering'");
                cbUsoLupulo.setValue(RecetaLupulo.UsoLupulo.BITTERING);
            }

            // Si pasa todas las validaciones: agregar
            Lupulo lup = cbLupuloSeleccionado.getValue();
            RecetaLupulo rl = new RecetaLupulo();
            rl.setLupulo(lup);
            rl.setCantidadGramos(cantidad);
            rl.setTiempoMinutos(tiempo);
            rl.setUso(cbUsoLupulo.getValue() != null ? cbUsoLupulo.getValue() : RecetaLupulo.UsoLupulo.BITTERING);
            lupulosObs.add(rl);
            
            log.info("✅ Lúpulo agregado: {} - {} g - {} min - {}", 
                lup.getNombre(), cantidad, tiempo, rl.getUso());
            txtCantidadLupulo.clear();
            txtTiempoLupulo.clear();
            calcularIBU(null);
            recalcularTodo();
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "✅ Éxito", 
                "Lúpulo agregado: " + lup.getNombre() + " (" + cantidad + "g)");

        } catch (Exception ex) {
            log.error("❌ Error inesperado en agregarLupulo: ", ex);
            mostrarAlerta(Alert.AlertType.ERROR, "❌ Error Inesperado", 
                "Ocurrió un error: " + ex.getMessage());
        }
    }

    @FXML
    public void eliminarLupulo(ActionEvent event) {
        RecetaLupulo seleccionado = tblLupulos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "⚠️ Sin Selección", 
                "❌ Seleccioná un lúpulo en la tabla primero.");
            return;
        }
        try {
            String nombre = seleccionado.getLupulo() != null ? seleccionado.getLupulo().getNombre() : "Desconocido";
            lupulosObs.remove(seleccionado);
            log.info("✅ Lúpulo eliminado: {}", nombre);
            calcularIBU(null);
            recalcularTodo();
            mostrarAlerta(Alert.AlertType.INFORMATION, "✅ Eliminado", 
                "Lúpulo eliminado: " + nombre);
        } catch (Exception ex) {
            log.error("❌ Error al eliminar lúpulo: ", ex);
            mostrarAlerta(Alert.AlertType.ERROR, "❌ Error", 
                "No se pudo eliminar el lúpulo: " + ex.getMessage());
        }
    }

    @FXML
    public void agregarLevadura(ActionEvent event) {
        log.debug("🔧 Llamado agregarLevadura()");
        
        try {
            // Validación 1: Levadura seleccionada
            if (cbLevaduraSeleccionada.getValue() == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "⚠️ Validación", 
                    "❌ Seleccioná una levadura de la lista desplegable.");
                cbLevaduraSeleccionada.requestFocus();
                return;
            }

            // Validación 2: Cantidad opcional pero si se proporciona, debe ser válida
            Double cantidad = null;
            if (txtCantidadLevadura.getText() != null && !txtCantidadLevadura.getText().isBlank()) {
                try {
                    cantidad = Double.parseDouble(txtCantidadLevadura.getText().trim());
                    
                    if (cantidad <= 0) {
                        mostrarAlerta(Alert.AlertType.ERROR, "❌ Valor Inválido", 
                            "La cantidad debe ser mayor a 0 gramos.\nRecibido: " + cantidad);
                        txtCantidadLevadura.clear();
                        txtCantidadLevadura.requestFocus();
                        return;
                    }
                } catch (NumberFormatException ex) {
                    mostrarAlerta(Alert.AlertType.ERROR, "❌ Error de Número", 
                        "La cantidad debe ser un número válido (ej: 100 o 10.5)\nRecibido: \"" + txtCantidadLevadura.getText() + "\"");
                    txtCantidadLevadura.clear();
                    txtCantidadLevadura.requestFocus();
                    return;
                }
            }

            // Si pasa validaciones: agregar
            Levadura lev = cbLevaduraSeleccionada.getValue();
            RecetaLevadura rl = new RecetaLevadura();
            rl.setLevadura(lev);
            if (cantidad != null) {
                rl.setCantidadGramos(cantidad);
            }
            levaduraObs.add(rl);
            
            log.info("✅ Levadura agregada: {}" + (cantidad != null ? " - " + cantidad + "g" : ""), lev.getNombre());
            txtCantidadLevadura.clear();
            recalcularTodo();
            
            mostrarAlerta(Alert.AlertType.INFORMATION, "✅ Éxito", 
                "Levadura agregada: " + lev.getNombre() + 
                (cantidad != null ? " (" + cantidad + "g)" : ""));

        } catch (Exception ex) {
            log.error("❌ Error inesperado en agregarLevadura: ", ex);
            mostrarAlerta(Alert.AlertType.ERROR, "❌ Error Inesperado", 
                "Ocurrió un error: " + ex.getMessage());
        }
    }

    @FXML
    public void eliminarLevadura(ActionEvent event) {
        RecetaLevadura seleccionada = tblLevaduras.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "⚠️ Sin Selección", 
                "❌ Seleccioná una levadura en la tabla primero.");
            return;
        }
        try {
            String nombre = seleccionada.getLevadura() != null ? seleccionada.getLevadura().getNombre() : "Desconocida";
            levaduraObs.remove(seleccionada);
            log.info("✅ Levadura eliminada: {}", nombre);
            recalcularTodo();
            mostrarAlerta(Alert.AlertType.INFORMATION, "✅ Eliminada", 
                "Levadura eliminada: " + nombre);
        } catch (Exception ex) {
            log.error("❌ Error al eliminar levadura: ", ex);
            mostrarAlerta(Alert.AlertType.ERROR, "❌ Error", 
                "No se pudo eliminar la levadura: " + ex.getMessage());
        }
    }

    @FXML
    public void calcularDensidad(ActionEvent event) {
        try {
            double volumen = 20.0;
            if (txtVolumenLitros.getText() != null && !txtVolumenLitros.getText().isBlank()) {
                volumen = Double.parseDouble(txtVolumenLitros.getText());
            }
            double totalPoints = 0.0;
            for (RecetaMalta rm : maltasObs) {
                double kg = (rm.getCantidadGramos() != null ? rm.getCantidadGramos() : 0.0) / 1000.0;
                double rendimiento = (rm.getMalta() != null && rm.getMalta().getRendimientoPorcentaje() != null) ? rm.getMalta().getRendimientoPorcentaje() : 75.0;
                double ppg = 300.0;
                double mcu = (kg * rendimiento/100.0 * ppg) / volumen;
                totalPoints += mcu;
            }
            double og = 1.0 + (totalPoints / 1000.0);
            lblOgEstimada.setText(String.format("%.3f", og));
            calcularABV();
        } catch (NumberFormatException ex) {
            // ignore
        }
    }

    @FXML
    public void calcularIBU(ActionEvent event) {
        double ibuTotal = 0.0;
        double volumen = 20.0;
        try {
            if (txtVolumenLitros.getText() != null && !txtVolumenLitros.getText().isBlank()) {
                volumen = Double.parseDouble(txtVolumenLitros.getText());
            }
            for (RecetaLupulo rl : lupulosObs) {
                if (rl.getUso() == RecetaLupulo.UsoLupulo.DRY_HOP) continue;
                double minutos = rl.getTiempoMinutos() != null ? rl.getTiempoMinutos() : 0;
                double utilization = (1 - Math.exp(-0.04 * minutos)) / 4.15;
                double alpha = rl.getLupulo() != null && rl.getLupulo().getPorcentajeAlpha() != null ? rl.getLupulo().getPorcentajeAlpha() : 5.0;
                double contrib = (rl.getCantidadGramos() * alpha * utilization * 1000.0) / (volumen * 100.0);
                ibuTotal += contrib;
            }
            lblIbuEstimado.setText(String.format("%.0f", ibuTotal));
        } catch (NumberFormatException ex) {
            // ignore
        }
    }

    private void calcularABV() {
        try {
            double og = Double.parseDouble(lblOgEstimada.getText());
            double fg = 1.010;
            if (!lblFgEstimada.getText().equals("—")) {
                fg = Double.parseDouble(lblFgEstimada.getText());
            }
            double abv = (og - fg) * 131.25;
            lblAbvEstimado.setText(String.format("%.1f%%", abv));
        } catch (Exception ex) {
            // ignore
        }
    }

    private void calcularColorEBC() {
        double volumen = 20.0;
        try {
            if (txtVolumenLitros.getText() != null && !txtVolumenLitros.getText().isBlank()) volumen = Double.parseDouble(txtVolumenLitros.getText());
            double mcu = 0.0;
            for (RecetaMalta rm : maltasObs) {
                double kg = (rm.getCantidadGramos() != null ? rm.getCantidadGramos() : 0.0) / 1000.0;
                double color = rm.getMalta() != null && rm.getMalta().getColorEbc() != null ? rm.getMalta().getColorEbc() : 5.0;
                mcu += (kg * color) / volumen;
            }
            double srm = 1.4922 * Math.pow(mcu, 0.6859);
            double ebc = srm * 1.97;
            lblColorEbc.setText(String.format("%.0f EBC", ebc));
        } catch (NumberFormatException ex) {
            // ignore
        }
    }

    @FXML
    public void recalcularTodo() {
        calcularDensidad(null);
        calcularIBU(null);
        calcularABV();
        calcularColorEBC();
    }

    @FXML
    public void guardarReceta(ActionEvent event) {
        log.debug("🔧 Llamado guardarReceta()");
        
        if (!validarFormulario()) {
            return;
        }
        
        try {
            Receta receta = new Receta();
            receta.setNombre(txtNombre.getText().trim());
            receta.setEstilo(cbEstilo.getValue());
            receta.setDescripcion(txaDescripcion.getText());
            
            // Validar volumen
            try {
                Double volumen = Double.parseDouble(txtVolumenLitros.getText());
                receta.setVolumenLitros(volumen);
            } catch (NumberFormatException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Volumen Inválido", 
                    "El volumen debe ser un número válido en litros.");
                txtVolumenLitros.requestFocus();
                return;
            }
            
            // Agregar parámetros calculados (best-effort)
            try {
                receta.setOgEstimada(Double.parseDouble(lblOgEstimada.getText()));
            } catch (Exception ignored) {
                log.debug("No se pudo parsear OG");
            }
            
            try {
                receta.setIbuEstimado(Integer.parseInt(lblIbuEstimado.getText().replaceAll("[^0-9]", "")));
            } catch (Exception ignored) {
                log.debug("No se pudo parsear IBU");
            }

            // Agregar ingredientes
            for (RecetaMalta rm : maltasObs) {
                receta.agregarMalta(rm);
            }
            for (RecetaLupulo rl : lupulosObs) {
                receta.agregarLupulo(rl);
            }
            for (RecetaLevadura rl : levaduraObs) {
                receta.agregarLevadura(rl);
            }

            // Guardar en BD
            if (recetaService != null) {
                Receta saved = recetaService.guardar(receta);
                log.info("✅ Receta guardada en BD: ID={}, Nombre={}", saved.getId(), saved.getNombre());
                mostrarAlerta(Alert.AlertType.INFORMATION, "✅ Guardado Exitoso", 
                    "Receta guardada correctamente.\n\n" +
                    "ID: " + saved.getId() + "\n" +
                    "Nombre: " + saved.getNombre() + "\n" +
                    "Estilo: " + saved.getEstilo());
                lstRecetas.getItems().add(0, saved);
                limpiarFormulario(null);
            } else {
                log.warn("RecetaService no está disponible. Guardando en memoria.");
                mostrarAlerta(Alert.AlertType.INFORMATION, "⚠️ Guardado (Modo Demo)", 
                    "Receta creada en memoria (sin persistencia en BD).\n\n" +
                    "Para persistencia, asegurate de que RecetaService esté inyectado.");
                limpiarFormulario(null);
            }
        } catch (Exception ex) {
            log.error("❌ Error al guardar receta: ", ex);
            mostrarAlerta(Alert.AlertType.ERROR, "❌ Error al Guardar", 
                "No se pudo guardar la receta:\n\n" + ex.getMessage());
        }
    }

    @FXML
    public void limpiarFormulario(ActionEvent event) {
        txtNombre.clear();
        txaDescripcion.clear();
        txtVolumenLitros.clear();
        cbEstilo.getSelectionModel().clearSelection();

        maltasObs.clear();
        lupulosObs.clear();
        levaduraObs.clear();

        lblOgEstimada.setText("—");
        lblFgEstimada.setText("—");
        lblIbuEstimado.setText("—");
        lblAbvEstimado.setText("—");
        lblColorEbc.setText("—");
    }

    @FXML
    public void cancelar(ActionEvent event) {
        boolean hayCambios = !maltasObs.isEmpty() || !lupulosObs.isEmpty() || !levaduraObs.isEmpty();
        if (hayCambios) {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Hay cambios sin guardar. Salir de todas formas?", ButtonType.OK, ButtonType.CANCEL);
            a.setTitle("Confirmar salida");
            var res = a.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                Platform.exit();
            }
        } else {
            Platform.exit();
        }
    }

    private boolean validarFormulario() {
        try {
            // Validación 1: Nombre obligatorio
            if (txtNombre.getText() == null || txtNombre.getText().isBlank()) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Nombre Obligatorio", 
                    "El nombre de la receta es obligatorio.\n\nCompletá el campo 'Nombre'.");
                txtNombre.requestFocus();
                return false;
            }

            // Validación 2: Nombre no muy corto
            if (txtNombre.getText().trim().length() < 3) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Nombre Muy Corto", 
                    "El nombre debe tener al menos 3 caracteres.\n\nRecibido: \"" + txtNombre.getText() + "\"");
                txtNombre.requestFocus();
                return false;
            }

            // Validación 3: Volumen válido y presente
            if (txtVolumenLitros.getText() == null || txtVolumenLitros.getText().isBlank()) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Volumen Obligatorio", 
                    "Completá el volumen de la receta en litros.\n\nTípicamente: 20, 10 o 50 litros.");
                txtVolumenLitros.requestFocus();
                return false;
            }

            // Validación 4: Volumen es número
            double vol;
            try {
                vol = Double.parseDouble(txtVolumenLitros.getText().trim());
            } catch (NumberFormatException ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Volumen Inválido", 
                    "El volumen debe ser un número válido en litros.\n\nRecibido: \"" + txtVolumenLitros.getText() + "\"");
                txtVolumenLitros.clear();
                txtVolumenLitros.requestFocus();
                return false;
            }

            // Validación 5: Volumen positivo
            if (vol <= 0) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Volumen Inválido", 
                    "El volumen debe ser mayor a 0 litros.\n\nRecibido: " + vol);
                txtVolumenLitros.clear();
                txtVolumenLitros.requestFocus();
                return false;
            }

            // Validación 6: Volumen razonable (máximo 1000L)
            if (vol > 1000) {
                mostrarAlerta(Alert.AlertType.WARNING, "⚠️ Volumen Muy Grande", 
                    "¿Seguro? Ingresaste " + vol + " litros. Típicamente no supera 100L.");
                return false;
            }

            // Validación 7: Al menos una malta
            if (maltasObs.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "❌ Sin Maltas", 
                    "La receta debe contener al menos una malta.\n\n" +
                    "Seleccioná una malta de la pestaña 'Maltas' y completá la cantidad.");
                return false;
            }

            // Validación 8: Estilo seleccionado (recomendado)
            if (cbEstilo.getValue() == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "⚠️ Estilo No Seleccionado", 
                    "Es recomendable seleccionar un estilo cervecero.\n\n¿Continuar sin estilo?");
                return true;  // Allow pero warn
            }

            return true;

        } catch (Exception ex) {
            log.error("❌ Error en validarFormulario: ", ex);
            mostrarAlerta(Alert.AlertType.ERROR, "❌ Error de Validación", 
                "Ocurrió un error inesperado durante la validación:\n\n" + ex.getMessage());
            return false;
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void mostrarAcercaDe(ActionEvent event) {
        mostrarAlerta(Alert.AlertType.INFORMATION, "Acerca de", "BrujaBeer Recetario\nVersión demo\nDesarrollado para la compañía cervecera.");
    }

}
