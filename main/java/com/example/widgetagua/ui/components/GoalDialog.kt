package com.example.widgetagua.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.widgetagua.R
import com.example.widgetagua.model.WaterIntake
import kotlin.math.roundToInt

/**
 * Diálogo Material 3 para definir a meta diária (em copos).
 */
@Composable
fun GoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    var goal by rememberSaveable(currentGoal) { mutableFloatStateOf(currentGoal.toFloat()) }
    val selectedGoal = goal.roundToInt()

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(imageVector = Icons.Filled.WaterDrop, contentDescription = null) },
        title = { Text(text = stringResource(R.string.goal_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(
                        R.string.goal_dialog_message,
                        WaterIntake.VOLUME_POR_COPO_ML,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = stringResource(
                        R.string.goal_dialog_value,
                        selectedGoal,
                        formatLiters(selectedGoal * WaterIntake.VOLUME_POR_COPO_ML / 1000.0),
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Slider(
                    value = goal,
                    onValueChange = { goal = it },
                    valueRange = WaterIntake.MIN_GOAL_GLASSES.toFloat()..WaterIntake.MAX_GOAL_GLASSES.toFloat(),
                    steps = WaterIntake.MAX_GOAL_GLASSES - WaterIntake.MIN_GOAL_GLASSES - 1,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedGoal) }) {
                Text(text = stringResource(R.string.dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.dialog_cancel))
            }
        },
    )
}
