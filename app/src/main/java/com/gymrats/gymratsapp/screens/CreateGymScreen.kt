package com.gymrats.gymratsapp.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import coil3.compose.AsyncImage
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.viewModels.GymViewModel
import kotlinx.coroutines.launch

@Composable
fun CreateGymScreen(
    gymViewModel: GymViewModel,
    onClose: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val poppinsSemiBold = FontFamily(Font(R.font.poppins_semibold))

    // Estados para los campos
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var maxCapacity by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Validación: Todos obligatorios excepto descripción
    val isFormValid = name.isNotBlank() &&
            address.isNotBlank() &&
            phone.isNotBlank() &&
            email.isNotBlank() &&
            price.isNotBlank() &&
            maxCapacity.isNotBlank() &&
            imageUri != null &&
            !isLoading

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) imageUri = uri }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.create_gym_title),
                        fontSize = 28.sp,
                        fontFamily = poppinsSemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.action_close),
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onClose() },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { pickImageLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.label_add_gym_image) + " *",
                            color = Color.Gray,
                            fontFamily = poppinsSemiBold
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                CustomOutlinedTextField(
                    name,
                    { name = it },
                    stringResource(R.string.label_gym_name)
                )
                Spacer(modifier = Modifier.height(12.dp))

                CustomOutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = stringResource(R.string.label_gym_description_optional),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomOutlinedTextField(
                    address,
                    { address = it },
                    stringResource(R.string.label_gym_address)
                )

                Spacer(modifier = Modifier.height(12.dp))

                CustomOutlinedTextField(
                    phone,
                    { phone = it },
                    stringResource(R.string.label_phone),
                    KeyboardType.Phone
                )
                Spacer(modifier = Modifier.height(12.dp))
                CustomOutlinedTextField(
                    email,
                    { email = it },
                    stringResource(R.string.label_email_contact),
                    KeyboardType.Email
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth()) {
                    Box(Modifier.weight(1f)) {
                        CustomOutlinedTextField(
                            value = price,
                            onValueChange = {
                                if (it.isEmpty() || it.matches(Regex("""^\d*\.?\d*$"""))) price = it
                            },
                            label = stringResource(R.string.label_price_euro),
                            keyboardType = KeyboardType.Decimal
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.weight(1f)) {
                        CustomOutlinedTextField(
                            value = maxCapacity,
                            onValueChange = { if (it.all { c -> c.isDigit() }) maxCapacity = it },
                            label = stringResource(R.string.label_max_capacity_short),
                            keyboardType = KeyboardType.Number
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            val file = imageUri?.let { processAndCompressImage(context, it) }
                            val result = gymViewModel.registrarSede(
                                name = name,
                                description = description,
                                address = address,
                                phone = phone,
                                email = email,
                                price = price.toDoubleOrNull() ?: 0.0,
                                maxCapacity = maxCapacity.toIntOrNull() ?: 0,
                                imageFile = file
                            )
                            isLoading = false
                            result.onSuccess {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.gym_created),
                                    Toast.LENGTH_SHORT
                                ).show()
                                onSuccess()
                            }.onFailure { exception ->
                                val errorMsg = exception.message ?: ""
                                val friendlyError = when {
                                    errorMsg.contains("email", ignoreCase = true) -> {
                                        getString(context,R.string.error_email)
                                    }
                                    errorMsg.contains("body", ignoreCase = true) -> {
                                        getString(context,R.string.error_fields)
                                    }
                                    else -> errorMsg
                                }

                                Toast.makeText(context, friendlyError, Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = isFormValid
                ) {
                    if (isLoading) CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    else Text(
                        stringResource(R.string.button_create_gym),
                        fontFamily = poppinsSemiBold
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = singleLine,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    )
}