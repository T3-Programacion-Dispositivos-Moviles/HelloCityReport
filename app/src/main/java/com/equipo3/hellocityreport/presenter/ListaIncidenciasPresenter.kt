package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.ListaIncidenciasContract
import com.equipo3.hellocityreport.model.Incidencia
import com.equipo3.hellocityreport.model.IncidenciaRepository

/**
 * Presenter de la lista. Pide los datos al Modelo y le dice a la Vista qué mostrar.
 *
 * No importa nada de Android: por eso se puede probar con pruebas unitarias
 * normales (ver src/test).
 */
class ListaIncidenciasPresenter(
    private var view: ListaIncidenciasContract.View?,
    private val repositorio: IncidenciaRepository
) : ListaIncidenciasContract.Presenter {

    override fun cargarIncidencias() {
        val incidencias = repositorio.obtenerTodas()
        if (incidencias.isEmpty()) {
            view?.mostrarListaVacia()
        } else {
            view?.mostrarIncidencias(incidencias)
        }
    }

    override fun alSeleccionarIncidencia(incidencia: Incidencia) {
        view?.abrirDetalle(incidencia.id)
    }

    override fun destruir() {
        view = null
    }
}
