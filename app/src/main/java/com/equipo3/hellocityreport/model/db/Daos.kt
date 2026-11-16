package com.equipo3.hellocityreport.model.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/*
 * DAOs (Data Access Objects): las operaciones sobre cada tabla.
 * Room revisa el SQL al compilar y genera el código. Las funciones suspend
 * y los Flow se ejecutan fuera del hilo principal.
 */

@Dao
interface IncidenciaDao {

    /** Todas las incidencias, la más reciente primero. El Flow avisa cada cambio. */
    @Query(
        """
        SELECT i.*, c.clave AS categoria_clave
        FROM incidencia i
        INNER JOIN categoria c ON c.id_categoria = i.id_categoria
        ORDER BY i.fecha_creacion DESC, i.id_incidencia DESC
        """
    )
    fun observarTodas(): Flow<List<IncidenciaConCategoria>>

    @Query(
        """
        SELECT i.*, c.clave AS categoria_clave
        FROM incidencia i
        INNER JOIN categoria c ON c.id_categoria = i.id_categoria
        WHERE i.id_incidencia = :id
        """
    )
    suspend fun buscarPorId(id: Int): IncidenciaConCategoria?

    /** Regresa el id que SQLite le asignó a la nueva fila. */
    @Insert
    suspend fun insertar(incidencia: IncidenciaEntity): Long
}

@Dao
interface CategoriaDao {

    @Query("SELECT id_categoria FROM categoria WHERE clave = :clave")
    suspend fun buscarIdPorClave(clave: String): Int?

    @Query("SELECT * FROM categoria ORDER BY id_categoria")
    suspend fun obtenerTodas(): List<CategoriaEntity>

    @Insert
    suspend fun insertar(categoria: CategoriaEntity): Long

    @Delete
    suspend fun borrar(categoria: CategoriaEntity)
}

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuario WHERE id_usuario = :id")
    suspend fun buscarPorId(id: Int): UsuarioEntity?

    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Delete
    suspend fun borrar(usuario: UsuarioEntity)
}
