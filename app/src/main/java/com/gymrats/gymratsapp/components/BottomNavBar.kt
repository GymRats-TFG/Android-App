package com.gymrats.gymratsapp.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.navigation.NavBarRoutes
import com.gymrats.gymratsapp.viewModels.AuthViewModel

@Composable
fun BottomNavBar(
    navController: NavController,
    isEnterprise: Boolean,
    authViewModel: AuthViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val poppinsSemiBold = FontFamily(Font(R.font.poppins_semibold))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
        shadowElevation = 24.dp,
        color = MaterialTheme.colorScheme.onBackground
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            tonalElevation = 0.dp
        ) {
            val homeSelected = currentRoute == NavBarRoutes.Home.route
            NavigationBarItem(
                selected = homeSelected,
                onClick = {
                    if (!authViewModel.isLoading) {
                        navigateTo(navController, NavBarRoutes.Home.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.home),
                        fontFamily = poppinsSemiBold,
                        fontSize = 11.sp
                    )
                },
                colors = navigationItemColors()
            )

            val middleRoute =
                if (isEnterprise) NavBarRoutes.Scanner.route else NavBarRoutes.QR.route
            val middleLabel =
                if (isEnterprise) stringResource(R.string.scanner) else stringResource(R.string.my_qr)
            val middleIcon = if (isEnterprise) Icons.Default.QrCodeScanner else Icons.Default.QrCode
            val middleSelected = currentRoute == middleRoute

            NavigationBarItem(
                selected = middleSelected,
                onClick = {
                    if (!authViewModel.isLoading) {
                        navigateTo(navController, middleRoute)
                    }
                },
                icon = {
                    Icon(
                        imageVector = middleIcon,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = {
                    Text(
                        text = middleLabel,
                        fontFamily = poppinsSemiBold,
                        fontSize = 11.sp
                    )
                },
                colors = navigationItemColors()
            )

            val profileSelected = currentRoute == NavBarRoutes.Profile.route
            NavigationBarItem(
                selected = profileSelected,
                onClick = {
                    if (!authViewModel.isLoading) {
                        navigateTo(navController, NavBarRoutes.Profile.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.profile),
                        fontFamily = poppinsSemiBold,
                        fontSize = 11.sp
                    )
                },
                colors = navigationItemColors()
            )
        }
    }
}

@Composable
private fun navigationItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    indicatorColor = Color(0x55FF4500),
    unselectedIconColor = Color.Gray,
    unselectedTextColor = Color.Gray
)

private fun navigateTo(navController: NavController, route: String) {
    if (navController.currentBackStackEntry?.destination?.route == route) return

    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}