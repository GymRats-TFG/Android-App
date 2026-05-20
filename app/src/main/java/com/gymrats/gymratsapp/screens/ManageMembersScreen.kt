package com.gymrats.gymratsapp.screens

import android.os.Build
import android.widget.Toast
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.gymrats.gymratsapp.data.MemberInfoResponse
import com.gymrats.gymratsapp.viewModels.GymViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMembersScreen(
    gymId: String,
    gymViewModel: GymViewModel,
    onBack: () -> Unit
) {
    val poppinsBold = FontFamily(Font(R.font.poppins_bold))
    val poppinsSemiBold = FontFamily(Font(R.font.poppins_semibold))
    val poppinsRegular = FontFamily(Font(R.font.poppins_regular))

    var searchQuery by remember { mutableStateOf("") }

    val members = gymViewModel.gymMembers

    val isRefreshing = gymViewModel.isRefreshing

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isAdding by remember { mutableStateOf(false) }

    // Eliminar
    var memberToDelete by remember { mutableStateOf<MemberInfoResponse?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Editar
    var memberToEdit by remember { mutableStateOf<MemberInfoResponse?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.manage_members_title),
                        fontFamily = poppinsSemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Abrir Cámara/Scanner */ }) {
                        Icon(
                            Icons.Rounded.QrCodeScanner,
                            "Scanner",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                windowInsets = WindowInsets(0, 0, 0, 0),
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { gymViewModel.cargarMiembrosGym(gymId) },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                item {
                    SectionTitle(stringResource(R.string.manage_members_add_new_title))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text(stringResource(R.string.manage_members_search_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            if (isAdding) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                IconButton(
                                    onClick = {
                                        if (searchQuery.isNotBlank()) {
                                            scope.launch {
                                                isAdding = true
                                                val result =
                                                    gymViewModel.vincularSocio(gymId, searchQuery)
                                                isAdding = false

                                                result.onSuccess { msg ->
                                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                                                        .show()
                                                    searchQuery = ""
                                                }.onFailure { err ->
                                                    Toast.makeText(
                                                        context,
                                                        err.message,
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Rounded.PersonAdd,
                                        contentDescription = stringResource(R.string.manage_members_add_button),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    )

                    SectionTitle(stringResource(R.string.manage_members_current_list))
                }

                if (members.isEmpty()) {
                    item {
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
                    }
                } else {
                    items(members.size) { index ->
                        val member = members[index]
                        MemberItem(
                            member = member,
                            showManagementOptions = true,
                            onEditExpiration = {
                                memberToEdit = it
                                showDatePicker = true
                            },
                            onDeleteMember = {
                                memberToDelete = it
                                showDeleteDialog = true
                            }
                        )
                    }
                }

                // Espacio extra al final para scroll cómodo
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }

    // Eliminar
    if (showDeleteDialog && memberToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Eliminar socio",
                    fontFamily = poppinsSemiBold
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar a @${memberToDelete?.username}? Esta acción no se puede deshacer.",
                    fontFamily = poppinsRegular
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            showDeleteDialog = false
                            val result = gymViewModel.eliminarSocio(gymId, memberToDelete!!.subscription_id)

                            result.onSuccess {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }.onFailure {
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            }
                            memberToDelete = null
                        }
                    }
                ) {
                    Text("ELIMINAR", color = Color.Red, fontFamily = poppinsBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("CANCELAR", fontFamily = poppinsBold)
                }
            }
        )
    }

    // Editar
    if (showDatePicker && memberToEdit != null) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        // Convertir milisegundos a String ISO (YYYY-MM-DD)
                        val instant = java.time.Instant.ofEpochMilli(selectedDateMillis)
                        val localDate = java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.of("UTC"))

                        scope.launch {
                            val result = gymViewModel.actualizarSuscripcion(
                                gymId,
                                memberToEdit!!,
                                localDate.toLocalDate().toString()
                            )
                            result.onSuccess {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }.onFailure {
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    showDatePicker = false
                }) {
                    Text("ACEPTAR", fontFamily = poppinsBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("CANCELAR", fontFamily = poppinsBold)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}