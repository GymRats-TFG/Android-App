package com.gymrats.gymratsapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.gymrats.gymratsapp.R

@Composable
fun GymCardSmall(
    name: String,
    currentCapacity: Int,
    imageUrl: String?,
    isOpen: Boolean,
    onClick: () -> Unit
) {
    val poppinsBold = FontFamily(Font(R.font.poppins_bold))
    val poppinsSemiBold = FontFamily(Font(R.font.poppins_semibold))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = remember(imageUrl) {
                    if (imageUrl != null) "${imageUrl}?t=${System.currentTimeMillis()}"
                    else R.drawable.gymrats_logo
                },
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(90.dp),
//                    .clip(RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontFamily = poppinsBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${stringResource(R.string.gym_card_current_capacity).uppercase()}: $currentCapacity",
                    fontSize = 12.sp,
                    fontFamily = poppinsSemiBold,
                    color = Color(0xFF1A45A0)
                )
                Text(
                    text = if (isOpen) stringResource(R.string.open) else stringResource(R.string.closed),
                    fontSize = 10.sp,
                    fontFamily = poppinsSemiBold,
                    color = if (isOpen) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp).size(28.dp),
                tint = Color.LightGray
            )
        }
    }
}