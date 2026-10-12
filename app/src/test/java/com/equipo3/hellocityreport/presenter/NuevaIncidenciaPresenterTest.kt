package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.NuevaIncidenciaContract
import com.equipo3.hellocityreport.contract.NuevaIncidenciaContract.Campo
import com.equipo3.hellocityreport.model.Categoria
import com.equipo3.hellocityreport.model.Estado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NuevaIncidenciaPresenterTest {

    private class VistaFalsa : NuevaIncidenciaContract.View {
        var categorias = emptyList<String>()
        var errores = emptySet<Campo>()
        var guardada = false
        var regreso = false

        override fun mostrarCategorias(categorias: List<String>) { this.categorias = categorias }
        override fun mostrarErrores(camposConError: Set<Campo>) { errores = camposConError }
        override fun mostrarIncidenciaGuardada() { guardada = true }
        override fun regresarALista() { regreso = true }
    }

    private val vista = VistaFalsa()
    private val repositorio = FakeIncidenciaRepository()
    private val presenter = NuevaIncidenciaPresenter(vista, repositorio)

    @Test
    fun `al iniciar muestra las categorias del enum`() {
        presenter.iniciar()

        assertEquals(Categoria.entries.map { it.etiqueta }, vista.categorias)
    }

    @Test
    fun `con campos vacios marca los errores y no guarda`() {
        presenter.guardar(titulo = "  ", categoria = "", ubicacion = "", descripcion = "")

        assertEquals(Campo.entries.toSet(), vista.errores)
        assertTrue(repositorio.incidencias.isEmpty())
        assertFalse(vista.regreso)
    }

    @Test
    fun `una categoria que no existe cuenta como error`() {
        presenter.guardar("Bache", "Inventada", "Av. IPN", "Muy grande")

        assertEquals(setOf(Campo.CATEGORIA), vista.errores)
        assertTrue(repositorio.incidencias.isEmpty())
    }

    @Test
    fun `con datos completos guarda, avisa y regresa a la lista`() {
        presenter.guardar("  Bache en Té  ", "Bache", "Té 950", "Bache profundo")

        val guardada = repositorio.incidencias.single()
        assertEquals("Bache en Té", guardada.titulo)
        assertEquals(Categoria.BACHE, guardada.categoria)
        assertEquals(Estado.PENDIENTE, guardada.estado)
        assertTrue(vista.errores.isEmpty())
        assertTrue(vista.guardada)
        assertTrue(vista.regreso)
    }
}
