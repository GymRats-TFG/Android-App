package com.gymrats.gymratsapp.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.components.DashedAddButton
import com.gymrats.gymratsapp.components.EmptyStateCard
import com.gymrats.gymratsapp.components.GymCardSmall
import com.gymrats.gymratsapp.components.SectionTitle
import com.gymrats.gymratsapp.components.StatCard
import com.gymrats.gymratsapp.viewModels.AuthViewModel
import com.gymrats.gymratsapp.data.UserData
import com.gymrats.gymratsapp.ui.theme.GymRatsTheme
import com.gymrats.gymratsapp.viewModels.GymViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    gymViewModel: GymViewModel,
    userProfile: UserData?,
    onLogout: () -> Unit,
    onEditClick: () -> Unit,
    onCreateGym: () -> Unit,
    onGymClick: (String) -> Unit
) {
    val stats = gymViewModel.enterpriseStats
    val gyms = gymViewModel.gyms

    LaunchedEffect(Unit) {
        authViewModel.cargarPerfil()
        if (userProfile?.is_enterprise == true) {
            gymViewModel.cargarStatsEnterprise()
            gymViewModel.cargarSedes()
        }
    }

    LaunchedEffect(authViewModel.errorMessage) {
        if (authViewModel.errorMessage != null) {
            onLogout()
        }
    }

    if (userProfile == null) {
        GymRatsTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        return
    }

    val poppinsBold = FontFamily(Font(R.font.poppins_bold))
    val poppinsRegular = FontFamily(Font(R.font.poppins_regular))

    val isEnterprise = userProfile.is_enterprise

    GymRatsTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                            contentDescription = stringResource(R.string.profile_close_session),
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onLogout() },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = userProfile.avatar_url,
                        contentDescription = stringResource(R.string.profile_avatar_description),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = userProfile.name,
                        fontFamily = poppinsBold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = userProfile.email,
                        fontFamily = poppinsRegular,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { onEditClick() },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.Gray),
                        modifier = Modifier
                            .width(160.dp)
                            .height(40.dp)
                    ) {
                        Text(
                            stringResource(R.string.profile_edit_profile),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontFamily = poppinsBold
                        )
                    }
                }
            }

            if (isEnterprise) {
                item { SectionTitle(stringResource(R.string.section_company_summary)) }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            stringResource(R.string.stat_active_subscribers),
                            value = stats?.active_subscribers?.toString() ?: "0",
                            Modifier.weight(1f)
                        )
                        StatCard(
                            stringResource(R.string.stat_total_capacity),
                            value = stats?.total_current_capacity?.toString() ?: "0",
                            Modifier.weight(1f)
                        )
                    }
                }
                item { SectionTitle(stringResource(R.string.section_your_locations)) }

                if (gyms.isEmpty()) {
                    item { EmptyStateCard(stringResource(R.string.empty_no_locations)) }
                } else {
                    items(gyms.size) { index ->
                        val gym = gyms[index]
                        GymCardSmall(
                            name = gym.name,
                            currentCapacity = gym.current_capacity,
                            imageUrl = gym.image_url,
                            onClick = { onGymClick(gym.id) }
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }

                item {
                    DashedAddButton({ onCreateGym() })
                }
            } else {
                item {
                    SectionTitle(
                        stringResource(R.string.section_activity_log),
                        showVerTodo = true
                    )
                }
                item { EmptyStateCard(stringResource(R.string.empty_no_recent_activity)) }

                item {
                    SectionTitle(
                        stringResource(R.string.stat_active_subscribers),
                        showVerTodo = true
                    )
                }
                item { EmptyStateCard(stringResource(R.string.empty_no_active_subscriptions)) }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}