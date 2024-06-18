//package com.example.appturismo
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.Menu
//import android.view.MenuItem
//import android.widget.ImageView
//import android.widget.RatingBar
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.text.HtmlCompat
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.appturismo.adapter.ComentarioAdapter
//import com.example.appturismo.adapter.MonumentoAdapter
//import com.example.appturismo.model.MonumentoResponse
//import com.example.appturismo.model.ArtistaResponse
//import com.example.appturismo.model.ComentarioResponse
//import com.example.appturismo.model.MediaRespones
//import com.example.appturismo.service.ComentarioService
//import com.example.appturismo.service.MonumentoArtistaService
//import com.example.appturismo.utils.ComentarioEnviadoListener
//import com.google.gson.Gson
//import com.google.gson.internal.LinkedTreeMap
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.create
//
//class DetalleMonumentoActivity : AppCompatActivity(), ComentarioEnviadoListener {
//    private lateinit var allComentarios: List<ComentarioResponse>
//    private lateinit var comentarioAdapter: ComentarioAdapter
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.layout_monumento_detalle)
//        val monumento: MonumentoResponse? = intent.getParcelableExtra("monumento")
//        val nombreMonumento = monumento?.nombre ?: "Nombre no disponible"
//        getArtistaFromMonumentoId(monumento?.id ?: 1) { artista ->
//            runOnUiThread {
//                val tvArtista: TextView = findViewById(R.id.tvArtista)
//                tvArtista.text = HtmlCompat.fromHtml("<b>Autor:</b> ${artista?.nombre ?: "Nombre no disponible"}", HtmlCompat.FROM_HTML_MODE_LEGACY)
//            }
//        }
//        val ivMonumento: ImageView = findViewById(R.id.ivMonumento)
//        if (monumento != null && !monumento.imagen.isNullOrEmpty()) {
//            val imageResourceId = getImageResourceId(this, monumento.imagen)
//
//            if (imageResourceId != 0) {
//                ivMonumento.setImageResource(imageResourceId)
//            } else {
//                Log.e("ImageBinding", "Error getting image")
//            }
//        } else {
//            Log.e("ImageBinding", "Monumento o Imagen nulos")
//        }
//        val tvNombreMonumento: TextView = findViewById(R.id.tvNombreMonumento)
//        tvNombreMonumento.text = HtmlCompat.fromHtml("<b>Nombre:</b> $nombreMonumento", HtmlCompat.FROM_HTML_MODE_LEGACY)
//
//        if (monumento != null) {
//            getMediaPuntuacion(monumento.id)
//        }
//        val tvDescripcionMonumento: TextView = findViewById((R.id.tvDescripcionMonumento))
//        tvDescripcionMonumento.text = HtmlCompat.fromHtml("<b>Descripcion: </b> ${monumento?.descripcion ?: "No se conoce el contexto historico de este monumento"}", HtmlCompat.FROM_HTML_MODE_LEGACY)
//
//        val bottomToolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(bottomToolbar)
//        bottomToolbar.inflateMenu(R.menu.bottom_menu)
//
//        bottomToolbar.setOnMenuItemClickListener { menuItem ->
//            handleMenuItemClick(menuItem)
//        }
//        val recyclerView = findViewById<RecyclerView>(R.id.rvComentarios)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        comentarioAdapter = ComentarioAdapter(emptyList())
//        recyclerView.adapter = comentarioAdapter
//        getComentarios(monumento?.id ?: 0)
//
//        val tvComentar: TextView = findViewById(R.id.tvComentar)
//
//        tvComentar.setOnClickListener {
//            val intent = Intent(this, NuevoComentarioActivity::class.java)
//            intent.putExtra("monumento", monumento)
//            startActivity(intent)
//        }
//
//    }
//
//    private fun getImageResourceId(context: Context, imageName: String?): Int {
//        if (imageName.isNullOrEmpty()) {
//            return 0
//        }
//        return try {
//            val resName = imageName.substringBeforeLast(".")
//            val resType = "drawable"
//            context.resources.getIdentifier(resName, resType, context.packageName)
//        } catch (e: Exception) {
//            Log.e("ImageBinding", "Error getting image resource ID for: $imageName", e)
//            0
//        }
//    }
//
//    private fun getRetrofit(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl("http://10.0.2.2:8080/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    fun getArtistaFromMonumentoId(monumentoId: Long?, onComplete: (ArtistaResponse?) -> Unit) {
//        val id = monumentoId ?: -1
//        val service = getRetrofit().create(MonumentoArtistaService::class.java)
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = service.getMonumentoArtistaByMonumentoId(id)
//            if (response.isSuccessful) {
//                val monumentoArtista = response.body()
//                val artista = monumentoArtista?.artista
//                onComplete(artista)
//            } else {
//                Log.e("MonumentoArtista", "Failed to get response")
//                onComplete(null)
//            }
//        }
//    }
//
//    private fun getComentarios(monumentoId: Long) {
//        val service = getRetrofit().create(ComentarioService::class.java)
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = service.getComentarios()
//            runOnUiThread {
//                if (response.isSuccessful) {
//                    allComentarios = response.body() ?: emptyList()
//                    val filteredComentarios = allComentarios.filter { it.monumento.id == monumentoId }
//                    comentarioAdapter.updateData(filteredComentarios)
//                } else {
//                    showError()
//                }
//            }
//        }
//    }
//
//    private fun getMediaPuntuacion(idMonumento: Long) {
//        val service = getRetrofit().create(ComentarioService::class.java)
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = service.getMediaPuntuacionComentarios()
//                if (response.isSuccessful) {
//                    val mediaPuntuacionListRaw: List<Array<Any>>? = response.body()
//                    runOnUiThread {
//                        if (!mediaPuntuacionListRaw.isNullOrEmpty()) {
//                            val mediaPuntuacionList = mediaPuntuacionListRaw.mapNotNull {
//                                val monumentoMap = it[0] as? LinkedTreeMap<*, *>
//                                val mediaPuntuacion = (it[1] as? Number)?.toDouble()
//                                if (monumentoMap != null && mediaPuntuacion != null) {
//                                    val monumentoJson = Gson().toJson(monumentoMap)
//                                    val monumento = Gson().fromJson(monumentoJson, MonumentoResponse::class.java)
//                                    MediaRespones(monumento, mediaPuntuacion)
//                                } else {
//                                    null
//                                }
//                            }
//                            if (mediaPuntuacionList.isNotEmpty()) {
//                                val mediaPuntuacion = mediaPuntuacionList.firstOrNull { it.monumento.id == idMonumento }
//                                if (mediaPuntuacion != null) {
//                                    val ratingBar: RatingBar = findViewById(R.id.ratingBar)
//                                    ratingBar.rating = mediaPuntuacion.mediaPuntuacion.toFloat()
//                                } else {
//                                    Log.e("DetalleMonumento", "No se encontró la media puntuación para el monumento con ID: $idMonumento")
//                                }
//                            } else {
//                                Log.e("DetalleMonumento", "Lista de media puntuación vacía o nula")
//                            }
//                        } else {
//                            Log.e("DetalleMonumento", "Lista de media puntuación vacía o nula")
//                        }
//                    }
//                } else {
//                    Log.e("DetalleMonumento", "Error al obtener la lista de media de puntuación: ${response.message()}")
//                    Log.e("DetalleMonumento", "Error body: ${response.errorBody()?.string()}")
//                }
//            } catch (e: Exception) {
//                Log.e("DetalleMonumento", "Excepción al obtener la lista de media de puntuación", e)
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private fun handleMenuItemClick(menuItem: MenuItem): Boolean {
//        when (menuItem.itemId) {
//            R.id.action_back -> {
//                onBackPressed()
//                return true
//            }
//            else -> return false
//        }
//    }
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.bottom_menu, menu)
//        return true
//    }
//
//    override fun onComentarioEnviado(monumento: MonumentoResponse?) {
//        val monumento: MonumentoResponse? = intent.getParcelableExtra("monumento")
//        getComentarios(monumento?.id ?: 0)
//    }
//
//    private fun showError(mensaje: String = "Error al cargar los comentarios") {
//        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
//    }
//}