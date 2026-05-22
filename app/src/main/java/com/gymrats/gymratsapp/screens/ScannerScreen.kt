package com.gymrats.gymratsapp.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.core.content.ContextCompat.getString
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
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(gymViewModel: GymViewModel) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val gyms = gymViewModel.gyms
    var selectedGym by remember { mutableStateOf<GymResponse?>(null) }
    var showGymDialog by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<ScanResponse?>(null) }

    // BLOQUEO DE ESCANEO: Evita que lea mientras procesa o muestra resultado
    var isScanningBlocked by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val poppinsBold = FontFamily(Font(R.font.poppins_bold))
    val poppinsSemiBold = FontFamily(Font(R.font.poppins_semibold))

    LaunchedEffect(Unit) {
        if (gyms.isEmpty()) gymViewModel.cargarSedes()
    }

    LaunchedEffect(gyms) {
        if (selectedGym == null && gyms.isNotEmpty()) {
            selectedGym = gyms.first()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        Box(modifier = Modifier.fillMaxSize()) {

            QRScannerView { userId ->
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

            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val boxSize = 320.dp.toPx()

                with(drawContext.canvas.nativeCanvas) {
                    val checkPoint = saveLayer(null, null)

                    drawRect(Color.Black.copy(alpha = 0.7f))

                    drawRoundRect(
                        color = Color.Transparent,
                        topLeft = Offset(
                            x = (canvasWidth - boxSize) / 2,
                            y = (canvasHeight - boxSize) / 2
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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.scanner),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 32.sp,
                    fontFamily = poppinsBold
                )

                Spacer(Modifier.height(20.dp))

                Card(
                    onClick = { showGymDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D).copy(alpha = 0.9f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, null, tint = Color.White)
                        Text(
                            text = selectedGym?.name ?: stringResource(R.string.select_gym),
                            color = Color.White,
                            fontFamily = poppinsSemiBold,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        )
                        Icon(Icons.Default.ArrowDropDown, null, tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                scanResult?.let { result ->
                    Surface(
                        color = if (result.success) Color(0xFF2E7D32) else Color(0xFFC62828),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (result.success) stringResource(R.string.access_granted) else stringResource(
                                    R.string.access_denied
                                ),
                                color = Color.White,
                                fontFamily = poppinsBold
                            )
                            Text(
                                text = result.message,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp
                            )
                            if (result.user_name != null) {
                                Text(
                                    text = stringResource(R.string.partner) + ": ${result.user_name}",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontFamily = poppinsSemiBold
                                )
                            }
                        }
                    }

                    // desbloqueamos el escáner
                    LaunchedEffect(result) {
                        delay(4000)
                        scanResult = null
                        isScanningBlocked = false
                    }
                }
            }
        }

        if (showGymDialog) {
            AlertDialog(
                onDismissRequest = { showGymDialog = false },
                title = {
                    Text(
                        stringResource(R.string.select_location),
                        fontFamily = poppinsBold
                    )
                },
                text = {
                    Column {
                        gyms.forEach { gym ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedGym = gym; showGymDialog = false }
                                    .padding(vertical = 12.dp),
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
        // Pantalla de Permiso
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.scanner),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp,
                fontFamily = poppinsBold
            )
            Text(
                text = stringResource(R.string.camera_needed),
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = { cameraPermissionState.launchPermissionRequest() },
                modifier = Modifier
                    .width(300.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.grant_permission).uppercase(Locale.getDefault()),
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = FontFamily(Font(R.font.poppins_semibold))
                )
            }
        }
    }
}