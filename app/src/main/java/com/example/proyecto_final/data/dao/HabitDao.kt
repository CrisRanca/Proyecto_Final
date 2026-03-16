package com.example.proyecto_final.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyecto_final.data.entities.HabitEntity

@Dao
interface HabitDao {

    @Insert
    suspend fun insertarHabito(habito: HabitEntity): Long

    @Update
    suspend fun actualizarHabito(habito: HabitEntity)

    @Delete
    suspend fun eliminarHabito(habit: HabitEntity)

    @Query("SELECT * FROM habitos WHERE id_usuario = :idUsuario AND activo = 1 ORDER BY id_habito DESC")
    suspend fun obtenerHabitosActivosPorUsuario(idUsuario: Int): List<HabitEntity>

    @Query("SELECT * FROM habitos WHERE id_habito = :idHabito LIMIT 1")
    suspend fun obtenerHabitoPorId(idHabito: Int): HabitEntity?

    @Query("DELETE FROM seguimientos WHERE id_seguimiento = :id")
    suspend fun borrarSeguimiento(id: Int)
}
