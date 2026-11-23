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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.equipo3.hellocityreport.R
import com.equipo3.hellocityreport.app
import com.equipo3.hellocityreport.contract.AjustesContract
import com.equipo3.hellocityreport.contract.CuentaContract
import com.equipo3.hellocityreport.databinding.FragmentAjustesBinding
import com.equipo3.hellocityreport.model.Tema
import com.equipo3.hellocityreport.model.UsuarioSesion
import com.equipo3.hellocityreport.presenter.AjustesPresenter
import com.equipo3.hellocityreport.presenter.CuentaPresenter
import com.equipo3.hellocityreport.view.CuentaGoogle
import com.equipo3.hellocityreport.view.Notificaciones
import com.equipo3.hellocityreport.view.ResultadoGoogle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import com.equipo3.hellocityreport.view.aplicarTema as cambiarTemaDeLaApp

/**
 * Vista de ajustes. Muestra las preferencias guardadas y avisa al Presenter
 * cuando el usuario cambia alguna.
 */
class AjustesFragment : Fragment(), AjustesContract.View, CuentaContract.View {

    private var _binding: FragmentAjustesBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: AjustesContract.Presenter
    private lateinit var presenterCuenta: CuentaContract.Presenter

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

        presenterCuenta = CuentaPresenter(this, app.cuenta, viewLifecycleOwner.lifecycleScope)
        binding.btnIniciarSesion.setOnClickListener { presenterCuenta.alTocarIniciarSesion() }
        binding.btnCerrarSesion.setOnClickListener { presenterCuenta.alTocarCerrarSesion() }
        presenterCuenta.iniciar()
    }

    // ---------- cuenta (Google Sign-In) ----------

    override fun mostrarSesionIniciada(usuario: UsuarioSesion) {
        binding.txtCuentaNombre.text = usuario.nombre
        binding.txtCuentaDetalle.text = usuario.correo
        binding.progresoCuenta.isVisible = false
        binding.btnIniciarSesion.isVisible = false
        binding.btnCerrarSesion.isVisible = true
    }

    override fun mostrarSesionCerrada() {
        binding.txtCuentaNombre.setText(R.string.cuenta_sin_sesion)
        binding.txtCuentaDetalle.setText(R.string.cuenta_sin_sesion_ayuda)
        binding.progresoCuenta.isVisible = false
        binding.btnIniciarSesion.isVisible = true
        binding.btnIniciarSesion.isEnabled = true
        binding.btnCerrarSesion.isVisible = false
    }

    override fun mostrarIniciandoSesion() {
        binding.txtCuentaDetalle.setText(R.string.cuenta_iniciando)
        binding.progresoCuenta.isVisible = true
        binding.btnIniciarSesion.isEnabled = false
    }

    override fun pedirCuentaGoogle() {
        viewLifecycleOwner.lifecycleScope.launch {
            when (val resultado = CuentaGoogle(requireActivity()).pedirToken()) {
                is ResultadoGoogle.Token -> presenterCuenta.alRecibirTokenGoogle(resultado.idToken)
                ResultadoGoogle.Cancelado -> presenterCuenta.alCancelarCuentaGoogle()
                ResultadoGoogle.SinCuentas -> presenterCuenta.alNoHaberCuentasGoogle()
                ResultadoGoogle.Error -> presenterCuenta.alFallarCuentaGoogle()
            }
        }
    }

    override fun olvidarCuentaGoogle() {
        // Se usa el scope de la Activity para que termine aunque se salga de Ajustes.
        val activity = requireActivity()
        (activity as AppCompatActivity).lifecycleScope.launch { CuentaGoogle(activity).olvidar() }
    }

    override fun mostrarErrorSesion() {
        Snackbar.make(binding.root, R.string.cuenta_error, Snackbar.LENGTH_LONG).show()
    }

    override fun mostrarSinCuentasGoogle() {
        Snackbar.make(binding.root, R.string.cuenta_sin_cuentas, Snackbar.LENGTH_LONG).show()
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
        presenterCuenta.destruir()
        _binding = null
    }
}
