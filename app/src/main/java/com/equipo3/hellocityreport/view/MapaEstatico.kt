package com.equipo3.hellocityreport.view

import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.equipo3.hellocityreport.databinding.VistaMapaBinding
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.maplibre.android.snapshotter.MapSnapshotter
import kotlin.math.cos
import kotlin.math.pow

/**
 * Mapa estático (layout vista_mapa.xml): una imagen del mapa centrada en unas
 * coordenadas, con el pin encima. Se genera con el Snapshotter de MapLibre y
 * los mapas gratuitos de OpenFreeMap, que no requieren clave de API. Es el
 * equivalente al "modo lite" planeado en el wireframe: solo muestra el lugar.
 */
class MapaEstatico(private val vista: VistaMapaBinding) {

    private var snapshotter: MapSnapshotter? = null

    fun mostrar(latitud: Double, longitud: Double, precisionMetros: Float? = null) {
        vista.ivMapa.doOnLayout { imagen ->
            cancelar()
            val densidad = imagen.resources.displayMetrics.density
            // El tamaño va en dp; withPixelRatio lo convierte a pixeles reales.
            val opciones = MapSnapshotter.Options((imagen.width / densidad).toInt(), (imagen.height / densidad).toInt())
                .withStyleBuilder(Style.Builder().fromUri(ESTILO))
                .withCameraPosition(
                    CameraPosition.Builder().target(LatLng(latitud, longitud)).zoom(ZOOM).build()
                )
                .withPixelRatio(densidad)
                .withLogo(false)
            snapshotter = MapSnapshotter(imagen.context, opciones).also {
                it.start({ captura ->
                    vista.ivMapa.setImageBitmap(captura.bitmap)
                    vista.tvAtribucion.isVisible = true
                }) {
                    // Sin internet no hay mapa: se queda el fondo con el pin.
                }
            }
            vista.ivPin.isVisible = true
            mostrarPrecision(latitud, precisionMetros, densidad)
        }
    }

    /** Regresa al estado sin ubicación. */
    fun limpiar() {
        cancelar()
        vista.ivMapa.setImageDrawable(null)
        vista.ivPin.isVisible = false
        vista.vPrecision.isVisible = false
        vista.tvAtribucion.isVisible = false
    }

    fun cancelar() {
        snapshotter?.cancel()
        snapshotter = null
    }

    // Metros que mide un dp del mapa a este zoom: circunferencia de la Tierra * cos(latitud) / (512 * 2^zoom).
    private fun mostrarPrecision(latitud: Double, precisionMetros: Float?, densidad: Float) {
        if (precisionMetros == null || precisionMetros <= 0f) {
            vista.vPrecision.isVisible = false
            return
        }
        val metrosPorDp = 40_075_016.686 * cos(Math.toRadians(latitud)) / (512 * 2.0.pow(ZOOM))
        val diametro = (2 * precisionMetros / metrosPorDp * densidad).toInt()
            .coerceIn((24 * densidad).toInt(), vista.ivMapa.height)
        vista.vPrecision.updateLayoutParams { width = diametro; height = diametro }
        vista.vPrecision.isVisible = true
    }

    companion object {
        private const val ESTILO = "https://tiles.openfreemap.org/styles/liberty"
        private const val ZOOM = 16.0
    }
}
