package com.equipo3.hellocityreport.view.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.equipo3.hellocityreport.R
import com.equipo3.hellocityreport.contract.NuevaIncidenciaContract
import com.equipo3.hellocityreport.contract.NuevaIncidenciaContract.Campo
import com.equipo3.hellocityreport.databinding.FragmentNuevaIncidenciaBinding
import com.equipo3.hellocityreport.model.Incidencia
import com.equipo3.hellocityreport.model.IncidenciaDataSource
import com.equipo3.hellocityreport.model.ProveedorUbicacionFused
import com.equipo3.hellocityreport.model.Ubicacion
import com.equipo3.hellocityreport.presenter.NuevaIncidenciaPresenter
import com.equipo3.hellocityreport.view.AvisosIncidencia
import com.equipo3.hellocityreport.view.MapaEstatico
import com.equipo3.hellocityreport.view.mostrarFoto
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

/**
 * Vista del formulario (sección Reportar de la barra inferior).
 * Pide los permisos de cámara y ubicación, entrega al Presenter lo que hace el
 * usuario y muestra lo que el Presenter le indica.
 */
class NuevaIncidenciaFragment : Fragment(), NuevaIncidenciaContract.View {

    private var _binding: FragmentNuevaIncidenciaBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: NuevaIncidenciaContract.Presenter
    private var mapa: MapaEstatico? = null

    // Los permisos se piden en contexto: solo cuando el usuario toca la función.
    private val pedirCamara = registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) abrirCamara() else avisarPermisoNegado(R.string.permiso_camara_negado)
    }

    // Desde Android 12 se piden juntos el permiso preciso y el aproximado:
    // el usuario puede elegir compartir solo la ubicación aproximada.
    private val pedirUbicacion =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permisos ->
            when {
                permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true -> presenter.alPermitirUbicacion(precisa = true)
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> presenter.alPermitirUbicacion(precisa = false)
                else -> {
                    presenter.alNegarUbicacion()
                    avisarPermisoNegado(R.string.permiso_ubicacion_negado)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // El Presenter se crea con el Fragment (no con su vista) para que la foto y
        // la ubicación sigan ahí al regresar de la cámara.
        presenter = NuevaIncidenciaPresenter(
            IncidenciaDataSource,
            ProveedorUbicacionFused(requireContext())
        )
        savedInstanceState?.let { restaurarEstado(it) }
    }

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
        mapa = MapaEstatico(binding.mapa)
        presenter.adjuntarVista(this)

        // La cámara deja aquí la ruta de la foto al regresar.
        val pila = findNavController().currentBackStackEntry?.savedStateHandle
        pila?.getLiveData<String>(CamaraFragment.CLAVE_FOTO)?.observe(viewLifecycleOwner) { ruta ->
            if (ruta != null) {
                presenter.alTomarFoto(ruta)
                pila.remove<String>(CamaraFragment.CLAVE_FOTO)
            }
        }

        binding.grupoSinFoto.setOnClickListener { pedirFoto() }
        binding.btnTomarFoto.setOnClickListener { pedirFoto() }
        binding.btnRepetirFoto.setOnClickListener { pedirFoto() }
        binding.btnQuitarFoto.setOnClickListener { presenter.alQuitarFoto() }
        binding.btnUbicacion.setOnClickListener { pedirUbicacionActual() }
        binding.btnActualizarUbicacion.setOnClickListener { pedirUbicacionActual() }

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

    // ---------- permisos ----------

    private fun pedirFoto() {
        if (tienePermiso(Manifest.permission.CAMERA)) abrirCamara()
        else pedirCamara.launch(Manifest.permission.CAMERA)
    }

    private fun abrirCamara() {
        findNavController().navigate(R.id.action_nueva_to_camara)
    }

    private fun pedirUbicacionActual() {
        when {
            tienePermiso(Manifest.permission.ACCESS_FINE_LOCATION) -> presenter.alPermitirUbicacion(precisa = true)
            tienePermiso(Manifest.permission.ACCESS_COARSE_LOCATION) -> presenter.alPermitirUbicacion(precisa = false)
            else -> pedirUbicacion.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    private fun tienePermiso(permiso: String) =
        ContextCompat.checkSelfPermission(requireContext(), permiso) == PackageManager.PERMISSION_GRANTED

    // Si el usuario negó el permiso, se ofrece abrir los Ajustes de la app.
    private fun avisarPermisoNegado(mensaje: Int) {
        val vista = _binding?.root ?: return
        Snackbar.make(vista, mensaje, Snackbar.LENGTH_LONG)
            .setAction(R.string.boton_ajustes) {
                startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.fromParts("package", requireContext().packageName, null))
                )
            }
            .show()
    }

    // ---------- métodos del contrato ----------

    override fun mostrarCategorias(categorias: List<String>) {
        binding.actvCategoria.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categorias)
        )
    }

    override fun mostrarFoto(ruta: String) {
        binding.ivFoto.mostrarFoto(ruta)
        binding.grupoConFoto.isVisible = true
        binding.grupoSinFoto.isVisible = false
        binding.tvErrorFoto.isVisible = false
    }

    override fun mostrarSinFoto() {
        binding.ivFoto.setImageDrawable(null)
        binding.grupoConFoto.isVisible = false
        binding.grupoSinFoto.isVisible = true
    }

    override fun mostrarBuscandoUbicacion() {
        val vista = _binding ?: return
        vista.btnUbicacion.isVisible = false
        vista.btnActualizarUbicacion.isVisible = false
        vista.grupoBuscando.isVisible = true
        vista.tvCoordenadas.setText(R.string.ubicacion_buscando)
    }

    override fun mostrarUbicacion(ubicacion: Ubicacion) {
        val vista = _binding ?: return
        vista.grupoBuscando.isVisible = false
        vista.btnUbicacion.isVisible = false
        vista.btnActualizarUbicacion.isVisible = true
        vista.tvCoordenadas.text = getString(
            R.string.ubicacion_coordenadas, ubicacion.latitud, ubicacion.longitud, ubicacion.precision.roundToInt()
        )
        mapa?.mostrar(ubicacion.latitud, ubicacion.longitud, ubicacion.precision)
    }

    override fun mostrarUbicacionNoDisponible() {
        val vista = _binding ?: return
        mapa?.limpiar()
        vista.grupoBuscando.isVisible = false
        vista.btnUbicacion.isVisible = true
        vista.btnActualizarUbicacion.isVisible = false
        vista.tvCoordenadas.setText(R.string.ubicacion_no_disponible)
    }

    override fun sugerirDireccion(direccion: String) {
        val campo = _binding?.etUbicacion ?: return
        if (campo.text.isNullOrBlank()) campo.setText(direccion)
    }

    override fun mostrarErrores(camposConError: Set<Campo>) {
        val obligatorio = getString(R.string.error_campo_obligatorio)
        binding.tvErrorFoto.isVisible = Campo.FOTO in camposConError
        binding.tilTitulo.error = if (Campo.TITULO in camposConError) obligatorio else null
        binding.tilUbicacion.error = if (Campo.UBICACION in camposConError) obligatorio else null
        binding.tilDescripcion.error = if (Campo.DESCRIPCION in camposConError) obligatorio else null
        binding.tilCategoria.error =
            if (Campo.CATEGORIA in camposConError) getString(R.string.error_categoria) else null
        // Si falta la foto, sube hasta arriba para que el usuario vea el aviso.
        if (Campo.FOTO in camposConError) binding.root.smoothScrollTo(0, 0)
    }

    override fun mostrarIncidenciaGuardada() {
        // Snackbar es el aviso breve que recomienda Material 3; se queda visible
        // sobre la barra inferior aunque cambiemos de pantalla.
        Snackbar.make(binding.root, R.string.mensaje_incidencia_guardada, Snackbar.LENGTH_SHORT)
            .show()
    }

    override fun notificarIncidenciaGuardada(incidencia: Incidencia) {
        // La Activity pide el permiso (Android 13+) y muestra la notificación.
        (activity as? AvisosIncidencia)?.avisarIncidenciaGuardada(incidencia)
    }

    override fun regresarALista() {
        // Cierra el teclado y vuelve a Inicio; la lista se recarga con la nueva incidencia.
        WindowCompat.getInsetsController(requireActivity().window, binding.root)
            .hide(WindowInsetsCompat.Type.ime())
        findNavController().popBackStack()
    }

    // ---------- conservar foto y ubicación si el sistema recrea la pantalla ----------

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ESTADO_FOTO, presenter.fotoRuta)
        presenter.ubicacion?.let {
            outState.putDoubleArray(ESTADO_UBICACION, doubleArrayOf(it.latitud, it.longitud, it.precision.toDouble()))
        }
    }

    private fun restaurarEstado(estado: Bundle) {
        val ubicacion = estado.getDoubleArray(ESTADO_UBICACION)?.let { (latitud, longitud, precision) ->
            Ubicacion(latitud, longitud, precision.toFloat())
        }
        presenter.restaurar(estado.getString(ESTADO_FOTO), ubicacion)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapa?.cancelar()
        mapa = null
        presenter.destruir()
        _binding = null
    }

    private companion object {
        const val ESTADO_FOTO = "estado_foto"
        const val ESTADO_UBICACION = "estado_ubicacion"
    }
}
