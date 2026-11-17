package com.example.widgetagua.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.components.FilledButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.widgetagua.R
import com.example.widgetagua.data.WaterRepositoryImpl
import com.example.widgetagua.model.WaterIntake
import com.example.widgetagua.ui.components.formatLiters
import com.example.widgetagua.ui.theme.waterColorScheme
import kotlinx.coroutines.flow.first

/**
 * App Widget de "Consumo de Água" construído com Jetpack Glance.
 *
 * O layout segue o estilo visual do Material 3 (cores dinâmicas, tipografia e botão M3)
 * e oferece o botão "+ Adicionar copo" que grava no SQLite sem abrir o app.
 */
class WaterWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(
            DpSize(180.dp, 110.dp),
            DpSize(250.dp, 110.dp),
        ),
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = WaterRepositoryImpl.create(context)
        val todayFlow = repository.observeToday()
        val initialIntake = todayFlow.first()
        val colors = widgetColorProviders(context)

        provideContent {
            GlanceTheme(colors = colors) {
                val intake by todayFlow.collectAsState(initial = initialIntake)
                WaterWidgetContent(intake = intake)
            }
        }
    }
}

/** Cores do Widget derivadas do esquema Material 3 do app (com suporte a Dynamic Color). */
private fun widgetColorProviders(context: Context) = ColorProviders(
    light = waterColorScheme(context, darkTheme = false, dynamicColor = true),
    dark = waterColorScheme(context, darkTheme = true, dynamicColor = true),
)

@Composable
private fun WaterWidgetContent(intake: WaterIntake) {
    val colors = GlanceTheme.colors
    val context = LocalContext.current

    Scaffold(
        modifier = GlanceModifier.fillMaxSize(),
        backgroundColor = colors.widgetBackground,
        titleBar = {
            TitleBar(
                startIcon = ImageProvider(R.drawable.ic_water_drop),
                title = context.getString(R.string.water_widget_title),
                iconColor = colors.primary,
                textColor = colors.onSurface,
            )
        },
    ) {
        Column(modifier = GlanceModifier.fillMaxWidth().padding(top = 2.dp)) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
            ) {
                Column(modifier = GlanceModifier.defaultWeight()) {
                    Text(
                        text = context.getString(R.string.glasses_count, intake.coposConsumidos),
                        style = TextStyle(
                            color = colors.onSurface,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 1,
                    )
                    Text(
                        text = context.getString(
                            R.string.liters_of_goal,
                            formatLiters(intake.litrosConsumidos),
                            formatLiters(intake.litrosMeta),
                        ),
                        style = TextStyle(color = colors.onSurfaceVariant, fontSize = 13.sp),
                        maxLines = 1,
                    )
                }
                Text(
                    text = context.getString(R.string.progress_percent, intake.progressoPercentual),
                    style = TextStyle(
                        color = colors.primary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                )
            }

            Spacer(GlanceModifier.height(10.dp))

            LinearProgressIndicator(
                progress = intake.progresso,
                modifier = GlanceModifier.fillMaxWidth().height(10.dp),
                color = colors.primary,
                backgroundColor = colors.surfaceVariant,
            )

            Spacer(GlanceModifier.height(6.dp))

            Text(
                text = if (intake.metaAtingida) {
                    context.getString(R.string.goal_reached)
                } else {
                    context.getString(R.string.widget_remaining, intake.coposRestantes)
                },
                style = TextStyle(color = colors.onSurfaceVariant, fontSize = 12.sp),
                maxLines = 1,
            )

            Spacer(GlanceModifier.height(12.dp))

            FilledButton(
                text = context.getString(R.string.water_widget_add_glass),
                icon = ImageProvider(R.drawable.ic_add),
                onClick = actionRunCallback<AddGlassAction>(),
                modifier = GlanceModifier.fillMaxWidth(),
            )
        }
    }
}

/** Callback executado ao tocar em "+ Adicionar copo" no Widget, sem abrir o app. */
class AddGlassAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        // O repositório grava no SQLite e, internamente, chama WaterWidget().updateAll(context),
        // atualizando imediatamente a UI do Widget.
        WaterRepositoryImpl.create(context).addGlass()
    }
}
