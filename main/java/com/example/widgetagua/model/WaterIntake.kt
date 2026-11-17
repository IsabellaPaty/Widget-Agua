package com.example.widgetagua.model

import kotlin.math.roundToInt

/**
 * Modelo de domínio do consumo de água.
 *
 * Concentra as regras de negócio (conversão de copos para litros, progresso em relação à meta,
 * etc.) para que a UI e o Widget permaneçam "burros" e apenas apresentem o estado.
 */
data class WaterIntake(
    val id: Long = 0L,
    /** Início do dia (00:00) em epoch millis. Identifica o registro diário. */
    val timestamp: Long = 0L,
    val coposConsumidos: Int = 0,
    val metaDiaria: Int = DEFAULT_GOAL_GLASSES,
    val volumePorCopoMl: Int = VOLUME_POR_COPO_ML,
) {

    /** Total consumido em litros (ex.: 8 copos * 200 ml = 1.6 L). */
    val litrosConsumidos: Double
        get() = coposConsumidos * volumePorCopoMl / 1000.0

    /** Meta diária em litros (ex.: 10 copos * 200 ml = 2.0 L). */
    val litrosMeta: Double
        get() = metaDiaria * volumePorCopoMl / 1000.0

    /** Progresso normalizado entre 0f e 1f. */
    val progresso: Float
        get() = if (metaDiaria <= 0) 0f else (coposConsumidos.toFloat() / metaDiaria).coerceIn(0f, 1f)

    /** Progresso em porcentagem inteira (0 a 100). */
    val progressoPercentual: Int
        get() = (progresso * 100).roundToInt()

    /** Copos que ainda faltam para bater a meta (nunca negativo). */
    val coposRestantes: Int
        get() = (metaDiaria - coposConsumidos).coerceAtLeast(0)

    /** Indica se a meta diária foi atingida. */
    val metaAtingida: Boolean
        get() = coposConsumidos >= metaDiaria

    companion object {
        const val VOLUME_POR_COPO_ML = 200
        const val DEFAULT_GOAL_GLASSES = 10
        const val MIN_GOAL_GLASSES = 1
        const val MAX_GOAL_GLASSES = 30
    }
}
