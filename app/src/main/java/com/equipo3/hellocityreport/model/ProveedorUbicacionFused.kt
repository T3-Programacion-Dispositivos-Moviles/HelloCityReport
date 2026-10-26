package com.equipo3.hellocityreport.model

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.io.IOException
import java.util.Locale
import kotlin.concurrent.thread

/**
 * Obtiene la ubicación con Fused Location Provider y la convierte en una
 * dirección legible con Geocoder.
 *
 * Se usa getCurrentLocation() (una sola lectura) en lugar de
 * getLastLocation(), que puede devolver una ubicación vieja o null.
 */
class ProveedorUbicacionFused(context: Context) : ProveedorUbicacion {

    private val contexto = context.applicationContext
    private val cliente = LocationServices.getFusedLocationProviderClient(contexto)
    private val hiloPrincipal = Handler(Looper.getMainLooper())

    // La Vista pide el permiso de ubicación antes de llamar a este método.
    @SuppressLint("MissingPermission")
    override fun obtenerUbicacion(precisa: Boolean, alTerminar: (Ubicacion?) -> Unit) {
        val prioridad = if (precisa) Priority.PRIORITY_HIGH_ACCURACY
        else Priority.PRIORITY_BALANCED_POWER_ACCURACY
        try {
            cliente.getCurrentLocation(prioridad, CancellationTokenSource().token)
                .addOnSuccessListener { location: Location? ->
                    // null = ubicación apagada en el teléfono o sin señal.
                    if (location == null) alTerminar(null) else buscarDireccion(location, alTerminar)
                }
                .addOnFailureListener { alTerminar(null) }
        } catch (e: SecurityException) {
            alTerminar(null)
        }
    }

    // Geocoder necesita internet; si falla, la ubicación se entrega sin dirección.
    private fun buscarDireccion(location: Location, alTerminar: (Ubicacion?) -> Unit) {
        val ubicacion = Ubicacion(location.latitude, location.longitude, location.accuracy)
        if (!Geocoder.isPresent()) {
            alTerminar(ubicacion)
            return
        }
        val geocoder = Geocoder(contexto, Locale.getDefault())
        val entregar = { direccion: Address? ->
            hiloPrincipal.post { alTerminar(ubicacion.copy(direccion = formatear(direccion))) }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.latitude, location.longitude, 1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(direcciones: MutableList<Address>) {
                        entregar(direcciones.firstOrNull())
                    }

                    override fun onError(mensaje: String?) {
                        entregar(null)
                    }
                })
        } else {
            // En Android 12 o anterior la consulta bloquea, por eso va en otro hilo.
            thread {
                val direccion = try {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()
                } catch (e: IOException) {
                    null
                }
                entregar(direccion)
            }
        }
    }

    // "Té 950, Granjas México, Iztacalco, 08400 Ciudad de México..." -> primeras tres partes.
    private fun formatear(direccion: Address?): String? =
        direccion?.getAddressLine(0)?.split(", ")?.take(3)?.joinToString(", ")
}
