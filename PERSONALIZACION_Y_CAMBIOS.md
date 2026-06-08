╔════════════════════════════════════════════════════════════════════════════════╗
║                         🍺 BRUJABEER RECETARIO                                 ║
║               MEJORAS REALIZADAS PARA PROYECTO FINAL - FACULTAD                ║
║                                                                                ║
║                    Documentación de Cambios y Personalización                  ║
╚════════════════════════════════════════════════════════════════════════════════╝

═══════════════════════════════════════════════════════════════════════════════════

📋 ÍNDICE:
1. Archivos de Interfaz (UI/UX)
2. Mejoras de Validación y Blindaje
3. Cómo Personalizar la Interfaz
4. Cómo Ejecutar la Aplicación
5. Documentación Técnica para Integración

═══════════════════════════════════════════════════════════════════════════════════

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
1️⃣ ARCHIVOS DE INTERFAZ (UI/UX)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📁 ARCHIVO FXML (Estructura XML de la UI):
   Ubicación: src/main/resources/fxml/RecipeForm.fxml
   Ruta completa: C:\Users\Santiago Torres\Documents\brujabeer-recetario\src\main\resources\fxml\RecipeForm.fxml
   
   ✅ Mejoras implementadas:
   - Diseño inspirado en BrewerFriends (profesional y limpio)
   - Barra de menú mejorada con emojis y separadores
   - Toolbar con logo y versión
   - Sección izquierda con lista de recetas guardadas (con border)
   - Formulario central con secciones claramente delimitadas
   - Pestañas para Maltas, Lúpulos y Levaduras con mejor espaciado
   - Sección de parámetros calculados (OG, IBU, ABV, Color) con diseño profesional
   - Botones con emojis para mejor UX
   - Pie with botones de acción prominentes
   
   📝 PARA PERSONALIZAR:
   1. Abre el archivo RecipeForm.fxml en tu IDE
   2. Edita los estilos directamente en los atributos style=""
   3. Ejemplos:
      - Cambiar tamaños: -fx-font-size (11px, 12px, 14px, etc.)
      - Cambiar colores de fondo: -fx-background-color (#ffffff, #f8f9fa, etc.)
      - Cambiar ancho de columnas: prefWidth en TableColumn
      - Cambiar espaciados: spacing en HBox/VBox, padding en Insets


📁 ARCHIVO CSS (Estilos Globales):
   Ubicación: src/main/resources/styles/main.css
   Ruta completa: C:\Users\Santiago Torres\Documents\brujabeer-recetario\src\main\resources\styles\main.css
   
   ✅ Mejoras implementadas:
   - Paleta completa de colores (personalizables)
   - Estilos para todos los controles (botones, campos, tablas, etc.)
   - Colores coherentes y profesionales (inspiración BrewerFriends)
   - Efectos hover y focus en botones
   - Estilos de error para campos inválidos
   - Scroll bars estilizadas
   - Colores temáticos cerveceros
   
   🎨 PALETA DE COLORES ACTUAL (línea 16-23):
   -bb-primario:     #c97e27  (Naranja cervecero - puedes cambiar)
   -bb-secundario:   #6b4423  (Marrón oscuro - puedes cambiar)
   -bb-fondo:        #f8f9fa  (Gris muy claro)
   -bb-borde:        #e0e0e0  (Gris claro)
   -bb-texto:        #333     (Gris oscuro)
   -bb-error:        #ff6b6b  (Rojo para errores)
   -bb-exito:        #51cf66  (Verde para éxito)
   
   📝 PARA PERSONALIZAR COLORES:
   1. Abre main.css
   2. En la sección .root (líneas 16-23), reemplaza los valores hexadecimales:
      
      Ejemplos de colores profesionales tipo BrewerFriends:
      - Azul corporativo:    #0066cc
      - Verde cervecero:     #2d5016
      - Naranja artesanal:   #ff6633
      - Marrón claro:        #d4860a
      - Oro cervecero:       #FFD700
   
   3. Todos los estilos se actualizarán automáticamente


═══════════════════════════════════════════════════════════════════════════════════

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
2️⃣ MEJORAS DE VALIDACIÓN Y BLINDAJE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔒 CLASE BLINDADA: RecipeFormController.java
   Ubicación: src/main/java/com/brujabeer/recetario/ui/controller/RecipeFormController.java

   ✅ VALIDACIONES IMPLEMENTADAS EN:

   🔧 agregarMalta():
   └─ ✓ Valida que malta esté seleccionada
   └─ ✓ Valida que cantidad no esté vacía
   └─ ✓ Valida que cantidad sea número (NumberFormatException → Alert)
   └─ ✓ Valida que cantidad sea > 0
   └─ ✓ Valida que cantidad sea razonable (< 100kg)
   └─ ✓ Mensaje de éxito al agregar

   🔧 agregarLupulo():
   └─ ✓ Valida que lúpulo esté seleccionado
   └─ ✓ Valida que cantidad no esté vacía
   └─ ✓ Valida que cantidad sea número (NumberFormatException → Alert)
   └─ ✓ Valida que cantidad sea > 0
   └─ ✓ Valida tiempo de hervido (si se proporciona)
   └─ ✓ Valida que tiempo sea razonable (0-120 min)
   └─ ✓ Maneja uso predeterminado si no está seleccionado
   └─ ✓ Mensaje de éxito al agregar

   🔧 agregarLevadura():
   └─ ✓ Valida que levadura esté seleccionada
   └─ ✓ Valida que cantidad sea número si se proporciona
   └─ ✓ Valida que cantidad sea > 0
   └─ ✓ Maneja cantidad opcional
   └─ ✓ Mensaje de éxito al agregar

   🔧 eliminarMalta/LupuloLevadura():
   └─ ✓ Valida que item esté seleccionado
   └─ ✓ Try-catch para manejar excepciones inesperadas
   └─ ✓ Logs detallados de cada operación
   └─ ✓ Mensajes de error claros

   🔧 guardarReceta():
   └─ ✓ Validación completa de formulario
   └─ ✓ Try-catch para manejar excepciones
   └─ ✓ Manejo de parámetros calculados (OG, IBU) con best-effort
   └─ ✓ Logs detallados de persistencia
   └─ ✓ Mensages informativos con IDs y detalles

   🔧 validarFormulario():
   └─ ✓ Nombre obligatorio y no muy corto (>= 3 caracteres)
   └─ ✓ Volumen obligatorio y válido (número > 0)
   └─ ✓ Volumen razonable (< 1000L)
   └─ ✓ Al menos una malta obligatoria
   └─ ✓ Estilo cervecero recomendado (warning, no obligatorio)
   └─ ✓ Mensajes descriptivos con ejemplos

   ⚠️ TIPOS DE ALERTAS IMPLEMENTADAS:
   - Alert.AlertType.ERROR     → Para errores críticos (campo rojo, bloquea acción)
   - Alert.AlertType.WARNING   → Para advertencias (permite continuar)
   - Alert.AlertType.INFORMATION → Para confirmación de éxito


═══════════════════════════════════════════════════════════════════════════════════

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
3️⃣ CÓMO PERSONALIZAR LA INTERFAZ
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📝 PASO 1: Personalizar Colores (RECOMENDADO Y MÁS FÁCIL)
─────────────────────────────────────────────────────────
1. Abre: src/main/resources/styles/main.css
2. Busca la sección .root (líneas 15-23)
3. Reemplaza los valores hexadecimales:

   Antes:
   -bb-primario:     #c97e27;
   -bb-secundario:   #6b4423;

   Después (ejemplo con azul profesional):
   -bb-primario:     #0066cc;
   -bb-secundario:   #003d99;

4. Guarda el archivo
5. Ejecuta: mvn clean compile


📝 PASO 2: Personalizar Layout (Espaciados, Tamaños)
─────────────────────────────────────────────────────
1. Abre: src/main/resources/fxml/RecipeForm.fxml
2. Busca los atributos que quieras cambiar:
   
   Ejemplos:
   - Aumentar espaciado entre elementos: spacing="12" → spacing="16"
   - Aumentar ancho de columna: prefWidth="280" → prefWidth="350"
   - Cambiar tamaño de fuente: -fx-font-size: 11px → -fx-font-size: 13px
   - Aumentar padding: <Insets top="12" ... /> → <Insets top="16" ... />

3. Guarda y compila


📝 PASO 3: Agregar Efectos Visuales Avanzados
─────────────────────────────────────────────
En main.css puedes agregar:
- Sombras: -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);
- Bordes redondeados: -fx-border-radius: 8;
- Gradientes: -fx-background-color: linear-gradient(from 0% 0% to 0% 100%, #ffffff, #f0f0f0);


═══════════════════════════════════════════════════════════════════════════════════

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
4️⃣ CÓMO EJECUTAR LA APLICACIÓN
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Prerrequisitos:
✓ MySQL corriendo y base de datos "brujabeer_db" creada
✓ Maven 3.9.16+ instalado
✓ Java 21+ instalado

Pasos:
1. Abre PowerShell en el directorio del proyecto
2. Ejecuta:
   cd "C:\Users\Santiago Torres\Documents\brujabeer-recetario"
   & "C:\Users\Santiago Torres\Documents\apache-maven-3.9.16\bin\mvn.cmd" javafx:run

3. Espera a que la aplicación abra (15-20 segundos)
4. Verás:
   ✅ DataLoader poblando la BD automáticamente
   ✅ JavaFX abriendo con la interfaz mejorada
   ✅ ComboBoxes llenos con 15 Maltas, 15 Lúpulos, 10 Levaduras
   ✅ Validaciones activas en todos los campos


═══════════════════════════════════════════════════════════════════════════════════

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
5️⃣ INTEGRACIÓN EN APLICACIÓN MÁS GRANDE
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🏗️ ARQUITECTURA LISTA PARA INTEGRACIÓN:

Paquete: com.brujabeer.recetario
├── BrujaRecetarioApp.java              ← Punto de entrada Spring Boot
├── config/
│   └── AppConfig.java                  ← Configuración Spring
├── model/                              ← Entidades JPA (compartibles)
│   ├── Malta.java
│   ├── Lupulo.java
│   ├── Levadura.java
│   ├── Receta.java
│   ├── RecetaMalta.java
│   ├── RecetaLupulo.java
│   └── RecetaLevadura.java
├── repository/                         ← Interfaces Spring Data JPA
│   ├── MaltaRepository.java
│   ├── LupuloRepository.java
│   ├── LevaduraRepository.java
│   └── RecetaRepository.java
├── service/                            ← Lógica empresarial
│   ├── RecetaService.java
│   ├── DataLoader.java                 ← Población automática
│   └── impl/
│       └── RecetaServiceImpl.java
└── ui/                                 ← Interfaz JavaFX (no acoplada)
    ├── controller/
    │   └── RecipeFormController.java   ← Controlador UI independiente
    └── util/
        ├── RecetaMaltaRow.java
        ├── RecetaLupuloRow.java
        └── RecetaLevaduraRow.java


✅ VENTAJAS PARA INTEGRACIÓN:
1. Separación clara de capas (model, repository, service, ui)
2. No hay dependencias circulares
3. Toda la lógica está en service/ (fácil de reutilizar)
4. La UI es completamente opcional (puedes integrar controllers REST)
5. Fácil de testear (cada capa es independiente)


🚀 PARA INTEGRAR EN UNA APP MÁS GRANDE:

   Opción A - Mantener UI JavaFX:
   ──────────────────────────────
   1. Copia la carpeta recetario/ a tu proyecto principal
   2. Mantén los paquetes model/, repository/, service/
   3. Integra los Controllers UI en tu MainWindow
   4. Importa el CSS y FXML en tu aplicación principal

   Opción B - Usar solo Backend (REST API):
   ──────────────────────────────────────
   1. Copia model/, repository/, service/
   2. Crea @RestController que exponga endpoints
   3. Ejemplo:
      
      @RestController
      @RequestMapping("/api/recetas")
      public class RecetaRestController {
          
          @Autowired
          private RecetaService service;
          
          @GetMapping
          public List<Receta> listarTodas() {
              return service.listarTodas();
          }
          
          @PostMapping
          public Receta crear(@RequestBody Receta receta) {
              return service.guardar(receta);
          }
      }

   Opción C - Usar en Otra Aplicación:
   ────────────────────────────────────
   1. Extrae model/ y repository/ como JAR reutilizable
   2. Publica en tu repositorio privado Maven
   3. Impórtalo como dependencia en otra app:
      
      <dependency>
          <groupId>com.brujabeer</groupId>
          <artifactId>recetario-models</artifactId>
          <version>1.0.0</version>
      </dependency>


═══════════════════════════════════════════════════════════════════════════════════

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📚 RESUMEN TÉCNICO PARA DOCUMENTACIÓN FINAL
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📌 Stack Tecnológico:
   - Backend: Spring Boot 3.3.5 + Hibernate 6 + MySQL 8.x
   - Frontend: JavaFX 21.0.4 + FXML + CSS
   - Build: Maven 3.9.16
   - Java: 21

📌 Patrón Arquitectónico:
   - MVC (Model-View-Controller) en JavaFX
   - Repository Pattern con Spring Data JPA
   - Inyección de Dependencias (Spring DI)
   - Service Layer para lógica empresarial
   - AutoLoader Pattern (DataLoader)

📌 Características de Robustez:
   ✓ Validación exhaustiva en UI (no confía en el usuario)
   ✓ Try-catch en todos los métodos críticos
   ✓ Logs detallados (SLF4J + Logback)
   ✓ Mensajes de error claros en español
   ✓ Database constraints en JPA (@NotBlank, @Positive, etc.)
   ✓ Transacciones manejadas por Spring
   ✓ Conexión pooling con HikariCP

📌 Preparado para:
   ✓ Expansión futura (microservicios, REST API)
   ✓ Multitenant (agregar tenant_id a entidades)
   ✓ Autenticación (agregar User, Role entities)
   ✓ Auditoría (agregar created_by, updated_by)
   ✓ Temas personalizables (cambiar colores en CSS)


═══════════════════════════════════════════════════════════════════════════════════

¡Tu aplicación está lista para presentar como proyecto final! 🎓🍺

═══════════════════════════════════════════════════════════════════════════════════

