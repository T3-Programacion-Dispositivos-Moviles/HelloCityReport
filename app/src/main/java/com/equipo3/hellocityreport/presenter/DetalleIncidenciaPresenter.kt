package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.DetalleIncidenciaContract
import com.equipo3.hellocityreport.model.IncidenciaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Presenter del detalle: busca la incidencia por id y decide qué mostrar. */
class DetalleIncidenciaPresenter(
    private var view: DetalleIncidenciaContract.View?,
    private val repositorio: IncidenciaRepository,
    private val scope: CoroutineScope
) : DetalleIncidenciaContract.Presenter {

    override fun cargarIncidencia(id: Int) {
        scope.launch {
            val incidencia = repositorio.buscarPorId(id)
            if (incidencia == null) {
                view?.mostrarNoEncontrada()
            } else {
                view?.mostrarIncidencia(incidencia)
            }
        }
    }

    override fun destruir() {
        view = null
    }
}
