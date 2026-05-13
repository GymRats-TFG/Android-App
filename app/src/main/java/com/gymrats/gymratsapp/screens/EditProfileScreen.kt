package com.gymrats.gymratsapp.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import coil3.compose.AsyncImage
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.ViewModels.AuthViewModel
import com.gymrats.gymratsapp.ui.theme.GymRatsTheme
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    authViewModel: AuthViewModel,
    onCloseClick: () -> Unit,
    onSaveChangesClick: () -> Unit
) {
    val user = authViewModel.userProfile
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val poppinsSemiBold = FontFamily(Font(R.font.poppins_semibold))

    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    // Valores editables
    var name by remember { mutableStateOf(user?.name ?: "") }
    var username by remember { mutableStateOf(user?.username ?: "") }
    var itemImageUri by remember { mutableStateOf<Uri?>(null) }

    val hasChanges by remember(name, username, itemImageUri) {
        derivedStateOf {
            name != (user?.name ?: "") || username != (user?.username ?: "") || itemImageUri != null
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) itemImageUri = uri }

    GymRatsTheme(dynamicColor = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.edit_profile_title),
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
                        .clickable { if (hasChanges) showDialog = true else onCloseClick() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Avatar editable
            Box(contentAlignment = Alignment.BottomEnd) {
                AsyncImage(
                    model = itemImageUri ?: user?.avatar_url ?: R.drawable.gymrats_logo,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .clickable { pickImageLauncher.launch("image/*") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.label_full_name)) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.label_username)) },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true

                        val imageFile = itemImageUri?.let { uriToFile(context, it) }

                        val result = authViewModel.updateUserProfile(
                            newUsername = if (username != user?.username) username else null,
                            newName = if (name != user?.name) name else null,
                            imageFile = imageFile
                        )

                        isLoading = false

                        result.onSuccess {
                            Toast.makeText(context, getString(context, R.string.toast_profile_updated), Toast.LENGTH_SHORT).show()
                            onSaveChangesClick()
                        }.onFailure { error ->
                            Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                        }
                    }
                },
                enabled = hasChanges && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.button_save_changes), fontFamily = poppinsSemiBold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(stringResource(R.string.dialog_title_unsaved_changes)) },
                text = { Text(stringResource(R.string.dialog_message_discard)) },
                confirmButton = {
                    TextButton(onClick = { showDialog = false; onCloseClick() }) {
                        Text(stringResource(R.string.dialog_action_discard), color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text(stringResource(R.string.dialog_action_continue_editing))
                    }
                }
            )
        }
    }
}

fun uriToFile(context: android.content.Context, uri: Uri): java.io.File? {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return null
    val tempFile = java.io.File.createTempFile("avatar_", ".png", context.cacheDir)
    tempFile.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return tempFile
}