package com.example.proyecto_final.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla HABITO.
 *
 * Incluye:
 * - Frecuencia en JSON (RN-09).
 * - Tipo de cumplimiento (boolean, contador, temporizador).
 * - Meta de cumplimiento.
 * - Rachas y puntuación.
 */
@Entity(
    tableName = "habitos",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["id_usuario"])]
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_habito")
    val idHabito: Int = 0,

    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int,

    val titulo: String,
    val subtitulo: String? = null,

    // Color guardado como String (por ejemplo "#DDDDDD" o "#D2EFEF")
    val color: String = "#DDDDDD",

    // JSON de frecuencia (RN-08 y RN-09)
    // Ej: {"tipo":"diario"} o {"tipo":"personalizado","veces":3,"periodo":"semana"}
    val frecuencia: String = """{"tipo":"diario"}""",

    // Tipo de cumplimiento (RN-11): "boolean", "contador", "temporizador"
    @ColumnInfo(name = "tipo_cumplimiento")
    val tipoCumplimiento: String = "boolean",

    // Meta de cumplimiento (RN-12 a RN-14)
    @ColumnInfo(name = "meta_cumplimiento")
    val metaCumplimiento: Int = 1,

    // Recordatorios
    @ColumnInfo(name = "hora_recordatorio")
    val horaRecordatorio: String? = null,

    @ColumnInfo(name = "notificaciones")
    val notificaciones: Boolean = false,

    // Nota opcional del hábito
    val nota: String? = null,

    // Racha actual de cumplimiento (RN-17)
    @ColumnInfo(name = "racha_actual")
    val rachaActual: Int = 0,

    // Racha máxima alcanzada (RN-18)
    @ColumnInfo(name = "racha_maxima")
    val rachaMaxima: Int = 0,

    // Puntuación actual del hábito (0–100) (RN-15)
    val puntuacion: Int = 0,

    // Estado activo/inactivo (RN-19)
    val activo: Boolean = true
)
