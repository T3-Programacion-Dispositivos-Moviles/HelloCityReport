package com.equipo3.hellocityreport

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.equipo3.hellocityreport.databinding.ActivityMainBinding

/**
 * Activity única que aloja la navegación.
 *
 * No dibuja pantallas por sí misma ni tiene Presenter: solo arma la estructura
 * de Material 3 (Top App Bar arriba, BottomNavigationView abajo) y delega en el
 * NavHostFragment, que muestra los Fragments definidos en nav_graph.xml.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

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
            val tecladoVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val alturaTeclado = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            binding.main.updatePadding(
                left = barras.left,
                right = barras.right,
                bottom = if (tecladoVisible) alturaTeclado else 0
            )
            binding.appBar.updatePadding(top = barras.top)
            binding.bottomNav.updatePadding(bottom = barras.bottom)
            binding.bottomNav.isVisible = !tecladoVisible

            // Ya se aplicaron aquí; así las vistas hijas no los vuelven a sumar.
            WindowInsetsCompat.CONSUMED
        }
    }
}
