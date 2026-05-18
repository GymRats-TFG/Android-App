package com.gymrats.gymratsapp.viewModels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymrats.gymratsapp.data.GymResponse
import com.gymrats.gymratsapp.data.SessionManager
import com.gymrats.gymratsapp.remote.RetrofitClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GymViewModel(private val sessionManager: SessionManager) : ViewModel() {
    var gyms by mutableStateOf<List<GymResponse>>(emptyList())
    var isRefreshing by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun cargarSedes() {
        viewModelScope.launch {
            isRefreshing = true
            errorMessage = null
            try {
                val token = sessionManager.userToken.first()
                if (!token.isNullOrEmpty()) {
                    val response = RetrofitClient.instance.getMyGyms("Bearer $token")
                    if (response.isSuccessful) {
                        gyms = response.body() ?: emptyList()
                    } else {
                        errorMessage = "Error al cargar sedes"
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión"
            } finally {
                isRefreshing = false
            }
        }
    }
}