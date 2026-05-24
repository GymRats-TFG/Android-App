package com.gymrats.gymratsapp.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FlipCameraAndroid
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.components.MemberItem
import com.gymrats.gymratsapp.components.QRScannerView
import com.gymrats.gymratsapp.components.SectionTitle
import com.gymrats.gymratsapp.data.MemberInfoResponse
import com.gymrats.gymratsapp.viewModels.GymViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.text.contains

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
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

    // Escáner
    var showScanner by remember { mutableStateOf(false) }
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var canShowCameraView by remember { mutableStateOf(false) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }

    // Eliminar
    var memberToDelete by remember { mutableStateOf<MemberInfoResponse?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Editar
    var memberToEdit by remember { mutableStateOf<MemberInfoResponse?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Filtrar
    var filterQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val filteredMembers = remember(filterQuery, members) {
        if (filterQuery.isEmpty()) {
            members
        } else {
            members.filter {
                it.username.contains(filterQuery, ignoreCase = true) ||
                        it.id.contains(filterQuery, ignoreCase = true) ||
                        (it.name?.contains(filterQuery, ignoreCase = true) ?: false)
            }
        }
    }

    Scaffold(
        topBar = if (!showScanner) {
            {
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
                        IconButton(onClick = {
                            if (cameraPermissionState.status.isGranted) {
                                showScanner = true
                            } else {
                                cameraPermissionState.launchPermissionRequest()
                            }
                        }) {
                            Icon(
                                Icons.Rounded.QrCodeScanner,
                                stringResource(R.string.scanner),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                    windowInsets = WindowInsets(0, 0, 0, 0),
                )
            }
        } else {
            {}
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                filterQuery = ""
                isSearchExpanded = false
                gymViewModel.cargarMiembrosGym(gymId)
            },
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
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.manage_members_current_list),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp,
                            fontFamily = poppinsSemiBold
                        )
                        IconButton(onClick = {
                            isSearchExpanded = !isSearchExpanded
                            filterQuery = ""
                        }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(R.string.manage_members_filter),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (isSearchExpanded) {
                        OutlinedTextField(
                            value = filterQuery,
                            onValueChange = { filterQuery = it },
                            label = { Text(stringResource(R.string.manage_members_filter)) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    filterQuery = ""
                                    isSearchExpanded = false
                                }) {
                                    Icon(Icons.Default.Close, null, modifier = Modifier.size(20.dp))
                                }
                            }
                        )
                    }

                    Spacer(Modifier.height(8.dp))
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
                    if (filteredMembers.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(
                                    if (filterQuery.isEmpty()) stringResource(R.string.gym_detail_subs_list)
                                    else stringResource(R.string.manage_members_no_results),
                                    fontSize = 14.sp,
                                    fontFamily = poppinsRegular,
                                    modifier = Modifier.padding(16.dp),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    } else {
                        items(filteredMembers.size) { index ->
                            val member = filteredMembers[index]
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
                }

                // Espacio extra al final para scroll cómodo
                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }

        // Escáner
        if (showScanner && cameraPermissionState.status.isGranted) {
            LaunchedEffect(Unit) {
                delay(300)
                canShowCameraView = true
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                if (canShowCameraView) {
                    QRScannerView(lensFacing = lensFacing) { qrCode ->
                        if (!isAdding) {
                            scope.launch {
                                isAdding = true
                                val result = gymViewModel.vincularSocio(gymId, qrCode)
                                isAdding = false
                                result.onSuccess {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                    showScanner = false
                                }.onFailure {
                                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                                    showScanner = false
                                }
                            }
                        }
                    }
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val boxSize = 280.dp.toPx()
                    with(drawContext.canvas.nativeCanvas) {
                        val checkPoint = saveLayer(null, null)
                        drawRect(Color.Black.copy(alpha = 0.7f))
                        drawRoundRect(
                            color = Color.Transparent,
                            topLeft = Offset(
                                (canvasWidth - boxSize) / 2,
                                (canvasHeight - boxSize) / 2
                            ),
                            size = Size(boxSize, boxSize),
                            cornerRadius = CornerRadius(24.dp.toPx()),
                            blendMode = BlendMode.Clear
                        )
                        restoreToCount(checkPoint)
                    }
                }


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(48.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.scanner),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 32.sp,
                            fontFamily = poppinsBold
                        )

                        Row(
                            modifier = Modifier.width(104.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    lensFacing =
                                        if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                            CameraSelector.LENS_FACING_FRONT
                                        } else {
                                            CameraSelector.LENS_FACING_BACK
                                        }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.FlipCameraAndroid,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(
                                onClick = { showScanner = false },
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(Icons.Default.Close, null, tint = Color.White)
                            }
                        }
                    }

                    Text(
                        text = stringResource(R.string.scan_qr),
                        color = Color.White,
                        fontFamily = poppinsSemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    )
                }
            }
        }
    }

    // Eliminar
    if (showDeleteDialog && memberToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    stringResource(R.string.manager_members_remove_sub),
                    fontFamily = poppinsSemiBold
                )
            },
            text = {
                Text(
                    stringResource(R.string.manager_members_sure_remove_msg1) +
                            " @${memberToDelete?.username}? "
                            + stringResource(R.string.manager_members_sure_remove_msg2),
                    fontFamily = poppinsRegular
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            showDeleteDialog = false
                            val result =
                                gymViewModel.eliminarSocio(gymId, memberToDelete!!.subscription_id)

                            result.onSuccess {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }.onFailure {
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            }
                            memberToDelete = null
                        }
                    }
                ) {
                    Text(
                        stringResource(R.string.remove),
                        color = Color.Red,
                        fontFamily = poppinsBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel), fontFamily = poppinsBold)
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
                        val localDate =
                            java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.of("UTC"))

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
                    Text(stringResource(R.string.accept), fontFamily = poppinsBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel), fontFamily = poppinsBold)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}