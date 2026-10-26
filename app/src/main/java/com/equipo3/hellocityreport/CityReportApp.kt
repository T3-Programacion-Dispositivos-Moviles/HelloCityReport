package com.equipo3.hellocityreport

import android.app.Application
import com.equipo3.hellocityreport.view.Notificaciones
import com.google.android.material.color.DynamicColors
import org.maplibre.android.MapLibre

/**
 * Clase Application: se crea una sola vez, antes que cualquier pantalla.
 * Está registrada en AndroidManifest.xml (android:name=".CityReportApp").
 */
class CityReportApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        // MapLibre debe iniciarse antes de generar cualquier mapa.
        MapLibre.getInstance(this)

        // El canal de notificaciones se puede crear varias veces sin problema.
        Notificaciones.crearCanal(this)
    }
}
