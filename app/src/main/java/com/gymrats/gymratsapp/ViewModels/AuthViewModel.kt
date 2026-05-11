package com.gymrats.gymratsapp.ViewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gymrats.gymratsapp.data.SessionManager
import com.gymrats.gymratsapp.data.SignupRequest
import com.gymrats.gymratsapp.remote.RetrofitClient
import kotlinx.coroutines.launch


class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var success by mutableStateOf(false)
        private set

    fun ejecutarLogin(email: String, pass: String) {
        success = false
        errorMessage = null
        viewModelScope.launch {
            try {
                // Llamamos directamente con los parámetros @Field
                val response = RetrofitClient.instance.login(email, pass)

                if (response.isSuccessful) {
                    val token = response.body()?.access_token
                    token?.let {
                        sessionManager.saveToken(it) // <--- GUARDAMOS EL TOKEN
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
        viewModelScope.launch {
            try {
                val registro = SignupRequest(email, username, pass, isEnterprise)
                val response = RetrofitClient.instance.signup(registro)

                if (response.isSuccessful) {
                    success = true
                } else {
                    errorMessage = "Error en registro: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.message}"
            }
        }
    }
}