package com.equipo3.hellocityreport.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.equipo3.hellocityreport.R
import com.equipo3.hellocityreport.databinding.FragmentCamaraBinding
import java.io.File

/**
 * Pantalla de cámara con CameraX (wireframe S8, pantalla B).
 *
 * Es solo Vista: no tiene reglas de negocio, solo maneja la cámara. Al tomar
 * la foto la guarda en un archivo privado de la app y le regresa la ruta al
 * formulario a través del back stack de navegación.
 */
class CamaraFragment : Fragment() {

    private var _binding: FragmentCamaraBinding? = null
    private val binding get() = _binding!!

    // LifecycleCameraController es la forma sencilla de CameraX: une la vista
    // previa y la captura, se enciende y apaga solo con el ciclo de vida, y ya
    // trae "tocar para enfocar" y "pellizcar para acercar".
    private lateinit var camara: LifecycleCameraController

    private val modosFlash = listOf(
        Triple(ImageCapture.FLASH_MODE_AUTO, R.drawable.ic_flash_auto, R.string.flash_auto),
        Triple(ImageCapture.FLASH_MODE_ON, R.drawable.ic_flash_encendido, R.string.flash_encendido),
        Triple(ImageCapture.FLASH_MODE_OFF, R.drawable.ic_flash_apagado, R.string.flash_apagado)
    )
    private var flashActual = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCamaraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ajustarABarrasDelSistema(view)

        camara = LifecycleCameraController(requireContext()).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            // Prioriza la calidad sobre la rapidez para que la foto salga clara.
            imageCaptureMode = ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            bindToLifecycle(viewLifecycleOwner)
        }
        binding.previewView.controller = camara
        aplicarFlash()

        // El botón para cambiar de cámara solo aparece si el teléfono tiene frontal.
        camara.initializationFuture.addListener({
            _binding?.btnCambiarCamara?.isVisible = camara.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
        }, ContextCompat.getMainExecutor(requireContext()))

        binding.btnCerrar.setOnClickListener { findNavController().popBackStack() }
        binding.btnFlash.setOnClickListener {
            flashActual = (flashActual + 1) % modosFlash.size
            aplicarFlash()
        }
        binding.btnCambiarCamara.setOnClickListener {
            camara.cameraSelector =
                if (camara.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
        }
        binding.btnDisparador.setOnClickListener { tomarFoto() }
    }

    private fun aplicarFlash() {
        val (modo, icono, texto) = modosFlash[flashActual]
        camara.imageCaptureFlashMode = modo
        binding.btnFlash.setImageResource(icono)
        binding.btnFlash.contentDescription = getString(texto)
        binding.tvFlash.setText(texto)
    }

    private fun tomarFoto() {
        binding.btnDisparador.isEnabled = false
        binding.vDestello.animate().alpha(0.8f).setDuration(60).withEndAction {
            _binding?.vDestello?.animate()?.alpha(0f)?.setDuration(160)
        }

        // La carpeta privada de la app no necesita permisos de almacenamiento.
        val carpeta = File(requireContext().filesDir, "fotos").apply { mkdirs() }
        val archivo = File(carpeta, "incidencia_${System.currentTimeMillis()}.jpg")
        val opciones = ImageCapture.OutputFileOptions.Builder(archivo).build()

        camara.takePicture(opciones, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(resultado: ImageCapture.OutputFileResults) {
                    if (_binding == null) return  // el usuario ya cerró la cámara
                    // Deja la ruta en el back stack del formulario y regresa a él.
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(CLAVE_FOTO, archivo.absolutePath)
                    findNavController().popBackStack()
                }

                override fun onError(error: ImageCaptureException) {
                    _binding?.btnDisparador?.isEnabled = true
                    Toast.makeText(requireContext(), R.string.error_guardar_foto, Toast.LENGTH_SHORT).show()
                }
            })
    }

    // La cámara ocupa toda la pantalla; los botones se apartan de las barras del sistema.
    private fun ajustarABarrasDelSistema(view: View) {
        view.doOnAttach {
            val barras = ViewCompat.getRootWindowInsets(it)
                ?.getInsets(WindowInsetsCompat.Type.systemBars()) ?: return@doOnAttach
            binding.barraSuperior.updatePadding(top = barras.top + binding.barraSuperior.paddingBottom)
            binding.barraInferior.updatePadding(bottom = barras.bottom + binding.barraInferior.paddingBottom)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /** Clave con la que el formulario recibe la ruta de la foto. */
        const val CLAVE_FOTO = "fotoRuta"
    }
}
