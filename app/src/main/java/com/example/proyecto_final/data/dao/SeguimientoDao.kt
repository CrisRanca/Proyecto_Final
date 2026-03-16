package com.example.proyecto_final.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyecto_final.data.entities.SeguimientoEntity

@Dao
interface SeguimientoDao {

    @Insert
    suspend fun insertarSeguimiento(seg: SeguimientoEntity): Long

    @Update
    suspend fun actualizarSeguimiento(seg: SeguimientoEntity)

    @Query("SELECT * FROM seguimientos WHERE id_habito = :idHabito AND fecha = :fecha LIMIT 1")
    suspend fun obtenerPorHabitoYFecha(idHabito: Int, fecha: Long): SeguimientoEntity?

    @Query("SELECT * FROM seguimientos WHERE id_habito = :idHabito ORDER BY fecha ASC")
    suspend fun obtenerPorHabito(idHabito: Int): List<SeguimientoEntity>

    @Query("SELECT * FROM seguimientos WHERE id_habito = :idHabito AND fecha BETWEEN :desde AND :hasta ORDER BY fecha ASC")
    suspend fun obtenerPorRangoFechas(idHabito: Int, desde: Long, hasta: Long): List<SeguimientoEntity>

    // ⭐ NECESARIO PARA DESMARCAR EL HÁBITO
    @Query("DELETE FROM seguimientos WHERE id_seguimiento = :id")
    suspend fun borrarSeguimiento(id: Int)
}
