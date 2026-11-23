package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.CuentaContract
import com.equipo3.hellocityreport.model.CuentaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Presenter de la cuenta. El flujo de Google Sign-In se reparte así:
 * la vista consigue el token de Google (necesita una Activity) y el Presenter
 * lo entrega al Modelo, que inicia la sesión en Firebase.
 */
class CuentaPresenter(
    private var view: CuentaContract.View?,
    private val cuenta: CuentaRepository,
    private val scope: CoroutineScope
) : CuentaContract.Presenter {

    override fun iniciar() {
        mostrarEstado()
    }

    override fun alTocarIniciarSesion() {
        view?.mostrarIniciandoSesion()
        view?.pedirCuentaGoogle()
    }

    override fun alRecibirTokenGoogle(idToken: String) {
        scope.launch {
            try {
                view?.mostrarSesionIniciada(cuenta.iniciarSesionConGoogle(idToken))
            } catch (e: CancellationException) {
                throw e    // la pantalla se cerró: no es un error
            } catch (e: Exception) {
                view?.mostrarSesionCerrada()
                view?.mostrarErrorSesion()
            }
        }
    }

    override fun alCancelarCuentaGoogle() {
        mostrarEstado()
    }

    override fun alNoHaberCuentasGoogle() {
        mostrarEstado()
        view?.mostrarSinCuentasGoogle()
    }

    override fun alFallarCuentaGoogle() {
        mostrarEstado()
        view?.mostrarErrorSesion()
    }

    override fun alTocarCerrarSesion() {
        cuenta.cerrarSesion()
        view?.olvidarCuentaGoogle()
        view?.mostrarSesionCerrada()
    }

    private fun mostrarEstado() {
        val usuario = cuenta.usuarioActual
        if (usuario == null) view?.mostrarSesionCerrada() else view?.mostrarSesionIniciada(usuario)
    }

    override fun destruir() {
        view = null
    }
}
