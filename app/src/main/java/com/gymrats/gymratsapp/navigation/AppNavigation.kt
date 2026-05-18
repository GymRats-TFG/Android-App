package com.gymrats.gymratsapp.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.gymrats.gymratsapp.data.SessionManager
import com.gymrats.gymratsapp.viewModels.AuthViewModel
import com.gymrats.gymratsapp.screens.LoginScreen
import com.gymrats.gymratsapp.screens.SignupScreen
import com.gymrats.gymratsapp.screens.SplashScreen

@Composable
fun AppNavigation(){
    val context = LocalContext.current
    val navController = rememberNavController()
    val sessionManager = remember { SessionManager(context) }
    val authViewModel: AuthViewModel = viewModel(factory = object :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(context.applicationContext as Application, sessionManager) as T
        }
    })


    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route
    ) {
        // Pantalla de Splash
        composable(Routes.Splash.route) {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(Routes.Main.route) {
                        popUpTo(Routes.Main.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(Routes.AuthGraph.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                authViewModel,
                sessionManager
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
                        navController.navigate(Routes.Main.route) {
                            popUpTo(Routes.Main.route) { inclusive = true }
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
                        navController.navigate(Routes.Main.route) {
                            popUpTo(Routes.Main.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onBackToLogin = {
                        navController.popBackStack() // vuelve al Login
                    }
                )
            }
        }

        // Navegación entre pantallas una vez autenticado
        composable(Routes.Main.route) {
            MainScaffold(navController, authViewModel, sessionManager)
        }
    }
}