// Archivo de construcción de nivel raíz.
// Los plugins se declaran aquí con `apply false` y se aplican en :app,
// siempre a través del catálogo de versiones (gradle/libs.versions.toml).
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
