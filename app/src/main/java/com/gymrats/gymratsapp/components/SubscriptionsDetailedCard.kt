package com.gymrats.gymratsapp.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymrats.gymratsapp.R

@Composable
fun SubscriptionDetailedCard(
    subName: String,
    gymAddress: String,
    startDate: String,
    endDate: String,
    poppinsBold: FontFamily,
    poppinsRegular: FontFamily
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Column {
                Text(subName, fontSize = 20.sp, fontFamily = poppinsBold)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, Modifier.size(14.dp), tint = Color.Gray)
                    Text(
                        text = gymAddress,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontFamily = poppinsRegular
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.start),
                        fontSize = 10.sp,
                        color = Color.LightGray,
                        fontFamily = poppinsBold
                    )
                    Text(startDate, fontSize = 15.sp, fontFamily = poppinsBold)
                }
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.end), fontSize = 10.sp, color = Color.LightGray, fontFamily = poppinsBold)
                    Text(endDate, fontSize = 15.sp, fontFamily = poppinsBold)
                }
            }
        }
    }
}