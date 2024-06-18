package com.example.appturismo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appturismo.R
import com.example.appturismo.model.LocalidadResponse
import com.example.appturismo.utils.OnLocalidadClickListener
import com.example.appturismo.viewHolder.LocalidadViewHolder


class LocalidadAdapter(private var localidades: List<LocalidadResponse>, private val listener: OnLocalidadClickListener) : RecyclerView.Adapter<LocalidadViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalidadViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_localidad, parent, false)
        return LocalidadViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LocalidadViewHolder, position: Int) {
        holder.bind(localidades[position],listener)
        if (position == localidades.size - 1) {
            holder.itemView.findViewById<View>(R.id.divider_line).visibility = View.GONE
        } else {
            holder.itemView.findViewById<View>(R.id.divider_line).visibility = View.VISIBLE
        }
    }


    override fun getItemCount(): Int = localidades.size

    fun updateData(newLocalidades: List<LocalidadResponse>) {
        localidades = newLocalidades
        notifyDataSetChanged()
    }
}