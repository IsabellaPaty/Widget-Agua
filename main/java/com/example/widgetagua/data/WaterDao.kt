package com.example.widgetagua.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object do consumo de água.
 *
 * Fornece operações de inserção, atualização e consulta do consumo do dia atual,
 * além de observar o histórico dos últimos dias.
 */
@Dao
interface WaterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WaterEntity): Long

    @Update
    suspend fun update(entity: WaterEntity)

    /** Busca pontual do registro de um dia específico. */
    @Query("SELECT * FROM water_intake WHERE timestamp = :dayTimestamp LIMIT 1")
    suspend fun getByDay(dayTimestamp: Long): WaterEntity?

    /** Observa em tempo real o consumo do dia atual. Emite `null` quando ainda não há registro. */
    @Query("SELECT * FROM water_intake WHERE timestamp = :dayTimestamp LIMIT 1")
    fun observeByDay(dayTimestamp: Long): Flow<WaterEntity?>

    /** Observa o histórico dos últimos [limit] dias (mais recentes primeiro). */
    @Query("SELECT * FROM water_intake ORDER BY timestamp DESC LIMIT :limit")
    fun observeHistory(limit: Int): Flow<List<WaterEntity>>

    @Query("DELETE FROM water_intake WHERE timestamp = :dayTimestamp")
    suspend fun deleteByDay(dayTimestamp: Long)
}
