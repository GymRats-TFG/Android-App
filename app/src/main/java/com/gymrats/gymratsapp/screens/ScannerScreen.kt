package com.gymrats.gymratsapp.screens

import androidx.camera.core.CameraSelector
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.rounded.FlipCameraAndroid
import androidx.compose.material3.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.components.QRScannerView
import com.gymrats.gymratsapp.data.GymResponse
import com.gymrats.gymratsapp.data.ScanResponse
import com.gymrats.gymratsapp.viewModels.GymViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(gymViewModel: GymViewModel) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val gyms = gymViewModel.gyms
    var selectedGym by remember { mutableStateOf<GymResponse?>(null) }
    var showGymDialog by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<ScanResponse?>(null) }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }

    var isScanningBlocked by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val poppinsRegular = FontFamily(Font(R.font.poppins_regular))
    val poppinsSemiBold = FontFamily(Font(R.font.poppins_semibold))
    val poppinsBold = FontFamily(Font(R.font.poppins_bold))

    LaunchedEffect(Unit) {
        if (gyms.isEmpty()) gymViewModel.cargarSedes(true)
    }

    LaunchedEffect(gyms) {
        if (selectedGym == null && gyms.isNotEmpty()) {
            selectedGym = gyms.first()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        Box(modifier = Modifier.fillMaxSize()) {

            var canShowCamera by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(300)
                canShowCamera = true
            }

            if (canShowCamera) {
                QRScannerView(lensFacing = lensFacing) { userId ->
                    if (!isScanningBlocked) {
                        selectedGym?.let { gym ->
                            scope.launch {
                                isScanningBlocked = true
                                val result = gymViewModel.procesarEscaneo(gym.id, userId)
                                result.onSuccess {
                                    scanResult = it
                                }.onFailure {
                                    isScanningBlocked = false
                                }
                            }
                        }
                    }
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val boxSize = 320.dp.toPx()

                with(drawContext.canvas.nativeCanvas) {
                    val checkPoint = saveLayer(null, null)
                    drawRect(Color.Black.copy(alpha = 0.7f))
                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = Offset(x = (canvasWidth - boxSize) / 2, y = (canvasHeight - boxSize) / 2),
                        size = Size(boxSize, boxSize),
                        cornerRadius = CornerRadius(24.dp.toPx()),
                        blendMode = BlendMode.Clear
                    )
                    restoreToCount(checkPoint)
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.scanner),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 32.sp,
                        fontFamily = poppinsBold
                    )

                    IconButton(
                        onClick = {
                            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                        },
                        modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.FlipCameraAndroid,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Card(
                    onClick = { showGymDialog = true },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D).copy(alpha = 0.9f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.White)
                        Text(
                            text = selectedGym?.name ?: stringResource(R.string.select_gym),
                            color = Color.White,
                            fontFamily = poppinsSemiBold,
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                        )
                        Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
                    }
                }
            }

            scanResult?.let { result ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp,)
                        .zIndex(1f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Surface(
                        color = if (result.success) Color(0xFF2E7D32) else Color(0xFFC62828),
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 10.dp,
                        modifier = Modifier.fillMaxWidth().padding(bottom=18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
//                            Text(
//                                text = if (result.success) stringResource(R.string.access_granted)
//                                else stringResource(R.string.access_denied),
//                                color = Color.White,
//                                fontFamily = poppinsBold,
//                                fontSize = 18.sp
//                            )
//                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = result.message,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 18.sp,
                                fontFamily = poppinsSemiBold
                            )
                            if (result.user_name != null) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "${stringResource(R.string.partner)}: ${result.user_name}",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontFamily = poppinsSemiBold
                                )
                            }
                        }
                    }
                }

                LaunchedEffect(result) {
                    delay(4000)
                    scanResult = null
                    isScanningBlocked = false
                }
            }
        }

        if (showGymDialog) {
            AlertDialog(
                onDismissRequest = { showGymDialog = false },
                title = { Text(stringResource(R.string.select_location), fontFamily = poppinsBold) },
                text = {
                    Column {
                        gyms.forEach { gym ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { selectedGym = gym; showGymDialog = false }.padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = (gym.id == selectedGym?.id), onClick = null)
                                Spacer(Modifier.width(12.dp))
                                Text(gym.name, fontFamily = poppinsSemiBold)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showGymDialog = false }) {
                        Text(stringResource(R.string.action_close), fontFamily = poppinsBold)
                    }
                }
            )
        }
    } else {
        // Pantalla de Permisos
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = stringResource(R.string.scanner), color = MaterialTheme.colorScheme.primary, fontSize = 32.sp, fontFamily = poppinsBold)
            Text(text = stringResource(R.string.camera_needed), modifier = Modifier.padding(16.dp))
            Button(
                onClick = { cameraPermissionState.launchPermissionRequest() },
                modifier = Modifier.width(300.dp).height(52.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = stringResource(R.string.grant_permission).uppercase(), fontFamily = poppinsSemiBold)
            }
        }
    }
}