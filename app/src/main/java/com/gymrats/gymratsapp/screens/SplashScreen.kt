package com.gymrats.gymratsapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.data.SessionManager
import com.gymrats.gymratsapp.ui.theme.GymRatsTheme
import com.gymrats.gymratsapp.viewModels.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToAuth: () -> Unit,
    authViewModel: AuthViewModel,
    sessionManager: SessionManager
) {
    LaunchedEffect(Unit) {
        delay(1000)

        val rToken = sessionManager.refreshToken.first()

        if (!rToken.isNullOrEmpty()) {
            authViewModel.cargarPerfil()

            if (authViewModel.success && authViewModel.userProfile != null) {
                onNavigateToMain()
            } else {
                onNavigateToAuth()
            }
        } else {
            onNavigateToAuth()
        }
    }

    GymRatsTheme(dynamicColor = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.gymrats_logo),
                    contentDescription = stringResource(R.string.gymrats_logo),
                    modifier = Modifier.size(312.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}