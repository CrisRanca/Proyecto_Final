package com.example.proyecto_final.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla CONFIGURACION_UI.
 *
 * Cada usuario tiene exactamente una configuración (RN-43).
 */
@Entity(
    tableName = "config_ui",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("id_usuario")]
)
data class ConfiguracionUIEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_configuracion")
    val idConfiguracion: Int = 0,

    @ColumnInfo(name = "id_usuario")
    val idUsuario: Int,

    val tema: String = "azul",
    val idioma: String = "español",

    @ColumnInfo(name = "formato_fecha")
    val formatoFecha: String = "DD/MM/YYYY",

    @ColumnInfo(name = "modo_oscuro")
    val modoOscuro: Boolean = false,

    val fuente: String = "default"
)
