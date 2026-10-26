package com.equipo3.hellocityreport.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.equipo3.hellocityreport.R
import com.equipo3.hellocityreport.databinding.ItemIncidenciaBinding
import com.equipo3.hellocityreport.model.Incidencia
import com.equipo3.hellocityreport.view.Fotos
import com.equipo3.hellocityreport.view.mostrarEstado

/**
 * Adaptador del RecyclerView.
 *
 * Un RecyclerView no sabe dibujar datos por sí solo: necesita un Adapter que
 * cree las vistas de cada fila y rellene esas vistas con los datos.
 *
 * @param incidencias lista de datos a mostrar.
 * @param onClick lambda que se ejecuta al tocar una fila. Además de la
 *        incidencia recibe la tarjeta tocada, que es el elemento compartido de
 *        la transición hacia el detalle.
 */
class IncidenciaAdapter(
    private val incidencias: List<Incidencia>,
    private val onClick: (Incidencia, View) -> Unit
) : RecyclerView.Adapter<IncidenciaAdapter.IncidenciaViewHolder>() {

    /**
     * El ViewHolder guarda las referencias a las vistas de UNA fila para no
     * tener que buscarlas (findViewById) cada vez que se recicla. Usamos
     * ViewBinding para acceder a las vistas de forma segura.
     */
    inner class IncidenciaViewHolder(
        val binding: ItemIncidenciaBinding
    ) : RecyclerView.ViewHolder(binding.root)

    // Se llama cuando el RecyclerView necesita crear una fila nueva.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidenciaViewHolder {
        val binding = ItemIncidenciaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return IncidenciaViewHolder(binding)
    }

    // Se llama para "pintar" los datos de la posición indicada en la fila.
    override fun onBindViewHolder(holder: IncidenciaViewHolder, position: Int) {
        val incidencia = incidencias[position]
        holder.binding.txtTitulo.text = incidencia.titulo
        holder.binding.txtUbicacion.text = incidencia.ubicacion
        holder.binding.chipEstado.mostrarEstado(incidencia.estado)
        holder.binding.txtCategoriaFecha.text = holder.binding.root.context.getString(
            R.string.incidencia_resumen,
            incidencia.categoria.etiqueta,
            incidencia.fecha
        )

        val ruta = incidencia.fotoRuta
        holder.binding.ivMiniatura.isVisible = ruta != null
        holder.binding.ivMiniatura.setImageBitmap(ruta?.let { Fotos.cargar(it, 200) })

        // Nombre único por tarjeta: el detalle usa el mismo para la transición.
        ViewCompat.setTransitionName(holder.binding.root, "incidencia_${incidencia.id}")
        holder.binding.root.setOnClickListener { onClick(incidencia, holder.binding.root) }
    }

    // Cuántos elementos hay en total.
    override fun getItemCount(): Int = incidencias.size
}
