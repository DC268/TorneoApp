# 🏆 Torneo Master - Aplicación de Gestión de Torneos y Emparejamientos

<p align="center">
  <img src="https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Android-8.0%2B%20(API%2026%2B)-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge" alt="License" />
</p>

Aplicación integral para la administración, emparejamiento aleatorio y seguimiento de resultados de torneos en tiempo real. Desarrollada con estricta separación entre **Backend** (Java 21 Spring Boot) y **Frontend** (Kotlin Jetpack Compose para Android y Panel Web interactivo), siguiendo la arquitectura modular del proyecto **Hato**.

---

## 📥 Descargas Directas

Puedes descargar e instalar la aplicación inmediatamente desde este repositorio:

| Archivo | Tipo | Enlace de Descarga Directa |
|---|---|---|
| **`torneo-app.apk`** | 📱 Aplicación Móvil Android | [⬇️ Descargar APK Oficial (Directo)](https://github.com/DC268/TorneoApp/raw/main/apk/torneo-app.apk) |
| **`torneo-debug.apk`** | 📱 APK Modo Debug | [⬇️ Descargar APK Debug](https://github.com/DC268/TorneoApp/raw/main/apk/torneo-debug.apk) |
| **`torneo-backend.jar`** | ⚙️ Servidor Backend Ejecutable | [⬇️ Descargar Servidor JAR (Release v1.0.0)](https://github.com/DC268/TorneoApp/releases/download/v1.0.0/torneo-backend.jar) |
| **Página de Versiones** | 📦 GitHub Releases | [Visitar Releases en GitHub](https://github.com/DC268/TorneoApp/releases) |

---

## 🔐 Autenticación y Roles

La plataforma implementa control de acceso basado en roles (**RBAC**):

| Rol | Correo Preconfigurado | Contraseña Inicial | Privilegios |
|---|---|---|---|
| **ADMINISTRADOR** (`ADMIN`) | `dcagua268@gmail.com` | `Holasoyyo12345` | Control total: Crear torneos, inscribir participantes, emparejar de forma random y registrar resultados oficiales. |
| **CLIENTE** (`CLIENT`) | `cliente@torneo.com` | `Cliente12345` | Modo espectador: Consultar torneos, ver participantes, seguir cruces y marcadores en vivo. |

### Reglas de Seguridad y Google Sign-In
- **Validación de Contraseña**: Requiere obligatoriamente **letras y números** (mínimo 6 caracteres).
- **Inicio de Sesión con Google**: Integrado vía Google ID Token (`/api/auth/google`). Si el usuario ingresa con `dcagua268@gmail.com`, se le asigna de forma automática el rol de **ADMINISTRADOR**.

---

## 📑 Las 3 Pestañas del Administrador

La interfaz del Administrador está dividida en las 3 secciones requeridas:

1. 📝 **Pestaña 1: Inscribir Personas a un Torneo**
   - Formulario con Nombre, Correo electrónico (validado contra duplicados), Teléfono, Documento/ID y Categoría.
   - Lista en tiempo real de personas inscritas con números de *seed* correlativos y botón de eliminación.

2. 🎲 **Pestaña 2: Emparejar Automáticamente y de Forma Random**
   - Botón de sorteo automático: baraja a los participantes de forma imparcial usando `SecureRandom`.
   - Generación de llaves 1 vs 1. Si el número de inscritos es impar, el sistema asigna automáticamente un pase directo (**BYE**) al competidor restante.
   - Visualización clara de los cruces generados.

3. 🏆 **Pestaña 3: Resultados de los Encuentros**
   - Tablero de resultados organizado por rondas (Preliminares, Cuartos, Semifinal, Gran Final).
   - Modal para ingresar los marcadores de ambos jugadores, seleccionar al ganador oficial y añadir notas de observación.
   - Avance automático de ganadores hacia la ronda final.

---

## 🏗️ Arquitectura del Repositorio

```text
TorneoApp/
├── apk/                                 # 📦 Binarios listos para instalar
│   ├── torneo-app.apk                   # APK oficial para Android
│   └── torneo-debug.apk                 # APK de depuración
│
├── backend/                             # ☕ Backend (Java 21 + Spring Boot 3.3.4)
│   ├── build.gradle
│   ├── src/main/java/com/torneo/
│   │   ├── api/controller/              # AuthController, ParticipantController, MatchController
│   │   ├── api/dto/                     # DTOs y validaciones de contraseña (letras y números)
│   │   ├── api/security/                # JWT Provider y verificador de Google Sign-In
│   │   ├── application/service/         # Lógica de negocio (Sorteo aleatorio, bracket, auth)
│   │   ├── domain/entity/               # Entidades JPA (User, Tournament, Participant, Match)
│   │   └── config/                      # DataSeeder con admin preconfigurado
│   └── src/main/resources/
│       ├── application.properties       # Base de datos H2 persistente
│       └── static/index.html            # Panel Web interactivo (Tailwind CSS + Google Sign-In)
│
├── app/                                 # 📱 Frontend Móvil (100% Kotlin + Jetpack Compose)
│   ├── build.gradle.kts                 # Jetpack Compose, Material 3, Retrofit 2, Corrutinas
│   └── src/main/java/com/torneo/app/
│       ├── MainActivity.kt              # Punto de entrada Compose
│       ├── data/api/                    # Retrofit service y OkHttp con interceptor JWT
│       ├── data/repository/             # Repositorios y sesión
│       └── ui/screens/
│           ├── auth/LoginScreen.kt      # Login con Google y validación de contraseña
│           ├── admin/                   # Las 3 pestañas principales
│           │   ├── TabInscribirScreen.kt   (Pestaña 1)
│           │   ├── TabEmparejarScreen.kt   (Pestaña 2)
│           │   └── TabResultadosScreen.kt  (Pestaña 3)
│           └── client/ClientDashboardScreen.kt
│
├── settings.gradle.kts                  # Configuración raíz
├── build.gradle.kts
└── README.md
```

---

## 🚀 Instrucciones de Ejecución

### Opción 1: Ejecutar Backend y Panel Web (Navegador)
1. Navega a `backend/`:
   ```bash
   cd backend
   .\gradlew.bat bootRun
   ```
2. Abre en tu navegador:
   👉 **http://localhost:8080**
3. Inicia sesión con **Google** o usa las credenciales de administrador:
   - **Correo**: `dcagua268@gmail.com`
   - **Contraseña**: `Holasoyyo12345`

### Opción 2: Abrir en Android Studio (App Móvil Kotlin)
1. Abre **Android Studio** y selecciona **Open**.
2. Selecciona la carpeta raíz del proyecto `TorneoApp`.
3. Conecta tu dispositivo o emulador y haz clic en **Run 'app'** (▶️).
4. O si prefieres instalar directamente en tu teléfono, descarga el archivo [`apk/torneo-app.apk`](https://github.com/DC268/TorneoApp/raw/main/apk/torneo-app.apk) e instálalo.
