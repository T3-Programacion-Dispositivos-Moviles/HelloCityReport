package com.equipo3.hellocityreport.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.equipo3.hellocityreport.R
import com.equipo3.hellocityreport.databinding.FragmentListaIncidenciasBinding
import com.equipo3.hellocityreport.model.IncidenciaDataSource
import com.equipo3.hellocityreport.view.adapter.IncidenciaAdapter

/**
 * Primera pantalla (startDestination del NavGraph).
 * Muestra la lista de incidencias en un RecyclerView y, al tocar una,
 * navega hacia el detalle pasándole el id de la incidencia.
 */
class ListaIncidenciasFragment : Fragment() {

    // Patrón recomendado de ViewBinding en Fragments: la referencia se libera
    // en onDestroyView para evitar fugas de memoria.
    private var _binding: FragmentListaIncidenciasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaIncidenciasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = IncidenciaAdapter(IncidenciaDataSource.obtenerTodas()) { incidencia ->
            // Empaquetamos el id para enviarlo como argumento de navegación.
            val bundle = Bundle().apply { putInt("incidenciaId", incidencia.id) }
            findNavController().navigate(R.id.action_lista_to_detalle, bundle)
        }

        binding.recyclerIncidencias.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerIncidencias.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
