package com.example.proyecto_final.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyecto_final.data.entities.ConfiguracionUIEntity

@Dao
interface ConfiguracionUIDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarConfiguracion(config: ConfiguracionUIEntity): Long

    @Update
    suspend fun actualizarConfiguracion(config: ConfiguracionUIEntity)

    @Query("SELECT * FROM config_ui WHERE id_usuario = :idUsuario LIMIT 1")
    suspend fun obtenerPorUsuario(idUsuario: Int): ConfiguracionUIEntity?
}
