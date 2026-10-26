package com.equipo3.hellocityreport.view.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.transition.Fade
import com.equipo3.hellocityreport.R
import com.equipo3.hellocityreport.databinding.FragmentFotoBinding
import com.equipo3.hellocityreport.view.mostrarFoto
import com.google.android.material.transition.MaterialContainerTransform

/**
 * Muestra la foto de la incidencia en pantalla completa.
 *
 * La foto del detalle y esta comparten el mismo transitionName: al abrir, la
 * imagen crece desde su lugar en el detalle hasta ocupar la pantalla, y al
 * cerrar regresa a su lugar (container transform de Material).
 */
class FotoFragment : Fragment() {

    private var _binding: FragmentFotoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val duracion = resources.getInteger(R.integer.duracion_transicion).toLong()
        // La animación se dibuja en una capa encima de todo (drawingViewId), así
        // se ve igual al abrir y al cerrar; FADE_MODE_CROSS funde la foto recortada
        // del detalle con la foto completa mientras cambia de tamaño.
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = duracion
            scrimColor = Color.TRANSPARENT
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
            setAllContainerColors(Color.BLACK)
        }
        // El fondo negro y el botón aparecen y desaparecen suavemente detrás de la foto.
        enterTransition = Fade().setDuration(duracion)
            .addTarget(R.id.vFondoFoto).addTarget(R.id.btnCerrarFoto)
        returnTransition = Fade().setDuration(duracion)
            .addTarget(R.id.vFondoFoto).addTarget(R.id.btnCerrarFoto)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setTransitionName(binding.ivFotoCompleta, arguments?.getString("nombreTransicion"))
        arguments?.getString("fotoRuta")?.let { binding.ivFotoCompleta.mostrarFoto(it, ladoMaximo = 2048) }

        binding.btnCerrarFoto.setOnClickListener { findNavController().popBackStack() }
        binding.ivFotoCompleta.setOnClickListener { findNavController().popBackStack() }

        // Pantalla completa: el botón se aparta de la barra de estado.
        view.doOnAttach {
            val barras = ViewCompat.getRootWindowInsets(it)
                ?.getInsets(WindowInsetsCompat.Type.systemBars()) ?: return@doOnAttach
            binding.btnCerrarFoto.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin += barras.top
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
