package com.gymrats.gymratsapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gymrats.gymratsapp.R

@Composable
fun SectionTitle(title: String, showVerTodo: Boolean = false, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.poppins_semibold))
        )
        if (showVerTodo) {
            Text(
                text = stringResource(R.string.view_all),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                modifier = Modifier.clickable { onClick() }
            )
        }
    }
}