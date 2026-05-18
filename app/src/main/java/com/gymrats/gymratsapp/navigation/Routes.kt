package com.gymrats.gymratsapp.navigation

sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object AuthGraph : Routes("auth")
    data object Login : Routes("login")
    data object Signup : Routes("signup")
    data object Main : Routes("main")
}

sealed class NavBarRoutes(val route: String) {
    // Rutas compartidas
    object Home : NavBarRoutes("home")
    object Profile : NavBarRoutes("profile")
    // Rutas específicas
    object QR : NavBarRoutes("qr") // Usuario normal
    object Scanner : NavBarRoutes("scanner") // Enterprise

    object EditProfile : NavBarRoutes("edit_profile")

    object CreateGym : NavBarRoutes("create_gym")
}