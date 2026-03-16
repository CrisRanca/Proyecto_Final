package com.example.proyecto_final.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla SEGUIMIENTO_HABITO.
 *
 * Un registro por hábito y fecha (RN-21).
 */
@Entity(
    tableName = "seguimientos",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id_habito"],
            childColumns = ["id_habito"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("id_habito")]
)
data class SeguimientoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_seguimiento")
    val idSeguimiento: Int = 0,

    @ColumnInfo(name = "id_habito")
    val idHabito: Int,

    // Fecha del seguimiento (en millis, sin hora o normalizado a 00:00)
    val fecha: Long,

    // Nota opcional (RN-27)
    val nota: String? = null,

    // Estado de ánimo 1–5 o null (RN-23)
    val animo: Int? = null,

    // Veces de cumplimiento (RN-24)
    @ColumnInfo(name = "veces_cumplimiento")
    val vecesCumplimiento: Int
)
