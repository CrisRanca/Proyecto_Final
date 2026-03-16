package com.example.proyecto_final.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla CATEGORIA.
 *
 * Las categorías pueden ser:
 * - Predefinidas (Salud, Deporte, etc.).
 * - Personalizadas por el usuario.
 */
@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_categoria")
    val idCategoria: Int = 0,

    val nombre: String,
    val descripcion: String? = null,
    val icono: String? = null
)
