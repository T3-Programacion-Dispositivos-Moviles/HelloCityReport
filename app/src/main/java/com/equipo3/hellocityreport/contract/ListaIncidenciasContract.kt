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
        /** Se llama cada vez que se crea la vista (también al regresar a ella). */
        fun cargarIncidencias()
        fun alSeleccionarIncidencia(incidencia: Incidencia)
    }
}
