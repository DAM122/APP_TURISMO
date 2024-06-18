package com.example.appturismo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appturismo.adapter.MonumentoAdapter
import com.example.appturismo.databinding.MonumentosBinding
import com.example.appturismo.model.LocalidadResponse
import com.example.appturismo.model.MediaRespones
import com.example.appturismo.model.MonumentoResponse
import com.example.appturismo.service.ComentarioService
import com.example.appturismo.service.MonumentoService
import com.example.appturismo.utils.OnMonumentoClickListener
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MonumentosActivity : AppCompatActivity(), SearchView.OnQueryTextListener, OnMonumentoClickListener {
    private lateinit var binding: MonumentosBinding
    private lateinit var monumentoAdapter: MonumentoAdapter
    private lateinit var allMonumentos: List<MonumentoResponse>
    private lateinit var btnHome: ImageView
    private lateinit var btnCities: ImageView
    private lateinit var btnMonuments: ImageView
    private lateinit var btnProfile: ImageView
    private lateinit var rvMonumentos: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MonumentosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchView = findViewById<SearchView>(R.id.svMonumentos)
        searchView.setOnQueryTextListener(this)

        rvMonumentos = findViewById(R.id.rvMonumentos)
        rvMonumentos.layoutManager = LinearLayoutManager(this)
        monumentoAdapter = MonumentoAdapter(emptyList(),this)
        rvMonumentos.adapter = monumentoAdapter
        allMonumentos = emptyList()
        val localidad: LocalidadResponse? = intent.getParcelableExtra("localidad")
        val bottomToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(bottomToolbar)
        if (localidad != null) {
            getMonumentosByLocalidad(localidad.id)
            supportActionBar?.title = "Monumentos de ${localidad.nombre}"


        } else {
            getMonumentos()
            supportActionBar?.title = "Monumentos"
        }

        val idsMonumentos = mutableListOf<Long>()

        for (monumento in allMonumentos) {
            idsMonumentos.add(monumento.id)
        }
        bottomToolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        getMediaPuntuacion()

        btnHome = binding.customToolbar.btnHome
        btnCities = binding.customToolbar.btnCities
        btnMonuments = binding.customToolbar.btnMonuments
        btnProfile = binding.customToolbar.btnProfile
        btnHome.setOnClickListener {
            startActivity(Intent(this, InicioActivity::class.java))
        }

        btnCities.setOnClickListener {
            startActivity(Intent(this, LocalidadesActivity::class.java))
        }

        btnMonuments.setOnClickListener {
        }

        btnProfile.setOnClickListener {
            if (LoginActivity.UsuarioSingleton.usuarioResponse == null)
                startActivity(Intent(this, LoginActivity::class.java))
            else
                startActivity(Intent(this, PerfilActivity::class.java))
        }

    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://172.201.248.10:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getMonumentos() {
        val service = getRetrofit().create(MonumentoService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getMonumentos()
            runOnUiThread {
                if (response.isSuccessful) {
                    allMonumentos = response.body() ?: emptyList()
                    monumentoAdapter.updateData(allMonumentos)
                } else {
                    showError()
                }
            }
        }
    }

    private fun getMonumentosByLocalidad(localidadId: Long) {
        val service = getRetrofit().create(MonumentoService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getMonumentosByLocalidadId(localidadId)
                runOnUiThread {
                    if (response.isSuccessful) {
                        allMonumentos = response.body() ?: emptyList()
                        monumentoAdapter.updateData(allMonumentos)
                    } else {
                        showError()
                    }
                }
            } catch (e: Exception) {
                Log.e("MonumentosActivity", "Error al obtener monumentos por localidad: $e")
                showError("Error al obtener monumentos por localidad")
            }
        }
    }

    private fun getMediaPuntuacion() {
        val service = getRetrofit().create(ComentarioService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getMediaPuntuacionComentarios()
                if (response.isSuccessful) {
                    val mediaPuntuacionListRaw: List<Array<Any>>? = response.body()
                    val monumentosConPuntuacion = mutableListOf<MediaRespones>()

                    // Obtener los monumentos con puntuación
                    if (!mediaPuntuacionListRaw.isNullOrEmpty()) {
                        mediaPuntuacionListRaw.forEach { item ->
                            val monumentoMap = item[0] as? LinkedTreeMap<*, *>
                            val mediaPuntuacion = (item[1] as? Number)?.toDouble()

                            if (monumentoMap != null && mediaPuntuacion != null) {
                                val monumentoJson = Gson().toJson(monumentoMap)
                                val monumento = Gson().fromJson(monumentoJson, MonumentoResponse::class.java)
                                monumentosConPuntuacion.add(MediaRespones(monumento, mediaPuntuacion))
                            }
                        }
                    }

                    runOnUiThread {
                        // Actualizar el adaptador con los monumentos que tienen puntuación
                        (rvMonumentos.adapter as? MonumentoAdapter)?.updateMediaPuntuacion(monumentosConPuntuacion)

                        // Obtener los monumentos sin puntuación
                        val monumentosConYPuntuacion = monumentosConPuntuacion.map { it.monumento.id }
                        val monumentosSinPuntuacion = allMonumentos.filter { monumento ->
                            monumento.id !in monumentosConYPuntuacion
                        }

                        // Crear una lista con puntuación 0.0 para los monumentos sin comentarios
                        val monumentosSinPuntuacionConCero = monumentosSinPuntuacion.map {
                            MediaRespones(it, 0.0)
                        }

                        // Actualizar el adaptador con todos los monumentos (con y sin puntuación)
                        val fullList = mutableListOf<MediaRespones>()
                        fullList.addAll(monumentosConPuntuacion)
                        fullList.addAll(monumentosSinPuntuacionConCero)
                        (rvMonumentos.adapter as? MonumentoAdapter)?.updateMediaPuntuacion(fullList)
                    }
                } else {
                    Log.e("MonumentosActivity", "Error al obtener la lista de media de puntuación: ${response.message()}")
                    Log.e("MonumentosActivity", "Error body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("MonumentosActivity", "Excepción al obtener la lista de media de puntuación", e)
                e.printStackTrace()
            }
        }
    }

    private fun searchByName(query: String) {
        val filteredList = allMonumentos.filter { it.nombre.contains(query, ignoreCase = true) }
        monumentoAdapter.updateData(filteredList)
    }

    private fun showError(mensaje: String = "Error al cargar los monumentos") {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (!query.isNullOrEmpty()) {
            searchByName(query)
            hideKeyboard()
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (!newText.isNullOrEmpty()) {
            val filteredList = allMonumentos.filter { it.nombre.contains(newText, ignoreCase = true) }
            monumentoAdapter.updateData(filteredList)
        } else {
            monumentoAdapter.updateData(allMonumentos)
        }
        return true
    }

    override fun onMonumentoClick(monumento: MonumentoResponse) {
//        val intent = Intent(this, DetalleMonumentoActivity::class.java)
        val intent = Intent(this, InfoActivity::class.java)
        intent.putExtra("monumento", monumento)
        startActivity(intent)
    }
}
