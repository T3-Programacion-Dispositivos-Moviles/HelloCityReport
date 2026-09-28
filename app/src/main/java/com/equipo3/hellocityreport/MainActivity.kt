package com.equipo3.hellocityreport

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.equipo3.hellocityreport.databinding.ActivityMainBinding

/**
 * Activity única que aloja la navegación.
 *
 * No dibuja pantallas por sí misma: delega en el NavHostFragment, que muestra
 * los Fragments según lo definido en res/navigation/nav_graph.xml.
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

        // Se ajusta el padding para no quedar debajo de las barras del sistema
        // ni del teclado
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val barras = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }

        // Obtenemos el NavController desde el NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Conecta la Toolbar con la navegación: el título cambia solo y aparece
        // la flecha "atrás" (Up) al entrar al detalle o a nueva incidencia
        appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}
