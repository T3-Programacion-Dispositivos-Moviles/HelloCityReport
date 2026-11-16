package com.equipo3.hellocityreport.model

import kotlinx.coroutines.flow.Flow

/**
 * Contrato del Modelo: qué operaciones de datos existen, sin decir cómo se guardan.
 *
 * Los Presenters dependen de esta interfaz y no de Room. Las operaciones son
 * asíncronas: leer o escribir en la base no debe trabar la pantalla.
 */
interface IncidenciaRepository {

    /** Emite la lista actual y luego una nueva cada vez que cambian los datos. */
    fun observarTodas(): Flow<List<Incidencia>>

    suspend fun buscarPorId(id: Int): Incidencia?

    suspend fun agregar(
        titulo: String,
        descripcion: String,
        categoria: Categoria,
        ubicacion: String,
        latitud: Double?,
        longitud: Double?,
        fotoRuta: String?
    ): Incidencia
}
