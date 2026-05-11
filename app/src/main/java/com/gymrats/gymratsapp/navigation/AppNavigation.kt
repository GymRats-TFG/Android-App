package com.gymrats.gymratsapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.gymrats.gymratsapp.ViewModels.AuthViewModel
import com.gymrats.gymratsapp.screens.HomeScreen
import com.gymrats.gymratsapp.screens.LoginScreen
import com.gymrats.gymratsapp.screens.SignupScreen
import com.gymrats.gymratsapp.screens.SplashScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route
    ) {
        // Pantalla de Splash
        composable(Routes.Splash.route) {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
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
                    authViewModel = authViewModel,
                    onGoToSignup = {
                        navController.navigate(Routes.Signup.route) {
                            launchSingleTop = true
                        }
                    },
                    onLogin = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Pantalla de SignUp
            composable(Routes.Signup.route) {
                SignupScreen(
                    authViewModel = authViewModel,
                    onSignUp = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onBackToLogin = {
                        navController.popBackStack() // vuelve al Login
                    }
                )
            }
        }

        // pantalla de home
        composable(Routes.Home.route){
            HomeScreen()
        }
    }
}