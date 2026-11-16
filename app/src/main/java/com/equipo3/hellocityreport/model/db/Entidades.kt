package com.equipo3.hellocityreport.model.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.equipo3.hellocityreport.model.Estado

/*
 * Entidades de Room: cada clase es una tabla del diagrama ER (docs/diagrama-er).
 * Los nombres de tablas y columnas son los mismos que en esquema.sql.
 */

/** Tabla usuario. Por ahora solo existe el usuario local (id 1). */
@Entity(tableName = "usuario", indices = [Index(value = ["correo"], unique = true)])
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_usuario") val id: Int = 0,
    val nombre: String,
    val correo: String,
    val telefono: String? = null,
    val rol: String = "CIUDADANO",
    @ColumnInfo(name = "fecha_registro") val fechaRegistro: Long
)

/** Tabla categoria. La columna clave guarda el nombre del enum Categoria. */
@Entity(
    tableName = "categoria",
    indices = [Index(value = ["clave"], unique = true), Index(value = ["nombre"], unique = true)]
)
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_categoria") val id: Int = 0,
    val clave: String,
    val nombre: String,
    val descripcion: String? = null,
    val icono: String? = null
)

/**
 * Tabla incidencia. Cada incidencia pertenece a un usuario y a una categoría:
 * - si se borra el usuario, se borran sus incidencias (CASCADE);
 * - no se puede borrar una categoría que tenga incidencias (RESTRICT).
 */
@Entity(
    tableName = "incidencia",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id_categoria"],
            childColumns = ["id_categoria"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("id_usuario"), Index("id_categoria")]
)
data class IncidenciaEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id_incidencia") val id: Int = 0,
    @ColumnInfo(name = "id_usuario") val idUsuario: Int,
    @ColumnInfo(name = "id_categoria") val idCategoria: Int,
    val titulo: String,
    val descripcion: String,
    val estado: Estado = Estado.PENDIENTE,
    val ubicacion: String,
    val latitud: Double? = null,
    val longitud: Double? = null,
    @ColumnInfo(name = "foto_ruta") val fotoRuta: String? = null,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long
)

/** Resultado de un JOIN: la incidencia con la clave de su categoría. */
data class IncidenciaConCategoria(
    @Embedded val incidencia: IncidenciaEntity,
    @ColumnInfo(name = "categoria_clave") val categoriaClave: String
)
