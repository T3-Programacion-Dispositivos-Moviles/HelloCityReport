package com.equipo3.hellocityreport.model

/**
 * Contrato del Modelo: qué operaciones de datos existen, sin decir cómo se guardan.
 *
 * Los Presenters dependen de esta interfaz y no de IncidenciaDataSource.
 */
interface IncidenciaRepository {

    fun obtenerTodas(): List<Incidencia>

    fun buscarPorId(id: Int): Incidencia?

    fun agregar(
        titulo: String,
        descripcion: String,
        categoria: Categoria,
        ubicacion: String,
        latitud: Double?,
        longitud: Double?,
        fotoRuta: String?
    ): Incidencia
}
