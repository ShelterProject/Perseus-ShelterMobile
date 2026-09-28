package sidev.app.shelter.feature.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import sidev.app.shelter.feature.auth.AuthViewModel
import sidev.app.shelter.feature.auth.LoginScreen
import sidev.app.shelter.feature.auth.RegisterScreen
import sidev.app.shelter.feature.dashboard.DashboardScreen

@Composable
fun ShelterNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
    ) {
        composable(Screen.Splash.route) {
            SplashRoute(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigateToDashboard() },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigateToDashboard() },
                onNavigateToLogin = { navController.popBackStack() },
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
    }
}

@Composable
private fun SplashRoute(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        val destination = if (currentUser != null) Screen.Dashboard.route else Screen.Login.route
        navController.navigate(destination) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

private fun NavHostController.navigateToDashboard() {
    navigate(Screen.Dashboard.route) {
        popUpTo(Screen.Login.route) { inclusive = true }
    }
}
