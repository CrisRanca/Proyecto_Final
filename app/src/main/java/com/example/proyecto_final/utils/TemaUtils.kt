package com.example.proyecto_final.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.Button
import androidx.core.graphics.drawable.DrawableCompat

object TemaUtils {

    fun aplicarTemaBoton(context: Context, boton: Button) {
        val prefs = context.getSharedPreferences("habitrack_prefs", Context.MODE_PRIVATE)
        val color = prefs.getString("color_tema_actual", "#E5E5E5")!!

        val fondo: Drawable? = boton.background

        if (fondo != null) {
            val wrap = DrawableCompat.wrap(fondo.mutate())
            DrawableCompat.setTint(wrap, Color.parseColor(color))
            boton.background = wrap
        }

        boton.setTextColor(Color.BLACK)
    }
}
