package com.example.widgetagua.ui.feature

import com.example.widgetagua.model.WaterIntake

/**
 * Estado de UI exposto pelo [WaterViewModel].
 */
sealed interface WaterUiState {
    data object Loading : WaterUiState
    data class Ready(val intake: WaterIntake) : WaterUiState
}
