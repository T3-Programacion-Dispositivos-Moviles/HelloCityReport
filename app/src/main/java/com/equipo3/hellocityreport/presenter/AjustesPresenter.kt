package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.AjustesContract
import com.equipo3.hellocityreport.model.PreferenciasRepository
import com.equipo3.hellocityreport.model.Tema
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Presenter de ajustes: lee y guarda el tema y los avisos en las preferencias. */
class AjustesPresenter(
    private var view: AjustesContract.View?,
    private val preferencias: PreferenciasRepository,
    private val scope: CoroutineScope
) : AjustesContract.Presenter {

    override fun iniciar() {
        scope.launch { preferencias.tema.collect { view?.mostrarTema(it) } }
        scope.launch { preferencias.avisosActivos.collect { view?.mostrarAvisos(it) } }
    }

    override fun alElegirTema(tema: Tema) {
        scope.launch {
            preferencias.guardarTema(tema)
            view?.aplicarTema(tema)
        }
    }

    override fun alCambiarAvisos(activar: Boolean, tienePermiso: Boolean) {
        // Para activarlos hace falta el permiso; se guarda hasta que el usuario responde.
        if (activar && !tienePermiso) {
            view?.pedirPermisoAvisos()
        } else {
            scope.launch { preferencias.guardarAvisos(activar) }
        }
    }

    override fun alResponderPermisoAvisos(concedido: Boolean) {
        if (concedido) {
            scope.launch { preferencias.guardarAvisos(true) }
        } else {
            scope.launch { preferencias.guardarAvisos(false) }
            view?.mostrarAvisos(false)
            view?.mostrarPermisoAvisosNegado()
        }
    }

    override fun destruir() {
        view = null
    }
}
