package com.example.widgetagua.ui

/**
 * Eventos emitidos pela UI (e potencialmente pelo Widget) para o ViewModel.
 */
sealed interface UiEvent {
    data object AddGlass : UiEvent
    data object RemoveGlass : UiEvent
    data object ResetToday : UiEvent
    data class SetGoal(val copos: Int) : UiEvent
}
