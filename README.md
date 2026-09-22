# 🍺 BrujaBeer V1.2 — Recetario & Sistema de Gestión Cervecera

![Java 21](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot 3.3](https://img.shields.io/badge/Spring_Boot-3.3-green.svg)
![JavaFX 21](https://img.shields.io/badge/JavaFX-21-blue.svg)
![Database](https://img.shields.io/badge/Database-H2_Embedded-red.svg)
![Status](https://img.shields.io/badge/Status-Portable_v1.2-gold.svg)

Aplicación de escritorio **100% portable y profesional** diseñada para cervecerías artesanales. Integra un simulador físico-químico completo, control de lotes de cocción, historial interactivo y gestión documental.

---

## 🌟 Novedades V1.2

- 🌓 **Modo Claro / Modo Oscuro**: Selector rápido en la cabecera (☀️ / 🌙) con persistencia automática de preferencias.
- 🎨 **Rediseño Visual Moderno**: Sistema de estilos CSS semánticos centralizados, esquinas redondeadas, sombras suaves y tipografía limpia.
- 📜 **Historial Interactivo**: Doble clic sobre cualquier lote del historial para cargar y revisar todos los datos de esa cocción en el formulario.
- 🧪 **Feedback de Elaboración**: Alertas automáticas de control de pH y comentarios del cervecero actualizados al instante en pantalla.
- 🚀 **Ejecutable Nativo (`BrujaBeer.exe`)**: Acceso directo sin ventanas negras de consola (CMD/PowerShell) con el logo oficial de la marca.

---

## 🌟 Módulos Principales

### 🍺 1. Recetario & Simulador Cervecero
- **Cálculo de Densidades**: OG (Inicial) y FG (Final) estimadas en tiempo real.
- **Amargor Tinseth (IBU)**: Cálculo científico según tiempo de hervor y porcentaje de ácidos alfa.
- **Color Morey (EBC)**: Determinación de color EBC/SRM según el grist de maltas.
- **Cálculo de Priming**: Dextrosa requerida en gramos según volúmenes de CO₂ deseados y temperatura de fermentación.
- **Estilos Editables**: Selección de lista BJCP o ingreso de estilos personalizados a mano.

### 📊 2. Lotes de Cocción (Día de Elaboración)
- **Medición de Control**: Registro de pH de macerado (con alertas automáticas de rango óptimo 5.2–5.6) y pH de lavado.
- **Eficiencia Brewhouse Real (%)**: Cálculo del rendimiento real del equipo por lote considerando la extracción de cada malta.
- **Mediciones Reales**: Registro de OG real, FG real, litros finales y ABV real (%) obtenido.
- **Historial Completo**: Registro persistido por fecha y número de lote con apertura por doble clic.

### 🎒 3. Mochila de Producción (Documentos & PDFs)
- **Gestión Documental**: Almacenamiento y organización de manuales de equipo, fichas técnicas y POEs de limpieza.
- **Carpetas Personalizadas**: Creación de subcarpetas jerárquicas.
- **Subir y Mover PDFs**: Importar documentos y moverlos de carpeta fácilmente.
- **Visor Integrado**: Apertura directa en el visor predeterminado del sistema operativo.

---

## 🛠 Tecnologías Utilizadas

- **Core**: Java 21 LTS
- **Framework UI**: JavaFX 21
- **Backend & IoC**: Spring Boot 3.3 + Spring Data JPA
- **Persistencia**: H2 Database (embebida en archivo local `brujabeer-data.mv.db`, sin dependencias externas)
- **Build System**: Apache Maven 3.9

---

## 🚀 Cómo Ejecutar

### 📦 Modo Portable (Recomendado para producción)
1. Descargar la carpeta de distribución `BrujaBeer-V1.2`.
2. Asegurarse de tener **Java 21** instalado en la computadora.
3. Hacer doble-clic en `BrujaBeer.exe`.

### 💻 Modo Desarrollo (Desde código fuente)
```bash
# Clonar repositorio
git clone https://github.com/santiagustintorres06-dotcom/brujabeer-recetario.git
cd brujabeer-recetario

# Compilar y ejecutar pruebas
mvn clean package

# Iniciar la aplicación
mvn javafx:run
```

---

## 📁 Estructura del Proyecto

```
brujabeer-recetario/
├── src/main/java/com/brujabeer/recetario/
│   ├── model/          # Entidades JPA (Receta, Malta, Lupulo, Lote, etc.)
│   ├── repository/     # Interfaces Spring Data JPA
│   ├── service/        # Lógica de negocio y fórmulas fisicoquímicas
│   └── ui/controller/  # Controladores JavaFX
├── src/main/resources/
│   ├── fxml/           # Vistas de interfaz de usuario
│   ├── styles/         # Hojas de estilo CSS (temas claro/oscuro)
│   └── images/         # Recursos gráficos y logo
└── pom.xml             # Configuración y dependencias Maven
```

---

Desarrollado para **La Bruja Beer — Cervecería Artesanal**.
