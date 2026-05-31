package com.gymrats.gymratsapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.components.GymCard
import com.gymrats.gymratsapp.components.GymCardSmall
import com.gymrats.gymratsapp.components.SectionTitle
import com.gymrats.gymratsapp.viewModels.AuthViewModel
import com.gymrats.gymratsapp.viewModels.GymViewModel
import com.gymrats.gymratsapp.viewModels.UserHomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    viewModel: UserHomeViewModel,
    authViewModel: AuthViewModel,
    gymViewModel: GymViewModel,
    onGymClick: (String) -> Unit
) {
    val user = authViewModel.userProfile
    val subscriptions = viewModel.subscriptions
    val allGyms = gymViewModel.gyms
    val isRefreshing = viewModel.isLoading || gymViewModel.isRefreshing

    val poppinsBold = FontFamily(Font(R.font.poppins_bold))
    val poppinsRegular = FontFamily(Font(R.font.poppins_regular))

    // Filtrar suscripciones activas
    val activeSubscriptions = subscriptions.filter { it.status == "active" }

    // Filtrar los gimnasios que NO tenemos activos para la sección "Descubre"
    // Así no se repiten los gimnasios arriba y abajo
    val discoveryGyms = allGyms.filter { gym ->
        activeSubscriptions.none { sub -> sub.gym.id == gym.id }
    }

    LaunchedEffect(Unit) {
        viewModel.loadSubscriptions()
        gymViewModel.cargarSedes(false)
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            viewModel.loadSubscriptions()
            gymViewModel.cargarSedes(false)
        },
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val welcomeText = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append(stringResource(R.string.welcome_hello))
                            }
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                                append(user?.name?.split(" ")?.firstOrNull() ?: stringResource(R.string.business_man))
                            }
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append("!")
                            }
                        }
                        Text(text = welcomeText, fontSize = 32.sp, fontFamily = poppinsBold)
                    }
                }
            }

            if (activeSubscriptions.isNotEmpty()) {
                item {
                    SectionTitle(
                        title = stringResource(R.string.active_gyms),
                        showVerTodo = false
                    )
                }

                items(activeSubscriptions) { sub ->
                    Box(modifier = Modifier.clickable {
                        onGymClick(sub.gym.id)
                    }) {
                        GymCard(
                            name = sub.gym.name,
                            address = sub.gym.address,
                            currentCapacity = sub.gym.current_capacity,
                            maxCapacity = sub.gym.max_capacity,
                            imageUrl = sub.gym.image_url,
                            isOpen = sub.gym.is_open,
                            poppinsBold = poppinsBold,
                            poppinsRegular = poppinsRegular
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                SectionTitle(
                    title = stringResource(R.string.discover),
                    showVerTodo = false
                )
            }

            items(discoveryGyms) { gym ->
                Box(modifier = Modifier.clickable {
                    onGymClick(gym.id)
                }) {
                    GymCardSmall(
                        name = gym.name,
                        currentCapacity = gym.current_capacity,
                        imageUrl = gym.image_url,
                        isOpen = gym.is_open,
                        onClick = { onGymClick(gym.id) }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}