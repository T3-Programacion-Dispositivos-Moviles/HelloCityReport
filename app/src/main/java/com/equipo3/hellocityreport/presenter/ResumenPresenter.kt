package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.ResumenContract
import com.equipo3.hellocityreport.contract.ResumenContract.Conteo
import com.equipo3.hellocityreport.model.Categoria
import com.equipo3.hellocityreport.model.Estado
import com.equipo3.hellocityreport.model.IncidenciaRepository
import kotlin.math.roundToInt

/** Presenter del resumen: cuenta las incidencias por estado y por categoría. */
class ResumenPresenter(
    private var view: ResumenContract.View?,
    private val repositorio: IncidenciaRepository
) : ResumenContract.Presenter {

    override fun cargarResumen() {
        val incidencias = repositorio.obtenerTodas()
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
        view = null
    }
}
