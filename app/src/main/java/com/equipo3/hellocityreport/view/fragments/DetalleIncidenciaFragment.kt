package com.equipo3.hellocityreport.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.equipo3.hellocityreport.R
import com.equipo3.hellocityreport.databinding.FragmentDetalleIncidenciaBinding
import com.equipo3.hellocityreport.model.IncidenciaDataSource

/**
 * Segunda pantalla del NavGraph.
 * Recibe el id de la incidencia por argumentos, la busca en la fuente de datos
 * y muestra todos sus campos.
 */
class DetalleIncidenciaFragment : Fragment() {

    private var _binding: FragmentDetalleIncidenciaBinding? = null
    private val binding get() = _binding!!

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

        // Leemos el argumento que envió la lista (-1 = valor por defecto).
        val id = arguments?.getInt("incidenciaId") ?: -1
        val incidencia = IncidenciaDataSource.buscarPorId(id) ?: return

        binding.txtDetalleTitulo.text = incidencia.titulo
        binding.txtDetalleCategoria.text =
            getString(R.string.detalle_categoria, incidencia.categoria.etiqueta)
        binding.txtDetalleEstado.text =
            getString(R.string.detalle_estado, incidencia.estado.etiqueta)
        binding.txtDetalleUbicacion.text =
            getString(R.string.detalle_ubicacion, incidencia.ubicacion)
        binding.txtDetalleFecha.text =
            getString(R.string.detalle_fecha, incidencia.fecha)
        binding.txtDetalleDescripcion.text = incidencia.descripcion
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
