package com.equipo3.hellocityreport.contract

/**
 * Lo que todo Presenter debe hacer, sin importar la pantalla.
 *
 * La vista (Fragment) se destruye cuando el usuario cambia de pantalla; si el
 * Presenter siguiera guardando la referencia, habría una fuga de memoria. Por eso
 * el Fragment llama a destruir() en onDestroyView().
 */
interface BasePresenter {
    fun destruir()
}
