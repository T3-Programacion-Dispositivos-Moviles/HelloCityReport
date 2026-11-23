package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.CuentaContract
import com.equipo3.hellocityreport.model.CuentaRepository
import com.equipo3.hellocityreport.model.UsuarioSesion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CuentaPresenterTest {

    /** Cuenta falsa: no usa Firebase; acepta el token "valido" y rechaza los demás. */
    private class FakeCuenta(override var usuarioActual: UsuarioSesion? = null) : CuentaRepository {
        var tokenRecibido: String? = null
        var cerroSesion = false

        override suspend fun iniciarSesionConGoogle(idToken: String): UsuarioSesion {
            tokenRecibido = idToken
            if (idToken != "valido") throw IllegalStateException("Token rechazado")
            return LUIS.also { usuarioActual = it }
        }

        override fun cerrarSesion() {
            cerroSesion = true
            usuarioActual = null
        }
    }

    private class VistaFalsa : CuentaContract.View {
        var usuario: UsuarioSesion? = null
        var sesionCerrada = false
        var iniciando = false
        var pidioCuenta = false
        var olvidoCuenta = false
        var error = false
        var sinCuentas = false

        override fun mostrarSesionIniciada(usuario: UsuarioSesion) { this.usuario = usuario; sesionCerrada = false }
        override fun mostrarSesionCerrada() { usuario = null; sesionCerrada = true }
        override fun mostrarIniciandoSesion() { iniciando = true }
        override fun pedirCuentaGoogle() { pidioCuenta = true }
        override fun olvidarCuentaGoogle() { olvidoCuenta = true }
        override fun mostrarErrorSesion() { error = true }
        override fun mostrarSinCuentasGoogle() { sinCuentas = true }
    }

    private val vista = VistaFalsa()

    @Test
    fun `sin sesion muestra el boton de iniciar sesion`() {
        CuentaPresenter(vista, FakeCuenta(), alInstante()).iniciar()
        assertTrue(vista.sesionCerrada)
    }

    @Test
    fun `con sesion guardada muestra al usuario`() {
        CuentaPresenter(vista, FakeCuenta(LUIS), alInstante()).iniciar()
        assertEquals(LUIS, vista.usuario)
    }

    @Test
    fun `tocar iniciar sesion pide la cuenta de Google`() {
        CuentaPresenter(vista, FakeCuenta(), alInstante()).alTocarIniciarSesion()
        assertTrue(vista.iniciando)
        assertTrue(vista.pidioCuenta)
    }

    @Test
    fun `con un token valido inicia sesion en el modelo`() {
        val cuenta = FakeCuenta()
        CuentaPresenter(vista, cuenta, alInstante()).alRecibirTokenGoogle("valido")

        assertEquals("valido", cuenta.tokenRecibido)
        assertEquals(LUIS, vista.usuario)
        assertFalse(vista.error)
    }

    @Test
    fun `si Firebase rechaza el token avisa el error`() {
        CuentaPresenter(vista, FakeCuenta(), alInstante()).alRecibirTokenGoogle("otro")

        assertTrue(vista.error)
        assertTrue(vista.sesionCerrada)
    }

    @Test
    fun `si no hay cuentas en el telefono lo avisa`() {
        CuentaPresenter(vista, FakeCuenta(), alInstante()).alNoHaberCuentasGoogle()
        assertTrue(vista.sinCuentas)
        assertTrue(vista.sesionCerrada)
    }

    @Test
    fun `cerrar sesion limpia Firebase y Credential Manager`() {
        val cuenta = FakeCuenta(LUIS)
        CuentaPresenter(vista, cuenta, alInstante()).alTocarCerrarSesion()

        assertTrue(cuenta.cerroSesion)
        assertTrue(vista.olvidoCuenta)
        assertNull(vista.usuario)
    }

    private companion object {
        val LUIS = UsuarioSesion("uid123", "Luis González", "luis@ejemplo.com")
    }
}
