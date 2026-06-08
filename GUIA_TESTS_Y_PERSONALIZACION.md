╔════════════════════════════════════════════════════════════════════════════════╗
║           🎨 GUÍA DE PERSONALIZACIÓN - EJEMPLOS PRÁCTICOS                      ║
║                    BrujaBeer Recetario - Proyecto Final                        ║
╚════════════════════════════════════════════════════════════════════════════════╝

═══════════════════════════════════════════════════════════════════════════════════

📂 ARCHIVOS A PERSONALIZAR:

1. Colores y Estilos Globales:
   → src/main/resources/styles/main.css

2. Layout y Estructura UI:
   → src/main/resources/fxml/RecipeForm.fxml

3. Lógica y Validaciones (NO necesitas cambiar, ya está blindado):
   → src/main/java/com/brujabeer/recetario/ui/controller/RecipeFormController.java

═══════════════════════════════════════════════════════════════════════════════════

🎨 EJEMPLO 1: CAMBIAR PALETA DE COLORES A TONOS AZULES
───────────────────────────────────────────────────────

Archivo: main.css (líneas 15-23)

ANTES (Naranja cervecero):
────
.root {
    -bb-primario:     #c97e27;    /* Naranja cervecero */
    -bb-secundario:   #6b4423;    /* Marrón oscuro */
    -bb-fondo:        #f8f9fa;    /* Gris muy claro */
    ...

DESPUÉS (Azul profesional):
────
.root {
    -bb-primario:     #0066cc;    /* Azul corporativo */
    -bb-secundario:   #003d99;    /* Azul oscuro */
    -bb-fondo:        #f0f4f8;    /* Azul muy claro */
    ...

RESULTADO: Todos los botones, bordes activos y acentos serán azules! 🎨


═══════════════════════════════════════════════════════════════════════════════════

🎨 EJEMPLO 2: CAMBIAR PALETA A VERDE NATURAL (ECO-FRIENDLY)
──────────────────────────────────────────────────────────

PALETA SUGERIDA:
────
-bb-primario:     #2d5016;    /* Verde oscuro natural */
-bb-secundario:   #1a3a0a;    /* Verde muy oscuro */
-bb-fondo:        #f0fdf4;    /* Verde claro para fondo */
-bb-borde:        #d1e7dd;    /* Verde claro para bordes */
-bb-texto:        #1b3a26;    /* Verde oscuro para texto */
-bb-error:        #dc2626;    /* Rojo para errores */
-bb-exito:        #16a34a;    /* Verde para éxito */

EFECTO: Interfaz verde natural, ideal para aspectos ambientales 🌱


═══════════════════════════════════════════════════════════════════════════════════

🎨 EJEMPLO 3: CAMBIAR ESPACIADOS Y TAMAÑOS (PARA PANTALLAS GRANDES)
───────────────────────────────────────────────────────────────────

Archivo: RecipeForm.fxml

ANTES (Compacto):
────
<VBox spacing="12" style="-fx-padding: 0;">
    <HBox spacing="6">
        <ComboBox prefWidth="250" />
        <TextField prefWidth="80" />
        ...

DESPUÉS (Más espacioso - pantalla 4K):
────
<VBox spacing="16" style="-fx-padding: 0;">
    <HBox spacing="10">
        <ComboBox prefWidth="320" />
        <TextField prefWidth="100" />
        ...

CAMBIOS:
- spacing="12" → spacing="16" (espaciado entre elementos)
- prefWidth="250" → prefWidth="320" (ancho de ComboBox)
- prefWidth="80" → prefWidth="100" (campos de cantidad)
- Label style -fx-font-size: 12px → 14px (textos más grandes)


═══════════════════════════════════════════════════════════════════════════════════

📝 EJEMPLO 4: AGREGAR BORDES Y SOMBRAS (DISEÑO MÁS MODERNO)
──────────────────────────────────────────────────────────

Archivo: main.css

AGREGAR DESPUÉS DE .button:hover:
────
.button {
    ...
    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0.5, 0, 1);
}

.button:hover {
    ...
    -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 8, 0.7, 0, 2);
}

RESULTADO: Botones con sombra profunda que se intensifica al pasar el mouse


═══════════════════════════════════════════════════════════════════════════════════

🎯 VALIDACIONES IMPLEMENTADAS - CASOS DE PRUEBA
────────────────────────────────────────────────

Prueba cada caso para verificar que funciona:

1️⃣ AGREGAR MALTA SIN SELECCIONAR
   ├─ Haz clic en "Agregar" sin seleccionar malta
   └─ RESULTADO: Alert ERROR → "Seleccioná una malta de la lista desplegable"

2️⃣ AGREGAR MALTA CON CANTIDAD VACÍA
   ├─ Selecciona malta pero deja cantidad vacía
   ├─ Haz clic en "Agregar"
   └─ RESULTADO: Alert ERROR → "Completá la cantidad de gramos"

3️⃣ AGREGAR MALTA CON CANTIDAD INVÁLIDA (letras)
   ├─ Selecciona malta
   ├─ Escribe "abc" en cantidad
   ├─ Haz clic en "Agregar"
   └─ RESULTADO: Alert ERROR → "Cantidad debe ser número válido. Recibido: abc"

4️⃣ AGREGAR MALTA CON CANTIDAD NEGATIVA
   ├─ Selecciona malta
   ├─ Escribe "-500" en cantidad
   ├─ Haz clic en "Agregar"
   └─ RESULTADO: Alert ERROR → "Cantidad debe ser > 0. Recibido: -500"

5️⃣ GUARDAR SIN NOMBRE
   ├─ Deja nombre vacío
   ├─ Haz clic en "Guardar"
   └─ RESULTADO: Alert ERROR → "Nombre obligatorio"

6️⃣ GUARDAR CON NOMBRE MUY CORTO
   ├─ Escribe "AB" (2 caracteres)
   ├─ Haz clic en "Guardar"
   └─ RESULTADO: Alert ERROR → "Nombre debe tener >= 3 caracteres"

7️⃣ GUARDAR SIN VOLUMEN
   ├─ Completa nombre, estilo, pero deja volumen vacío
   ├─ Haz clic en "Guardar"
   └─ RESULTADO: Alert ERROR → "Volumen obligatorio"

8️⃣ GUARDAR CON VOLUMEN INVÁLIDO
   ├─ Escribe "veinte litros" en volumen
   ├─ Haz clic en "Guardar"
   └─ RESULTADO: Alert ERROR → "Volumen debe ser número. Recibido: veinte litros"

9️⃣ GUARDAR SIN MALTAS
   ├─ Completa todos los campos
   ├─ Pero NO agregues maltas
   ├─ Haz clic en "Guardar"
   └─ RESULTADO: Alert ERROR → "Receta debe contener >= 1 malta"

🔟 AGREGAR LÚPULO CON TIEMPO INVÁLIDO
   ├─ Selecciona lúpulo
   ├─ Cantidad: 10
   ├─ Tiempo: "abc" (inválido)
   ├─ Haz clic en "Agregar"
   └─ RESULTADO: Alert ERROR → "Tiempo debe ser número entero. Recibido: abc"


═══════════════════════════════════════════════════════════════════════════════════

✅ CASOS DE ÉXITO (HAPPY PATH)
──────────────────────────────

1️⃣ AGREGAR MALTA CORRECTA
   ├─ Selecciona: Pilsen
   ├─ Cantidad: 300
   ├─ Haz clic en "Agregar"
   └─ RESULTADO: 
      • Alert INFORMATION → "Malta agregada: Pilsen (300g)"
      • Pilsen aparece en la tabla
      • Campo cantidad se limpia
      • OG se recalcula

2️⃣ AGREGAR LÚPULO CON TODO
   ├─ Selecciona: Cascade
   ├─ Cantidad: 10
   ├─ Tiempo: 60
   ├─ Uso: Bittering
   ├─ Haz clic en "Agregar"
   └─ RESULTADO:
      • Alert INFORMATION → "Lúpulo agregado: Cascade (10g)"
      • Cascade aparece en tabla
      • IBU se recalcula
      • Campos se limpian

3️⃣ GUARDAR RECETA VÁLIDA
   ├─ Nombre: "Mi IPA Argentina"
   ├─ Estilo: American IPA
   ├─ Volumen: 20
   ├─ Maltas: Pilsen (300g) + Caramelo 60 (50g)
   ├─ Lúpulos: Cascade (10g @ 60 min)
   ├─ Levaduras: US-05
   ├─ Haz clic en "Guardar"
   └─ RESULTADO:
      • Alert INFORMATION → Receta guardada con ID
      • Receta aparece en lista izquierda
      • Formulario se limpia
      • En consola: SQL INSERT


═══════════════════════════════════════════════════════════════════════════════════

🔍 DÓNDE ESTÁN LOS LOGS
──────────────────────

Logging automático en fichero y consola (configurado en application.properties):

Ubicación: src/main/resources/application.properties (línea 50-53)

logging.level.root=WARN
logging.level.com.brujabeer=DEBUG          ← Logs de la app
logging.level.org.hibernate.SQL=DEBUG      ← Logs de SQL

Verás mensajes como:
- ✅ Malta agregada: Pilsen - 300 g
- ❌ Error inesperado en agregarMalta
- 🔧 Llamado agregarLupulo()
- 🧬 Cargando 10 Levaduras...


═══════════════════════════════════════════════════════════════════════════════════

💡 TIPS DE MEJORA ADICIONAL
──────────────────────────

1. Auto-complete en ComboBox:
   Busca en RecipeFormController la línea donde se agrega botón focus listener

2. Guardar preferencias de usuario:
   Las últimas recetas se cargan en lstRecetas automáticamente

3. Exportar receta como PDF:
   Puedes agregar iText7 o Apache PDFBox para generar reportes

4. Copiar receta existente:
   Click derecho en receta → "Duplicar receta"
   (Necesitarías agregar ContextMenu en ListView)

5. Agregar más ingredientes (miel, especias):
   Crea entidades Miel.java, Especias.java siguiendo el patrón Malta
   Agrega RecetaMiel.java, RecetaEspecias.java
   El resto es automático (repositories, service, controller)


═══════════════════════════════════════════════════════════════════════════════════

📞 SOPORTE RÁPIDO
─────────────────

Si tienes problemas:

❌ "Aplicación no arranca"
└─ Verifica MySQL esté corriendo: mysql -u root -p
└─ Verifica conexión en application.properties

❌ "ComboBoxes vacíos"
└─ Verifica que DataLoader ejecutó: busca "✅ 15 Maltas cargadas" en logs
└─ Si no aparece, la tabla está vacía

❌ "Validaciones no funcionan"
└─ Verifica que RecipeFormController esté compilado
└─ ejecuta: mvn clean compile

❌ "Interfaz se ve fea"
└─ Verifica que main.css se copió: target/classes/styles/main.css existe
└─ Los estilos se aplican solo en javafx:run, no en IDE preview


═══════════════════════════════════════════════════════════════════════════════════

🎓 PARA TU PRESENTACIÓN FINAL
─────────────────────────────

Estructura de tu presentación:

1. OBJETIVO
   "Crear un recetario cervecero que permita diseñar recetas con 
    cálculos automáticos de OG, IBU, ABV y Color"

2. ARQUITECTURA
   MVC con Spring Boot, Hibernate, MySQL, JavaFX
   Separación clara de capas (model, repository, service, ui)

3. FUNCIONALIDADES
   ✓ Gestión de maltas, lúpulos y levaduras
   ✓ Cálculos automáticos de parámetros cerveceros
   ✓ Validaciones exhaustivas + manejo de errores
   ✓ Persistencia en MySQL
   ✓ Interfaz profesional personalizable

4. INNOVACIONES
   ✓ DataLoader automático poblando BD
   ✓ Validaciones con mensajes descriptivos en español
   ✓ Archivos CSS para temáticas personalizables
   ✓ Logs detallados para debugging
   ✓ Listo para escalar a multi-tenant o REST API

5. CÓDIGO DESTACADO
   - Muestra la clase DataLoader (15 maltas, 15 lúpulos, 10 levaduras)
   - Muestra validación en agregarMalta() (manejo de excepciones)
   - Muestra CSS (paleta de colores personalizable)

═══════════════════════════════════════════════════════════════════════════════════

¡Éxito en tu presentación! 🎓🍺

═══════════════════════════════════════════════════════════════════════════════════

