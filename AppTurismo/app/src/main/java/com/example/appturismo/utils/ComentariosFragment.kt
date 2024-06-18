package com.example.appturismo.utils

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appturismo.LoginActivity
import com.example.appturismo.NuevoComentarioActivity
import com.example.appturismo.R
import com.example.appturismo.adapter.ComentarioAdapter
import com.example.appturismo.model.ComentarioResponse
import com.example.appturismo.model.MonumentoResponse
import com.example.appturismo.service.ComentarioService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ComentariosFragment : Fragment() , ComentarioEnviadoListener{
    private lateinit var allComentarios: List<ComentarioResponse>
    private lateinit var monumento: MonumentoResponse
    private lateinit var comentarioAdapter: ComentarioAdapter
    private lateinit var recyclerView: RecyclerView

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
        return inflater.inflate(R.layout.fragment_comentarios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fabComentario = view.findViewById<FloatingActionButton>(R.id.fabComentario)
        fabComentario?.setOnClickListener {
            if (LoginActivity.UsuarioSingleton.usuarioResponse != null) {
                val intent = Intent(activity, NuevoComentarioActivity::class.java)
                intent.putExtra("monumento", monumento)
                startActivity(intent)
            } else {
                Toast.makeText(context, "Debes iniciar sesión para agregar un comentario", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        comentarioAdapter = ComentarioAdapter(emptyList())
        recyclerView.adapter = comentarioAdapter

        getComentarios(monumento.id)
    }

    private fun getComentarios(monumentoId: Long) {
        val service = getRetrofit().create(ComentarioService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getComentarios()
            if (response.isSuccessful) {
                allComentarios = response.body() ?: emptyList()
                val filteredComentarios = allComentarios.filter { it.monumento.id == monumentoId }
                activity?.runOnUiThread {
                    comentarioAdapter.updateData(filteredComentarios)
                }
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error al cargar los comentarios", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://172.201.248.10:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {
        @JvmStatic
        fun newInstance(monumento: MonumentoResponse) =
            ComentariosFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("monumento", monumento)
                }
            }
    }

    override fun onComentarioEnviado(monumento: MonumentoResponse?) {
        getComentarios(this.monumento.id)
    }
}