package com.example.widgetagua.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.widgetagua.ui.feature.WaterViewModel
import com.example.widgetagua.ui.feature.history.HistoryScreen
import com.example.widgetagua.ui.feature.home.HomeScreen

/** Destinos de navegação do app. */
sealed class WaterDestination(val route: String) {
    /** Tela principal com o progresso do dia. */
    data object Home : WaterDestination("home")

    /** Tela com o histórico de consumo. */
    data object History : WaterDestination("history")
}

/**
 * Grafo de navegação do app.
 *
 * Um único [WaterViewModel] é compartilhado entre as telas para que Home e Histórico
 * observem exatamente o mesmo estado, sempre em sincronia com o Widget.
 */
@Composable
fun WaterNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: WaterViewModel = viewModel(),
) {
    NavHost(
        navController = navController,
        startDestination = WaterDestination.Home.route,
        modifier = modifier,
    ) {
        composable(route = WaterDestination.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToHistory = {
                    navController.navigate(WaterDestination.History.route)
                },
            )
        }

        composable(route = WaterDestination.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
