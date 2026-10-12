package com.equipo3.hellocityreport.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.equipo3.hellocityreport.contract.DetalleIncidenciaContract
import com.equipo3.hellocityreport.databinding.FragmentDetalleIncidenciaBinding
import com.equipo3.hellocityreport.model.Incidencia
import com.equipo3.hellocityreport.model.IncidenciaDataSource
import com.equipo3.hellocityreport.presenter.DetalleIncidenciaPresenter
import com.equipo3.hellocityreport.view.mostrarEstado

/**
 * Vista del detalle. Lee el id que llega por argumentos y se lo pasa al
 * Presenter, que decide si muestra la incidencia o el mensaje de "no encontrada".
 */
class DetalleIncidenciaFragment : Fragment(), DetalleIncidenciaContract.View {

    private var _binding: FragmentDetalleIncidenciaBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: DetalleIncidenciaContract.Presenter

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

        presenter = DetalleIncidenciaPresenter(this, IncidenciaDataSource)
        // Leemos el argumento que envió la lista (-1 = valor por defecto).
        presenter.cargarIncidencia(arguments?.getInt("incidenciaId") ?: -1)
    }

    override fun mostrarIncidencia(incidencia: Incidencia) {
        binding.txtDetalleTitulo.text = incidencia.titulo
        binding.chipDetalleEstado.mostrarEstado(incidencia.estado)
        binding.txtDetalleCategoria.text = incidencia.categoria.etiqueta
        binding.txtDetalleUbicacion.text = incidencia.ubicacion
        binding.txtDetalleFecha.text = incidencia.fecha
        binding.txtDetalleDescripcion.text = incidencia.descripcion
    }

    override fun mostrarNoEncontrada() {
        binding.contenidoDetalle.isVisible = false
        binding.txtNoEncontrada.isVisible = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.destruir()
        _binding = null
    }
}
