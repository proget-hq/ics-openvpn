package pl.proget.openvpn.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.proget.openvpn.presentation.about.AboutScreen
import pl.proget.openvpn.presentation.logs.LogsScreen
import pl.proget.openvpn.presentation.logs.LogsViewModel
import pl.proget.openvpn.presentation.main.MainScreen
import pl.proget.openvpn.presentation.main.MainViewModel
import pl.proget.openvpn.presentation.viewModelFactory

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val factory = LocalContext.current.viewModelFactory

    NavHost(navController, startDestination = Route.MainRoute) {
        composable<Route.MainRoute> {
            val viewModel = viewModel<MainViewModel>(factory = factory)
            MainScreen(
                viewModel = viewModel,
                navigateToLogs = { navController.navigate(Route.LogsRoute) },
                navigateToAbout = { navController.navigate(Route.AboutRoute) }
            )
        }

        composable<Route.AboutRoute> {
            AboutScreen { navController.navigateUp() }
        }

        composable<Route.LogsRoute> {
            val viewModel = viewModel<LogsViewModel>(factory = factory)
            LogsScreen(viewModel = viewModel) { navController.navigateUp() }
        }
    }
}
