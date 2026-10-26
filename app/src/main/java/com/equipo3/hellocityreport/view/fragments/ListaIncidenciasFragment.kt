package com.equipo3.hellocityreport.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.equipo3.hellocityreport.R
import com.equipo3.hellocityreport.contract.ListaIncidenciasContract
import com.equipo3.hellocityreport.databinding.FragmentListaIncidenciasBinding
import com.equipo3.hellocityreport.model.Incidencia
import com.equipo3.hellocityreport.model.IncidenciaDataSource
import com.equipo3.hellocityreport.presenter.ListaIncidenciasPresenter
import com.equipo3.hellocityreport.view.adapter.IncidenciaAdapter
import com.google.android.material.transition.Hold

/**
 * Vista de la lista (Inicio, startDestination del NavGraph).
 *
 * En MVP la vista es "pasiva": no decide nada. Avisa al Presenter de lo que
 * hace el usuario y muestra lo que el Presenter le pide (métodos del contrato).
 */
class ListaIncidenciasFragment : Fragment(), ListaIncidenciasContract.View {

    // Patrón recomendado de ViewBinding en Fragments: la referencia se libera
    // en onDestroyView para evitar fugas de memoria.
    private var _binding: FragmentListaIncidenciasBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: ListaIncidenciasContract.Presenter

    // Tarjeta que tocó el usuario; es el punto de partida de la transición.
    private var tarjetaSeleccionada: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hold deja la lista quieta debajo mientras la tarjeta crece hacia el detalle.
        exitTransition = Hold().apply {
            duration = resources.getInteger(R.integer.duracion_transicion).toLong()
        }
    }

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

        // Al regresar del detalle hay que esperar a que la lista dibuje sus tarjetas
        // para que la animación de regreso encuentre la tarjeta de destino.
        postponeEnterTransition()
        binding.recyclerIncidencias.doOnPreDraw { startPostponedEnterTransition() }

        // La vista crea su Presenter y le entrega el Modelo (repositorio de datos).
        presenter = ListaIncidenciasPresenter(this, IncidenciaDataSource)
        presenter.cargarIncidencias()
    }

    override fun mostrarIncidencias(incidencias: List<Incidencia>) {
        binding.txtListaVacia.isVisible = false
        binding.recyclerIncidencias.isVisible = true
        binding.recyclerIncidencias.adapter = IncidenciaAdapter(incidencias) { incidencia, tarjeta ->
            tarjetaSeleccionada = tarjeta
            presenter.alSeleccionarIncidencia(incidencia)
        }
    }

    override fun mostrarListaVacia() {
        binding.recyclerIncidencias.isVisible = false
        binding.txtListaVacia.isVisible = true
    }

    override fun abrirDetalle(incidenciaId: Int) {
        // Empaquetamos el id para enviarlo como argumento de navegación.
        val argumentos = Bundle().apply { putInt("incidenciaId", incidenciaId) }
        // La tarjeta viaja como elemento compartido con su transitionName.
        val extras = tarjetaSeleccionada?.let { FragmentNavigatorExtras(it to "incidencia_$incidenciaId") }
        findNavController().navigate(R.id.action_lista_to_detalle, argumentos, null, extras)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tarjetaSeleccionada = null
        presenter.destruir()
        _binding = null
    }
}
