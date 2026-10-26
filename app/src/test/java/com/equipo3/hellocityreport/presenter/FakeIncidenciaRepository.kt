package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.model.Categoria
import com.equipo3.hellocityreport.model.Estado
import com.equipo3.hellocityreport.model.Incidencia
import com.equipo3.hellocityreport.model.IncidenciaRepository
import com.equipo3.hellocityreport.model.ProveedorUbicacion
import com.equipo3.hellocityreport.model.Ubicacion

/**
 * Repositorio falso para las pruebas: guarda todo en una lista y permite revisar
 * qué se agregó. Funciona porque los Presenters dependen de la interfaz
 * IncidenciaRepository y no de IncidenciaDataSource.
 */
class FakeIncidenciaRepository(iniciales: List<Incidencia> = emptyList()) : IncidenciaRepository {

    val incidencias = iniciales.toMutableList()

    override fun obtenerTodas(): List<Incidencia> = incidencias.toList()

    override fun buscarPorId(id: Int): Incidencia? = incidencias.find { it.id == id }

    override fun agregar(
        titulo: String,
        descripcion: String,
        categoria: Categoria,
        ubicacion: String,
        latitud: Double?,
        longitud: Double?,
        fotoRuta: String?
    ): Incidencia {
        val nueva = incidencia(incidencias.size + 1, titulo, categoria, Estado.PENDIENTE).copy(
            descripcion = descripcion,
            ubicacion = ubicacion,
            latitud = latitud,
            longitud = longitud,
            fotoRuta = fotoRuta
        )
        incidencias.add(nueva)
        return nueva
    }
}

/**
 * Proveedor de ubicación falso: no usa GPS, responde al instante con lo que la
 * prueba le indique (una ubicación o null si "no hay señal").
 */
class FakeProveedorUbicacion(var respuesta: Ubicacion?) : ProveedorUbicacion {

    var pidioPrecisa: Boolean? = null

    override fun obtenerUbicacion(precisa: Boolean, alTerminar: (Ubicacion?) -> Unit) {
        pidioPrecisa = precisa
        alTerminar(respuesta)
    }
}

/** Crea una incidencia de prueba con datos de relleno. */
fun incidencia(
    id: Int,
    titulo: String = "Incidencia $id",
    categoria: Categoria = Categoria.BACHE,
    estado: Estado = Estado.PENDIENTE
) = Incidencia(id, titulo, "Descripción", categoria, estado, "Ubicación", "01/10/2026")
