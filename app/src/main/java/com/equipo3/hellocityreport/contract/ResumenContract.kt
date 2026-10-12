package com.equipo3.hellocityreport.contract

/** Contrato MVP de la pantalla de resumen. */
interface ResumenContract {

    /** Una fila del resumen ya calculada por el Presenter, lista para mostrarse. */
    data class Conteo(val etiqueta: String, val cantidad: Int, val porcentaje: Int)

    interface View {
        fun mostrarTotal(total: Int)
        fun mostrarConteoPorEstado(conteos: List<Conteo>)
        fun mostrarConteoPorCategoria(conteos: List<Conteo>)
    }

    interface Presenter : BasePresenter {
        fun cargarResumen()
    }
}
