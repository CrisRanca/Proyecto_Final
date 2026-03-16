package com.example.proyecto_final.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyecto_final.data.dao.CategoriaDao
import com.example.proyecto_final.data.dao.ConfiguracionUIDao
import com.example.proyecto_final.data.dao.HabitDao
import com.example.proyecto_final.data.dao.SeguimientoDao
import com.example.proyecto_final.data.dao.UsuarioDao
import com.example.proyecto_final.data.entities.CategoriaEntity
import com.example.proyecto_final.data.entities.ConfiguracionUIEntity
import com.example.proyecto_final.data.entities.HabitEntity
import com.example.proyecto_final.data.entities.SeguimientoEntity
import com.example.proyecto_final.data.entities.UsuarioEntity

@Database(
    entities = [
        UsuarioEntity::class,
        HabitEntity::class,
        CategoriaEntity::class,
        SeguimientoEntity::class,
        ConfiguracionUIEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class HabiTrackDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun habitDao(): HabitDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun seguimientoDao(): SeguimientoDao
    abstract fun configuracionDao(): ConfiguracionUIDao

    companion object {
        @Volatile
        private var INSTANCE: HabiTrackDatabase? = null

        fun getInstance(context: Context): HabiTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabiTrackDatabase::class.java,
                    "habitrack_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
