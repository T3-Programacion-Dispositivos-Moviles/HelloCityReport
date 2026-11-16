package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.AjustesContract
import com.equipo3.hellocityreport.model.PreferenciasRepository
import com.equipo3.hellocityreport.model.Tema
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AjustesPresenterTest {

    /** Preferencias falsas: en lugar de DataStore guardan los valores en memoria. */
    private class FakePreferencias : PreferenciasRepository {
        override val tema = MutableStateFlow(Tema.SISTEMA)
        override val avisosActivos = MutableStateFlow(true)
        override suspend fun guardarTema(tema: Tema) { this.tema.value = tema }
        override suspend fun guardarAvisos(activos: Boolean) { avisosActivos.value = activos }
    }

    private class VistaFalsa : AjustesContract.View {
        var tema: Tema? = null
        var avisos: Boolean? = null
        var temaAplicado: Tema? = null
        var pidioPermiso = false
        var avisoPermisoNegado = false

        override fun mostrarTema(tema: Tema) { this.tema = tema }
        override fun mostrarAvisos(activos: Boolean) { avisos = activos }
        override fun aplicarTema(tema: Tema) { temaAplicado = tema }
        override fun pedirPermisoAvisos() { pidioPermiso = true }
        override fun mostrarPermisoAvisosNegado() { avisoPermisoNegado = true }
    }

    private val preferencias = FakePreferencias()
    private val vista = VistaFalsa()
    private val presenter = AjustesPresenter(vista, preferencias, alInstante())

    @Test
    fun `al iniciar muestra los valores guardados`() {
        preferencias.tema.value = Tema.OSCURO
        preferencias.avisosActivos.value = false
        presenter.iniciar()

        assertEquals(Tema.OSCURO, vista.tema)
        assertEquals(false, vista.avisos)
    }

    @Test
    fun `elegir un tema lo guarda y lo aplica`() {
        presenter.iniciar()
        presenter.alElegirTema(Tema.CLARO)

        assertEquals(Tema.CLARO, preferencias.tema.value)
        assertEquals(Tema.CLARO, vista.temaAplicado)
    }

    @Test
    fun `desactivar avisos lo guarda sin pedir permiso`() {
        presenter.alCambiarAvisos(activar = false, tienePermiso = false)

        assertFalse(preferencias.avisosActivos.value)
        assertFalse(vista.pidioPermiso)
    }

    @Test
    fun `activar avisos sin permiso lo pide antes de guardar`() {
        preferencias.avisosActivos.value = false
        presenter.alCambiarAvisos(activar = true, tienePermiso = false)

        assertTrue(vista.pidioPermiso)
        assertFalse(preferencias.avisosActivos.value)
    }

    @Test
    fun `si conceden el permiso los avisos quedan activos`() {
        preferencias.avisosActivos.value = false
        presenter.alResponderPermisoAvisos(concedido = true)

        assertTrue(preferencias.avisosActivos.value)
        assertFalse(vista.avisoPermisoNegado)
    }

    @Test
    fun `si niegan el permiso los avisos quedan apagados y se avisa`() {
        presenter.alResponderPermisoAvisos(concedido = false)

        assertFalse(preferencias.avisosActivos.value)
        assertEquals(false, vista.avisos)
        assertTrue(vista.avisoPermisoNegado)
    }

    @Test
    fun `despues de destruir ya no llama a la vista`() {
        presenter.destruir()
        presenter.iniciar()

        assertNull(vista.tema)
    }
}
