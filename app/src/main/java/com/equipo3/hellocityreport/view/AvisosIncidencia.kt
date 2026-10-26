package com.equipo3.hellocityreport.view

import com.equipo3.hellocityreport.model.Incidencia

/**
 * Lo implementa MainActivity. El formulario se cierra justo después de guardar,
 * así que el permiso de notificaciones (Android 13+) lo pide la Activity, que
 * sigue viva, y ella muestra la notificación cuando el usuario responde.
 */
interface AvisosIncidencia {
    fun avisarIncidenciaGuardada(incidencia: Incidencia)
}
