package com.example.widgetagua.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidade Room que representa o consumo de água de um dia.
 *
 * É mantido um único registro por dia. O campo [timestamp] guarda o início do dia (00:00)
 * em epoch millis e possui índice único para impedir duplicidades.
 */
@Entity(
    tableName = "water_intake",
    indices = [Index(value = ["timestamp"], unique = true)],
)
data class WaterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val timestamp: Long,
    val coposConsumidos: Int,
    val metaDiaria: Int,
)
