package com.gymrats.gymratsapp.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import coil3.compose.AsyncImage
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.viewModels.GymViewModel
import kotlinx.coroutines.launch

@Composable
fun EditGymScreen(
    gymViewModel: GymViewModel,
    onClose: () -> Unit,
    onSuccess: () -> Unit,
    onDelete: () -> Unit
) {
    val gym = gymViewModel.selectedGym ?: return
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val poppinsSemiBold = FontFamily(Font(R.font.poppins_semibold))
    val poppinsRegular = FontFamily(Font(R.font.poppins_regular))
    val poppinsBold = FontFamily(Font(R.font.poppins_bold))

    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    // Estados de edición
    var name by remember { mutableStateOf(gym.name) }
    var description by remember { mutableStateOf(gym.description ?: "") }
    var address by remember { mutableStateOf(gym.address) }
    var phone by remember { mutableStateOf(gym.phone) }
    var email by remember { mutableStateOf(gym.email) }
    var price by remember { mutableStateOf(gym.price.toString()) }
    var maxCapacity by remember { mutableStateOf(gym.max_capacity.toString()) }
    var itemImageUri by remember { mutableStateOf<Uri?>(null) }

    // Eliminar
    var showDeleteDialog by remember { mutableStateOf(false) }

    val hasChanges by remember(
        name,
        description,
        address,
        phone,
        email,
        price,
        maxCapacity,
        itemImageUri
    ) {
        derivedStateOf {
            name != gym.name || description != (gym.description ?: "") ||
                    address != gym.address || phone != gym.phone || email != gym.email ||
                    price != gym.price.toString() || maxCapacity != gym.max_capacity.toString() ||
                    itemImageUri != null
        }
    }

    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) itemImageUri = uri
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .verticalScroll(
                rememberScrollState()
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.edit_gym),
                fontSize = 28.sp,
                fontFamily = poppinsSemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(
                    1f
                )
            )
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Rounded.Delete,
                    null,
                    tint = Color(0xFFC62828),
                    modifier = Modifier.size(22.dp)
                )
            }
            IconButton(onClick = { if (hasChanges) showDialog = true else onClose() }) {
                Icon(
                    Icons.Filled.Close,
                    null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Imagen editable
        Box(contentAlignment = Alignment.BottomEnd) {
            AsyncImage(
                model = remember(gym.image_url) {
                    if (gym.image_url != null) {
                        "${gym.image_url}?t=${System.currentTimeMillis()}"
                    } else {
                        R.drawable.gymrats_logo
                    }
                },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { pickImageLauncher.launch("image/*") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.label_gym_name)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.label_gym_description_optional)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = address,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            onValueChange = { address = it },
            label = { Text(stringResource(R.string.label_gym_address)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = phone,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            onValueChange = { phone = it },
            label = { Text(stringResource(R.string.label_phone)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.label_email_contact)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = price,
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                onValueChange = { price = it },
                label = { Text(stringResource(R.string.label_price_euro)) },
                modifier = Modifier.weight(
                    1f
                )
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = maxCapacity,
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                onValueChange = { maxCapacity = it },
                label = { Text(stringResource(R.string.label_max_capacity)) },
                modifier = Modifier.weight(
                    1f
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    val imageFile = itemImageUri?.let { processAndCompressImage(context, it) }
                    val result = gymViewModel.actualizarSede(
                        gymId = gym.id,
                        name = if (name != gym.name) name else null,
                        description = if (description != gym.description) description else null,
                        address = if (address != gym.address) address else null,
                        phone = if (phone != gym.phone) phone else null,
                        email = if (email != gym.email) email else null,
                        price = price.toDoubleOrNull(),
                        maxCapacity = maxCapacity.toIntOrNull(),
                        imageFile = imageFile
                    )
                    isLoading = false
                    result.onSuccess {
                        Toast.makeText(
                            context,
                            getString(context, R.string.gym_updated),
                            Toast.LENGTH_SHORT
                        ).show()
                        onSuccess()
                    }.onFailure {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            },
            enabled = hasChanges && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            if (isLoading) CircularProgressIndicator(
                color = Color.White, modifier = Modifier.size(
                    24.dp
                )
            )
            else Text(stringResource(R.string.save_changes), fontFamily = poppinsSemiBold)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.changes_not_saved)) },
            text = { Text(stringResource(R.string.discard_changes)) },
            confirmButton = {
                TextButton(onClick = { onClose() }) {
                    Text(
                        stringResource(R.string.dialog_action_discard),
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                }) { Text(stringResource(R.string.keep_editing)) }
            }
        )
    }

    // Eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    stringResource(R.string.delete_gym_delete_gym),
                    fontFamily = poppinsSemiBold
                )
            },
            text = {
                Text(
                    stringResource(R.string.delete_gym_sure_remove_msg1)
                            + " ${gym.name} "
                            + stringResource(R.string.delete_gym_sure_remove_msg2)
                            + " ${gym.address}?",
                    fontFamily = poppinsRegular
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            showDeleteDialog = false
                            val result = gymViewModel.eliminarGym(gym.id)

                            result.onSuccess { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                showDeleteDialog = false
                                onDelete()
                            }.onFailure { error ->
                                Toast.makeText(context, error.message ?: "Error", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                ) {
                    Text(
                        stringResource(R.string.remove),
                        color = Color.Red,
                        fontFamily = poppinsBold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel), fontFamily = poppinsBold)
                }
            }
        )
    }
}