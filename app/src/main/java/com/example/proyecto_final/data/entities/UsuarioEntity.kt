package com.example.proyecto_final.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Representa la tabla USUARIO de la base de datos.
 *
 * Reglas importantes:
 * - Email único (UK).
 * - Nivel aumenta cada 100 puntos.
 * - Puntos nunca negativos.
 * - Al registrarse: nivel 0, puntos 0, activo = true.
 */
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int = 0,

    val email: String,
    val password: String,
    val nombre: String,

    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: Long,

    var nivel: Int = 0,

    @ColumnInfo(name = "puntos_totales")
    var puntosTotales: Int = 0,

    val activo: Boolean = true,

    // Nombre/clave de la mascota seleccionada (ej: "gato", "perro")
    val mascota: String? = null,

    // Color de tema de la UI (hex)
    @ColumnInfo(name = "color_tema")
    val colorTema: String = "#E5E5E5"
)
