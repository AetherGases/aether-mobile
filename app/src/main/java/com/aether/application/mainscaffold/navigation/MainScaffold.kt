package com.aether.application.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aether.application.R
import com.aether.core.ui.components.AetherBottomBar
import com.aether.core.ui.components.BottomBarItem
import com.aether.core.ui.theme.*

object AppRoute {
    const val HOME = "home"
    const val REPORTS = "reports"
    const val CALCULATOR = "calculator"
    const val CHATBOT = "aeko"
    const val PROFILE = "settings"

    val bottomBarRoutes = setOf(HOME, REPORTS, CALCULATOR, PROFILE)
}

private val homeItem = BottomBarItem(AppRoute.HOME, "Início", R.drawable.ic_nav_home)

private fun bottomBarItems(isManager: Boolean) = listOf(
    BottomBarItem(
        route = AppRoute.REPORTS,
        label = if (isManager) "Relatórios" else "Criação",
        iconRes = R.drawable.ic_nav_reports
    ),
    BottomBarItem(AppRoute.CALCULATOR, "Calculadora", R.drawable.ic_nav_calculator),
    BottomBarItem(AppRoute.CHATBOT, "Chatbot", R.drawable.ic_nav_chatbot),
    BottomBarItem(AppRoute.PROFILE, "Perfil", R.drawable.ic_nav_profile)
)

@Composable
fun MainScaffold(
    isManager: Boolean,
    modifier: Modifier = Modifier,
    startDestination: String = AppRoute.HOME,
    navController: NavHostController = rememberNavController(),
    graph: NavGraphBuilder.(NavHostController) -> Unit
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val items = bottomBarItems(isManager)

    val showBottomBar = destination?.hierarchy?.any { it.route in AppRoute.bottomBarRoutes } == true
    val selectedRoute = (items + homeItem)
        .firstOrNull { item -> destination?.hierarchy?.any { it.route == item.route } == true }
        ?.route

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                AetherBottomBar(
                    items = items,
                    homeItem = homeItem,
                    selectedRoute = selectedRoute,
                    onItemClick = { item ->
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            graph(navController)
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, style = titleLarge, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Preview(showBackground = true, device = "spec:width=375dp,height=812dp")
@Composable
fun MainScaffoldPreview() {
    AetherTheme(darkTheme = false) {
        MainScaffold(isManager = true) {
            composable(AppRoute.HOME) { PlaceholderScreen("Início") }
            composable(AppRoute.REPORTS) { PlaceholderScreen("Relatórios") }
            composable(AppRoute.CALCULATOR) { PlaceholderScreen("Calculadora") }
            composable(AppRoute.CHATBOT) { PlaceholderScreen("Chatbot") }
            composable(AppRoute.PROFILE) { PlaceholderScreen("Perfil") }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=375dp,height=812dp")
@Composable
fun MainScaffoldEmployeeDarkPreview() {
    AetherTheme(darkTheme = true) {
        MainScaffold(isManager = false) {
            composable(AppRoute.HOME) { PlaceholderScreen("Início") }
            composable(AppRoute.REPORTS) { PlaceholderScreen("Criação") }
            composable(AppRoute.CALCULATOR) { PlaceholderScreen("Calculadora") }
            composable(AppRoute.CHATBOT) { PlaceholderScreen("Chatbot") }
            composable(AppRoute.PROFILE) { PlaceholderScreen("Perfil") }
        }
    }
}