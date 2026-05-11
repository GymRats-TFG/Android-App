package com.gymrats.gymratsapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.gymrats.gymratsapp.screens.LoginScreen
import com.gymrats.gymratsapp.screens.SignupScreen
import com.gymrats.gymratsapp.screens.SplashScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route
    ) {
        // Pantalla de Splash
        composable(Routes.Splash.route) {
            SplashScreen(
                onNavigateToMain = {},
                onNavigateToAuth = {
                    navController.navigate(Routes.AuthGraph.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Navegación entre pantallas de autenticación
        navigation(
            route = Routes.AuthGraph.route,
            startDestination = Routes.Login.route
        ) {

            // Pantalla de Login
            composable(Routes.Login.route) {
                LoginScreen(
                    onGoToSignup = {
                        navController.navigate(Routes.Signup.route) {
                            launchSingleTop = true
                        }
                    },
                    onLogin = {}
                )
            }

            // Pantalla de SignUp
            composable(Routes.Signup.route) {
                SignupScreen(
                    onSignUp = {},
                    onBackToLogin = {
                        navController.popBackStack() // vuelve al Login
                    }
                )
            }
        }
    }
}