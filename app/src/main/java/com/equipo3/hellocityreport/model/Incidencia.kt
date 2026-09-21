package com.equipo3.hellocityreport.model

/**
 * Modelo de datos principal de la app CityReport.
 *
 * Incidencias urbanas que reporta la ciudadanía.
 *
 * Programación Orientada a Objetos:
 *  - Cada propiedad (`val`) es un atributo del objeto Incidencia (encapsulación).
 *  - `Categoria` y `Estado` son enum classes: conjuntos cerrados de valores
 *    válidos, lo que evita "strings mágicos" y errores de dedo.
 */
data class Incidencia(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val categoria: Categoria,
    val estado: Estado,
    val ubicacion: String,
    val fecha: String
)

/** Tipo de incidencia que puede reportar la ciudadanía. */
enum class Categoria(val etiqueta: String) {
    BACHE("Bache"),
    LUMINARIA("Luminaria"),
    BASURA("Acumulación de basura"),
    FUGA_AGUA("Fuga de agua"),
    OTRO("Otro")
}

/** Estado de atención de la incidencia. */
enum class Estado(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    EN_PROCESO("En proceso"),
    RESUELTO("Resuelto")
}
