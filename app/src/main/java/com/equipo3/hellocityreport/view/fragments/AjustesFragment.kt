package com.equipo3.hellocityreport.view.fragments

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.equipo3.hellocityreport.R
import com.equipo3.hellocityreport.app
import com.equipo3.hellocityreport.contract.AjustesContract
import com.equipo3.hellocityreport.databinding.FragmentAjustesBinding
import com.equipo3.hellocityreport.model.Tema
import com.equipo3.hellocityreport.presenter.AjustesPresenter
import com.equipo3.hellocityreport.view.Notificaciones
import com.google.android.material.snackbar.Snackbar
import com.equipo3.hellocityreport.view.aplicarTema as cambiarTemaDeLaApp

/**
 * Vista de ajustes. Muestra las preferencias guardadas y avisa al Presenter
 * cuando el usuario cambia alguna.
 */
class AjustesFragment : Fragment(), AjustesContract.View {

    private var _binding: FragmentAjustesBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: AjustesContract.Presenter

    // Evita tratar como elección del usuario los cambios que hace la propia vista.
    private var mostrandoTema = false

    private val botonesTema by lazy {
        mapOf(
            Tema.CLARO to R.id.btnTemaClaro,
            Tema.OSCURO to R.id.btnTemaOscuro,
            Tema.SISTEMA to R.id.btnTemaSistema
        )
    }

    private val pedirPermiso =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
            presenter.alResponderPermisoAvisos(concedido)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAjustesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = AjustesPresenter(this, app.preferencias, viewLifecycleOwner.lifecycleScope)

        binding.grupoTema.addOnButtonCheckedListener { _, botonId, marcado ->
            if (!marcado || mostrandoTema) return@addOnButtonCheckedListener
            val tema = botonesTema.entries.first { it.value == botonId }.key
            presenter.alElegirTema(tema)
        }

        // Se usa el clic (no el cambio de estado) para distinguir al usuario de la vista.
        binding.switchAvisos.setOnClickListener {
            presenter.alCambiarAvisos(binding.switchAvisos.isChecked, Notificaciones.tienePermiso(requireContext()))
        }
        binding.filaAvisos.setOnClickListener { binding.switchAvisos.performClick() }

        presenter.iniciar()
    }

    // ---------- métodos del contrato ----------

    override fun mostrarTema(tema: Tema) {
        mostrandoTema = true
        binding.grupoTema.check(botonesTema.getValue(tema))
        mostrandoTema = false
    }

    override fun mostrarAvisos(activos: Boolean) {
        binding.switchAvisos.isChecked = activos
    }

    override fun aplicarTema(tema: Tema) {
        cambiarTemaDeLaApp(tema)
    }

    override fun pedirPermisoAvisos() {
        // Solo pasa en Android 13 o superior: antes no existía este permiso.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun mostrarPermisoAvisosNegado() {
        Snackbar.make(binding.root, R.string.ajustes_permiso_negado, Snackbar.LENGTH_LONG)
            .setAction(R.string.boton_ajustes) {
                startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.fromParts("package", requireContext().packageName, null))
                )
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.destruir()
        _binding = null
    }
}
