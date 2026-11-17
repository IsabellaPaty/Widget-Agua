package com.example.widgetagua.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = WaterDarkPrimary,
    onPrimary = WaterDarkOnPrimary,
    primaryContainer = WaterDarkPrimaryContainer,
    onPrimaryContainer = WaterDarkOnPrimaryContainer,
    secondary = WaterDarkSecondary,
    onSecondary = WaterDarkOnSecondary,
    secondaryContainer = WaterDarkSecondaryContainer,
    onSecondaryContainer = WaterDarkOnSecondaryContainer,
    tertiary = WaterDarkTertiary,
    onTertiary = WaterDarkOnTertiary,
    tertiaryContainer = WaterDarkTertiaryContainer,
    onTertiaryContainer = WaterDarkOnTertiaryContainer,
)

private val LightColorScheme = lightColorScheme(
    primary = WaterLightPrimary,
    onPrimary = WaterLightOnPrimary,
    primaryContainer = WaterLightPrimaryContainer,
    onPrimaryContainer = WaterLightOnPrimaryContainer,
    secondary = WaterLightSecondary,
    onSecondary = WaterLightOnSecondary,
    secondaryContainer = WaterLightSecondaryContainer,
    onSecondaryContainer = WaterLightOnSecondaryContainer,
    tertiary = WaterLightTertiary,
    onTertiary = WaterLightOnTertiary,
    tertiaryContainer = WaterLightTertiaryContainer,
    onTertiaryContainer = WaterLightOnTertiaryContainer,
)

/**
 * Resolve o [ColorScheme] Material 3 de acordo com o modo (claro/escuro) e o Dynamic Color.
 *
 * É uma função "pura" (não-@Composable) para também poder ser reaproveitada pelo Widget Glance,
 * garantindo que app e Widget compartilhem exatamente as mesmas cores.
 */
fun waterColorScheme(
    context: Context,
    darkTheme: Boolean,
    dynamicColor: Boolean = true,
): ColorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

    darkTheme -> DarkColorScheme
    else -> LightColorScheme
}

@Composable
fun WidgetAguaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color (Material You) disponível no Android 12+.
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = waterColorScheme(
            context = LocalContext.current,
            darkTheme = darkTheme,
            dynamicColor = dynamicColor,
        ),
        typography = Typography,
        content = content,
    )
}
