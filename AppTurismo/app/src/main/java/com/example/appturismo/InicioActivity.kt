package com.example.appturismo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appturismo.adapter.ListaInicioAdapter
import com.example.appturismo.databinding.InicioBinding
import com.example.appturismo.model.MonumentoResponse
import com.example.appturismo.service.MonumentoService
import com.example.appturismo.utils.OnMonumentoClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class InicioActivity : AppCompatActivity(), SearchView.OnQueryTextListener, OnMonumentoClickListener {
    private lateinit var binding: InicioBinding
    private var isTyping = false
    private lateinit var listaInicioAdapter: ListaInicioAdapter
    private var allMonumentos: List<MonumentoResponse> = emptyList()
    private lateinit var btnHome: ImageView
    private lateinit var btnCities: ImageView
    private lateinit var btnMonuments: ImageView
    private lateinit var btnProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = InicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnHome = binding.customToolbar.btnHome
        btnCities = binding.customToolbar.btnCities
        btnMonuments = binding.customToolbar.btnMonuments
        btnProfile = binding.customToolbar.btnProfile

        listaInicioAdapter = ListaInicioAdapter(allMonumentos, this)
        binding.rvMonumentos.layoutManager = LinearLayoutManager(this)
        binding.rvMonumentos.adapter = listaInicioAdapter
        binding.rvMonumentos.adapter = listaInicioAdapter

        val searchView = findViewById<SearchView>(R.id.svMonumentos)
        searchView.setOnQueryTextListener(this)

        btnHome.setOnClickListener {

        }

        btnCities.setOnClickListener {
            startActivity(Intent(this, LocalidadesActivity::class.java))
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

        getMonumentos()
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
                } else {
                    showError()
                }
            }
        }
    }

    private fun filterMonumentos(query: String) {
        val filteredList = allMonumentos.filter { monumento ->
            monumento.nombre.contains(query, ignoreCase = true)
        }
        listaInicioAdapter.updateData(filteredList)
    }

    private fun showError() {
        Toast.makeText(this, "Error al obtener los monumentos", Toast.LENGTH_SHORT).show()
    }

    override fun onMonumentoClick(monumento: MonumentoResponse) {
        val intent = Intent(this, InfoActivity::class.java)
        intent.putExtra("monumento", monumento)
        startActivity(intent)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        hideKeyboard()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val recyclerView = findViewById<RecyclerView>(R.id.rvMonumentos)
        isTyping = !newText.isNullOrEmpty()
        updateSearchViewShape()
        if (newText != null) {
            filterMonumentos(newText)
            recyclerView.visibility = if (newText.isEmpty()) {
                RecyclerView.GONE
            } else {
                RecyclerView.VISIBLE
            }
        }
        return true
    }
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun updateSearchViewShape() {
        val searchView = findViewById<SearchView>(R.id.svMonumentos)
        if (isTyping) {
            searchView.setBackgroundResource(R.drawable.border_rectangle)
        } else {
            searchView.setBackgroundResource(R.drawable.border_rounded_corner)
        }
    }
}
