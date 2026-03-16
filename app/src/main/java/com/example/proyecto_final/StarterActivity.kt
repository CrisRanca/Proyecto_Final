package com.example.proyecto_final

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle

class StarterActivity : Activity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("habitrack_prefs", MODE_PRIVATE)
        decidirPantallaInicial()
    }

    private fun decidirPantallaInicial() {
        val yaRegistrado = prefs.getBoolean("ya_registrado", false)
        val sesionIniciada = prefs.getBoolean("sesion_iniciada", false)

        val intent = when {
            !yaRegistrado -> Intent(this, RegisterActivity::class.java)
            !sesionIniciada -> Intent(this, LoginActivity::class.java)
            else -> Intent(this, MainActivity::class.java)
        }

        startActivity(intent)
        finish()
    }
}
