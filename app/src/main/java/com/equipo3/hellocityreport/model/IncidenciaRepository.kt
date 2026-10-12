package com.equipo3.hellocityreport.model

/**
 * Contrato del Modelo: qué operaciones de datos existen, sin decir cómo se guardan.
 *
 * Los Presenters dependen de esta interfaz y no de IncidenciaDataSource. Así,
 * si después se cambia a una base de datos, los Presenters no se modifican, y
 * en las pruebas unitarias se puede usar un repositorio falso.
 */
interface IncidenciaRepository {

    fun obtenerTodas(): List<Incidencia>

    fun buscarPorId(id: Int): Incidencia?

    fun agregar(
        titulo: String,
        descripcion: String,
        categoria: Categoria,
        ubicacion: String
    ): Incidencia
}
