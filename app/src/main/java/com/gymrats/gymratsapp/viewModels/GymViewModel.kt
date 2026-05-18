package com.gymrats.gymratsapp.viewModels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymrats.gymratsapp.data.GymResponse
import com.gymrats.gymratsapp.data.SessionManager
import com.gymrats.gymratsapp.remote.RetrofitClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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
            } catch (_: Exception) {
                errorMessage = "Error de conexión"
            } finally {
                isRefreshing = false
            }
        }
    }

    suspend fun registrarSede(
        name: String,
        description: String,
        address: String,
        phone: String,
        email: String,
        price: Double,
        maxCapacity: Int,
        imageFile: File?
    ): Result<String> {
        return try {
            val token = sessionManager.userToken.first()
            if (token.isNullOrEmpty()) return Result.failure(Exception("No token"))

            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
            val addrPart = address.toRequestBody("text/plain".toMediaTypeOrNull())
            val phonePart = phone.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val pricePart = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val capPart = maxCapacity.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())

                val fileName = "${name.replace(" ", "_")}_new.png"

                MultipartBody.Part.createFormData("image_file", fileName, requestFile)
            }

            val response = RetrofitClient.instance.createGym(
                "Bearer $token", namePart, descPart, addrPart,
                phonePart, emailPart, pricePart, capPart, imagePart
            )

            if (response.isSuccessful) {
                cargarSedes()
                Result.success("Sede creada correctamente")
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error desconocido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}