# BrujaBeer V1.2 — Recetario y Sistema de Gestion Cervecera

![Java 21](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot 3.3](https://img.shields.io/badge/Spring_Boot-3.3-green.svg)
![JavaFX 21](https://img.shields.io/badge/JavaFX-21-blue.svg)
![Database](https://img.shields.io/badge/Database-H2_Embedded-red.svg)
![Status](https://img.shields.io/badge/Status-Portable_v1.2-gold.svg)

Aplicacion de escritorio portable diseñada para cervecerias artesanales. Integra simulador fisico-quimico, control de lotes de coccion, historial interactivo y gestion documental operativa.

---

## Novedades Version 1.2

- **Modo Claro / Modo Oscuro**: Selector de tema integrado en la interfaz con persistencia automatica de preferencias del usuario.
- **Rediseño Visual**: Sistema de estilos CSS semanticos centralizados, esquinas redondeadas, sombras suaves y tipografia optimizada para lectura prolongada.
- **Historial Interactivo**: Doble clic sobre cualquier lote del historial para cargar y auditar todos los parametros de la coccion en el formulario.
- **Control de Elaboracion**: Retroalimentacion inmediata con alertas automaticas de pH y actualizacion de comentarios tecnicos al registrar el lote.
- **Ejecutable Nativo (`BrujaBeer.exe`)**: Acceso directo para entornos Windows sin ventanas auxiliares de consola (CMD o PowerShell).

---

## Modulos del Sistema

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
