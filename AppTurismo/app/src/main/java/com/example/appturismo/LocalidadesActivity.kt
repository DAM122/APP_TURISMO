package com.example.appturismo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appturismo.adapter.LocalidadAdapter
import com.example.appturismo.databinding.ActivityLocalidadesBinding
import com.example.appturismo.model.LocalidadResponse
import com.example.appturismo.service.LocalidadService
import com.example.appturismo.utils.OnLocalidadClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LocalidadesActivity : AppCompatActivity(), SearchView.OnQueryTextListener,
    OnLocalidadClickListener {
    private lateinit var binding: ActivityLocalidadesBinding
    private lateinit var localidadAdapter: LocalidadAdapter
    private lateinit var allLocalidades: List<LocalidadResponse>
    private lateinit var rvLocalidades: RecyclerView
    private lateinit var btnHome: ImageView
    private lateinit var btnCities: ImageView
    private lateinit var btnMonuments: ImageView
    private lateinit var btnProfile: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalidadesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchView = findViewById<SearchView>(R.id.svLocalidades)
        searchView.setOnQueryTextListener(this)

        rvLocalidades = findViewById(R.id.rvLocalidades)
        rvLocalidades.layoutManager = LinearLayoutManager(this)
        localidadAdapter = LocalidadAdapter(emptyList(), this)
        rvLocalidades.adapter = localidadAdapter
        allLocalidades = emptyList()
        getLocalidades()
        btnHome = binding.customToolbar.btnHome
        btnCities = binding.customToolbar.btnCities
        btnMonuments = binding.customToolbar.btnMonuments
        btnProfile = binding.customToolbar.btnProfile
        btnHome.setOnClickListener {
            startActivity(Intent(this, InicioActivity::class.java))
        }

        btnCities.setOnClickListener {
        }

        btnMonuments.setOnClickListener {
            startActivity(Intent(this, MonumentosActivity::class.java))
        }

        btnProfile.setOnClickListener {
            if (LoginActivity.UsuarioSingleton.usuarioResponse == null)
                startActivity(Intent(this, LoginActivity::class.java))
            else
                startActivity(Intent(this, PerfilActivity::class.java))
        }
        val bottomToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(bottomToolbar)

        val nuevoTitulo = "Localidades"
        supportActionBar?.title = nuevoTitulo

        bottomToolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://172.201.248.10:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getLocalidades() {
        val service = getRetrofit().create(LocalidadService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getLocalidades()
            runOnUiThread {
                if (response.isSuccessful) {
                    allLocalidades = response.body() ?: emptyList()
                    localidadAdapter.updateData(allLocalidades)
                } else {
                    showError()
                }
            }
        }
    }

    private fun showError(message: String = "Error al cargar las localidades") {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { searchByName(it) }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let { searchByName(it) }
        return true
    }

    private fun searchByName(query: String) {
        val filteredList = allLocalidades.filter { it.nombre.contains(query, ignoreCase = true) }
        localidadAdapter.updateData(filteredList)
    }

    override fun onLocalidadClick(localidad: LocalidadResponse) {
        val intent = Intent(this, MonumentosActivity::class.java)
        intent.putExtra("localidad", localidad)
        startActivity(intent)
    }
}