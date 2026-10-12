package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.ResumenContract
import com.equipo3.hellocityreport.contract.ResumenContract.Conteo
import com.equipo3.hellocityreport.model.Categoria
import com.equipo3.hellocityreport.model.Estado
import org.junit.Assert.assertEquals
import org.junit.Test

class ResumenPresenterTest {

    private class VistaFalsa : ResumenContract.View {
        var total = -1
        var porEstado = emptyList<Conteo>()
        var porCategoria = emptyList<Conteo>()

        override fun mostrarTotal(total: Int) { this.total = total }
        override fun mostrarConteoPorEstado(conteos: List<Conteo>) { porEstado = conteos }
        override fun mostrarConteoPorCategoria(conteos: List<Conteo>) { porCategoria = conteos }
    }

    @Test
    fun `cuenta por estado y por categoria con su porcentaje`() {
        val repositorio = FakeIncidenciaRepository(
            listOf(
                incidencia(1, categoria = Categoria.BACHE, estado = Estado.PENDIENTE),
                incidencia(2, categoria = Categoria.BACHE, estado = Estado.PENDIENTE),
                incidencia(3, categoria = Categoria.LUMINARIA, estado = Estado.RESUELTO),
                incidencia(4, categoria = Categoria.BASURA, estado = Estado.EN_PROCESO)
            )
        )
        val vista = VistaFalsa()
        ResumenPresenter(vista, repositorio).cargarResumen()

        assertEquals(4, vista.total)
        assertEquals(
            listOf(
                Conteo("Pendiente", 2, 50),
                Conteo("En proceso", 1, 25),
                Conteo("Resuelto", 1, 25)
            ),
            vista.porEstado
        )
        assertEquals(Conteo("Bache", 2, 50), vista.porCategoria.first())
        assertEquals(Categoria.entries.size, vista.porCategoria.size)
    }

    @Test
    fun `sin incidencias todo queda en cero`() {
        val vista = VistaFalsa()
        ResumenPresenter(vista, FakeIncidenciaRepository()).cargarResumen()

        assertEquals(0, vista.total)
        assertEquals(listOf(0, 0, 0), vista.porEstado.map { it.porcentaje })
    }
}
