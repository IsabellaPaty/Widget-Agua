package com.example.widgetagua.ui.feature

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.widgetagua.data.WaterRepository
import com.example.widgetagua.data.WaterRepositoryImpl
import com.example.widgetagua.model.WaterIntake
import com.example.widgetagua.ui.UiEvent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel da tela de consumo de água.
 *
 * Observa o repositório e traduz [UiEvent] em operações de dados. Toda escrita no repositório
 * mantém automaticamente o Widget sincronizado.
 */
class WaterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WaterRepository = WaterRepositoryImpl.create(application)

    val uiState: StateFlow<WaterUiState> = repository.observeToday()
        .map<WaterIntake, WaterUiState> { WaterUiState.Ready(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = WaterUiState.Loading,
        )

    val history: StateFlow<List<WaterIntake>> = repository.observeHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = emptyList(),
        )

    fun onEvent(event: UiEvent) {
        viewModelScope.launch {
            when (event) {
                UiEvent.AddGlass -> repository.addGlass()
                UiEvent.RemoveGlass -> repository.removeGlass()
                UiEvent.ResetToday -> repository.resetToday()
                is UiEvent.SetGoal -> repository.setDailyGoal(event.copos)
            }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
