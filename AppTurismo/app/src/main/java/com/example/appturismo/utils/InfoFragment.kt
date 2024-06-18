package com.example.appturismo.utils

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.example.appturismo.R
import com.example.appturismo.model.ArtistaResponse
import com.example.appturismo.model.MediaRespones
import com.example.appturismo.model.MonumentoResponse
import com.example.appturismo.service.ComentarioService
import com.example.appturismo.service.MonumentoArtistaService
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InfoFragment : Fragment() {

    private lateinit var monumento: MonumentoResponse
    private lateinit var tvDescripcion: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            monumento = it.getParcelable("monumento")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_informacion, container, false)
        tvDescripcion = view.findViewById(R.id.tvDescripcion)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvDescripcion.text = monumento.descripcion
    }

    companion object {
        @JvmStatic
        fun newInstance(monumento: MonumentoResponse) =
            InfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("monumento", monumento)
                }
            }
    }
}