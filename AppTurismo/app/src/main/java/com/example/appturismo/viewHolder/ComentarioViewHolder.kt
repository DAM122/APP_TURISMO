package com.example.appturismo.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.appturismo.databinding.ItemComentarioBinding
import com.example.appturismo.model.ComentarioResponse

class ComentarioViewHolder(view:View):RecyclerView.ViewHolder(view) {

    private val binding = ItemComentarioBinding.bind(view)

    fun bind(comentario:ComentarioResponse){
        binding.tvComentario.text = comentario.mensaje
        binding.ratingBar.rating = comentario.puntuacion.toFloat()
        binding.tvAuthorName.text = comentario.usuario.nombre
    }

}