package com.equipo3.hellocityreport.model

import kotlinx.coroutines.flow.Flow

/** Tema de la app. SISTEMA sigue el modo claro u oscuro del teléfono. */
enum class Tema { SISTEMA, CLARO, OSCURO }

/**
 * Preferencias del usuario. Igual que con las incidencias, los Presenters solo
 * conocen esta interfaz; la implementación usa DataStore.
 */
interface PreferenciasRepository {
    val tema: Flow<Tema>
    val avisosActivos: Flow<Boolean>
    suspend fun guardarTema(tema: Tema)
    suspend fun guardarAvisos(activos: Boolean)
}
