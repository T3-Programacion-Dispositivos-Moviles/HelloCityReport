package com.equipo3.hellocityreport.view.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.equipo3.hellocityreport.R
import com.equipo3.hellocityreport.contract.DetalleIncidenciaContract
import com.equipo3.hellocityreport.databinding.FragmentDetalleIncidenciaBinding
import com.equipo3.hellocityreport.model.Incidencia
import com.equipo3.hellocityreport.model.IncidenciaDataSource
import com.equipo3.hellocityreport.presenter.DetalleIncidenciaPresenter
import com.equipo3.hellocityreport.view.MapaEstatico
import com.equipo3.hellocityreport.view.mostrarEstado
import com.equipo3.hellocityreport.view.mostrarFoto
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform

/**
 * Vista del detalle. Lee el id que llega por argumentos y se lo pasa al
 * Presenter, que decide si muestra la incidencia o el mensaje de "no encontrada".
 *
 * Transiciones de elementos compartidos:
 *  - Lista -> Detalle: la tarjeta "crece" hasta convertirse en esta pantalla
 *    (container transform de Material). Ambas vistas comparten el mismo transitionName.
 *  - Detalle -> Foto: la foto de portada se amplía hasta la pantalla completa.
 */
class DetalleIncidenciaFragment : Fragment(), DetalleIncidenciaContract.View {

    private var _binding: FragmentDetalleIncidenciaBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: DetalleIncidenciaContract.Presenter
    private var mapa: MapaEstatico? = null

    private val incidenciaId get() = arguments?.getInt("incidenciaId") ?: -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Animación de entrada (y de regreso) desde la tarjeta de la lista.
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.duracion_transicion).toLong()
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleIncidenciaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Mismo nombre que la tarjeta de la lista: así el sistema sabe qué vistas animar.
        ViewCompat.setTransitionName(view, "incidencia_$incidenciaId")
        (sharedElementEnterTransition as? MaterialContainerTransform)
            ?.setAllContainerColors(MaterialColors.getColor(view, com.google.android.material.R.attr.colorSurface))

        mapa = MapaEstatico(binding.mapaDetalle)
        presenter = DetalleIncidenciaPresenter(this, IncidenciaDataSource)
        presenter.cargarIncidencia(incidenciaId)
    }

    override fun mostrarIncidencia(incidencia: Incidencia) {
        binding.txtDetalleTitulo.text = incidencia.titulo
        binding.chipDetalleEstado.mostrarEstado(incidencia.estado)
        binding.txtDetalleCategoria.text = incidencia.categoria.etiqueta
        binding.txtDetalleUbicacion.text = incidencia.ubicacion
        binding.txtDetalleFecha.text = incidencia.fecha
        binding.txtDetalleDescripcion.text = incidencia.descripcion

        val ruta = incidencia.fotoRuta
        binding.ivFotoDetalle.isVisible = ruta != null
        if (ruta != null) {
            val nombreTransicion = "foto_${incidencia.id}"
            ViewCompat.setTransitionName(binding.ivFotoDetalle, nombreTransicion)
            binding.ivFotoDetalle.mostrarFoto(ruta)
            binding.ivFotoDetalle.setOnClickListener { abrirFoto(ruta, nombreTransicion) }
        }

        val latitud = incidencia.latitud
        val longitud = incidencia.longitud
        val hayCoordenadas = latitud != null && longitud != null
        binding.contenedorMapaDetalle.isVisible = hayCoordenadas
        binding.txtDetalleCoordenadas.isVisible = hayCoordenadas
        if (latitud != null && longitud != null) {
            binding.txtDetalleCoordenadas.text = getString(R.string.detalle_coordenadas, latitud, longitud)
            mapa?.mostrar(latitud, longitud)
            binding.contenedorMapaDetalle.setOnClickListener { abrirEnMapas(latitud, longitud, incidencia.titulo) }
        }
    }

    override fun mostrarNoEncontrada() {
        binding.contenidoDetalle.isVisible = false
        binding.txtNoEncontrada.isVisible = true
    }

    // La foto viaja a la pantalla completa como elemento compartido.
    private fun abrirFoto(ruta: String, nombreTransicion: String) {
        val argumentos = Bundle().apply {
            putString("fotoRuta", ruta)
            putString("nombreTransicion", nombreTransicion)
        }
        findNavController().navigate(
            R.id.action_detalle_to_foto,
            argumentos,
            null,
            FragmentNavigatorExtras(binding.ivFotoDetalle to nombreTransicion)
        )
    }

    // geo: es un Intent estándar que entiende cualquier app de mapas instalada.
    private fun abrirEnMapas(latitud: Double, longitud: Double, titulo: String) {
        val uri = "geo:$latitud,$longitud?q=$latitud,$longitud(${Uri.encode(titulo)})".toUri()
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            Snackbar.make(binding.root, R.string.detalle_sin_app_mapas, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapa?.cancelar()
        mapa = null
        presenter.destruir()
        _binding = null
    }
}
