package com.gymrats.gymratsapp.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivityCard(
    gymName: String,
    recordedAt: String,
    entryTime: String?,
    exitTime: String? = null,
    poppinsBold: FontFamily,
    poppinsRegular: FontFamily
) {
    // Parseo de fecha
    val date = ZonedDateTime.parse(recordedAt)
    val day = date.dayOfMonth.toString()
    val month = date.month.getDisplayName(TextStyle.SHORT, Locale("es", "ES")).uppercase().take(3)

    Card(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(80.dp)
                    .background(Color.Gray),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(month, fontSize = 14.sp, fontFamily = poppinsBold, color = MaterialTheme.colorScheme.secondary)
                Text(day, fontSize = 22.sp, fontFamily = poppinsBold, color = MaterialTheme.colorScheme.secondary)
            }

            Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                Text(gymName, fontSize = 18.sp, fontFamily = poppinsBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (entryTime != null) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(entryTime, fontSize = 14.sp, fontFamily = poppinsRegular, color = Color.Gray)
                    }
                    if (exitTime != null) {
                        Spacer(Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .graphicsLayer(rotationZ = 180f),
                            tint = Color.Gray
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(exitTime, fontSize = 14.sp, fontFamily = poppinsRegular, color = Color.Gray)
                    }
                }
            }
        }
    }
}