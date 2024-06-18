package com.example.appturismo.viewHolder

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appturismo.R
import com.example.appturismo.databinding.ItemLocalidadBinding
import com.example.appturismo.model.LocalidadResponse
import com.example.appturismo.utils.OnLocalidadClickListener

class LocalidadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = ItemLocalidadBinding.bind(itemView)
    private val tvNombreLocalidad: TextView = itemView.findViewById(R.id.tvNombreLocalidad)
    private val tvProvincia: TextView = itemView.findViewById(R.id.tvProvincia)

    fun bind(localidad: LocalidadResponse, listener: OnLocalidadClickListener) {
        tvNombreLocalidad.text = localidad.nombre
        tvProvincia.text = localidad.provincia
        val imageResourceId = getImageResourceId(itemView.context, localidad.imagen)
        if (imageResourceId != 0) {
            binding.ivLocalidad.setImageResource(imageResourceId)
        } else {
            Log.d("ImageBinding", "Image resource not found for: ${localidad.imagen}")
            binding.ivLocalidad.setImageResource(R.drawable.capricho)
        }
        itemView.setOnClickListener {
            listener.onLocalidadClick(localidad)
        }
    }

    fun getImageResourceId(context: Context, imageName: String?): Int {
        if (imageName.isNullOrEmpty()) {
            return 0
        }
        return try {
            val resName = imageName.substringBeforeLast(".")
            val resType = "drawable"
            context.resources.getIdentifier(resName, resType, context.packageName)
        } catch (e: Exception) {
            Log.e("ImageBinding", "Error getting image resource ID for: $imageName", e)
            0
        }
    }
}