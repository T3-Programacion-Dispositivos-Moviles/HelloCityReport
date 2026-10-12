package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.DetalleIncidenciaContract
import com.equipo3.hellocityreport.model.Incidencia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DetalleIncidenciaPresenterTest {

    private class VistaFalsa : DetalleIncidenciaContract.View {
        var mostrada: Incidencia? = null
        var noEncontrada = false

        override fun mostrarIncidencia(incidencia: Incidencia) { mostrada = incidencia }
        override fun mostrarNoEncontrada() { noEncontrada = true }
    }

    private val repositorio = FakeIncidenciaRepository(listOf(incidencia(1), incidencia(2)))

    @Test
    fun `si el id existe muestra la incidencia`() {
        val vista = VistaFalsa()
        DetalleIncidenciaPresenter(vista, repositorio).cargarIncidencia(2)

        assertEquals(2, vista.mostrada?.id)
        assertFalse(vista.noEncontrada)
    }

    @Test
    fun `si el id no existe muestra el mensaje de no encontrada`() {
        val vista = VistaFalsa()
        DetalleIncidenciaPresenter(vista, repositorio).cargarIncidencia(-1)

        assertTrue(vista.noEncontrada)
        assertNull(vista.mostrada)
    }
}
