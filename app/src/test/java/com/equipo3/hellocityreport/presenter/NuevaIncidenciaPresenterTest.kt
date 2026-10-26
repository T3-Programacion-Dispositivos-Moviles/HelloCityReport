package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.NuevaIncidenciaContract
import com.equipo3.hellocityreport.contract.NuevaIncidenciaContract.Campo
import com.equipo3.hellocityreport.model.Categoria
import com.equipo3.hellocityreport.model.Estado
import com.equipo3.hellocityreport.model.Incidencia
import com.equipo3.hellocityreport.model.Ubicacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NuevaIncidenciaPresenterTest {

    private class VistaFalsa : NuevaIncidenciaContract.View {
        var categorias = emptyList<String>()
        var foto: String? = null
        var buscando = false
        var ubicacion: Ubicacion? = null
        var ubicacionNoDisponible = false
        var direccionSugerida: String? = null
        var errores = emptySet<Campo>()
        var guardada = false
        var notificada: Incidencia? = null
        var regreso = false

        override fun mostrarCategorias(categorias: List<String>) { this.categorias = categorias }
        override fun mostrarFoto(ruta: String) { foto = ruta }
        override fun mostrarSinFoto() { foto = null }
        override fun mostrarBuscandoUbicacion() { buscando = true }
        override fun mostrarUbicacion(ubicacion: Ubicacion) { this.ubicacion = ubicacion }
        override fun mostrarUbicacionNoDisponible() { ubicacionNoDisponible = true }
        override fun sugerirDireccion(direccion: String) { direccionSugerida = direccion }
        override fun mostrarErrores(camposConError: Set<Campo>) { errores = camposConError }
        override fun mostrarIncidenciaGuardada() { guardada = true }
        override fun notificarIncidenciaGuardada(incidencia: Incidencia) { notificada = incidencia }
        override fun regresarALista() { regreso = true }
    }

    private val upiicsa = Ubicacion(19.3955, -99.0915, 8f, "Té 950, Granjas México, Iztacalco")

    private val vista = VistaFalsa()
    private val repositorio = FakeIncidenciaRepository()
    private val gps = FakeProveedorUbicacion(upiicsa)
    private val presenter = NuevaIncidenciaPresenter(repositorio, gps).also { it.adjuntarVista(vista) }

    @Test
    fun `al iniciar muestra las categorias del enum`() {
        presenter.iniciar()

        assertEquals(Categoria.entries.map { it.etiqueta }, vista.categorias)
    }

    @Test
    fun `con campos vacios y sin foto marca todos los errores y no guarda`() {
        presenter.guardar(titulo = "  ", categoria = "", ubicacion = "", descripcion = "")

        assertEquals(Campo.entries.toSet(), vista.errores)
        assertTrue(repositorio.incidencias.isEmpty())
        assertFalse(vista.regreso)
    }

    @Test
    fun `sin foto no se guarda aunque el texto este completo`() {
        presenter.guardar("Bache", "Bache", "Av. IPN", "Muy grande")

        assertEquals(setOf(Campo.FOTO), vista.errores)
        assertTrue(repositorio.incidencias.isEmpty())
    }

    @Test
    fun `una categoria que no existe cuenta como error`() {
        presenter.alTomarFoto("/fotos/1.jpg")
        presenter.guardar("Bache", "Inventada", "Av. IPN", "Muy grande")

        assertEquals(setOf(Campo.CATEGORIA), vista.errores)
        assertTrue(repositorio.incidencias.isEmpty())
    }

    @Test
    fun `tomar y quitar la foto actualiza la vista`() {
        presenter.alTomarFoto("/fotos/1.jpg")
        assertEquals("/fotos/1.jpg", vista.foto)

        presenter.alQuitarFoto()
        assertNull(vista.foto)
        assertNull(presenter.fotoRuta)
    }

    @Test
    fun `con permiso muestra la ubicacion y sugiere la direccion`() {
        presenter.alPermitirUbicacion(precisa = true)

        assertTrue(vista.buscando)
        assertEquals(true, gps.pidioPrecisa)
        assertEquals(upiicsa, vista.ubicacion)
        assertEquals("Té 950, Granjas México, Iztacalco", vista.direccionSugerida)
    }

    @Test
    fun `sin senal avisa que la ubicacion no esta disponible`() {
        gps.respuesta = null
        presenter.alPermitirUbicacion(precisa = false)

        assertTrue(vista.ubicacionNoDisponible)
        assertNull(presenter.ubicacion)
    }

    @Test
    fun `si la vista se recrea vuelve a mostrar la foto y la ubicacion`() {
        presenter.alTomarFoto("/fotos/1.jpg")
        presenter.alPermitirUbicacion(precisa = true)
        presenter.destruir()

        val nuevaVista = VistaFalsa()
        presenter.adjuntarVista(nuevaVista)
        presenter.iniciar()

        assertEquals("/fotos/1.jpg", nuevaVista.foto)
        assertEquals(upiicsa, nuevaVista.ubicacion)
    }

    @Test
    fun `con datos completos guarda foto y coordenadas, avisa, notifica y regresa`() {
        presenter.alTomarFoto("/fotos/1.jpg")
        presenter.alPermitirUbicacion(precisa = true)
        presenter.guardar("  Bache en Té  ", "Bache", "Té 950", "Bache profundo")

        val guardada = repositorio.incidencias.single()
        assertEquals("Bache en Té", guardada.titulo)
        assertEquals(Categoria.BACHE, guardada.categoria)
        assertEquals(Estado.PENDIENTE, guardada.estado)
        assertEquals("/fotos/1.jpg", guardada.fotoRuta)
        assertEquals(19.3955, guardada.latitud!!, 0.0)
        assertTrue(vista.errores.isEmpty())
        assertTrue(vista.guardada)
        assertEquals(guardada, vista.notificada)
        assertTrue(vista.regreso)
    }

    @Test
    fun `sin gps tambien se puede guardar con la direccion escrita`() {
        presenter.alTomarFoto("/fotos/1.jpg")
        presenter.guardar("Luminaria", "Luminaria", "Frente a la papelería", "No enciende")

        val guardada = repositorio.incidencias.single()
        assertNull(guardada.latitud)
        assertEquals("Frente a la papelería", guardada.ubicacion)
    }
}
