package com.gymrats.gymratsapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.components.DashedAddButton
import com.gymrats.gymratsapp.components.SectionTitle
import com.gymrats.gymratsapp.components.GymCard
import com.gymrats.gymratsapp.viewModels.AuthViewModel
import com.gymrats.gymratsapp.viewModels.GymViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterpriseHomeScreen(
    authViewModel: AuthViewModel,
    gymViewModel: GymViewModel,
    onCreateGym: () -> Unit,
    onGymClick: (String) -> Unit
) {
    val user = authViewModel.userProfile
    val gyms = gymViewModel.gyms
    val isRefreshing = gymViewModel.isRefreshing

    val poppinsBold = FontFamily(Font(R.font.poppins_bold))
    val poppinsRegular = FontFamily(Font(R.font.poppins_regular))

    // Cargar sedes al iniciar
    LaunchedEffect(Unit) {
        gymViewModel.cargarSedes()
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { gymViewModel.cargarSedes() },
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

                val welcomeText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(stringResource(R.string.welcome_hello)+" ")
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append(user?.name ?: stringResource(R.string.business_man))
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("!")
                    }
                }
                Text(text = welcomeText, fontSize = 28.sp, fontFamily = poppinsBold)

                SectionTitle(stringResource(R.string.section_your_locations))
            }

            if (gyms.isEmpty() && !isRefreshing) {
                item {
                    Text(
                        stringResource(R.string.empty_no_locations),
                        fontFamily = poppinsRegular,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                }
            } else {
                items(gyms.size) { index ->
                    val gym = gyms[index]
                    Box(modifier = Modifier.clickable {
                        onGymClick(gym.id)
                    }) {
                        GymCard(
                            name = gym.name,
                            address = gym.address,
                            currentCapacity = gym.current_capacity,
                            maxCapacity = gym.max_capacity,
                            imageUrl = gym.image_url,
                            poppinsBold = poppinsBold,
                            poppinsRegular = poppinsRegular
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                DashedAddButton({ onCreateGym() })
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}