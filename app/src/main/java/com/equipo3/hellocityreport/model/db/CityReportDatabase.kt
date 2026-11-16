package com.equipo3.hellocityreport.model.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.equipo3.hellocityreport.model.Categoria
import java.util.Calendar

/**
 * Base de datos local de CityReport (archivo cityreport.db dentro de la app).
 * Funciona sin internet. El esquema de cada versión se exporta a app/schemas.
 */
@Database(
    entities = [UsuarioEntity::class, CategoriaEntity::class, IncidenciaEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Convertidores::class)
abstract class CityReportDatabase : RoomDatabase() {

    abstract fun incidenciaDao(): IncidenciaDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        /** Mientras no haya inicio de sesión (Firebase Auth), todo se reporta con este usuario. */
        const val ID_USUARIO_LOCAL = 1

        private const val NOMBRE_ARCHIVO = "cityreport.db"

        @Volatile
        private var instancia: CityReportDatabase? = null

        /** Una sola instancia para toda la app (abrir la base es costoso). */
        fun obtener(context: Context): CityReportDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    CityReportDatabase::class.java,
                    NOMBRE_ARCHIVO
                )
                    .addCallback(DatosIniciales)
                    .build()
                    .also { instancia = it }
            }
    }

    /**
     * Se ejecuta una sola vez, cuando se crea el archivo de la base (al instalar
     * la app o después de borrar sus datos). Precarga categorías, el usuario
     * local y las incidencias de ejemplo.
     */
    private object DatosIniciales : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Categoria.entries.forEachIndexed { i, categoria ->
                insertar(db, "categoria", ContentValues().apply {
                    put("id_categoria", i + 1)
                    put("clave", categoria.name)
                    put("nombre", categoria.etiqueta)
                    put("descripcion", DESCRIPCIONES[categoria])
                })
            }
            insertar(db, "usuario", ContentValues().apply {
                put("id_usuario", ID_USUARIO_LOCAL)
                put("nombre", "Usuario local")
                put("correo", "local@cityreport.app")
                put("rol", "CIUDADANO")
                put("fecha_registro", System.currentTimeMillis())
            })
            EJEMPLOS.forEach { insertar(db, "incidencia", it) }
        }

        private fun insertar(db: SupportSQLiteDatabase, tabla: String, valores: ContentValues) {
            db.insert(tabla, SQLiteDatabase.CONFLICT_ABORT, valores)
        }

        private val DESCRIPCIONES = mapOf(
            Categoria.BACHE to "Hoyos o daños en el pavimento",
            Categoria.LUMINARIA to "Postes de luz apagados o dañados",
            Categoria.BASURA to "Basura sin recoger en la vía pública",
            Categoria.FUGA_AGUA to "Fugas en tomas, tuberías o banquetas",
            Categoria.OTRO to "Cualquier otro problema urbano"
        )

        private val EJEMPLOS by lazy {
            listOf(
                ejemplo(
                    "Bache en Av. Instituto Politécnico Nacional",
                    "Bache grande a la altura del cruce que está dañando las llantas de los " +
                        "vehículos y representa un riesgo para motociclistas.",
                    Categoria.BACHE, "PENDIENTE", "Av. IPN #123, Lindavista, CDMX",
                    fecha(18, 9), 19.48770, -99.13090
                ),
                ejemplo(
                    "Luminaria apagada frente a UPIICSA",
                    "El poste de luz lleva una semana sin funcionar; la calle queda muy " +
                        "oscura por las noches.",
                    Categoria.LUMINARIA, "EN_PROCESO", "Té 950, Granjas México, Iztacalco",
                    fecha(17, 9), 19.39550, -99.09150
                ),
                ejemplo(
                    "Acumulación de basura en esquina",
                    "El camión recolector no ha pasado y la basura se está acumulando, " +
                        "generando mal olor y fauna nociva.",
                    Categoria.BASURA, "PENDIENTE", "Calle Sur 4 y Oriente 3, Agrícola Oriental",
                    fecha(16, 9), 19.39660, -99.07450
                ),
                ejemplo(
                    "Fuga de agua potable",
                    "Sale agua constantemente de una toma en la banqueta; se está " +
                        "desperdiciando mucha agua.",
                    Categoria.FUGA_AGUA, "RESUELTO", "Av. Río Churubusco #200, Iztacalco",
                    fecha(12, 9), 19.38700, -99.11100
                ),
                ejemplo(
                    "Señalamiento vial derribado",
                    "El señalamiento de alto quedó tirado tras un choque y no se ha repuesto.",
                    Categoria.OTRO, "PENDIENTE", "Eje 5 Sur y Calzada de la Viga",
                    fecha(10, 9), 19.37380, -99.12360
                )
            )
        }

        private fun ejemplo(
            titulo: String, descripcion: String, categoria: Categoria, estado: String,
            ubicacion: String, fechaCreacion: Long, latitud: Double, longitud: Double
        ) = ContentValues().apply {
            put("id_usuario", ID_USUARIO_LOCAL)
            put("id_categoria", categoria.ordinal + 1)
            put("titulo", titulo)
            put("descripcion", descripcion)
            put("estado", estado)
            put("ubicacion", ubicacion)
            put("latitud", latitud)
            put("longitud", longitud)
            put("fecha_creacion", fechaCreacion)
        }

        // Mediodía del día indicado de 2026, en milisegundos.
        private fun fecha(dia: Int, mes: Int): Long = Calendar.getInstance().apply {
            clear()
            set(2026, mes - 1, dia, 12, 0)
        }.timeInMillis
    }
}
