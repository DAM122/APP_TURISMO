package com.example.appturismo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.appturismo.databinding.ActivityMainBinding
import com.example.appturismo.model.UsuarioResponse
import com.example.appturismo.service.UsuarioService
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var usuarioService: UsuarioService
    private lateinit var btnHome: ImageView
    private lateinit var btnCities: ImageView
    private lateinit var btnMonuments: ImageView
    private lateinit var btnProfile: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.201.248.10:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        usuarioService = retrofit.create(UsuarioService::class.java)

        val editTextUsuario = findViewById<EditText>(R.id.editTextNickname)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val tvRegistro = findViewById<TextView>(R.id.textViewRegister)

        buttonLogin.setOnClickListener {
            val usuario = editTextUsuario.text.toString()
            val contraseña = editTextPassword.text.toString()

            login(usuario, contraseña)
        }

        tvRegistro.setOnClickListener{
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
            finish()
        }

        val bottomToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(bottomToolbar)

        val nuevoTitulo = "Iniciar sesion"
        supportActionBar?.title = nuevoTitulo

        bottomToolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.white))

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
            if (LoginActivity.UsuarioSingleton.usuarioResponse == null)
                startActivity(Intent(this, LoginActivity::class.java))
            else
                startActivity(Intent(this, PerfilActivity::class.java))
        }

    }
    object UsuarioSingleton {
        var usuarioResponse: UsuarioResponse? = null
    }

    private fun login(usuario: String, contraseña: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = usuarioService.authenticate(usuario, contraseña)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val usuarioResponse: UsuarioResponse? = response.body()
                        if (usuarioResponse != null) {
                            UsuarioSingleton.usuarioResponse = usuarioResponse
                            iniciarActividadMonumentos()
                        } else {
                            mostrarMensajeError("Credenciales incorrectas")
                        }
                    } else {
                        mostrarMensajeError("Error en la solicitud")
                    }
                }
            } catch (e: Exception) {
                mostrarMensajeError("Error de red: ${e.message}")
            }
        }
    }

    private fun iniciarActividadMonumentos() {
//        val intent = Intent(this, MonumentosActivity::class.java)
        val intent = Intent(this, InicioActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun mostrarMensajeError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}
