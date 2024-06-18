package com.example.appturismo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.appturismo.databinding.ActivityPerfilBinding
import com.example.appturismo.model.ComentarioResponse
import com.example.appturismo.model.UsuarioResponse
import com.example.appturismo.service.ComentarioService
import com.example.appturismo.service.UsuarioService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PerfilActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPerfilBinding
    private lateinit var comentariosUsuario: List<ComentarioResponse>
    private lateinit var btnHome: ImageView
    private lateinit var btnCities: ImageView
    private lateinit var btnMonuments: ImageView
    private lateinit var btnProfile: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        comentariosUsuario = emptyList()
        val usuario: UsuarioResponse? = LoginActivity.UsuarioSingleton.usuarioResponse
        val nickname =  findViewById<EditText>(R.id.tvUsuario)
        val nombre = findViewById<EditText>(R.id.tvNombreEdit)
        val apellido1 = findViewById<EditText>(R.id.tvApellido1EdEdit)
        val apellido2 = findViewById<EditText>(R.id.tvApellido2EdEdit)
        val edit = findViewById<ImageView>(R.id.ivEditar)
        val logout = findViewById<ImageView>(R.id.ivLogOut)
        usuario?.let { getComentariosUsuario(it.id, this) }
        usuario?.let {
            nickname.setText(it.nickname)
            nombre.setText(it.nombre)
            apellido1.setText(it.apellido1)
            apellido2.setText(it.apellido2)
        }
        val btnGuardar = findViewById<Button>(R.id.btn_guardar)
        val btnCancel = findViewById<Button>(R.id.btn_cancelar)
        val listaEditText: List<EditText> = listOf(
            nickname,
            nombre,
            apellido1,
            apellido2
        )

        logout.setOnClickListener {
           LoginActivity.UsuarioSingleton.usuarioResponse = null
            val intent = Intent(this, InicioActivity::class.java)
            startActivity(intent)
            finish()
        }

        edit.setOnClickListener {
            listaEditText.forEach { editText ->
                editText.isEnabled = true
                editText.requestFocus()
            }
            btnGuardar.visibility = View.VISIBLE
            btnGuardar.isEnabled = true

            btnCancel.visibility = View.VISIBLE
            btnCancel.isEnabled = true
        }

        btnCancel.setOnClickListener{
            usuario?.let {
                nickname.setText(it.nickname)
                nombre.setText(it.nombre)
                apellido1.setText(it.apellido1)
                apellido2.setText(it.apellido2)
            }
            listaEditText.forEach { editText ->
                editText.isEnabled = false
                editText.clearFocus()
            }
            btnGuardar.visibility = View.INVISIBLE
            btnGuardar.isEnabled = false

            btnCancel.visibility = View.INVISIBLE
            btnCancel.isEnabled = false
        }

        btnGuardar.setOnClickListener{
            if (usuario != null) {
                usuario.nickname = nickname.text.toString()
                usuario.nombre = nombre.text.toString()
                usuario.apellido1 = apellido1.text.toString()
                usuario.apellido2 = apellido2.text.toString()
                guardarUsuario(usuario)
            }
            listaEditText.forEach { editText ->
                editText.isEnabled = false
                editText.clearFocus()
            }
        }

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
            startActivity(Intent(this, MonumentosActivity::class.java))
        }

        btnProfile.setOnClickListener {
        }

        val bottomToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(bottomToolbar)

        supportActionBar?.title = "Perfil de usuario"

        bottomToolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))

    }
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://172.201.248.10:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getComentariosUsuario(usuarioId: Long, context: Context) {
        val service = getRetrofit().create(ComentarioService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getComentariosPorUsuario(usuarioId)
            if (response.isSuccessful) {
                comentariosUsuario = response.body() ?: emptyList()
                runOnUiThread {
                    findViewById<TextView>(R.id.tvNumeroComentarios).text = "Comentarios: ${comentariosUsuario.size}"
                }
            } else {
                runOnUiThread {
                    Toast.makeText(context, "Error al cargar los comentarios del usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun guardarUsuario(usuario: UsuarioResponse) {
        val service = getRetrofit().create(UsuarioService::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.updateUsuario(usuario.id, usuario)
            runOnUiThread {
                if (response.isSuccessful) {
                    Toast.makeText(this@PerfilActivity, "Usuario actualizado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@PerfilActivity, "Error al actualizar el usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}