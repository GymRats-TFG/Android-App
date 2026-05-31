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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gymrats.gymratsapp.viewModels.AuthViewModel
import com.gymrats.gymratsapp.components.BottomNavBar
import com.gymrats.gymratsapp.data.SessionManager
import com.gymrats.gymratsapp.screens.CreateGymScreen
import com.gymrats.gymratsapp.screens.EditGymScreen
import com.gymrats.gymratsapp.screens.EditProfileScreen
import com.gymrats.gymratsapp.screens.EnterpriseHomeScreen
import com.gymrats.gymratsapp.screens.GymDetailScreen
import com.gymrats.gymratsapp.screens.ManageMembersScreen
import com.gymrats.gymratsapp.screens.ProfileScreen
import com.gymrats.gymratsapp.screens.QRScreen
import com.gymrats.gymratsapp.screens.ScannerScreen
import com.gymrats.gymratsapp.screens.UserHomeScreen
import com.gymrats.gymratsapp.viewModels.GymViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.gymrats.gymratsapp.viewModels.UserHomeViewModel

@Composable
fun MainScaffold(
    rootNavController: NavController,
    authViewModel: AuthViewModel,
    sessionManager: SessionManager,
    userHomeViewModel: UserHomeViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gymViewModel = remember { GymViewModel(sessionManager) }

    val isEnterprise = authViewModel.isEnterprise

    if (isEnterprise == null) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
        return
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val shouldShowBottomBar = when (currentRoute) {
        NavBarRoutes.EditProfile.route -> false
        NavBarRoutes.CreateGym.route -> false
        "${NavBarRoutes.GymDetail.route}/{gymId}" -> false
        "${NavBarRoutes.ManageMembers.route}/{gymId}" -> false
        NavBarRoutes.EditGym.route -> false
        else -> true
    }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                BottomNavBar(
                    navController = navController,
                    isEnterprise = isEnterprise,
                    authViewModel = authViewModel
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
                    EnterpriseHomeScreen(
                        authViewModel, gymViewModel,
                        onCreateGym = { navController.navigate(NavBarRoutes.CreateGym.route) },
                        onGymClick = { gymId ->
                            navController.navigate("${NavBarRoutes.GymDetail.route}/$gymId")
                        }
                    )
                } else {
                    UserHomeScreen(
                        viewModel = userHomeViewModel,
                        onGymClick = { gymId ->

                            val gym =
                                userHomeViewModel.gyms.find {
                                    it.id == gymId
                                }

                            if (gym != null) {
                                userHomeViewModel.addRecentGym(gym)
                            }

                            navController.navigate("gym_detail/$gymId")
                        }
                    )
                }
            }

            composable(NavBarRoutes.QR.route) { QRScreen(authViewModel) }
            composable(NavBarRoutes.Scanner.route) { ScannerScreen(gymViewModel) }

            composable(NavBarRoutes.Profile.route) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    gymViewModel = gymViewModel,
                    userProfile = authViewModel.userProfile,
                    userHomeViewModel = userHomeViewModel,
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
                    onCreateGym = { navController.navigate(NavBarRoutes.CreateGym.route) },
                    onGymClick = { gymId ->
                        navController.navigate("${NavBarRoutes.GymDetail.route}/$gymId")
                    }
                )
            }

            composable(NavBarRoutes.EditProfile.route) {
                EditProfileScreen(
                    authViewModel = authViewModel,
                    onCloseClick = { navController.popBackStack() },
                    onSaveChangesClick = {
                        scope.launch {
                            authViewModel.cargarPerfil()
                        }
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

            composable(
                route = "${NavBarRoutes.GymDetail.route}/{gymId}",
                arguments = listOf(navArgument("gymId") { type = NavType.StringType })
            ) { backStackEntry ->
                val gymId = backStackEntry.arguments?.getString("gymId")

                // Buscamos el objeto gym en la lista del ViewModel usando el ID
                val gym = if (isEnterprise) {
                    gymViewModel.gyms.find { it.id == gymId }
                } else {
                    userHomeViewModel.gyms.find { it.id == gymId }
                }

                if (gym != null) {
                    GymDetailScreen(
                        gym = gym,
                        isEnterprise = authViewModel.isEnterprise ?: false,
                        onBack = { navController.popBackStack() },
                        gymViewModel = gymViewModel,
                        onManageMembers = { id ->
                            navController.navigate("${NavBarRoutes.ManageMembers.route}/$id")
                        },
                        onEditGym = {
                            navController.navigate(NavBarRoutes.EditGym.route)
                        }
                    )
                }
            }

            composable(
                route = "${NavBarRoutes.ManageMembers.route}/{gymId}",
                arguments = listOf(navArgument("gymId") { type = NavType.StringType })
            ) { backStackEntry ->
                val gymId = backStackEntry.arguments?.getString("gymId") ?: ""
                ManageMembersScreen(
                    gymId = gymId,
                    gymViewModel = gymViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(NavBarRoutes.EditGym.route) {
                EditGymScreen(
                    gymViewModel = gymViewModel,
                    onClose = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() },
                    onDelete = {
                        navController.navigate(NavBarRoutes.Home.route) {
                            popUpTo(NavBarRoutes.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}