package com.gymrats.gymratsapp.navigation

sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object AuthGraph : Routes("auth")
    data object Login : Routes("login")
    data object Signup : Routes("signup")
    data object Home : Routes("home")
}