package com.equipo3.hellocityreport.model.db

import androidx.room.TypeConverter
import com.equipo3.hellocityreport.model.Estado

/**
 * SQLite no tiene tipo enum: el estado se guarda como texto
 * (PENDIENTE, EN_PROCESO o RESUELTO) y se convierte de regreso al leer.
 */
class Convertidores {

    @TypeConverter
    fun estadoATexto(estado: Estado): String = estado.name

    @TypeConverter
    fun textoAEstado(texto: String): Estado = Estado.valueOf(texto)
}
