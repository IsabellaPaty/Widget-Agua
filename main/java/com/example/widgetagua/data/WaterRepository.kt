package com.example.widgetagua.data

import com.example.widgetagua.model.WaterIntake
import kotlinx.coroutines.flow.Flow

/**
 * Contrato da camada de dados (Repository Pattern).
 *
 * A UI e o Widget dependem apenas desta abstração, nunca do Room diretamente.
 */
interface WaterRepository {

    /** Observa o consumo do dia atual em tempo real. */
    fun observeToday(): Flow<WaterIntake>

    /** Observa o histórico dos últimos [days] dias. */
    fun observeHistory(days: Int = 7): Flow<List<WaterIntake>>

    /** Adiciona um copo ao dia atual e retorna o estado atualizado. */
    suspend fun addGlass(): WaterIntake

    /** Remove um copo do dia atual (nunca abaixo de zero). */
    suspend fun removeGlass(): WaterIntake

    /** Define a meta diária (em copos) e persiste a preferência para os próximos dias. */
    suspend fun setDailyGoal(copos: Int): WaterIntake

    /** Zera o consumo do dia atual. */
    suspend fun resetToday(): WaterIntake
}
