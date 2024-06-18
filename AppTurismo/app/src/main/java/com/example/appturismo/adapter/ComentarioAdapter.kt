package com.example.appturismo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appturismo.R
import com.example.appturismo.model.ComentarioResponse
import com.example.appturismo.model.MonumentoResponse
import com.example.appturismo.viewHolder.ComentarioViewHolder

class ComentarioAdapter(private var comentarios: List<ComentarioResponse>) : RecyclerView.Adapter<ComentarioViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ComentarioViewHolder(layoutInflater.inflate(R.layout.item_comentario,parent,false))
    }

    override fun getItemCount(): Int = comentarios.size

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val item  = comentarios[position]
        holder.bind(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(comentario: List<ComentarioResponse>) {
        comentarios = comentario
        notifyDataSetChanged()
    }
}