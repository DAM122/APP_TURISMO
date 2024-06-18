package com.example.appturismo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.appturismo.model.Perfil
import com.example.appturismo.model.UsuarioResponse
import com.example.appturismo.service.PerfilService
import com.example.appturismo.service.UsuarioService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegistroActivity : AppCompatActivity() {

    private lateinit var usuarioService: UsuarioService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val etNickname = findViewById<EditText>(R.id.etNickname)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido1 = findViewById<EditText>(R.id.etApellido1)
        val etApellido2 = findViewById<EditText>(R.id.etApellido2)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        usuarioService = getRetrofit().create(UsuarioService::class.java)

        btnRegistrar.setOnClickListener {
            val nickname = etNickname.text.toString()
            val nombre = etNombre.text.toString()
            val apellido1 = etApellido1.text.toString()
            val apellido2 = etApellido2.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (nickname.isEmpty() || nombre.isEmpty() || apellido1.isEmpty() || apellido2.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    val perfil = getPerfil() // Obtenemos el perfil
                    if (perfil != null) {
                        val usuario = UsuarioResponse(
                            id = 0,
                            nickname = nickname,
                            nombre = nombre,
                            apellido1 = apellido1,
                            apellido2 = apellido2,
                            password = password,
                            perfil = perfil
                        )
                        registrarUsuario(usuario) // Registramos el usuario una vez obtenido el perfil
                    } else {
                        Toast.makeText(this@RegistroActivity, "Error al obtener el perfil", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.setOnClickListener {
            onBackPressed()
        }
        val bottomToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(bottomToolbar)

        supportActionBar?.title = "Crear nuevo usuario"

        bottomToolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://172.201.248.10:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun registrarUsuario(usuario: UsuarioResponse) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = usuarioService.createUsuario(usuario)
            runOnUiThread {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegistroActivity, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@RegistroActivity, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun getPerfil(): Perfil? {
        val service = getRetrofit().create(PerfilService::class.java)
        val response = service.getPerfilById(2)
        return if (response.isSuccessful) {
            val perfil = response.body()
            perfil?.apply {
                id = perfil.id ?: -1
            }
            perfil
        } else {
            Toast.makeText(this@RegistroActivity, "Error al obtener el perfil", Toast.LENGTH_SHORT).show()
            null
        }
    }
}