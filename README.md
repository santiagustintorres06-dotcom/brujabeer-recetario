# BrujaBeer V2.0 — Recetario, Control de Cocciones y Asistente Técnico IA

![Java 21](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot 3.3](https://img.shields.io/badge/Spring_Boot-3.3-green.svg)
![JavaFX 21](https://img.shields.io/badge/JavaFX-21-blue.svg)
![Database](https://img.shields.io/badge/Database-H2_Embedded-red.svg)
![IA Assistant](https://img.shields.io/badge/AI_Assistant-FastAPI_ChromaDB_Gemini-purple.svg)
![Status](https://img.shields.io/badge/Status-Version_2.0-gold.svg)

Aplicación de escritorio integral diseñada para cervecerías artesanales. El proyecto se desarrolló en un sprint intensivo, con asistencia de herramientas de IA agéntica para la implementación del asistente RAG. Integra simulador físico-químico, control de lotes de cocción con trazabilidad completa, repositorio documental operativo y un **Asistente Técnico Cervecero con Inteligencia Artificial** integrado nativamente.

---

## Novedades Versión 2.0

- **Asistente Técnico Cervecero con IA**: Widget de chat flotante integrado en la interfaz de usuario. Conecta de forma transparente con el microservicio RAG (FastAPI + ChromaDB + Gemini) para responder consultas técnicas sobre recetas, lotes, lupulado, levaduras, manuales de equipos y protocolos de limpieza.
- **Lanzamiento Silencioso y Desatendido**: El sistema Java detecta y levanta el microservicio de inteligencia artificial en segundo plano sin requerir interacción en consola ni pasos manuales por parte del operario.
- **Historial de Lotes Vinculado a Recetas**: Visualización del historial de cocciones y rendimientos brewhouse asociados directamente a cada receta.
- **Modo Claro / Modo Oscuro**: Selector de tema integrado en la interfaz con persistencia automática de preferencias.
- **Rediseño Visual**: Sistema de estilos CSS semánticos centralizados, sombras suaves y tipografía optimizada para plantas de elaboración.
- **Historial Interactivo de Cocciones**: Doble clic sobre cualquier lote del historial para auditar y cargar sus parámetros completos de elaboración.
- **Alertas y Control de pH**: Validación continua de pH de macerado y lavado con recomendaciones técnicas en tiempo real.

### 1. Recetario y Simulador Cervecero
- **Calculo de Densidades**: Gravedad inicial (OG) y final (FG) estimadas en tiempo real a partir del grist de granos y la atenuacion de la levadura.
- **Calculo de Amargor (IBU)**: Formula cientifica de Tinseth basada en tiempo de ebullicion, gravedad del mosto y porcentaje de alfa acidos.
- **Calculo de Color (EBC)**: Determinacion de color segun formula de Morey (MCU/SRM/EBC).
- **Calculo de Priming**: Estimacion de dextrosa en gramos segun volumenes de CO2 objetivo y temperatura de fermentacion.
- **Gestion de Estilos**: Compatibilidad con nomenclatura BJCP e ingreso de perfiles personalizados.

### 2. Lotes de Coccion y Control de Calidad
- **Monitoreo de pH**: Registro y evaluacion de pH de macerado (rango optimo 5.2 - 5.6) y pH de lavado.
- **Eficiencia Brewhouse Real**: Calculo del rendimiento real del equipo en base al extracto obtenido por cada malta utilizada.
- **Metricas Reales**: Captura de volumen final, densidades reales y porcentaje de alcohol por volumen (ABV) obtenido.
- **Trazabilidad**: Base de datos de registros historicos ordenados cronologicamente.

### 3. Mochila de Produccion (Documentacion y Protocolos)
- **Repositorio Tecnico**: Organizacion local de manuales de maquinaria, fichas tecnicas de insumos y Procedimientos Operativos Estandarizados (POE).
- **Estructura por Carpetas**: Creacion y administracion de directorios tematicos.
- **Gestion de Archivos**: Importacion, reubicacion y visualizacion directa en el visor predeterminado del sistema operativo.

---

## Tecnologias Utilizadas

- **Lenguaje**: Java 21 LTS
- **Interfaz de Usuario**: JavaFX 21
- **Arquitectura y Servicios**: Spring Boot 3.3 (Spring Data JPA, Context, Beans)
- **Base de Datos**: H2 Database embebida (almacenamiento en archivo local `brujabeer-data.mv.db`)
- **Herramienta de Construccion**: Apache Maven 3.9

---

## Instrucciones de Uso

### Modo Portable (Produccion)
1. Descargar o transferir la carpeta `BrujaBeer-V1.2`.
2. Verificar que el entorno cuente con **Java 21** o superior instalado.
3. Ejecutar el archivo `BrujaBeer.exe`.

### Modo Desarrollo (Compilacion desde codigo fuente)
```bash
# Clonar el repositorio
git clone https://github.com/santiagustintorres06-dotcom/brujabeer-recetario.git
cd brujabeer-recetario

# Compilar y ejecutar pruebas unitarias
mvn clean package

# Ejecutar la aplicacion
mvn javafx:run
```

---

## Estructura del Repositorio

```
brujabeer-recetario/
├── src/main/java/com/brujabeer/recetario/
│   ├── model/          # Entidades de dominio JPA (Receta, Malta, Lupulo, Lote, etc.)
│   ├── repository/     # Repositorios de persistencia Spring Data
│   ├── service/        # Logica de negocio y modelos de calculo matematico
│   └── ui/controller/  # Controladores de vistas JavaFX
├── src/main/resources/
│   ├── fxml/           # Definicion de interfaces graficas FXML
│   ├── styles/         # Estilos CSS centralizados (temas claro y oscuro)
│   └── images/         # Recursos visuales y logotipo
└── pom.xml             # Configuracion de dependencias y plugins de construccion
```

---

Desarrollado para **La Bruja Beer — Cerveceria Artesanal**.
