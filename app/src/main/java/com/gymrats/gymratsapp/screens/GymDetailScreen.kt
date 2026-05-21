package com.gymrats.gymratsapp.screens

import androidx.activity.result.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.components.MemberItem
import com.gymrats.gymratsapp.components.SectionTitle
import com.gymrats.gymratsapp.data.GymResponse
import com.gymrats.gymratsapp.viewModels.GymViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GymDetailScreen(
    gym: GymResponse,
    isEnterprise: Boolean,
    onBack: () -> Unit,
    gymViewModel: GymViewModel,
    onManageMembers: (String) -> Unit,
    onEditGym: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val poppinsBold = FontFamily(Font(R.font.poppins_bold))
    val poppinsRegular = FontFamily(Font(R.font.poppins_regular))
    val poppinsSemiBold = FontFamily(Font(R.font.poppins_semibold))

    val currentGym = gymViewModel.selectedGym ?: gym
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(gym.id) {
        if (isEnterprise) {
            gymViewModel.cargarMiembrosGym(gym.id)
        }
    }

    val members = gymViewModel.gymMembers
    val isRefreshing = gymViewModel.isRefreshing

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.gym_detail_title),
                        fontFamily = poppinsSemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    if (isEnterprise) {
                        IconButton(onClick = {
                            gymViewModel.cargarDatosDetalle(gym.id, true)
                            onEditGym(gym.id)
                        }) {
                            Icon(
                                Icons.Rounded.Edit,
                                contentDescription = stringResource(R.string.gym_detail_edit_button),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(onClick = {
                            scope.launch {
                                gymViewModel.toggleEstadoSede(currentGym.id)
                            }
                        }) {
                            Icon(
                                imageVector = if (currentGym.is_open) Icons.Default.LockOpen else Icons.Default.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { gymViewModel.cargarDatosDetalle(gym.id, isEnterprise) },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = remember(gym.image_url) {
                            if (gym.image_url != null) {
                                "${gym.image_url}?t=${System.currentTimeMillis()}"
                            } else {
                                R.drawable.gymrats_logo
                            }
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )

                    Surface(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopEnd),
                        color = if (currentGym.is_open) Color(0xFF2E7D32) else Color(0xFFC62828),
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = if (currentGym.is_open) stringResource(R.string.open) else stringResource(R.string.closed),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontFamily = poppinsBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = gym.name,
                        fontSize = 28.sp,
                        fontFamily = poppinsBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            null,
                            Modifier.size(18.dp),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            gym.address,
                            fontSize = 14.sp,
                            fontFamily = poppinsRegular,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val percentage =
                        if (gym.max_capacity > 0) gym.current_capacity.toFloat() / gym.max_capacity.toFloat() else 0f

                    Text(
                        stringResource(R.string.gym_card_current_capacity),
                        fontSize = 14.sp,
                        fontFamily = poppinsBold
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { percentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = Color.LightGray.copy(alpha = 0.4f),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    Text(
                        text = "${gym.current_capacity} / ${gym.max_capacity} ${stringResource(R.string.gym_card_members_count)}",
                        fontSize = 12.sp,
                        fontFamily = poppinsRegular,
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp)
                    )

                    HorizontalDivider(
                        Modifier.padding(vertical = 8.dp),
                        thickness = 0.5.dp,
                        color = Color.LightGray
                    )

                    SectionTitle(stringResource(R.string.gym_detail_section_info))
                    Text(
                        text = gym.description
                            ?: stringResource(R.string.gym_detail_no_description),
                        fontSize = 15.sp,
                        fontFamily = poppinsRegular,
                        modifier = Modifier.padding(bottom = 12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    InfoBadge(
                        icon = Icons.Default.Payments,
                        text = gym.price.toString() + "€ / mes",
                        fontFamily = poppinsBold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    SectionTitle(stringResource(R.string.gym_detail_contact_info))
                    ContactItem(Icons.Default.Email, gym.email, poppinsRegular)
                    ContactItem(Icons.Default.Phone, gym.phone, poppinsRegular)

                    if (isEnterprise) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionTitle(
                                stringResource(R.string.gym_detail_section_members),
                                true,
                                { onManageMembers(gym.id) })
                        }
                        if (members.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(
                                    stringResource(R.string.gym_detail_subs_list),
                                    fontSize = 14.sp,
                                    fontFamily = poppinsRegular,
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        } else {
                            members.forEach { member ->
                                MemberItem(member)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

@Composable
fun InfoBadge(icon: ImageVector, text: String, fontFamily: FontFamily, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.08f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(icon, null, Modifier.size(24.dp), tint = color)
            Spacer(Modifier.width(12.dp))
            Text(text, fontSize = 16.sp, fontFamily = fontFamily, color = color)
        }
    }
}

@Composable
fun ContactItem(icon: ImageVector, text: String, fontFamily: FontFamily) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Icon(icon, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(16.dp))
        Text(
            text,
            fontSize = 15.sp,
            fontFamily = fontFamily,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}