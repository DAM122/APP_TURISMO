package com.example.appturismo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appturismo.R
import com.example.appturismo.model.MonumentoResponse
import com.example.appturismo.utils.OnMonumentoClickListener
import com.example.appturismo.viewHolder.ListaInicioViewHolder

class ListaInicioAdapter(private var monumentos: List<MonumentoResponse>,private val clickListener: OnMonumentoClickListener) : RecyclerView.Adapter<ListaInicioViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaInicioViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ListaInicioViewHolder(layoutInflater.inflate(R.layout.item_lista_inicio,parent,false),clickListener)
    }

    override fun getItemCount(): Int = monumentos.size

    override fun onBindViewHolder(holder: ListaInicioViewHolder, position: Int) {
        val item = monumentos[position]
        holder.bind(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newMonumentos: List<MonumentoResponse>) {
        monumentos = newMonumentos
        notifyDataSetChanged()
    }
}