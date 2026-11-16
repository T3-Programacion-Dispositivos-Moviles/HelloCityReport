package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.ResumenContract
import com.equipo3.hellocityreport.contract.ResumenContract.Conteo
import com.equipo3.hellocityreport.model.Categoria
import com.equipo3.hellocityreport.model.Estado
import com.equipo3.hellocityreport.model.Incidencia
import com.equipo3.hellocityreport.model.IncidenciaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Presenter del resumen: cuenta las incidencias por estado y por categoría. */
class ResumenPresenter(
    private var view: ResumenContract.View?,
    private val repositorio: IncidenciaRepository,
    private val scope: CoroutineScope
) : ResumenContract.Presenter {

    private var suscripcion: Job? = null

    override fun cargarResumen() {
        suscripcion?.cancel()
        suscripcion = scope.launch {
            repositorio.observarTodas().collect { mostrarConteos(it) }
        }
    }

    private fun mostrarConteos(incidencias: List<Incidencia>) {
        val total = incidencias.size

        view?.mostrarTotal(total)
        view?.mostrarConteoPorEstado(
            Estado.entries.map { estado ->
                crearConteo(estado.etiqueta, incidencias.count { it.estado == estado }, total)
            }
        )
        view?.mostrarConteoPorCategoria(
            Categoria.entries.map { categoria ->
                crearConteo(categoria.etiqueta, incidencias.count { it.categoria == categoria }, total)
            }
        )
    }

    // Si no hay incidencias el porcentaje es 0 (evita dividir entre cero).
    private fun crearConteo(etiqueta: String, cantidad: Int, total: Int): Conteo {
        val porcentaje = if (total == 0) 0 else (cantidad * 100.0 / total).roundToInt()
        return Conteo(etiqueta, cantidad, porcentaje)
    }

    override fun destruir() {
        suscripcion?.cancel()
        view = null
    }
}
