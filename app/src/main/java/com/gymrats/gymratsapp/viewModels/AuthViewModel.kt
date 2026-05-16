package com.gymrats.gymratsapp.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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


class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var success by mutableStateOf(false)
        private set

    var userProfile by mutableStateOf<UserData?>(null)
        private set

    var isEnterprise by mutableStateOf(null as Boolean?)
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
                        sessionManager.saveIsEnterprise(isEnt)

                        isEnterprise = isEnt
                        userProfile = loginResponse.user

                        success = true
                    }
                } else {
                    errorMessage = "Error en login: ${response.code()} - ${response.errorBody()?.string()}"
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

                        sessionManager.saveIsEnterprise(userData.is_enterprise)

                        this@AuthViewModel.isEnterprise = isEnt
                        userProfile = userData

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

    fun cargarPerfil() {
        success = false
        errorMessage = null
        viewModelScope.launch {
            try {
                val token = sessionManager.userToken.first()
                if (!token.isNullOrEmpty()) {
                    val response = RetrofitClient.instance.getMyProfile("Bearer $token")
                    if (response.isSuccessful) {
                        userProfile = response.body()
                        userProfile?.let {
                            val isEnt = it.is_enterprise
                            sessionManager.saveIsEnterprise(isEnt)
                            isEnterprise = isEnt
                            success = true
                        }
                    } else {
                        clearState()
                        sessionManager.clearSession()
                        val errorBody = response.errorBody()?.string()
                        println("DEBUG_AUTH: Error ${response.code()} - $errorBody")
//                        errorMessage = "Sesión expirada"
                    }
                }
            } catch (e: Exception) {
                clearState()
                sessionManager.clearSession()
//                errorMessage = "Error de conexión"
            } finally {
                if(!success) userProfile = null
            }
        }
    }

    fun clearState() {
        success = false
        errorMessage = null
        userProfile = null
        isEnterprise = null
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