package com.equipo3.hellocityreport.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.equipo3.hellocityreport.R
import com.equipo3.hellocityreport.databinding.FragmentNuevaIncidenciaBinding
import com.equipo3.hellocityreport.model.Categoria
import com.equipo3.hellocityreport.model.IncidenciaDataSource

/**
 * Tercera pantalla del NavGraph: formulario para registrar una nueva incidencia.
 * Valida los campos, guarda la incidencia en IncidenciaDataSource y regresa a Home.
 */
class NuevaIncidenciaFragment : Fragment() {

    private var _binding: FragmentNuevaIncidenciaBinding? = null
    private val binding get() = _binding!!

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

        // Llena el menu desplegable con las etiquetas del enum Categoria.
        val etiquetas = Categoria.entries.map { it.etiqueta }
        binding.actvCategoria.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, etiquetas)
        )
        binding.actvCategoria.setOnItemClickListener { _, _, _, _ ->
            binding.tilCategoria.error = null
        }

        // Quita el mensaje de error en cuanto el usuario escribe en el campo.
        binding.etTitulo.doAfterTextChanged { binding.tilTitulo.error = null }
        binding.etUbicacion.doAfterTextChanged { binding.tilUbicacion.error = null }
        binding.etDescripcion.doAfterTextChanged { binding.tilDescripcion.error = null }

        binding.btnGuardar.setOnClickListener { guardar() }
    }

    private fun guardar() {
        val titulo = binding.etTitulo.text.toString().trim()
        val ubicacion = binding.etUbicacion.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        // Convierte el texto elegido en el menu al valor del enum (null si no se eligio).
        val categoria = Categoria.entries.find {
            it.etiqueta == binding.actvCategoria.text.toString()
        }

        // Marca los campos vacios: si falta alguno no se guarda.
        val obligatorio = getString(R.string.error_campo_obligatorio)
        binding.tilTitulo.error = if (titulo.isEmpty()) obligatorio else null
        binding.tilUbicacion.error = if (ubicacion.isEmpty()) obligatorio else null
        binding.tilDescripcion.error = if (descripcion.isEmpty()) obligatorio else null
        binding.tilCategoria.error =
            if (categoria == null) getString(R.string.error_categoria) else null

        if (titulo.isEmpty() || ubicacion.isEmpty() || descripcion.isEmpty() || categoria == null) {
            return
        }

        IncidenciaDataSource.agregar(titulo, descripcion, categoria, ubicacion)
        Toast.makeText(requireContext(), R.string.mensaje_incidencia_guardada, Toast.LENGTH_SHORT)
            .show()

        // Regresa a Home: la lista se vuelve a construir y ya incluye la nueva incidencia.
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
