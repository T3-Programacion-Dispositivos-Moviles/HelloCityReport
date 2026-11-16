package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.ListaIncidenciasContract
import com.equipo3.hellocityreport.model.Incidencia
import com.equipo3.hellocityreport.model.IncidenciaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Presenter de la lista. Pide los datos al Modelo y le dice a la Vista qué mostrar.
 *
 * [scope] lo da la vista y se cancela solo cuando la vista se destruye; así el
 * Presenter usa corrutinas sin depender de clases de Android.
 */
class ListaIncidenciasPresenter(
    private var view: ListaIncidenciasContract.View?,
    private val repositorio: IncidenciaRepository,
    private val scope: CoroutineScope
) : ListaIncidenciasContract.Presenter {

    private var suscripcion: Job? = null

    override fun cargarIncidencias() {
        suscripcion?.cancel()
        // Room avisa cada cambio: si se agrega una incidencia, la lista se actualiza sola.
        suscripcion = scope.launch {
            repositorio.observarTodas().collect { incidencias ->
                if (incidencias.isEmpty()) {
                    view?.mostrarListaVacia()
                } else {
                    view?.mostrarIncidencias(incidencias)
                }
            }
        }
    }

    override fun alSeleccionarIncidencia(incidencia: Incidencia) {
        view?.abrirDetalle(incidencia.id)
    }

    override fun destruir() {
        suscripcion?.cancel()
        view = null
    }
}
