package com.equipo3.hellocityreport.view

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import com.equipo3.hellocityreport.MainActivity
import com.equipo3.hellocityreport.R
import com.equipo3.hellocityreport.model.Incidencia

/**
 * Notificaciones de la app con NotificationCompat (funciona igual desde
 * Android 7, aunque los canales solo existen desde Android 8).
 */
object Notificaciones {

    private const val CANAL_REPORTES = "reportes"

    /** Desde Android 8 cada notificación pertenece a un canal que el usuario puede configurar. */
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val canal = NotificationChannel(
            CANAL_REPORTES,
            context.getString(R.string.canal_reportes_nombre),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = context.getString(R.string.canal_reportes_descripcion) }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    fun tienePermiso(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    /**
     * Confirma que el reporte se registró. Muestra la foto en grande y, al
     * tocarla, abre el detalle de esa incidencia.
     */
    fun avisarIncidenciaGuardada(context: Context, incidencia: Incidencia) {
        // Deep link: arma la pila Inicio > Detalle aunque la app esté cerrada.
        val abrirDetalle = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.detalleIncidenciaFragment)
            .setArguments(Bundle().apply { putInt("incidenciaId", incidencia.id) })
            .createPendingIntent()

        val resumen = context.getString(
            R.string.notif_resumen, incidencia.categoria.etiqueta, incidencia.estado.etiqueta
        )
        val builder = NotificationCompat.Builder(context, CANAL_REPORTES)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setColor(ContextCompat.getColor(context, R.color.md_primary))
            .setContentTitle(context.getString(R.string.notif_titulo_guardada))
            .setContentText(incidencia.titulo)
            .setSubText(resumen)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(abrirDetalle)
            .setAutoCancel(true)

        val foto = incidencia.fotoRuta?.let { Fotos.cargar(it, 1024) }
        if (foto != null) {
            // BigPictureStyle: al expandir la notificación se ve la foto completa.
            builder.setLargeIcon(foto).setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(foto)
                    .bigLargeIcon(null as Bitmap?)
                    .setBigContentTitle(incidencia.titulo)
                    .setSummaryText(incidencia.ubicacion)
            )
        } else {
            builder.setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(incidencia.titulo)
                    .bigText(incidencia.ubicacion)
            )
        }

        // El id de la incidencia evita que una notificación reemplace a otra.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(incidencia.id, builder.build())
        }
    }
}
