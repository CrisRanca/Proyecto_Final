package com.example.proyecto_final.utils

import com.example.proyecto_final.data.HabiTrackDatabase
import com.example.proyecto_final.data.entities.HabitEntity
import com.example.proyecto_final.data.entities.UsuarioEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RachaUtils {

    suspend fun recalcularRachaYPuntos(idHabito: Int, db: HabiTrackDatabase) {
        withContext(Dispatchers.IO) {

            val habitDao = db.habitDao()
            val segDao = db.seguimientoDao()
            val usuarioDao = db.usuarioDao()

            val habito = habitDao.obtenerHabitoPorId(idHabito) ?: return@withContext
            val seguimientos = segDao.obtenerPorHabito(idHabito)

            if (seguimientos.isEmpty()) return@withContext

            val ultimo = seguimientos.last()
            val completado = ultimo.vecesCumplimiento >= habito.metaCumplimiento

            var nuevaRacha = habito.rachaActual
            var nuevaRachaMax = habito.rachaMaxima
            var puntosGanados = 0

            if (completado) {
                nuevaRacha += 1
                if (nuevaRacha > nuevaRachaMax) nuevaRachaMax = nuevaRacha

                puntosGanados = 10

                if (nuevaRacha % 7 == 0) puntosGanados += 20
                if (nuevaRacha % 30 == 0) puntosGanados += 30
            } else {
                nuevaRacha = 0
            }

            val usuario = usuarioDao.obtenerUsuarioPorId(habito.idUsuario)
            if (usuario != null) {
                val nuevosPuntos = (usuario.puntosTotales + puntosGanados).coerceAtLeast(0)
                val nuevoNivel = nuevosPuntos / 100

                val actualizado = usuario.copy(
                    puntosTotales = nuevosPuntos,
                    nivel = nuevoNivel
                )
                usuarioDao.actualizarUsuario(actualizado)
            }

            val habitoActualizado = habito.copy(
                rachaActual = nuevaRacha,
                rachaMaxima = nuevaRachaMax
            )
            habitDao.actualizarHabito(habitoActualizado)
        }
    }
}
