package com.example.widgetagua.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Instância única (singleton) do banco SQLite via Room.
 */
@Database(
    entities = [WaterEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class WaterDatabase : RoomDatabase() {

    abstract fun waterDao(): WaterDao

    companion object {
        private const val DATABASE_NAME = "water_tracker.db"

        @Volatile
        private var instance: WaterDatabase? = null

        fun getInstance(context: Context): WaterDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    WaterDatabase::class.java,
                    DATABASE_NAME,
                ).build().also { instance = it }
            }
    }
}
