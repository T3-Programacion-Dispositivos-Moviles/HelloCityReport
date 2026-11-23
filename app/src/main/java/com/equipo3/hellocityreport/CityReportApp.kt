package com.equipo3.hellocityreport

import android.app.Application
import androidx.fragment.app.Fragment
import com.equipo3.hellocityreport.model.CuentaFirebase
import com.equipo3.hellocityreport.model.CuentaRepository
import com.equipo3.hellocityreport.model.IncidenciaRepository
import com.equipo3.hellocityreport.model.IncidenciaRepositoryRoom
import com.equipo3.hellocityreport.model.PreferenciasDataStore
import com.equipo3.hellocityreport.model.PreferenciasRepository
import com.equipo3.hellocityreport.model.db.CityReportDatabase
import com.equipo3.hellocityreport.view.Notificaciones
import com.equipo3.hellocityreport.view.aplicarTema
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.maplibre.android.MapLibre

/**
 * Clase Application: se crea una sola vez, antes que cualquier pantalla.
 * Está registrada en AndroidManifest.xml (android:name=".CityReportApp").
 *
 * También crea el Modelo (base de datos y preferencias) una sola vez y lo
 * comparte con todas las pantallas.
 */
class CityReportApp : Application() {

    val repositorio: IncidenciaRepository by lazy {
        IncidenciaRepositoryRoom(CityReportDatabase.obtener(this))
    }

    val preferencias: PreferenciasRepository by lazy { PreferenciasDataStore(this) }

    // Firebase se inicia solo (FirebaseInitProvider) con los datos de google-services.json.
    val cuenta: CuentaRepository by lazy { CuentaFirebase() }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        // El tema se aplica antes de abrir la primera pantalla para que no se vea
        // un instante en claro y luego cambie. Es un archivo pequeño y se lee una vez.
        aplicarTema(runBlocking { preferencias.tema.first() })

        // MapLibre debe iniciarse antes de generar cualquier mapa.
        MapLibre.getInstance(this)

        // El canal de notificaciones se puede crear varias veces sin problema.
        Notificaciones.crearCanal(this)
    }
}

/** Acceso al Modelo compartido desde cualquier Fragment. */
val Fragment.app: CityReportApp
    get() = requireActivity().application as CityReportApp
