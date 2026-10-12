package com.equipo3.hellocityreport.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.equipo3.hellocityreport.R
import com.equipo3.hellocityreport.contract.NuevaIncidenciaContract
import com.equipo3.hellocityreport.contract.NuevaIncidenciaContract.Campo
import com.equipo3.hellocityreport.databinding.FragmentNuevaIncidenciaBinding
import com.equipo3.hellocityreport.model.IncidenciaDataSource
import com.equipo3.hellocityreport.presenter.NuevaIncidenciaPresenter
import com.google.android.material.snackbar.Snackbar

/**
 * Vista del formulario (sección Reportar de la barra inferior).
 * Entrega al Presenter lo que escribió el usuario; el Presenter valida, guarda
 * y le indica a esta vista qué errores marcar o si debe regresar a la lista.
 */
class NuevaIncidenciaFragment : Fragment(), NuevaIncidenciaContract.View {

    private var _binding: FragmentNuevaIncidenciaBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: NuevaIncidenciaContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNuevaIncidenciaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = NuevaIncidenciaPresenter(this, IncidenciaDataSource)

        // Quitar el error en cuanto el usuario corrige el campo es solo un detalle
        // visual, por eso se queda en la vista.
        binding.actvCategoria.setOnItemClickListener { _, _, _, _ ->
            binding.tilCategoria.error = null
        }
        binding.etTitulo.doAfterTextChanged { binding.tilTitulo.error = null }
        binding.etUbicacion.doAfterTextChanged { binding.tilUbicacion.error = null }
        binding.etDescripcion.doAfterTextChanged { binding.tilDescripcion.error = null }

        binding.btnGuardar.setOnClickListener {
            presenter.guardar(
                titulo = binding.etTitulo.text.toString(),
                categoria = binding.actvCategoria.text.toString(),
                ubicacion = binding.etUbicacion.text.toString(),
                descripcion = binding.etDescripcion.text.toString()
            )
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // Al volver a esta sección Android restaura lo que ya se había escrito.
        // Si el menú ya tuviera sus opciones, al restaurar la categoría elegida
        // se filtraría y solo mostraría esa; por eso se cargan hasta este punto.
        presenter.iniciar()
    }

    override fun mostrarCategorias(categorias: List<String>) {
        binding.actvCategoria.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categorias)
        )
    }

    override fun mostrarErrores(camposConError: Set<Campo>) {
        val obligatorio = getString(R.string.error_campo_obligatorio)
        binding.tilTitulo.error = if (Campo.TITULO in camposConError) obligatorio else null
        binding.tilUbicacion.error = if (Campo.UBICACION in camposConError) obligatorio else null
        binding.tilDescripcion.error = if (Campo.DESCRIPCION in camposConError) obligatorio else null
        binding.tilCategoria.error =
            if (Campo.CATEGORIA in camposConError) getString(R.string.error_categoria) else null
    }

    override fun mostrarIncidenciaGuardada() {
        // Snackbar es el aviso breve que recomienda Material 3; se queda visible
        // sobre la barra inferior aunque cambiemos de pantalla.
        Snackbar.make(binding.root, R.string.mensaje_incidencia_guardada, Snackbar.LENGTH_SHORT)
            .show()
    }

    override fun regresarALista() {
        // Cierra el teclado y vuelve a Inicio; la lista se recarga con la nueva incidencia.
        WindowCompat.getInsetsController(requireActivity().window, binding.root)
            .hide(WindowInsetsCompat.Type.ime())
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.destruir()
        _binding = null
    }
}
