package com.example.appturismo.utils

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.example.appturismo.R
import com.example.appturismo.model.ArtistaResponse
import com.example.appturismo.model.MonumentoResponse
import com.example.appturismo.service.MonumentoArtistaService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FichaFragment : Fragment() {

    private lateinit var monumento: MonumentoResponse
    private lateinit var tvNombre: TextView
    private lateinit var tvLocalidad: TextView
    private lateinit var tvFecha: TextView
    private lateinit var tvArquitectos: TextView
    private lateinit var tvEstilos: TextView
    private lateinit var tvEstiloPrincipal: TextView
    private lateinit var tvInteriorVisitable: TextView
    private lateinit var tvParkingPrivado: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvArtista: TextView // TextView for displaying artist information

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
        val view = inflater.inflate(R.layout.fragment_ficha, container, false)
        // Initialize your TextViews
        tvNombre = view.findViewById(R.id.tvNombre)
        tvLocalidad = view.findViewById(R.id.tvLocalidad)
        tvFecha = view.findViewById(R.id.tvFecha)
        tvArquitectos = view.findViewById(R.id.tvArquitectos)
        tvEstilos = view.findViewById(R.id.tvEstilosArtisticos)
        tvInteriorVisitable = view.findViewById(R.id.tvInteriorVisitable)
        tvParkingPrivado = view.findViewById(R.id.tvParkingPrivado)
        tvTelefono = view.findViewById(R.id.tvTelefono)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvNombre.text = HtmlCompat.fromHtml("<b>Nombre:</b> ${monumento.nombre ?: "Nombre no disponible"}", HtmlCompat.FROM_HTML_MODE_LEGACY)
        tvLocalidad.text = HtmlCompat.fromHtml("<b>Localidad:</b> ${monumento.localidad?.nombre ?: "Localidad no disponible"}", HtmlCompat.FROM_HTML_MODE_LEGACY)
        tvFecha.text = HtmlCompat.fromHtml("<b>Fecha:</b> ${monumento.fecha ?: "Fecha no disponible"}", HtmlCompat.FROM_HTML_MODE_LEGACY)

        getArtistaFromMonumentoId(monumento.id) { artista ->
            requireActivity().runOnUiThread {
                tvArquitectos.text = HtmlCompat.fromHtml("<b>Arquitectos:</b> ${artista?.nombre ?: "Nombre no disponible"}", HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        }

        tvEstilos.text = HtmlCompat.fromHtml("<b>Estilo:</b> ${monumento.estilo ?: "No se dispone de este dato"}", HtmlCompat.FROM_HTML_MODE_LEGACY)
        tvInteriorVisitable.text = HtmlCompat.fromHtml("<b>Visitable:</b> ${if (monumento.visitable) "Interior/Exterior" else "Exterior"}", HtmlCompat.FROM_HTML_MODE_LEGACY)
        tvParkingPrivado.text = HtmlCompat.fromHtml("<b>Parking privado:</b> ${if (monumento.parking) "Si" else "No"}", HtmlCompat.FROM_HTML_MODE_LEGACY)
        tvTelefono.text = HtmlCompat.fromHtml("<b>Telefono:</b> ${monumento.telefono ?: "Teléfono no disponible"}", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://172.201.248.10:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getArtistaFromMonumentoId(monumentoId: Long?, onComplete: (ArtistaResponse?) -> Unit) {
        val id = monumentoId ?: -1
        val service = getRetrofit().create(MonumentoArtistaService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getMonumentoArtistaByMonumentoId(id)
            if (response.isSuccessful) {
                val monumentoArtista = response.body()
                val artista = monumentoArtista?.artista
                onComplete(artista)
            } else {
                Log.e("MonumentoArtista", "Failed to get response")
                onComplete(null)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(monumento: MonumentoResponse) =
            FichaFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("monumento", monumento)
                }
            }
    }
}
