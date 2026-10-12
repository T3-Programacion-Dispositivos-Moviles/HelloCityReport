package com.equipo3.hellocityreport

import android.app.Application
import com.google.android.material.color.DynamicColors

/**
 * Clase Application: se crea una sola vez, antes que cualquier pantalla.
 * Está registrada en AndroidManifest.xml (android:name=".CityReportApp").
 */
class CityReportApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Colores dinámicos de Material 3 (Android 12+): la paleta de la app se
        // toma del fondo de pantalla del teléfono. En versiones anteriores se usa
        // la paleta de respaldo de res/values/colors.xml.
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
