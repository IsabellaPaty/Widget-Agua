package com.example.widgetagua.ui.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.widgetagua.R
import com.example.widgetagua.model.WaterIntake
import com.example.widgetagua.ui.UiEvent
import com.example.widgetagua.ui.components.GoalDialog
import com.example.widgetagua.ui.components.WaterProgressCard
import com.example.widgetagua.ui.components.WaterStatCard
import com.example.widgetagua.ui.feature.WaterUiState
import com.example.widgetagua.ui.feature.WaterViewModel

/**
 * Tela principal do app: progresso do dia, estatísticas e ações rápidas.
 * Totalmente construída com componentes Material 3.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WaterViewModel,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showGoalDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(R.string.home_title)) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 12.dp),
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = stringResource(R.string.action_history),
                        )
                    }
                    IconButton(onClick = { showGoalDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.action_set_goal),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = stringResource(R.string.action_add_glass)) },
                icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
                onClick = { viewModel.onEvent(UiEvent.AddGlass) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            WaterUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is WaterUiState.Ready -> HomeContent(
                intake = state.intake,
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }

    if (showGoalDialog) {
        val currentGoal = (uiState as? WaterUiState.Ready)?.intake?.metaDiaria
            ?: WaterIntake.DEFAULT_GOAL_GLASSES
        GoalDialog(
            currentGoal = currentGoal,
            onDismiss = { showGoalDialog = false },
            onConfirm = { copos ->
                viewModel.onEvent(UiEvent.SetGoal(copos))
                showGoalDialog = false
            },
        )
    }
}

@Composable
private fun HomeContent(
    intake: WaterIntake,
    viewModel: WaterViewModel,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { WaterProgressCard(intake = intake) }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                WaterStatCard(
                    icon = Icons.Filled.WaterDrop,
                    label = stringResource(R.string.stat_consumed),
                    value = stringResource(R.string.glasses_count, intake.coposConsumidos),
                    modifier = Modifier.weight(1f),
                )
                WaterStatCard(
                    icon = Icons.Filled.Flag,
                    label = stringResource(R.string.stat_goal),
                    value = stringResource(R.string.glasses_count, intake.metaDiaria),
                    modifier = Modifier.weight(1f),
                )
                WaterStatCard(
                    icon = Icons.Filled.CheckCircle,
                    label = stringResource(R.string.stat_remaining),
                    value = stringResource(R.string.glasses_count, intake.coposRestantes),
                    modifier = Modifier.weight(1f),
                )
            }
        }

        item {
            QuickActionsCard(
                canRemove = intake.coposConsumidos > 0,
                canReset = intake.coposConsumidos > 0,
                onRemove = { viewModel.onEvent(UiEvent.RemoveGlass) },
                onReset = { viewModel.onEvent(UiEvent.ResetToday) },
            )
        }

        if (intake.metaAtingida) {
            item { GoalReachedCard() }
        }
    }
}

@Composable
private fun QuickActionsCard(
    canRemove: Boolean,
    canReset: Boolean,
    onRemove: () -> Unit,
    onReset: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.action_remove_glass),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilledTonalButton(
                    onClick = onRemove,
                    enabled = canRemove,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(imageVector = Icons.Filled.Remove, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.action_remove_glass))
                }
                OutlinedButton(
                    onClick = onReset,
                    enabled = canReset,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.action_reset_today))
                }
            }
        }
    }
}

@Composable
private fun GoalReachedCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )
            Text(
                text = stringResource(R.string.goal_reached),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
