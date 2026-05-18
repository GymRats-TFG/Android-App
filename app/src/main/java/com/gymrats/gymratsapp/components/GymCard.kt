package com.gymrats.gymratsapp.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.gymrats.gymratsapp.R

@Composable
fun SedeCard(
    name: String,
    address: String,
    currentCapacity: Int,
    maxCapacity: Int,
    imageUrl: String?,
    poppinsBold: FontFamily,
    poppinsRegular: FontFamily
) {
    val percentage = if (maxCapacity > 0) currentCapacity.toFloat() / maxCapacity.toFloat() else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column {
            AsyncImage(
                model = imageUrl ?: R.drawable.gymrats_logo, // Imagen por defecto si no hay URL
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.gymrats_logo)
            )

            Column(modifier = Modifier.padding(20.dp)) {
                Text(name, fontSize = 18.sp, fontFamily = poppinsBold, color = MaterialTheme.colorScheme.onSurface)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, Modifier.size(14.dp), tint = Color.Gray)
                    Text(address, fontSize = 12.sp, fontFamily = poppinsRegular, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Capacidad Actual", fontSize = 14.sp, fontFamily = poppinsBold)
                    Text("${(percentage * 100).toInt()}% Full", color = Color(0xFF1A45A0), fontFamily = poppinsBold)
                }

                LinearProgressIndicator(
                    progress = { percentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = Color(0xFF1A45A0),
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )

                Text(
                    text = "$currentCapacity / $maxCapacity miembros",
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