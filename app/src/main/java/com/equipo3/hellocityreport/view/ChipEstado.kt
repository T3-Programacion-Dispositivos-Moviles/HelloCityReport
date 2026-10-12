package com.equipo3.hellocityreport.view

import android.content.res.ColorStateList
import com.equipo3.hellocityreport.model.Estado
import com.google.android.material.chip.Chip
import com.google.android.material.color.MaterialColors
import com.google.android.material.R as MaterialR

/**
 * Muestra el estado de una incidencia en un Chip.
 *
 * Los colores son atributos del tema de Material 3 (no colores fijos), así que
 * cambian solos con los colores dinámicos y con el modo oscuro.
 */
fun Chip.mostrarEstado(estado: Estado) {
    val (fondo, texto) = when (estado) {
        Estado.PENDIENTE -> MaterialR.attr.colorErrorContainer to MaterialR.attr.colorOnErrorContainer
        Estado.EN_PROCESO -> MaterialR.attr.colorTertiaryContainer to MaterialR.attr.colorOnTertiaryContainer
        Estado.RESUELTO -> MaterialR.attr.colorPrimaryContainer to MaterialR.attr.colorOnPrimaryContainer
    }
    text = estado.etiqueta
    chipBackgroundColor = ColorStateList.valueOf(MaterialColors.getColor(this, fondo))
    setTextColor(MaterialColors.getColor(this, texto))
}
