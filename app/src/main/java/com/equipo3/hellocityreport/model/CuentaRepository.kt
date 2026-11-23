package com.equipo3.hellocityreport.model

/** Usuario con sesión iniciada (datos que entrega la cuenta de Google). */
data class UsuarioSesion(
    val uid: String,
    val nombre: String,
    val correo: String
)

/**
 * Contrato del Modelo para la cuenta del usuario. El Presenter no sabe que por
 * dentro se usa Firebase Authentication.
 */
interface CuentaRepository {

    /** null si no hay sesión iniciada. Firebase la recuerda aunque se cierre la app. */
    val usuarioActual: UsuarioSesion?

    /** Cambia el token de Google por una sesión de Firebase. Lanza excepción si falla. */
    suspend fun iniciarSesionConGoogle(idToken: String): UsuarioSesion

    fun cerrarSesion()
}
