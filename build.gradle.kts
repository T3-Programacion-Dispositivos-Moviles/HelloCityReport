// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    // AGP 9 trae Kotlin 2.2 integrado; Firebase BoM 35 está compilado con Kotlin 2.4.
    // Declararlo aquí (sin aplicarlo) hace que el Kotlin integrado use la versión 2.4.
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.google.services) apply false
}