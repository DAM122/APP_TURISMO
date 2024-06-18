package com.example.appturismo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.example.appturismo.R
import com.example.appturismo.model.MediaRespones
import com.example.appturismo.model.MonumentoResponse
import com.example.appturismo.utils.OnMonumentoClickListener
import com.example.appturismo.viewHolder.MonumentoViewHolder

class MonumentoAdapter(private var monumentos: List<MonumentoResponse>,private val clickListener: OnMonumentoClickListener) : RecyclerView.Adapter<MonumentoViewHolder>() {
    private var mediaPuntuaciones: List<MediaRespones> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonumentoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MonumentoViewHolder(layoutInflater.inflate(R.layout.item_monumento, parent, false),clickListener)
    }

    override fun getItemCount(): Int = monumentos.size

    override fun onBindViewHolder(holder: MonumentoViewHolder, position: Int) {
        val monumentoArtista = monumentos[position]
        val mediaPuntuacion = mediaPuntuaciones.find { it.monumento.id == monumentoArtista.id }
        mediaPuntuacion?.let { holder.bind(monumentoArtista, it) }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newMonumentos: List<MonumentoResponse>) {
        monumentos = newMonumentos
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMediaPuntuacion(newMediaPuntuaciones: List<MediaRespones>) {
        mediaPuntuaciones = newMediaPuntuaciones
        notifyDataSetChanged()
    }
}
