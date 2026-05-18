package com.gymrats.gymratsapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gymrats.gymratsapp.viewModels.AuthViewModel
import com.gymrats.gymratsapp.components.BottomNavBar
import com.gymrats.gymratsapp.data.SessionManager
import com.gymrats.gymratsapp.screens.CreateGymScreen
import com.gymrats.gymratsapp.screens.EditProfileScreen
import com.gymrats.gymratsapp.screens.EnterpriseHomeScreen
import com.gymrats.gymratsapp.screens.ProfileScreen
import com.gymrats.gymratsapp.screens.QRScreen
import com.gymrats.gymratsapp.screens.ScannerScreen
import com.gymrats.gymratsapp.screens.UserHomeScreen
import com.gymrats.gymratsapp.viewModels.GymViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainScaffold(rootNavController: NavController, authViewModel: AuthViewModel, sessionManager: SessionManager) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gymViewModel = remember { GymViewModel(sessionManager) }

    val isEnterprise = authViewModel.isEnterprise

    if (isEnterprise == null) {
        Box(Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background))
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val shouldShowBottomBar = when (currentRoute) {
        NavBarRoutes.EditProfile.route -> false
        NavBarRoutes.CreateGym.route -> false
        else -> true
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavBar(
                    navController = navController,
                    isEnterprise = isEnterprise
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavBarRoutes.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(NavBarRoutes.Home.route) {
                if (isEnterprise) {
                    EnterpriseHomeScreen(authViewModel, gymViewModel,
                        onCreateGym = { navController.navigate(NavBarRoutes.CreateGym.route) }
                    )
                } else {
                    UserHomeScreen()
                }
            }

            composable(NavBarRoutes.QR.route) { QRScreen() }
            composable(NavBarRoutes.Scanner.route) { ScannerScreen() }

            composable(NavBarRoutes.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    userProfile = authViewModel.userProfile,
                    onLogout = {
                        scope.launch {
                            authViewModel.clearState()
                            sessionManager.clearSession()

                            // Damos tiempo al sistema de archivos para persistir el borrado
//                            delay(800)

                            rootNavController.navigate(Routes.AuthGraph.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    onEditClick = { navController.navigate(NavBarRoutes.EditProfile.route) },
                    onCreateGym = { navController.navigate(NavBarRoutes.CreateGym.route) }
                )
            }

            composable(NavBarRoutes.EditProfile.route) {
                EditProfileScreen(
                    authViewModel = authViewModel,
                    onCloseClick = { navController.popBackStack() },
                    onSaveChangesClick = {
                        authViewModel.cargarPerfil()
                        navController.popBackStack()
                    }
                )
            }

            composable(NavBarRoutes.CreateGym.route) {
                CreateGymScreen(
                    gymViewModel = gymViewModel,
                    onClose = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }
        }
    }
}