# 🍺 BrujaBeer V1.1 — Recetario & Sistema de Gestión Cervecera

![Java 21](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot 3.3](https://img.shields.io/badge/Spring_Boot-3.3-green.svg)
![JavaFX 21](https://img.shields.io/badge/JavaFX-21-blue.svg)
![Database](https://img.shields.io/badge/Database-H2_Embedded-red.svg)
![Status](https://img.shields.io/badge/Status-Portable_v1.1-gold.svg)

Aplicación de escritorio **100% portable y profesional** diseñada para cervecerías artesanales. Integra un simulador físico-químico completo, control de lotes de cocción y gestión documental.

---

## 🌟 Módulos Principales

### 🍺 1. Recetario & Simulador Cervecero
- **Cálculo de Densidades**: OG (Inicial) y FG (Final) estimadas en tiempo real.
- **Amargor Tinseth (IBU)**: Cálculo científico según tiempo y porcentaje de ácidos alfa.
- **Color Morey (EBC)**: Determinación de color EBC/SRM según el grist de maltas.
- **Cálculo de Priming**: Dextrosa requerida en gramos según volúmenes de CO₂ deseados.
- **Estilos Editables**: Selección de lista BJCP o ingreso de estilos personalizados a mano.

### 📊 2. Lotes de Cocción (Día de Elaboración)
- **Medición de Control**: Registro de pH de macerado (con alertas automáticas) y pH de lavado.
- **Eficiencia Brewhouse Real (%)**: Cálculo del rendimiento real del equipo por lote considerando la extracción de cada malta.
- **Mediciones Reales**: Registro de OG real, FG real, litros en fermentador y ABV real (%) obtenido.
- **Historial de Cocciones**: Registro completo persistido por fecha y número de lote.

### 🎒 3. Mochila de Producción (Documentos & PDFs)
- **Gestión Documental**: Almacenamiento y organización de manuales de equipo, fichas técnicas y POEs de limpieza.
- **Carpetas Personalizadas**: Creación de subcarpetas jerárquicas.
- **Subir y Mover PDFs**: Botón para importar documentos y moverlos de carpeta.
- **Visor Integrado**: Apertura directa en el visor predeterminado del sistema.

---

## 🛠 Tecnologías Utilizadas

- **Core**: Java 21 LTS
- **Framework UI**: JavaFX 21
- **Backend & IoC**: Spring Boot 3.3 + Spring Data JPA
- **Persistencia**: H2 Database (embebida en archivo local `brujabeer-data.mv.db`, no requiere servidor MySQL)
- **Build System**: Apache Maven 3.9

---

## 🚀 Cómo Ejecutar

### 📦 Modo Portable (Recomendado para producción)
1. Descargar la carpeta de distribución `BrujaBeer-V1.1`.
2. Asegurarse de tener **Java 21** instalado en la PC.
3. Hacer doble-clic en `EJECUTAR-BRUJABEER.bat`.

### 💻 Modo Desarrollo (Desde código fuente)
```bash
# Clonar repositorio
git clone https://github.com/Santiago-Torres-N/brujabeer-recetario.git
cd brujabeer-recetario

# Compilar y ejecutar pruebas
mvn clean package

# Ejecutar aplicación
java -jar target/recetario-1.0.0-SNAPSHOT.jar
```

---

## 💾 Persistencia de Datos

Todos los datos (recetas, lotes, listas de maltas y lúpulos) se guardan en el archivo embebido `brujabeer-data.mv.db`.
- **Backup**: Simplemente copiá el archivo `brujabeer-data.mv.db`.
- **Mochila PDF**: Los documentos cargados se guardan automáticamente en la carpeta `./mochila`.

---

## 📜 Licencia

Desarrollado para **BrujaBeer Cervecería Artesanal**.
