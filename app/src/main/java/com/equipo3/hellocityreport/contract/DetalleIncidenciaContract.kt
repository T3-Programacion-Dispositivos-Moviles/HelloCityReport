package com.equipo3.hellocityreport.contract

import com.equipo3.hellocityreport.model.Incidencia

/** Contrato MVP de la pantalla de detalle. */
interface DetalleIncidenciaContract {

    interface View {
        fun mostrarIncidencia(incidencia: Incidencia)
        fun mostrarNoEncontrada()
    }

    interface Presenter : BasePresenter {
        fun cargarIncidencia(id: Int)
    }
}
