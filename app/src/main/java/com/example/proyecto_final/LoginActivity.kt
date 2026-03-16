package com.example.proyecto_final

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.example.proyecto_final.data.HabiTrackDatabase
import com.example.proyecto_final.utils.TemaUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : Activity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var btnMainAction: Button
    private lateinit var imgLogo: ImageView
    private lateinit var cabecera: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.iniciar_sesion)

        inicializarVistas()
        aplicarTemaBotones()
        configurarBotones()
    }

    private fun inicializarVistas() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        btnMainAction = findViewById(R.id.btnMainAction)
        imgLogo = findViewById(R.id.imgLogo)
        cabecera = findViewById(R.id.imgLogo) // no hay cabecera, usamos el logo como referencia
    }

    private fun aplicarTemaBotones() {
        TemaUtils.aplicarTemaBoton(this, btnLogin)
        TemaUtils.aplicarTemaBoton(this, btnRegister)
        TemaUtils.aplicarTemaBoton(this, btnMainAction)
    }

    private fun configurarBotones() {

        // Botón superior "Iniciar sesión"
        btnLogin.setOnClickListener {
            btnMainAction.text = "Iniciar Sesión"
        }

        // Botón superior "Registrarse"
        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Botón principal
        btnMainAction.setOnClickListener {
            iniciarSesion()
        }
    }

    private fun iniciarSesion() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Introduce email y contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        val db = HabiTrackDatabase.getInstance(this)
        val usuarioDao = db.usuarioDao()

        CoroutineScope(Dispatchers.IO).launch {
            val usuario = usuarioDao.login(email, password)

            runOnUiThread {
                if (usuario == null) {
                    Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                } else {
                    guardarSesion(usuario.idUsuario, usuario.colorTema)
                    navegarAMain()
                }
            }
        }
    }

    private fun guardarSesion(idUsuario: Int, colorTema: String) {
        val prefs = getSharedPreferences("habitrack_prefs", MODE_PRIVATE)
        prefs.edit()
            .putBoolean("sesion_iniciada", true)
            .putInt("id_usuario_actual", idUsuario)
            .putString("color_tema_actual", colorTema)
            .apply()
    }

    private fun navegarAMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
