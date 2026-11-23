package com.equipo3.hellocityreport.contract

import com.equipo3.hellocityreport.model.UsuarioSesion

/** Contrato MVP de la sección Cuenta (dentro de Ajustes): Google Sign-In con Firebase. */
interface CuentaContract {

    interface View {
        fun mostrarSesionIniciada(usuario: UsuarioSesion)
        fun mostrarSesionCerrada()
        fun mostrarIniciandoSesion()

        /** Abre la hoja de cuentas de Google (Credential Manager) para obtener el token. */
        fun pedirCuentaGoogle()

        /** Avisa a Credential Manager que se cerró la sesión. */
        fun olvidarCuentaGoogle()
        fun mostrarErrorSesion()
        fun mostrarSinCuentasGoogle()
    }

    interface Presenter : BasePresenter {
        fun iniciar()
        fun alTocarIniciarSesion()

        /** La vista ya obtuvo el token de la cuenta elegida. */
        fun alRecibirTokenGoogle(idToken: String)

        /** El usuario cerró la hoja de cuentas sin elegir una. */
        fun alCancelarCuentaGoogle()
        fun alNoHaberCuentasGoogle()
        fun alFallarCuentaGoogle()
        fun alTocarCerrarSesion()
    }
}
