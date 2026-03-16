package com.example.proyecto_final.utils

import com.example.proyecto_final.R

object MascotaUtils {

    fun obtenerImagenMascota(nivel: Int): Int {
        return when (nivel) {
            in 0..4 -> R.drawable.mascota_lv1
            in 5..9 -> R.drawable.mascota_lv5
            in 10..19 -> R.drawable.mascota_lv10
            in 20..29 -> R.drawable.mascota_lv20
            else -> R.drawable.mascota_lv30
        }
    }
}
