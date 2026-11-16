package com.equipo3.hellocityreport.view

import androidx.appcompat.app.AppCompatDelegate
import com.equipo3.hellocityreport.model.Tema

/**
 * Cambia el modo claro u oscuro de toda la app. Si cambia, AppCompat vuelve a
 * crear la pantalla abierta con los nuevos colores.
 */
fun aplicarTema(tema: Tema) {
    val modo = when (tema) {
        Tema.SISTEMA -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        Tema.CLARO -> AppCompatDelegate.MODE_NIGHT_NO
        Tema.OSCURO -> AppCompatDelegate.MODE_NIGHT_YES
    }
    if (AppCompatDelegate.getDefaultNightMode() != modo) AppCompatDelegate.setDefaultNightMode(modo)
}
