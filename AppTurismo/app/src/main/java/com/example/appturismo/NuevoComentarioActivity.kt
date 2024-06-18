package com.example.appturismo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.appturismo.model.ComentarioResponse
import com.example.appturismo.model.MonumentoResponse
import com.example.appturismo.service.ComentarioService
import com.example.appturismo.utils.ComentarioEnviadoListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NuevoComentarioActivity: AppCompatActivity() {
    private lateinit var comentarioService: ComentarioService
    private lateinit var comentarioEnviadoListener: ComentarioEnviadoListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nuevo_comentario)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.201.248.10:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        comentarioService = retrofit.create(ComentarioService::class.java)

        val etComentario = findViewById<EditText>(R.id.etComentario)
        val rbValoracion = findViewById<RatingBar>(R.id.rbValoracion)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)
        val monumento: MonumentoResponse? = intent.getParcelableExtra("monumento")
        val usuario = LoginActivity.UsuarioSingleton.usuarioResponse
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.setOnClickListener {
            onBackPressed()
        }
        val bottomToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(bottomToolbar)

        val nuevoTitulo = "Nuevo comentario"
        supportActionBar?.title = nuevoTitulo

        bottomToolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        btnEnviar.setOnClickListener {
            if (etComentario.text != null && !etComentario.text.toString().equals("")){
                val comentario = etComentario.text.toString()
                val valoracion = rbValoracion.rating

                val nuevoComentario = try {
                    ComentarioResponse(
                        id = null,
                        usuario = usuario ?: throw IllegalArgumentException("Usuario es nulo"),
                        monumento = monumento ?: throw IllegalArgumentException("Monumento es nulo"),
                        mensaje = comentario,
                        puntuacion = valoracion.toDouble()
                    )
                } catch (e: IllegalArgumentException) {
                    // Manejar la excepción
                    Log.e("NuevoComentarioActivity", "Error al crear el comentario: ${e.message}")
                    null
                }
                if (nuevoComentario != null) {
                    enviarComentario(nuevoComentario)
                }
            }else{
                Toast.makeText(this@NuevoComentarioActivity, "No se ha introducdo texto en el comentario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enviarComentario(nuevoComentario: ComentarioResponse) {
        val monumento: MonumentoResponse? = intent.getParcelableExtra("monumento")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = comentarioService.guardarComentario(nuevoComentario)
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@NuevoComentarioActivity, "Comentario enviado correctamente", Toast.LENGTH_SHORT).show()
                        comentarioEnviadoListener.onComentarioEnviado(monumento)
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@NuevoComentarioActivity, "Error al enviar el comentario", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@NuevoComentarioActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}