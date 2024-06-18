package com.example.appturismo

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.appturismo.databinding.ActivityLocalidadesBinding
import com.example.appturismo.databinding.FragmentInformacionBinding
import com.example.appturismo.model.MonumentoResponse
import com.example.appturismo.model.ArtistaResponse
import com.example.appturismo.model.MediaRespones
import com.example.appturismo.service.ComentarioService
import com.example.appturismo.service.MonumentoArtistaService
import com.example.appturismo.utils.InfoPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InfoActivity : AppCompatActivity() {
    private lateinit var monumento: MonumentoResponse
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_monumento)

        monumento = intent.getParcelableExtra("monumento")!!

        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val ivMonumento: ImageView = findViewById(R.id.ivMonumento)
        val tvNombreMonumento: TextView = findViewById(R.id.tvNombreMonumento)
        val adapter = InfoPagerAdapter(supportFragmentManager, monumento)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        adapter.setTabWidth(tabLayout)
        ivMonumento.setImageResource(getImageResourceId(this, monumento.imagen))
        tvNombreMonumento.text = HtmlCompat.fromHtml("<b>${monumento.nombre}</b>", HtmlCompat.FROM_HTML_MODE_LEGACY)

        getMediaPuntuacion(monumento.id)
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.setOnClickListener {
            onBackPressed()
        }
        val bottomToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(bottomToolbar)

        supportActionBar?.title = monumento.nombre

        bottomToolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))

    }
    private fun getImageResourceId(context: Context, imageName: String?): Int {
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

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://172.201.248.10:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getMediaPuntuacion(idMonumento: Long) {
        val service = getRetrofit().create(ComentarioService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getMediaPuntuacionComentarios()
                if (response.isSuccessful) {
                    val mediaPuntuacionListRaw: List<Array<Any>>? = response.body()
                    runOnUiThread {
                        if (!mediaPuntuacionListRaw.isNullOrEmpty()) {
                            val mediaPuntuacionList = mediaPuntuacionListRaw.mapNotNull {
                                val monumentoMap = it[0] as? LinkedTreeMap<*, *>
                                val mediaPuntuacion = (it[1] as? Number)?.toDouble()
                                if (monumentoMap != null && mediaPuntuacion != null) {
                                    val monumentoJson = Gson().toJson(monumentoMap)
                                    val monumento = Gson().fromJson(monumentoJson, MonumentoResponse::class.java)
                                    MediaRespones(monumento, mediaPuntuacion)
                                } else {
                                    null
                                }
                            }
                            if (mediaPuntuacionList.isNotEmpty()) {
                                val mediaPuntuacion = mediaPuntuacionList.firstOrNull { it.monumento.id == idMonumento }
                                if (mediaPuntuacion != null) {
                                    val ratingBar: RatingBar = findViewById(R.id.ratingBar)
                                    ratingBar.rating = mediaPuntuacion.mediaPuntuacion.toFloat()
                                } else {
                                    Log.e("DetalleMonumento", "No se encontró la media puntuación para el monumento con ID: $idMonumento")
                                }
                            } else {
                                Log.e("DetalleMonumento", "Lista de media puntuación vacía o nula")
                            }
                        } else {
                            Log.e("DetalleMonumento", "Lista de media puntuación vacía o nula")
                        }
                    }
                } else {
                    Log.e("DetalleMonumento", "Error al obtener la lista de media de puntuación: ${response.message()}")
                    Log.e("DetalleMonumento", "Error body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("DetalleMonumento", "Excepción al obtener la lista de media de puntuación", e)
                e.printStackTrace()
            }
        }
    }
}