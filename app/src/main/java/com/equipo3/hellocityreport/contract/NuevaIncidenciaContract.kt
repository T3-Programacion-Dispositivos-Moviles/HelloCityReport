package com.equipo3.hellocityreport.contract

import com.equipo3.hellocityreport.model.Incidencia
import com.equipo3.hellocityreport.model.Ubicacion

/** Contrato MVP del formulario para reportar una nueva incidencia. */
interface NuevaIncidenciaContract {

    /** Partes del formulario que pueden marcarse con error. */
    enum class Campo { FOTO, TITULO, CATEGORIA, UBICACION, DESCRIPCION }

    interface View {
        fun mostrarCategorias(categorias: List<String>)
        fun mostrarFoto(ruta: String)
        fun mostrarSinFoto()
        fun mostrarBuscandoUbicacion()
        fun mostrarUbicacion(ubicacion: Ubicacion)
        fun mostrarUbicacionNoDisponible()

        /** Propone la dirección encontrada; la vista solo la usa si el campo está vacío. */
        fun sugerirDireccion(direccion: String)
        fun mostrarErrores(camposConError: Set<Campo>)
        fun mostrarIncidenciaGuardada()
        fun notificarIncidenciaGuardada(incidencia: Incidencia)
        fun regresarALista()
    }

    /**
     * A diferencia de los otros Presenters, este vive lo mismo que el Fragment
     * (no que su vista): al ir a la cámara la vista se destruye y se vuelve a
     * crear, y la foto y la ubicación no se deben perder. Por eso la vista se
     * conecta con adjuntarVista() y se suelta con destruir().
     */
    interface Presenter : BasePresenter {
        fun adjuntarVista(vista: View)

        /** Llena el formulario: categorías y, si ya había, la foto y la ubicación. */
        fun iniciar()
        fun alTomarFoto(ruta: String)
        fun alQuitarFoto()

        /** La vista ya tiene permiso; [precisa] es false si solo hay ubicación aproximada. */
        fun alPermitirUbicacion(precisa: Boolean)
        fun alNegarUbicacion()

        /** Recibe el texto tal como lo escribió el usuario; el Presenter lo valida. */
        fun guardar(titulo: String, categoria: String, ubicacion: String, descripcion: String)

        // Para conservar la foto y la ubicación si el sistema recrea la pantalla
        // (por ejemplo, al girar el teléfono o cambiar a modo oscuro).
        val fotoRuta: String?
        val ubicacion: Ubicacion?
        fun restaurar(fotoRuta: String?, ubicacion: Ubicacion?)
    }
}
