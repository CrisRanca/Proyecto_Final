package com.example.proyecto_final

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.proyecto_final.data.HabiTrackDatabase
import com.example.proyecto_final.data.entities.UsuarioEntity
import com.example.proyecto_final.utils.TemaUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : Activity() {

    private lateinit var etNombre: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrarse)

        // Vincular views
        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnMainAction) // botón grande "Registrarse"
        btnLogin = findViewById(R.id.btnLogin)

        TemaUtils.aplicarTemaBoton(this, btnRegister)
        TemaUtils.aplicarTemaBoton(this, btnLogin)

        // Registro
        btnRegister.setOnClickListener {
            validarRegistro()
        }

        // Volver a login
        btnLogin.setOnClickListener {
            finish()
        }
    }

    private fun validarRegistro() {
        val nombre = etNombre.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validaciones
        if (nombre.isEmpty()) {
            etNombre.error = "Nombre requerido"
            return
        }
        if (nombre.length < 2) {
            etNombre.error = "Nombre muy corto"
            return
        }
        if (email.isEmpty()) {
            etEmail.error = "Email requerido"
            return
        }
        if (!isEmailValid(email)) {
            etEmail.error = "Email inválido"
            return
        }
        if (password.isEmpty()) {
            etPassword.error = "Contraseña requerida"
            return
        }
        if (password.length < 8) {
            etPassword.error = "Mínimo 8 caracteres"
            return
        }
        if (!hasUpperCase(password)) {
            etPassword.error = "1 mayúscula requerida"
            return
        }

        // Si todo es correcto, registramos en BD
        registrarEnBD(nombre, email, password)
    }

    private fun registrarEnBD(nombre: String, email: String, password: String) {
        val db = HabiTrackDatabase.getInstance(this)
        val usuarioDao = db.usuarioDao()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val nuevoUsuario = UsuarioEntity(
                    email = email,
                    password = password,
                    nombre = nombre,
                    fechaRegistro = System.currentTimeMillis()
                    // nivel, puntosTotales, activo, mascota, colorTema usan valores por defecto
                )

                val idUsuarioLong = usuarioDao.insertarUsuario(nuevoUsuario)

                runOnUiThread {
                    if (idUsuarioLong > 0) {
                        val prefs = getSharedPreferences("habitrack_prefs", MODE_PRIVATE)
                        prefs.edit()
                            .putBoolean("ya_registrado", true)
                            .putBoolean("sesion_iniciada", true)
                            .putInt("id_usuario_actual", idUsuarioLong.toInt())
                            // color_tema_actual inicial desde el valor por defecto de la entidad
                            .putString("color_tema_actual", "#E5E5E5")
                            .apply()

                        Toast.makeText(
                            this@RegisterActivity,
                            "¡Usuario registrado!",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            "No se pudo registrar el usuario",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Error al registrar: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun hasUpperCase(password: String): Boolean {
        return password.any { it.isUpperCase() }
    }
}
