package com.example.appturismo.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.appturismo.databinding.ItemListaInicioBinding
import com.example.appturismo.model.MonumentoResponse
import com.example.appturismo.utils.OnMonumentoClickListener

class ListaInicioViewHolder(view: View,private val clickListener: OnMonumentoClickListener): RecyclerView.ViewHolder(view){
    private val binding = ItemListaInicioBinding.bind(view)

    fun bind(monumento: MonumentoResponse){
        binding.tvNombreMonumento.text = monumento.nombre
        itemView.setOnClickListener {
            clickListener.onMonumentoClick(monumento)
        }
    }


}