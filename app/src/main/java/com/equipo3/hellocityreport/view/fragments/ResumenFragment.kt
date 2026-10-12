package com.equipo3.hellocityreport.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.equipo3.hellocityreport.R
import com.equipo3.hellocityreport.contract.ResumenContract
import com.equipo3.hellocityreport.contract.ResumenContract.Conteo
import com.equipo3.hellocityreport.databinding.FragmentResumenBinding
import com.equipo3.hellocityreport.databinding.ItemConteoBinding
import com.equipo3.hellocityreport.model.IncidenciaDataSource
import com.equipo3.hellocityreport.presenter.ResumenPresenter
import java.text.NumberFormat

/**
 * Vista del resumen (sección Resumen de la barra inferior).
 * Los cálculos los hace ResumenPresenter; aquí solo se dibujan los resultados.
 */
class ResumenFragment : Fragment(), ResumenContract.View {

    private var _binding: FragmentResumenBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: ResumenContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResumenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = ResumenPresenter(this, IncidenciaDataSource)
        presenter.cargarResumen()
    }

    override fun mostrarTotal(total: Int) {
        binding.txtTotal.text = NumberFormat.getIntegerInstance().format(total)
    }

    override fun mostrarConteoPorEstado(conteos: List<Conteo>) {
        agregarFilas(binding.contenedorEstados, conteos)
    }

    override fun mostrarConteoPorCategoria(conteos: List<Conteo>) {
        agregarFilas(binding.contenedorCategorias, conteos)
    }

    // Agrega una fila (item_conteo.xml) por cada conteo, debajo del título de la card.
    private fun agregarFilas(contenedor: LinearLayout, conteos: List<Conteo>) {
        conteos.forEach { conteo ->
            val fila = ItemConteoBinding.inflate(layoutInflater, contenedor, true)
            fila.txtEtiqueta.text = conteo.etiqueta
            fila.txtCantidad.text =
                getString(R.string.resumen_conteo, conteo.cantidad, conteo.porcentaje)
            fila.barraPorcentaje.progress = conteo.porcentaje
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.destruir()
        _binding = null
    }
}
