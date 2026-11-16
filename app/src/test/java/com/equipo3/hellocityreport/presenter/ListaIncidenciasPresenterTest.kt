package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.ListaIncidenciasContract
import com.equipo3.hellocityreport.model.Categoria
import com.equipo3.hellocityreport.model.Incidencia
import kotlinx.coroutines.runBlocking
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
        ListaIncidenciasPresenter(vista, FakeIncidenciaRepository(), alInstante()).cargarIncidencias()

        assertTrue(vista.listaVacia)
        assertNull(vista.mostradas)
    }

    @Test
    fun `con incidencias las manda a la vista`() {
        val vista = VistaFalsa()
        val repositorio = FakeIncidenciaRepository(listOf(incidencia(1), incidencia(2)))
        ListaIncidenciasPresenter(vista, repositorio, alInstante()).cargarIncidencias()

        assertEquals(2, vista.mostradas?.size)
        assertFalse(vista.listaVacia)
    }

    @Test
    fun `al seleccionar una incidencia abre su detalle`() {
        val vista = VistaFalsa()
        ListaIncidenciasPresenter(vista, FakeIncidenciaRepository(), alInstante())
            .alSeleccionarIncidencia(incidencia(7))

        assertEquals(7, vista.detalleAbierto)
    }

    @Test
    fun `despues de destruir ya no llama a la vista`() {
        val vista = VistaFalsa()
        val presenter = ListaIncidenciasPresenter(vista, FakeIncidenciaRepository(), alInstante())
        presenter.destruir()
        presenter.cargarIncidencias()

        assertFalse(vista.listaVacia)
    }

    @Test
    fun `la lista se actualiza sola al agregar una incidencia`() = runBlocking {
        val vista = VistaFalsa()
        val repositorio = FakeIncidenciaRepository(listOf(incidencia(1)))
        ListaIncidenciasPresenter(vista, repositorio, alInstante()).cargarIncidencias()
        assertEquals(1, vista.mostradas?.size)

        repositorio.agregar("Nueva", "Descripción", Categoria.BASURA, "Ubicación", null, null, "foto.jpg")

        assertEquals(2, vista.mostradas?.size)
    }
}
