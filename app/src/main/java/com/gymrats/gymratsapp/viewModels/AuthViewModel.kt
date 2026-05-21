package com.gymrats.gymratsapp.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gymrats.gymratsapp.data.RefreshRequest
import com.gymrats.gymratsapp.data.SessionManager
import com.gymrats.gymratsapp.data.SignupRequest
import com.gymrats.gymratsapp.data.UserData
import com.gymrats.gymratsapp.remote.RetrofitClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class AuthViewModel(application: Application, private val sessionManager: SessionManager) :
    AndroidViewModel(application) {

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var success by mutableStateOf(false)
        private set

    var userProfile by mutableStateOf<UserData?>(null)
        private set

    var isEnterprise by mutableStateOf(null as Boolean?)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun ejecutarLogin(email: String, pass: String) {
        success = false
        errorMessage = null
        isEnterprise = null
        viewModelScope.launch {
            try {
                // Llamamos directamente con los parámetros @Field
                val response = RetrofitClient.instance.login(email, pass)

                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    if (loginResponse != null) {
                        val isEnt = loginResponse.user.is_enterprise

                        sessionManager.saveToken(loginResponse.access_token)
                        sessionManager.saveRefreshToken(loginResponse.refresh_token)
                        sessionManager.saveIsEnterprise(isEnt)

                        isEnterprise = isEnt
                        userProfile = loginResponse.user

                        success = true
                    }
                } else {
                    errorMessage =
                        "Error en login: ${response.code()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            }
        }
    }

    fun ejecutarSignup(email: String, username: String, pass: String, isEnterprise: Boolean) {
        success = false
        errorMessage = null
        this@AuthViewModel.isEnterprise = null
        viewModelScope.launch {
            try {
                val registro = SignupRequest(email, username, pass, isEnterprise)
                val response = RetrofitClient.instance.signup(registro)

                if (response.isSuccessful) {
                    val userData = response.body()
                    if (userData != null) {
                        val isEnt = userData.is_enterprise

                        sessionManager.saveToken(userData.access_token)
                        sessionManager.saveRefreshToken(userData.refresh_token)
                        sessionManager.saveIsEnterprise(userData.is_enterprise)

                        this@AuthViewModel.isEnterprise = isEnt
                        userProfile = userData.user

                        success = true
                    }
                } else {
                    errorMessage = "Error en registro: Usuario existente o datos inválidos"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            }
        }
    }

    suspend fun cargarPerfil() {
        if (isLoading) return

        isLoading = true
        success = false
        errorMessage = null

        try {
            val currentRefreshToken = sessionManager.refreshToken.first()

            if (!currentRefreshToken.isNullOrEmpty()) {
                val refreshResponse = RetrofitClient.instance.refreshToken(
                    RefreshRequest(
                        currentRefreshToken
                    )
                )

                if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                    val body = refreshResponse.body()!!
                    sessionManager.saveToken(body.access_token)
                    sessionManager.saveRefreshToken(body.refresh_token)

                    val profileResponse =
                        RetrofitClient.instance.getMyProfile("Bearer ${body.access_token}")

                    if (profileResponse.isSuccessful) {
                        userProfile = profileResponse.body()
                        userProfile?.let {
                            val isEnt = it.is_enterprise
                            sessionManager.saveIsEnterprise(isEnt)
                            isEnterprise = isEnt
                            success = true
                        }
                    } else {
                        throw Exception("No se pudo obtener el perfil tras el refresco")
                    }
                } else {
                    clearState()
                    sessionManager.clearSession()
                    errorMessage = "Sesión expirada por completo"
                }
            } else {
                success = false
                isLoading = false
                errorMessage = "No hay datos de sesión guardados"
            }
        } catch (e: Exception) {
            clearState()
            sessionManager.clearSession()
            errorMessage = "Error de conexión o sesión inválida: ${e.message}"
        } finally {
            if (!success) userProfile = null
            isLoading = false
        }

    }

    fun clearState() {
        success = false
        errorMessage = null
        userProfile = null
        isEnterprise = null
        isLoading = false
    }

    suspend fun updateUserProfile(
        newUsername: String?,
        newName: String?,
        imageFile: File?
    ): Result<UserData> {
        return try {
            val token = sessionManager.userToken.first()
            if (token.isNullOrEmpty()) return Result.failure(Exception("No token"))

            // Convertimos Strings a RequestBody
            val namePart = newName?.toRequestBody("text/plain".toMediaTypeOrNull())
            val usernamePart = newUsername?.toRequestBody("text/plain".toMediaTypeOrNull())

            // Preparamos la imagen si existe
            val imagePart = imageFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("avatar_file", it.name, requestFile)
            }

            val response = RetrofitClient.instance.updateProfile(
                "Bearer $token",
                namePart,
                usernamePart,
                imagePart
            )

            if (response.isSuccessful && response.body() != null) {
                userProfile = response.body()
                Result.success(userProfile!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}