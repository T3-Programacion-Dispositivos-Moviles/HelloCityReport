package com.equipo3.hellocityreport.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.widget.ImageView
import androidx.exifinterface.media.ExifInterface
import java.io.IOException

/**
 * Carga las fotos que guarda CameraX.
 *
 * CameraX no gira los pixeles de la foto: guarda la orientación en los datos
 * EXIF del archivo. Si se carga tal cual puede verse de lado, por eso aquí se
 * lee el EXIF y se gira la imagen. También se reduce para no gastar memoria.
 */
object Fotos {

    fun cargar(ruta: String, ladoMaximo: Int): Bitmap? {
        val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(ruta, medidas)
        if (medidas.outWidth <= 0) return null

        // inSampleSize debe ser potencia de 2: 2 = mitad de ancho y alto, 4 = un cuarto...
        var muestra = 1
        while (maxOf(medidas.outWidth, medidas.outHeight) / (muestra * 2) >= ladoMaximo) muestra *= 2
        val bitmap = BitmapFactory.decodeFile(ruta, BitmapFactory.Options().apply { inSampleSize = muestra })
            ?: return null

        val grados = try {
            when (ExifInterface(ruta).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } catch (e: IOException) {
            0f
        }
        if (grados == 0f) return bitmap
        val giro = Matrix().apply { postRotate(grados) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, giro, true)
    }
}

fun ImageView.mostrarFoto(ruta: String, ladoMaximo: Int = 1280) {
    setImageBitmap(Fotos.cargar(ruta, ladoMaximo))
}
