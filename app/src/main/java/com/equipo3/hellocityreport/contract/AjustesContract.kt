package com.equipo3.hellocityreport.contract

import com.equipo3.hellocityreport.model.Tema

/** Contrato MVP de la pantalla de ajustes (preferencias guardadas con DataStore). */
interface AjustesContract {

    interface View {
        fun mostrarTema(tema: Tema)
        fun mostrarAvisos(activos: Boolean)

        /** Cambia el modo claro u oscuro de la app en ese momento. */
        fun aplicarTema(tema: Tema)

        /** Pide al sistema el permiso de notificaciones (Android 13 o superior). */
        fun pedirPermisoAvisos()
        fun mostrarPermisoAvisosNegado()
    }

    interface Presenter : BasePresenter {
        /** Muestra los valores guardados y se mantiene al tanto de sus cambios. */
        fun iniciar()
        fun alElegirTema(tema: Tema)

        /** [tienePermiso] lo revisa la vista, porque depende de Android. */
        fun alCambiarAvisos(activar: Boolean, tienePermiso: Boolean)
        fun alResponderPermisoAvisos(concedido: Boolean)
    }
}
