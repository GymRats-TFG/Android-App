package com.gymrats.gymratsapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.gymrats.gymratsapp.R

@Composable
fun GymCard(
    name: String,
    address: String,
    currentCapacity: Int,
    maxCapacity: Int,
    imageUrl: String?,
    isOpen: Boolean,
    poppinsBold: FontFamily,
    poppinsRegular: FontFamily
) {
    val percentage = if (maxCapacity > 0) currentCapacity.toFloat() / maxCapacity.toFloat() else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = remember(imageUrl) {
                        if (imageUrl != null) {
                            "${imageUrl}?t=${System.currentTimeMillis()}"
                        } else {
                            R.drawable.gymrats_logo
                        }
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd),
                    color = if (isOpen) Color(0xFF2E7D32) else Color(0xFFC62828),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = if (isOpen) stringResource(R.string.open) else stringResource(R.string.closed),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = poppinsBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    name,
                    fontSize = 18.sp,
                    fontFamily = poppinsBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, Modifier.size(14.dp), tint = Color.Gray)
                    Text(address, fontSize = 12.sp, fontFamily = poppinsRegular, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        stringResource(R.string.gym_card_current_capacity),
                        fontSize = 14.sp,
                        fontFamily = poppinsBold
                    )
                    Text(
                        "${(percentage * 100).toInt()}% " + stringResource(R.string.gym_card_full_percentage),
                        color = Color(0xFF1A45A0),
                        fontFamily = poppinsBold
                    )
                }

                LinearProgressIndicator(
                    progress = { percentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = Color.LightGray.copy(alpha = 0.5f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )

                Text(
                    text = "$currentCapacity / $maxCapacity " + stringResource(R.string.gym_card_members_count),
                    fontSize = 10.sp,
                    fontFamily = poppinsRegular,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )
            }
        }
    }
}