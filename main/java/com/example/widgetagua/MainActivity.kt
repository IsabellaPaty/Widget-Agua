package com.example.widgetagua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.widgetagua.navigation.WaterNavHost
import com.example.widgetagua.ui.theme.WidgetAguaTheme

/**
 * Única Activity do app (Single-Activity Architecture).
 *
 * O conteúdo é composto com Jetpack Compose, 100% Material 3, e delega a navegação
 * para o [WaterNavHost].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WidgetAguaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    WaterNavHost()
                }
            }
        }
    }
}
