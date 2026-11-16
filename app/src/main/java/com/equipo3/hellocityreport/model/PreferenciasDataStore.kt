package com.equipo3.hellocityreport.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Una sola instancia de DataStore por archivo (regla de la documentación):
// el delegado la crea la primera vez y después siempre regresa la misma.
private val Context.preferencias: DataStore<Preferences> by preferencesDataStore(name = "preferencias")

/** Preferencias guardadas con Preferences DataStore (archivo preferencias.preferences_pb). */
class PreferenciasDataStore(context: Context) : PreferenciasRepository {

    private val dataStore = context.applicationContext.preferencias

    // Si el archivo no se puede leer se usan los valores por defecto.
    private val datos: Flow<Preferences> = dataStore.data.catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }

    override val tema: Flow<Tema> = datos.map { prefs ->
        Tema.entries.find { it.name == prefs[CLAVE_TEMA] } ?: Tema.SISTEMA
    }

    override val avisosActivos: Flow<Boolean> = datos.map { prefs -> prefs[CLAVE_AVISOS] ?: true }

    // edit() es una transacción: lee, cambia y escribe de una sola vez.
    override suspend fun guardarTema(tema: Tema) {
        dataStore.edit { it[CLAVE_TEMA] = tema.name }
    }

    override suspend fun guardarAvisos(activos: Boolean) {
        dataStore.edit { it[CLAVE_AVISOS] = activos }
    }

    private companion object {
        val CLAVE_TEMA = stringPreferencesKey("tema")
        val CLAVE_AVISOS = booleanPreferencesKey("avisos_activos")
    }
}
