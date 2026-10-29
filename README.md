# EcoTrip 2026

Sistema resiliente de planificación de rutas e identidad visual adaptativa (Programación Móvil, RDA-2, PUCE).

Grupo: Daniel Gonzalez, Juan Hoyos, Dilan Mena, Samantha Nuñez.

## Entorno
- Kotlin + Jetpack Compose (sin XML de vistas ni `findViewById`; solo `strings.xml` y estilos de ventana)
- `compileSdk = 36`, `targetSdk = 36`, `minSdk = 29`
- Java 21 / JBR 21 (`jvmToolchain(21)`)
- Dependencias en `gradle/libs.versions.toml`

**Importante al abrir en Android Studio:** `Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK = jbr-21`. Si Android Studio sugiere actualizar versiones del catálogo, acéptalo y mantén `compileSdk = 36`.

## Arquitectura (UDF)
```
DataStore (disco) <-> EcoTripViewModel (+ SavedStateHandle) -> uiState -> NavHost -> Formulario / Resumen
                                          ^------------- eventos (lambdas) -------------|
```
| Nivel | Mecanismo | Sobrevive a |
|---|---|---|
| Memoria | ViewModel | Rotación |
| Proceso | SavedStateHandle | Muerte del proceso |
| Disco | DataStore | Cierre total de la app |

## Pruebas manuales (emulador API 36)
1. **Rotación:** escribe en los campos, rota el emulador, nada se borra.
2. **Muerte de proceso:** activa "Don't keep activities", pulsa Home y vuelve a abrir la app.
3. **Cierre total:** escribe nombre y activa baja huella, cierra la app desde recientes y ábrela; esos dos campos se restauran desde DataStore.
4. **Material You:** abre el resumen, cambia el wallpaper del emulador, vuelve a la app y verifica el cambio de paleta.
5. **Datos anómalos:** días vacío, `0`, `999`, letras: no hay crash, se muestran errores.

## Reparto de commits (uno o más por integrante)

Los commits van en orden, porque cada parte depende de la anterior. Cada persona debe configurar su propio usuario **antes** de commitear:

```
git config user.name "Nombre Apellido"
git config user.email "correo-asociado-a-su-GitHub"
```

| # | Integrante | Mensaje del commit | Archivos |
|---|---|---|---|
| 1 | Daniel Gonzalez | `feat: data models and routes` | `gradle/`, `gradle.properties`, `settings.gradle.kts`, `build.gradle.kts`, `.gitignore`, `README.md`, `app/build.gradle.kts`, `data/PreferenciaViaje.kt`, `navigation/Rutas.kt` |
| 2 | Juan Hoyos | `feat: viewmodel and dataStore implementation` | `data/PreferenciasRepository.kt`, `ui/EcoTripViewModel.kt`, `ui/Validacion.kt` |
| 3 | Dilan Mena | `feat: compose ui layout with material design 3` | `ui/theme/`, `ui/screens/`, `res/` |
| 4 | Samantha Nuñez | `feat: type-safe navigation and testing` | `navigation/EcoTripNavHost.kt`, `MainActivity.kt`, `AndroidManifest.xml`, `src/test/` |

Rutas relativas: los archivos Kotlin están en `app/src/main/java/ec/edu/puce/ecotrip/`.
Los commits intermedios no compilan por sí solos (faltan piezas de los siguientes); el proyecto completo compila tras el commit 4.

### Commit 1 (Daniel: crea el repositorio)
```
git init
git branch -M main
git add gradle gradle.properties settings.gradle.kts build.gradle.kts .gitignore README.md app/build.gradle.kts \
  app/src/main/java/ec/edu/puce/ecotrip/data/PreferenciaViaje.kt \
  app/src/main/java/ec/edu/puce/ecotrip/navigation/Rutas.kt
git commit -m "feat: data models and routes"
git remote add origin <URL-DEL-REPOSITORIO>
git push -u origin main
```
Después Daniel agrega a los demás como colaboradores en GitHub (Settings > Collaborators).

### Commits 2, 3 y 4 (cada quien, en su turno)
```
git clone <URL-DEL-REPOSITORIO>
cd EcoTrip2026
# copia desde el zip SOLO tus archivos, en las mismas rutas
git config user.name "..." && git config user.email "..."
git add <tus archivos>
git commit -m "<mensaje de la tabla>"
git pull --rebase
git push
```
Quien ya tiene el repositorio clonado solo hace `git pull` antes de copiar sus archivos.
