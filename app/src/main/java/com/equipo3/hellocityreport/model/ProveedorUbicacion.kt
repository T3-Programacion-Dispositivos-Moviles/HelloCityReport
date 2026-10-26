package com.equipo3.hellocityreport.model

/** Lugar donde ocurrió la incidencia. precision está en metros; direccion puede faltar. */
data class Ubicacion(
    val latitud: Double,
    val longitud: Double,
    val precision: Float,
    val direccion: String? = null
)

/**
 * Contrato para obtener la ubicación actual del dispositivo.
 *
 * El Presenter solo conoce esta interfaz; la implementación real
 * (ProveedorUbicacionFused) usa Fused Location Provider de Google Play services.
 * En las pruebas unitarias se usa un proveedor falso.
 */
interface ProveedorUbicacion {

    /**
     * Pide una sola ubicación. [precisa] es false cuando el usuario solo dio
     * permiso de ubicación aproximada. Devuelve null si no se pudo obtener.
     */
    fun obtenerUbicacion(precisa: Boolean, alTerminar: (Ubicacion?) -> Unit)
}
