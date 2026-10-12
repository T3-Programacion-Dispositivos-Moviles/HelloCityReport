package com.equipo3.hellocityreport.contract

/** Contrato MVP del formulario para reportar una nueva incidencia. */
interface NuevaIncidenciaContract {

    /** Campos del formulario que pueden marcarse con error. */
    enum class Campo { TITULO, CATEGORIA, UBICACION, DESCRIPCION }

    interface View {
        fun mostrarCategorias(categorias: List<String>)
        fun mostrarErrores(camposConError: Set<Campo>)
        fun mostrarIncidenciaGuardada()
        fun regresarALista()
    }

    interface Presenter : BasePresenter {
        fun iniciar()

        /** Recibe el texto tal como lo escribió el usuario; el Presenter lo valida. */
        fun guardar(titulo: String, categoria: String, ubicacion: String, descripcion: String)
    }
}
