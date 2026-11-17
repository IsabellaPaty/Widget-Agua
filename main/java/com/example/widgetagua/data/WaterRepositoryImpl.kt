package com.example.widgetagua.data

import android.content.Context
import androidx.core.content.edit
import androidx.glance.appwidget.updateAll
import com.example.widgetagua.model.WaterIntake
import com.example.widgetagua.widget.WaterWidget
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implementação do [WaterRepository] sobre o Room.
 *
 * Responsabilidades:
 *  - Normalizar o "dia atual" (meia-noite) para agrupar o consumo por dia.
 *  - Garantir escrita segura (Mutex) quando app e Widget disparam ações simultâneas.
 *  - Persistir a meta diária entre dias via SharedPreferences.
 *  - Atualizar o Widget (Glance) sempre que o banco muda.
 */
class WaterRepositoryImpl(
    private val dao: WaterDao,
    private val context: Context,
    private val clock: () -> Long = System::currentTimeMillis,
) : WaterRepository {

    private val mutex = Mutex()
    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun observeToday(): Flow<WaterIntake> =
        dao.observeByDay(todayKey()).map { entity ->
            entity?.toDomain() ?: emptyToday()
        }

    override fun observeHistory(days: Int): Flow<List<WaterIntake>> =
        dao.observeHistory(days).map { list -> list.map { it.toDomain() } }

    override suspend fun addGlass(): WaterIntake = mutate { entity ->
        entity.copy(coposConsumidos = entity.coposConsumidos + 1)
    }

    override suspend fun removeGlass(): WaterIntake = mutate { entity ->
        entity.copy(coposConsumidos = (entity.coposConsumidos - 1).coerceAtLeast(0))
    }

    override suspend fun setDailyGoal(copos: Int): WaterIntake {
        val sanitized = copos.coerceIn(
            WaterIntake.MIN_GOAL_GLASSES,
            WaterIntake.MAX_GOAL_GLASSES,
        )
        prefs.edit { putInt(KEY_DAILY_GOAL, sanitized) }
        return mutate { entity -> entity.copy(metaDiaria = sanitized) }
    }

    override suspend fun resetToday(): WaterIntake = mutate { entity ->
        entity.copy(coposConsumidos = 0)
    }

    /**
     * Aplica [transform] ao registro do dia atual de forma atômica, persistindo o resultado
     * e mantendo o Widget sincronizado.
     */
    private suspend fun mutate(transform: (WaterEntity) -> WaterEntity): WaterIntake = mutex.withLock {
        val key = todayKey()
        val current = dao.getByDay(key) ?: WaterEntity(
            timestamp = key,
            coposConsumidos = 0,
            metaDiaria = goalFromPrefs(),
        )

        val updated = transform(current)
        val persisted = if (updated.id == 0L) {
            updated.copy(id = dao.insert(updated))
        } else {
            dao.update(updated)
            updated
        }

        val domain = persisted.toDomain()
        refreshWidget()
        domain
    }

    /** Reexecuta a composição de todos os Widgets de água com o estado mais recente. */
    private suspend fun refreshWidget() {
        runCatching { WaterWidget().updateAll(context) }
    }

    private fun todayKey(): Long {
        val zone = ZoneId.systemDefault()
        return Instant.ofEpochMilli(clock())
            .atZone(zone)
            .toLocalDate()
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()
    }

    private fun goalFromPrefs(): Int =
        prefs.getInt(KEY_DAILY_GOAL, WaterIntake.DEFAULT_GOAL_GLASSES)
            .coerceIn(WaterIntake.MIN_GOAL_GLASSES, WaterIntake.MAX_GOAL_GLASSES)

    private fun emptyToday(): WaterIntake = WaterIntake(
        timestamp = todayKey(),
        coposConsumidos = 0,
        metaDiaria = goalFromPrefs(),
    )

    private fun WaterEntity.toDomain(): WaterIntake = WaterIntake(
        id = id,
        timestamp = timestamp,
        coposConsumidos = coposConsumidos,
        metaDiaria = metaDiaria,
    )

    companion object {
        private const val PREFS_NAME = "water_prefs"
        private const val KEY_DAILY_GOAL = "daily_goal_glasses"

        /** Cria uma instância do repositório a partir de um [Context] qualquer. */
        fun create(context: Context): WaterRepositoryImpl = WaterRepositoryImpl(
            dao = WaterDatabase.getInstance(context).waterDao(),
            context = context.applicationContext,
        )
    }
}
