package com.equipo3.hellocityreport.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Fuente de datos temporal (en memoria).
 *
 * Es un `object` (Singleton en Kotlin): existe una única instancia en toda la
 * app. Implementa IncidenciaRepository, que es lo único que conocen los
 * Presenters. Las incidencias nuevas se guardan mientras la app está abierta; al
 * cerrarla se pierden. En prácticas posteriores se usará una base de datos.
 */
object IncidenciaDataSource : IncidenciaRepository {

    private val incidencias = mutableListOf(
        Incidencia(
            id = 1,
            titulo = "Bache en Av. Instituto Politécnico Nacional",
            descripcion = "Bache grande a la altura del cruce que está dañando las " +
                "llantas de los vehículos y representa un riesgo para motociclistas.",
            categoria = Categoria.BACHE,
            estado = Estado.PENDIENTE,
            ubicacion = "Av. IPN #123, Lindavista, CDMX",
            fecha = "18/09/2026"
        ),
        Incidencia(
            id = 2,
            titulo = "Luminaria apagada frente a UPIICSA",
            descripcion = "El poste de luz lleva una semana sin funcionar; la calle " +
                "queda muy oscura por las noches.",
            categoria = Categoria.LUMINARIA,
            estado = Estado.EN_PROCESO,
            ubicacion = "Té 950, Granjas México, Iztacalco",
            fecha = "17/09/2026"
        ),
        Incidencia(
            id = 3,
            titulo = "Acumulación de basura en esquina",
            descripcion = "El camión recolector no ha pasado y la basura se está " +
                "acumulando, generando mal olor y fauna nociva.",
            categoria = Categoria.BASURA,
            estado = Estado.PENDIENTE,
            ubicacion = "Calle Sur 4 y Oriente 3, Agrícola Oriental",
            fecha = "16/09/2026"
        ),
        Incidencia(
            id = 4,
            titulo = "Fuga de agua potable",
            descripcion = "Sale agua constantemente de una toma en la banqueta; se " +
                "está desperdiciando mucha agua.",
            categoria = Categoria.FUGA_AGUA,
            estado = Estado.RESUELTO,
            ubicacion = "Av. Río Churubusco #200, Iztacalco",
            fecha = "12/09/2026"
        ),
        Incidencia(
            id = 5,
            titulo = "Señalamiento vial derribado",
            descripcion = "El señalamiento de alto quedó tirado tras un choque y no " +
                "se ha repuesto.",
            categoria = Categoria.OTRO,
            estado = Estado.PENDIENTE,
            ubicacion = "Eje 5 Sur y Calzada de la Viga",
            fecha = "10/09/2026"
        )
    )

    /** Devuelve una copia de todas las incidencias para mostrarlas en la lista. */
    override fun obtenerTodas(): List<Incidencia> = incidencias.toList()

    /** Busca una incidencia por su id (se usa en la pantalla de detalle). */
    override fun buscarPorId(id: Int): Incidencia? = incidencias.find { it.id == id }

    /**
     * Registra una nueva incidencia al inicio de la lista.
     * El id es el mayor existente + 1, el estado inicial es PENDIENTE
     * y la fecha es la del día.
     */
    override fun agregar(
        titulo: String,
        descripcion: String,
        categoria: Categoria,
        ubicacion: String
    ): Incidencia {
        val nueva = Incidencia(
            id = (incidencias.maxOfOrNull { it.id } ?: 0) + 1,
            titulo = titulo,
            descripcion = descripcion,
            categoria = categoria,
            estado = Estado.PENDIENTE,
            ubicacion = ubicacion,
            fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        )
        incidencias.add(0, nueva)
        return nueva
    }
}
