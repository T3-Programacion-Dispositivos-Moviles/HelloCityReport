package com.equipo3.hellocityreport

import android.Manifest
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.equipo3.hellocityreport.databinding.ActivityMainBinding
import com.equipo3.hellocityreport.model.Incidencia
import com.equipo3.hellocityreport.view.AvisosIncidencia
import com.equipo3.hellocityreport.view.Notificaciones
import com.google.android.material.appbar.AppBarLayout

/**
 * Activity única que aloja la navegación.
 *
 * Arma la estructura de Material 3 (Top App Bar arriba, BottomNavigationView abajo) y delega en el
 * NavHostFragment, que muestra los Fragments definidos en nav_graph.xml.
 * También pide el permiso de notificaciones y muestra el aviso de reporte guardado.
 */
class MainActivity : AppCompatActivity(), AvisosIncidencia {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    // Pantallas que ocupan todo el espacio, sin barras (cámara y foto completa).
    private val pantallasCompletas = setOf(R.id.camaraFragment, R.id.fotoFragment)
    private var pantallaCompleta = false
    private var tecladoVisible = false

    // Incidencia que espera a que el usuario responda el permiso de notificaciones.
    private var incidenciaPorAvisar: Incidencia? = null
    private val pedirPermisoNotificaciones =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
            val incidencia = incidenciaPorAvisar ?: return@registerForActivityResult
            incidenciaPorAvisar = null
            if (concedido) Notificaciones.avisarIncidenciaGuardada(this, incidencia)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ajustarABarrasDelSistema()

        // Obtenemos el NavController desde el NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Las tres secciones de la barra inferior son pantallas principales: en
        // ellas no aparece la flecha "atrás". En el detalle sí aparece.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.listaIncidenciasFragment,
                R.id.nuevaIncidenciaFragment,
                R.id.resumenFragment
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        // Conecta la barra inferior con el NavGraph: marca la opción de la
        // pantalla actual (los ids del menú son los mismos del NavGraph).
        binding.bottomNav.setupWithNavController(navController)

        // Al tocar una opción se navega a su pantalla. Se regresa primero a la
        // lista (popUpTo) sin guardar lo que estaba abierto, así cada sección
        // empieza desde su pantalla principal y con los datos actualizados.
        binding.bottomNav.setOnItemSelectedListener { item ->
            val opciones = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.listaIncidenciasFragment, false)
                .build()
            navController.navigate(item.itemId, null, opciones)
            true
        }

        // Tocar la sección en la que ya estás regresa a su pantalla principal
        // (por ejemplo, tocar Inicio estando en el detalle vuelve a la lista).
        binding.bottomNav.setOnItemReselectedListener { item ->
            navController.popBackStack(item.itemId, false)
        }

        // En la cámara y en la foto completa se ocultan las dos barras. Se aplica
        // en el siguiente ciclo (post) para que la transición de la foto registre
        // primero su posición inicial y no "brinque" cuando las barras desaparecen.
        navController.addOnDestinationChangedListener { _, destino, _ ->
            binding.root.post {
                pantallaCompleta = destino.id in pantallasCompletas
                actualizarBarras()
            }
        }
    }

    /**
     * La app se dibuja de borde a borde (edge-to-edge), así que cada barra se
     * aparta de las barras del sistema: la superior de la barra de estado y la
     * inferior de la barra de gestos. Con el teclado abierto se oculta la barra
     * inferior para dejar más espacio al formulario.
     */
    private fun ajustarABarrasDelSistema() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { _, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            tecladoVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val alturaTeclado = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            binding.main.updatePadding(
                left = barras.left,
                right = barras.right,
                bottom = if (tecladoVisible) alturaTeclado else 0
            )
            binding.appBar.updatePadding(top = barras.top)
            binding.bottomNav.updatePadding(bottom = barras.bottom)
            actualizarBarras()

            // Ya se aplicaron aquí; así las vistas hijas no los vuelven a sumar.
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun actualizarBarras() {
        binding.appBar.isVisible = !pantallaCompleta
        binding.bottomNav.isVisible = !pantallaCompleta && !tecladoVisible
        // Con la barra superior oculta, el contenido ya no debe acomodarse debajo de ella.
        binding.navHostFragment.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            if (pantallaCompleta && behavior != null) behavior = null
            if (!pantallaCompleta && behavior == null) behavior = AppBarLayout.ScrollingViewBehavior()
        }
        // Sobre fondo negro (cámara) los iconos del sistema deben ser claros.
        val modoOscuro = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
        WindowCompat.getInsetsController(window, binding.root).apply {
            isAppearanceLightStatusBars = !pantallaCompleta && !modoOscuro
            isAppearanceLightNavigationBars = !pantallaCompleta && !modoOscuro
        }
    }

    // Desde Android 13 mostrar notificaciones requiere permiso del usuario. Se pide
    // la primera vez que hay algo que avisar (al guardar un reporte).
    override fun avisarIncidenciaGuardada(incidencia: Incidencia) {
        if (Notificaciones.tienePermiso(this)) {
            Notificaciones.avisarIncidenciaGuardada(this, incidencia)
        } else {
            // Solo llega aquí en Android 13 o superior (antes no hacía falta el permiso).
            incidenciaPorAvisar = incidencia
            pedirPermisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
