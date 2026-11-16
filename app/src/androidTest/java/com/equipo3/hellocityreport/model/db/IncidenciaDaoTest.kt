package com.equipo3.hellocityreport.model.db

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.equipo3.hellocityreport.model.Estado
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba la base real de Room en el dispositivo, pero en memoria: cada prueba
 * empieza con la base vacía y no toca los datos de la app.
 */
@RunWith(AndroidJUnit4::class)
class IncidenciaDaoTest {

    private lateinit var db: CityReportDatabase
    private lateinit var incidenciaDao: IncidenciaDao
    private lateinit var categoriaDao: CategoriaDao
    private lateinit var usuarioDao: UsuarioDao

    private var idUsuario = 0
    private var idBache = 0

    @Before
    fun crearBase() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, CityReportDatabase::class.java).build()
        incidenciaDao = db.incidenciaDao()
        categoriaDao = db.categoriaDao()
        usuarioDao = db.usuarioDao()

        idUsuario = usuarioDao.insertar(
            UsuarioEntity(nombre = "Prueba", correo = "prueba@cityreport.app", fechaRegistro = 0)
        ).toInt()
        idBache = categoriaDao.insertar(CategoriaEntity(clave = "BACHE", nombre = "Bache")).toInt()
    }

    @After
    fun cerrarBase() {
        db.close()
    }

    private fun incidencia(titulo: String, fecha: Long, idCategoria: Int = idBache, idUsuario: Int = this.idUsuario) =
        IncidenciaEntity(
            idUsuario = idUsuario,
            idCategoria = idCategoria,
            titulo = titulo,
            descripcion = "Descripción",
            ubicacion = "UPIICSA",
            fechaCreacion = fecha
        )

    @Test
    fun insertarYLeerConSuCategoria() = runBlocking {
        val id = incidenciaDao.insertar(incidencia("Bache en Té", fecha = 100)).toInt()

        val leida = incidenciaDao.buscarPorId(id)
        assertEquals("Bache en Té", leida?.incidencia?.titulo)
        assertEquals("BACHE", leida?.categoriaClave)
        assertEquals(Estado.PENDIENTE, leida?.incidencia?.estado)
    }

    @Test
    fun laListaVieneDeLaMasRecienteALaMasAntigua() = runBlocking {
        incidenciaDao.insertar(incidencia("Vieja", fecha = 100))
        incidenciaDao.insertar(incidencia("Nueva", fecha = 300))
        incidenciaDao.insertar(incidencia("Media", fecha = 200))

        val titulos = incidenciaDao.observarTodas().first().map { it.incidencia.titulo }
        assertEquals(listOf("Nueva", "Media", "Vieja"), titulos)
    }

    @Test
    fun noSePuedeGuardarConUnaCategoriaQueNoExiste() = runBlocking {
        val error = runCatching { incidenciaDao.insertar(incidencia("Sin categoría", 100, idCategoria = 99)) }
        assertTrue(error.exceptionOrNull() is SQLiteConstraintException)
    }

    @Test
    fun noSePuedeBorrarUnaCategoriaEnUso() = runBlocking {
        incidenciaDao.insertar(incidencia("Bache", fecha = 100))
        val bache = categoriaDao.obtenerTodas().first { it.id == idBache }

        val error = runCatching { categoriaDao.borrar(bache) }
        assertTrue(error.exceptionOrNull() is SQLiteConstraintException)
    }

    @Test
    fun alBorrarUnUsuarioSeBorranSusIncidencias() = runBlocking {
        val id = incidenciaDao.insertar(incidencia("Bache", fecha = 100)).toInt()
        val usuario = checkNotNull(usuarioDao.buscarPorId(idUsuario))

        usuarioDao.borrar(usuario)

        assertNull(incidenciaDao.buscarPorId(id))
        assertTrue(incidenciaDao.observarTodas().first().isEmpty())
    }
}
