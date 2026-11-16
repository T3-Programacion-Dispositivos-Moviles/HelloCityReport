package com.equipo3.hellocityreport.contract

import com.equipo3.hellocityreport.model.Incidencia

/**
 * Contrato MVP de la pantalla de lista (Inicio).
 *
 * - View: lo que la pantalla sabe hacer (mostrar datos y navegar). La implementa
 *   ListaIncidenciasFragment.
 * - Presenter: los eventos que la pantalla le avisa al Presenter. La implementa
 *   ListaIncidenciasPresenter.
 *
 * Cada lado solo conoce la interfaz del otro, no su clase concreta.
 */
interface ListaIncidenciasContract {

    interface View {
        fun mostrarIncidencias(incidencias: List<Incidencia>)
        fun mostrarListaVacia()
        fun abrirDetalle(incidenciaId: Int)
    }

    interface Presenter : BasePresenter {
        /** Se llama al crear la vista; desde ahí la lista se actualiza sola con cada cambio. */
        fun cargarIncidencias()
        fun alSeleccionarIncidencia(incidencia: Incidencia)
    }
}
