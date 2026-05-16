package com.gymrats.gymratsapp.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gymrats.gymratsapp.R
import com.gymrats.gymratsapp.viewModels.AuthViewModel
import com.gymrats.gymratsapp.ui.theme.GymRatsTheme
import java.util.Locale

@Composable
fun SignupScreen(
    onSignUp: () -> Unit,
    onBackToLogin: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isEnterprise by remember { mutableStateOf(false) }
    var showEnterpriseDialog by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    // Para evitar realizar más de una vez la misma llamada
    var isLoading by remember { mutableStateOf(false) }

    BackHandler {
        // Navegar de vuelta a la pantalla de login
        // al presionar el botón de retroceso del dispositivo
        onBackToLogin()
    }

    // Observador de errores
    LaunchedEffect(authViewModel.errorMessage) {
        authViewModel.errorMessage?.let { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
        }
        isLoading = false
    }

    // Observador de éxito robusto
    LaunchedEffect(authViewModel.success, authViewModel.isEnterprise) {
        // Solo navegamos si success es true Y isEnterprise ya tiene un valor (true o false)
        if (authViewModel.success && authViewModel.isEnterprise != null) {
            onSignUp()
        }
    }

    // Diálogo de advertencia
    if (showEnterpriseDialog) {
        AlertDialog(
            onDismissRequest = { showEnterpriseDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.enterprise_account),
                    fontFamily = FontFamily(Font(R.font.poppins_semibold))
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.create_enterprise_account_msg),
                    fontFamily = FontFamily(Font(R.font.poppins_regular))
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    isEnterprise = true
                    showEnterpriseDialog = false
                }) {
                    Text(stringResource(R.string.confirm), color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isEnterprise = false
                    showEnterpriseDialog = false
                }) {
                    Text(stringResource(R.string.cancel), color = Color.Gray)
                }
            }
        )
    }

    GymRatsTheme(dynamicColor = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            IconButton(
                onClick = { onBackToLogin() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp, 32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_to_login),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gymrats_logo),
                    contentDescription = stringResource(R.string.gymrats_logo),
                    modifier = Modifier.size(184.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )

                Text(
                    text = stringResource(R.string.create_account),
                    fontSize = 36.sp,
                    fontFamily = FontFamily(Font(R.font.poppins_semibold)),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.email)) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(300.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(stringResource(R.string.username)) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(300.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.password)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = stringResource(R.string.toggle_password_visibility)
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(300.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(R.string.repeat_password)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = stringResource(R.string.toggle_password_visibility)
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(300.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .width(300.dp)
                        .clickable {
                            if (!isEnterprise) showEnterpriseDialog = true
                            else isEnterprise = false
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(
                                color = if (isEnterprise) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 2.dp,
                                color = if (isEnterprise) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isEnterprise) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = stringResource(R.string.create_enterprise_account),
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (!isLoading) {
                            if (password == confirmPassword && email.isNotEmpty() && username.isNotEmpty()) {
                                isLoading = true
                                authViewModel.ejecutarSignup(email, username, password, isEnterprise)
                            } else {
                                val msg = if (password != confirmPassword) getString(
                                    context,
                                    R.string.password_dont_match
                                ) else getString(
                                    context,
                                    R.string.complete_all_fields
                                )
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .width(300.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    } else {
                        Text(
                            text = getString(
                                context,
                                R.string.register
                            ).uppercase(Locale.getDefault()),
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily(Font(R.font.poppins_semibold))
                        )
                    }
                }
            }
        }
    }
}