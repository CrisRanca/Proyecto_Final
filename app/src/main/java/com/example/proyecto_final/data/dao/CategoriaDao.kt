package com.example.proyecto_final.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.proyecto_final.data.entities.CategoriaEntity

@Dao
interface CategoriaDao {

    @Insert
    suspend fun insertarCategoria(categoria: CategoriaEntity): Long

    @Update
    suspend fun actualizarCategoria(categoria: CategoriaEntity)

    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    suspend fun obtenerTodas(): List<CategoriaEntity>

    @Query("SELECT * FROM categorias WHERE id_categoria = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): CategoriaEntity?
}
