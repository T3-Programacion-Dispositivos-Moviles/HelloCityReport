package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.ListaIncidenciasContract
import com.equipo3.hellocityreport.model.Incidencia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ListaIncidenciasPresenterTest {

    // Vista falsa: en lugar de dibujar, anota lo que el Presenter le pidió.
    private class VistaFalsa : ListaIncidenciasContract.View {
        var mostradas: List<Incidencia>? = null
        var listaVacia = false
        var detalleAbierto: Int? = null

        override fun mostrarIncidencias(incidencias: List<Incidencia>) { mostradas = incidencias }
        override fun mostrarListaVacia() { listaVacia = true }
        override fun abrirDetalle(incidenciaId: Int) { detalleAbierto = incidenciaId }
    }

    @Test
    fun `sin incidencias muestra el mensaje de lista vacia`() {
        val vista = VistaFalsa()
        ListaIncidenciasPresenter(vista, FakeIncidenciaRepository()).cargarIncidencias()

        assertTrue(vista.listaVacia)
        assertNull(vista.mostradas)
    }

    @Test
    fun `con incidencias las manda a la vista`() {
        val vista = VistaFalsa()
        val repositorio = FakeIncidenciaRepository(listOf(incidencia(1), incidencia(2)))
        ListaIncidenciasPresenter(vista, repositorio).cargarIncidencias()

        assertEquals(2, vista.mostradas?.size)
        assertFalse(vista.listaVacia)
    }

    @Test
    fun `al seleccionar una incidencia abre su detalle`() {
        val vista = VistaFalsa()
        ListaIncidenciasPresenter(vista, FakeIncidenciaRepository())
            .alSeleccionarIncidencia(incidencia(7))

        assertEquals(7, vista.detalleAbierto)
    }

    @Test
    fun `despues de destruir ya no llama a la vista`() {
        val vista = VistaFalsa()
        val presenter = ListaIncidenciasPresenter(vista, FakeIncidenciaRepository())
        presenter.destruir()
        presenter.cargarIncidencias()

        assertFalse(vista.listaVacia)
    }
}
