# Torneo Master & MTG Life Tracker

Aplicación profesional y full-stack para la gestión de torneos de cartas y Life Tracker para **Magic: The Gathering (MTG)**.

---

## 📲 Descarga Automática de la App (APK)

Descarga e instala la versión más reciente directamente en tu celular Android haciendo clic en el siguiente enlace:

[![Descargar APK](https://img.shields.io/badge/Descargar%20APK-Torneo%20Master%20v1.0.0-success?style=for-the-badge&logo=android&logoColor=white)](https://github.com/DC268/TorneoApp/releases/download/v1.0.0/torneo-app.apk)

- ⚡ **Descarga Directa (Releases)**: [**torneo-app.apk**](https://github.com/DC268/TorneoApp/releases/download/v1.0.0/torneo-app.apk)
- 📦 **Mirror Alternativo**: [Descargar desde el repositorio (rama main)](https://github.com/DC268/TorneoApp/raw/main/apk/torneo-app.apk)
- 🖥️ **Servidor Backend (JAR Ejecutable)**: [Descargar torneo-backend.jar](https://github.com/DC268/TorneoApp/releases/download/v1.0.0/torneo-backend.jar)

---

## 🚀 Arquitectura y Tecnologías

- **Backend**: Spring Boot 3.3.4 con Java 21, Spring Security (JWT + Google OAuth2), Spring Data JPA, HikariCP, Virtual Threads.
- **Bases de Datos**:
  - **Producción**: Supabase (PostgreSQL) con pooler de conexiones y esquema relacional optimizado (`supabase-schema.sql`).
  - **Local**: Base de datos H2 en memoria para pruebas offline inmediatas sin dependencias externas.
- **Frontend Android**: 100% Kotlin con Jetpack Compose y Material 3 (Arquitectura MVVM limpia con StateFlow, Retrofit 2, Wakelock nativo).
- **Frontend Web**: SPA integrado con Tailwind CSS y FontAwesome en `/index.html` para acceso instantáneo desde cualquier navegador.
- **Despliegue**: Preparado para Render mediante `Dockerfile` multi-stage y `render.yaml`.

---

## 🛠️ Cómo Abrir y Desarrollar en IntelliJ IDEA

1. Abre **IntelliJ IDEA**.
2. Selecciona **File -> Open...** y elige la carpeta:
   ```text
   G:\DC\Programas\TorneoApp
   ```
3. IntelliJ IDEA detectará automáticamente la configuración de Gradle multi-módulo (`app` y `backend`).
4. Selecciona el JDK:
   - Para el Backend: **Java 21 / 26 (Temurin o OpenJDK 21)**.
   - Para el Android App: **Java 17 / 21**.

---

## 📋 Las 3 Pestañas del Administrador

1. **Pestaña 1 - Inscribir Personas**:
   - Registro de participantes con nombre, correo y arquetipo de mazo MTG.
   - Listado en tiempo real con contador de participantes y opción de eliminar.
2. **Pestaña 2 - Emparejamiento Aleatorio**:
   - Algoritmo de emparejamiento 1v1 justo y aleatorio.
   - Detección y gestión automática de BYE para número impar de jugadores.
   - Asignación automática de número de mesa.
3. **Pestaña 3 - Resultados de los Encuentros**:
   - Consulta de emparejamientos y estado (`IN_PROGRESS`, `COMPLETED`).
   - Registro del jugador ganador y actualización automática de puntos en la tabla.

---

## ⚔️ MTG Life Tracker (2 a 4 Jugadores)

- Selector dinámico de jugadores (2, 3 o 4) y formato (Estándar 20 vidas o Commander 40 vidas).
- División equitativa de la pantalla con **rotación de 180°** para oponentes sentados al frente de la mesa.
- Botones grandes de `(+)` y `(-)` para modificar vidas.
- Subcontadores de **Veneno / Infect** (derrota con 10 contadores) y **Daño de Comandante** individual por oponente (derrota con 21 de daño).
- Temporizador central de 50 minutos con controles de pausa, play y reinicio.
- **Wakelock Nativo**: Mantiene la pantalla encendida permanentemente mientras se juega.

---

## 🌐 Despliegue en Render y Conexión a Supabase

### Variables de Entorno en Render:
- `SPRING_PROFILES_ACTIVE`: `prod`
- `SUPABASE_DB_URL`: `jdbc:postgresql://<HOST_SUPABASE>:6543/postgres?sslmode=require`
- `SUPABASE_DB_USER`: `postgres`
- `SUPABASE_DB_PASSWORD`: `<TU_PASSWORD>`
- `JWT_SECRET`: `<TU_CLAVE_JWT>`
- `ADMIN_EMAIL`: `<CORREO_ADMIN>`
- `ADMIN_PASSWORD`: `<CONTRASENA_ADMIN>`
