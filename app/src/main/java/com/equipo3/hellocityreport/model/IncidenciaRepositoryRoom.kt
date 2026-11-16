package com.equipo3.hellocityreport.model

import com.equipo3.hellocityreport.model.db.CityReportDatabase
import com.equipo3.hellocityreport.model.db.IncidenciaConCategoria
import com.equipo3.hellocityreport.model.db.IncidenciaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Implementación del repositorio con Room. Convierte las filas de la base
 * (entidades) en el modelo Incidencia que usan los Presenters y la vista.
 */
class IncidenciaRepositoryRoom(private val db: CityReportDatabase) : IncidenciaRepository {

    private val incidenciaDao = db.incidenciaDao()
    private val categoriaDao = db.categoriaDao()

    override fun observarTodas(): Flow<List<Incidencia>> =
        incidenciaDao.observarTodas().map { filas -> filas.map { it.aIncidencia() } }

    override suspend fun buscarPorId(id: Int): Incidencia? =
        incidenciaDao.buscarPorId(id)?.aIncidencia()

    override suspend fun agregar(
        titulo: String,
        descripcion: String,
        categoria: Categoria,
        ubicacion: String,
        latitud: Double?,
        longitud: Double?,
        fotoRuta: String?
    ): Incidencia {
        val idCategoria = checkNotNull(categoriaDao.buscarIdPorClave(categoria.name)) {
            "La categoría ${categoria.name} no existe en la base"
        }
        val id = incidenciaDao.insertar(
            IncidenciaEntity(
                idUsuario = CityReportDatabase.ID_USUARIO_LOCAL,
                idCategoria = idCategoria,
                titulo = titulo,
                descripcion = descripcion,
                estado = Estado.PENDIENTE,
                ubicacion = ubicacion,
                latitud = latitud,
                longitud = longitud,
                fotoRuta = fotoRuta,
                fechaCreacion = System.currentTimeMillis()
            )
        )
        return checkNotNull(buscarPorId(id.toInt()))
    }

    private fun IncidenciaConCategoria.aIncidencia() = Incidencia(
        id = incidencia.id,
        titulo = incidencia.titulo,
        descripcion = incidencia.descripcion,
        categoria = Categoria.entries.find { it.name == categoriaClave } ?: Categoria.OTRO,
        estado = incidencia.estado,
        ubicacion = incidencia.ubicacion,
        fecha = formatoFecha.format(Date(incidencia.fechaCreacion)),
        latitud = incidencia.latitud,
        longitud = incidencia.longitud,
        fotoRuta = incidencia.fotoRuta
    )

    private val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-MX"))
}
