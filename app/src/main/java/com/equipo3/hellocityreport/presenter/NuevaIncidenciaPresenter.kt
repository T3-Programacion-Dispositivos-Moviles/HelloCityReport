package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.NuevaIncidenciaContract
import com.equipo3.hellocityreport.contract.NuevaIncidenciaContract.Campo
import com.equipo3.hellocityreport.model.Categoria
import com.equipo3.hellocityreport.model.IncidenciaRepository

/**
 * Presenter del formulario. Aquí vive la regla de negocio "todos los campos son
 * obligatorios"; el Fragment solo muestra lo que este Presenter le indica.
 */
class NuevaIncidenciaPresenter(
    private var view: NuevaIncidenciaContract.View?,
    private val repositorio: IncidenciaRepository
) : NuevaIncidenciaContract.Presenter {

    override fun iniciar() {
        view?.mostrarCategorias(Categoria.entries.map { it.etiqueta })
    }

    override fun guardar(titulo: String, categoria: String, ubicacion: String, descripcion: String) {
        // Convierte el texto elegido en el menú al valor del enum (null si no se eligió).
        val categoriaElegida = Categoria.entries.find { it.etiqueta == categoria }

        val camposConError = buildSet {
            if (titulo.isBlank()) add(Campo.TITULO)
            if (categoriaElegida == null) add(Campo.CATEGORIA)
            if (ubicacion.isBlank()) add(Campo.UBICACION)
            if (descripcion.isBlank()) add(Campo.DESCRIPCION)
        }
        view?.mostrarErrores(camposConError)

        // Si falta algún dato no se guarda.
        if (categoriaElegida == null || camposConError.isNotEmpty()) return

        repositorio.agregar(titulo.trim(), descripcion.trim(), categoriaElegida, ubicacion.trim())
        view?.mostrarIncidenciaGuardada()
        view?.regresarALista()
    }

    override fun destruir() {
        view = null
    }
}
