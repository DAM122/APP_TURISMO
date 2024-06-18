package com.example.appturismo.viewHolder

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.RatingBar
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.appturismo.R
import com.example.appturismo.databinding.ItemMonumentoBinding
import com.example.appturismo.model.MediaRespones
import com.example.appturismo.model.MonumentoResponse
import com.example.appturismo.utils.OnMonumentoClickListener

class MonumentoViewHolder(view: View,private val clickListener: OnMonumentoClickListener): RecyclerView.ViewHolder(view){
    private val binding = ItemMonumentoBinding.bind(view)
    private val ratingBarMonumento: RatingBar = itemView.findViewById(R.id.ratingBarMonumento)
    @SuppressLint("SetTextI18n")
    fun bind(monumento: MonumentoResponse, mediaPuntuacion: MediaRespones) {
        if (monumento.imagen.isNullOrEmpty()) {
            binding.ivMonumento.setImageResource(R.drawable.capricho)
        } else {
            val imageResourceId = getImageResourceId(itemView.context, monumento.imagen)
            if (imageResourceId != 0) {
                binding.ivMonumento.setImageResource(imageResourceId)
            } else {
                Log.d("ImageBinding", "Image resource not found for: ${monumento.imagen}")
                binding.ivMonumento.setImageResource(R.drawable.capricho)
            }
        }
        binding.tvNombreMonumento.text = monumento.nombre.uppercase()

        if (mediaPuntuacion != null) {
            ratingBarMonumento.rating = mediaPuntuacion.mediaPuntuacion.toFloat()
        } else {
            ratingBarMonumento.rating = 0f
        }

        if (monumento.visitable) {
            binding.tvVisitable.text = HtmlCompat.fromHtml("<b>Visitable: </b> Interior/Exterior", HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            binding.tvVisitable.text = HtmlCompat.fromHtml("<b>Visitable: </b> Exterior", HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        itemView.setOnClickListener {
            clickListener.onMonumentoClick(monumento)
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