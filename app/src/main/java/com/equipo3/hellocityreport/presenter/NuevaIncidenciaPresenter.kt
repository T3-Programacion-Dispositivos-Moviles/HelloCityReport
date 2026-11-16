package com.equipo3.hellocityreport.presenter

import com.equipo3.hellocityreport.contract.NuevaIncidenciaContract
import com.equipo3.hellocityreport.contract.NuevaIncidenciaContract.Campo
import com.equipo3.hellocityreport.model.Categoria
import com.equipo3.hellocityreport.model.IncidenciaRepository
import com.equipo3.hellocityreport.model.ProveedorUbicacion
import com.equipo3.hellocityreport.model.Ubicacion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Presenter del formulario. [scope] vive lo mismo que el Fragment, no que su vista,
 * para que el guardado no se cancele si la vista se recrea.
 */
class NuevaIncidenciaPresenter(
    private val repositorio: IncidenciaRepository,
    private val proveedorUbicacion: ProveedorUbicacion,
    private val scope: CoroutineScope
) : NuevaIncidenciaContract.Presenter {

    private var view: NuevaIncidenciaContract.View? = null
    private var guardando = false

    override var fotoRuta: String? = null
        private set
    override var ubicacion: Ubicacion? = null
        private set

    override fun adjuntarVista(vista: NuevaIncidenciaContract.View) {
        view = vista
    }

    override fun restaurar(fotoRuta: String?, ubicacion: Ubicacion?) {
        this.fotoRuta = fotoRuta
        this.ubicacion = ubicacion
    }

    override fun iniciar() {
        view?.mostrarCategorias(Categoria.entries.map { it.etiqueta })
        fotoRuta?.let { view?.mostrarFoto(it) }
        ubicacion?.let { view?.mostrarUbicacion(it) }
    }

    override fun alTomarFoto(ruta: String) {
        fotoRuta = ruta
        view?.mostrarFoto(ruta)
    }

    override fun alQuitarFoto() {
        fotoRuta = null
        view?.mostrarSinFoto()
    }

    override fun alPermitirUbicacion(precisa: Boolean) {
        view?.mostrarBuscandoUbicacion()
        // La respuesta llega después; si mientras tanto la vista se recreó,
        // se usa la vista que esté conectada en ese momento.
        proveedorUbicacion.obtenerUbicacion(precisa) { resultado ->
            if (resultado == null) {
                view?.mostrarUbicacionNoDisponible()
            } else {
                ubicacion = resultado
                view?.mostrarUbicacion(resultado)
                resultado.direccion?.let { view?.sugerirDireccion(it) }
            }
        }
    }

    override fun alNegarUbicacion() {
        view?.mostrarUbicacionNoDisponible()
    }

    override fun guardar(titulo: String, categoria: String, ubicacion: String, descripcion: String) {
        // Convierte el texto elegido en el menú al valor del enum (null si no se eligió).
        val categoriaElegida = Categoria.entries.find { it.etiqueta == categoria }
        val foto = fotoRuta

        val camposConError = buildSet {
            if (foto == null) add(Campo.FOTO)
            if (titulo.isBlank()) add(Campo.TITULO)
            if (categoriaElegida == null) add(Campo.CATEGORIA)
            if (ubicacion.isBlank()) add(Campo.UBICACION)
            if (descripcion.isBlank()) add(Campo.DESCRIPCION)
        }
        view?.mostrarErrores(camposConError)

        // Si falta algún dato no se guarda; tampoco si ya se está guardando (doble toque).
        if (foto == null || categoriaElegida == null || camposConError.isNotEmpty() || guardando) return

        val gps = this.ubicacion
        guardando = true
        // La escritura en la base corre fuera del hilo principal.
        scope.launch {
            try {
                val nueva = repositorio.agregar(
                    titulo = titulo.trim(),
                    descripcion = descripcion.trim(),
                    categoria = categoriaElegida,
                    ubicacion = ubicacion.trim(),
                    latitud = gps?.latitud,
                    longitud = gps?.longitud,
                    fotoRuta = foto
                )
                fotoRuta = null
                this@NuevaIncidenciaPresenter.ubicacion = null
                view?.mostrarIncidenciaGuardada()
                view?.notificarIncidenciaGuardada(nueva)
                view?.regresarALista()
            } finally {
                guardando = false
            }
        }
    }

    override fun destruir() {
        view = null
    }
}
