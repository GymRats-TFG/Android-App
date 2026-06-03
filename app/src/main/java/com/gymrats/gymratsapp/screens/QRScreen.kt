package com.gymrats.gymratsapp.screens

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.viewModels.AuthViewModel
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

@Composable
fun QRScreen(authViewModel: AuthViewModel) {
    val user = authViewModel.userProfile
    val poppinsBold = FontFamily(Font(R.font.poppins_bold))
    val poppinsRegular = FontFamily(Font(R.font.poppins_regular))

    val isDarkMode = isSystemInDarkTheme()

    // Generamos el QR a partir del ID del usuario
    val qrBitmap = remember(user?.id) {
        user?.id?.let { generateQRCode(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = stringResource(R.string.qr_register),
            fontSize = 32.sp,
            fontFamily = poppinsBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.qr_scan_code),
            fontSize = 16.sp,
            fontFamily = poppinsRegular,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(280.dp)
                .background(MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(16.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {

            qrBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = stringResource(R.string.qr_code),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                    colorFilter = if (isDarkMode) {
                        ColorFilter.tint(
                            color = com.gymrats.gymratsapp.ui.theme.White,
                            blendMode = BlendMode.Difference
                        )
                    } else {
                        null
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(R.string.qr_user),
            fontSize = 14.sp,
            fontFamily = poppinsBold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = user?.name?.uppercase() ?: stringResource(R.string.qr_user),
            fontSize = 20.sp,
            fontFamily = poppinsBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = user?.id ?: "",
            fontSize = 14.sp,
            fontFamily = poppinsRegular,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.WbSunny,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.qr_increase_brightness),
                    fontSize = 10.sp,
                    fontFamily = poppinsBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun generateQRCode(content: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap[x, y] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
            }
        }
        bitmap
    } catch (_: Exception) {
        null
    }
}