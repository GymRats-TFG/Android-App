package com.gymrats.gymratsapp.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymrats.gymratsapp.data.EnterpriseStats
import com.gymrats.gymratsapp.data.GymResponse
import com.gymrats.gymratsapp.data.MemberInfoResponse
import com.gymrats.gymratsapp.data.MemberLinkRequest
import com.gymrats.gymratsapp.data.ScanRequest
import com.gymrats.gymratsapp.data.ScanResponse
import com.gymrats.gymratsapp.data.SessionManager
import com.gymrats.gymratsapp.data.SubscriptionUpdateRequest
import com.gymrats.gymratsapp.remote.RetrofitClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.time.LocalDate

class GymViewModel(private val sessionManager: SessionManager) : ViewModel() {
    var gyms by mutableStateOf<List<GymResponse>>(emptyList())
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var gymMembers by mutableStateOf<List<MemberInfoResponse>>(emptyList())
        private set

    var selectedGym by mutableStateOf<GymResponse?>(null)
        private set

    var enterpriseStats by mutableStateOf<EnterpriseStats?>(null)
        private set

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

    fun cargarMiembrosGym(gymId: String) {
        isRefreshing = true
        viewModelScope.launch {
            try {
                val token = sessionManager.userToken.first()
                if (!token.isNullOrEmpty()) {
                    val response = RetrofitClient.instance.getGymMembers("Bearer $token", gymId)
                    if (response.isSuccessful) {
                        gymMembers = response.body() ?: emptyList()
                    }
                }
            } catch (_: Exception) {
                errorMessage = "Error al cargar los socios"
            } finally {
                isRefreshing = false
            }
        }
    }

    fun cargarDatosDetalle(gymId: String, isEnterprise: Boolean) {
        viewModelScope.launch {
            isRefreshing = true
            try {
                val token = sessionManager.userToken.first()
                if (!token.isNullOrEmpty()) {
                    val gymRes = RetrofitClient.instance.getGym("Bearer $token", gymId)
                    if (gymRes.isSuccessful) {
                        selectedGym = gymRes.body()
                    }

                    if (isEnterprise) {
                        val membersRes = RetrofitClient.instance.getGymMembers("Bearer $token", gymId)
                        if (membersRes.isSuccessful) {
                            gymMembers = membersRes.body() ?: emptyList()
                        }
                    }
                }
            } catch (_: Exception) {
                errorMessage = "Error al actualizar datos"
            } finally {
                isRefreshing = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun vincularSocio(gymId: String, identifier: String): Result<String> {
        return try {
            val token = sessionManager.userToken.first()
            if (token.isNullOrEmpty()) return Result.failure(Exception("No token"))

            // Fechas por defecto: Hoy hasta dentro de 1 mes
            val hoy = LocalDate.now().toString()
            val mesSiguiente = LocalDate.now().plusMonths(1).toString()

            // Creamos la request. Probamos por username si no parece un UUID,
            val request = MemberLinkRequest(
                user_identifier = identifier,
                start_date = hoy,
                expiration_date = mesSiguiente
            )

            val response = RetrofitClient.instance.addMemberToGym("Bearer $token", gymId, request)

            if (response.isSuccessful) {
                cargarMiembrosGym(gymId) // Recargamos la lista automáticamente
                Result.success("Socio vinculado correctamente")
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error al vincular"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun actualizarSuscripcion(gymId: String, member: MemberInfoResponse, nuevaExpiracion: String): Result<String> {
        return try {
            val token = sessionManager.userToken.first() ?: ""

            val request = SubscriptionUpdateRequest(member.status, member.start_date, expiration_date = nuevaExpiracion)
            val response = RetrofitClient.instance.updateSubscription("Bearer $token", member.subscription_id, request)

            if (response.isSuccessful) {
                cargarMiembrosGym(gymId)
                Result.success("Suscripción actualizada")
            } else {
                Result.failure(Exception("Error al actualizar"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun eliminarSocio(gymId: String, subscriptionId: String): Result<String> {
        return try {
            val token = sessionManager.userToken.first() ?: ""
            val response = RetrofitClient.instance.deleteSubscription("Bearer $token", subscriptionId)
            if (response.isSuccessful) {
                cargarMiembrosGym(gymId) // Recargar lista
                Result.success("Socio eliminado correctamente")
            } else {
                Result.failure(Exception("Error al eliminar"))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun actualizarSede(
        gymId: String,
        name: String?,
        description: String?,
        address: String?,
        phone: String?,
        email: String?,
        price: Double?,
        maxCapacity: Int?,
        imageFile: File?
    ): Result<GymResponse> {
        return try {
            val token = sessionManager.userToken.first()
            if (token.isNullOrEmpty()) return Result.failure(Exception("No token"))

            val namePart = name?.toRequestBody("text/plain".toMediaTypeOrNull())
            val descPart = description?.toRequestBody("text/plain".toMediaTypeOrNull())
            val addrPart = address?.toRequestBody("text/plain".toMediaTypeOrNull())
            val phonePart = phone?.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailPart = email?.toRequestBody("text/plain".toMediaTypeOrNull())
            val pricePart = price?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val capPart = maxCapacity?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image_file", it.name, requestFile)
            }

            val response = RetrofitClient.instance.updateGym(
                "Bearer $token", gymId, namePart, descPart, addrPart,
                phonePart, emailPart, pricePart, capPart, imagePart
            )

            if (response.isSuccessful && response.body() != null) {
                val updatedGym = response.body()!!.gym
                // Actualizar el estado local
                selectedGym = updatedGym
                cargarSedes() // Refrescar la lista general
                Result.success(updatedGym)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cargarStatsEnterprise() {
        viewModelScope.launch {
            try {
                val token = sessionManager.userToken.first()
                if (!token.isNullOrEmpty()) {
                    val response = RetrofitClient.instance.getEnterpriseStats("Bearer $token")
                    if (response.isSuccessful) {
                        enterpriseStats = response.body()
                    }
                }
            } catch (_: Exception) {
                errorMessage = "Error al cargar estadísticas"
            }
        }
    }

    suspend fun toggleEstadoSede(gymId: String): Result<Boolean> {
        return try {
            val token = sessionManager.userToken.first() ?: ""
            val response = RetrofitClient.instance.toggleGymOpenStatus("Bearer $token", gymId)

            if (response.isSuccessful && response.body() != null) {
                val nuevoEstado = response.body()!!.is_open

                selectedGym = selectedGym?.copy(is_open = nuevoEstado)

                gyms = gyms.map { if (it.id == gymId) it.copy(is_open = nuevoEstado) else it }

                Result.success(nuevoEstado)
            } else {
                Result.failure(Exception("Error al cambiar estado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun procesarEscaneo(gymId: String, userId: String): Result<ScanResponse> {
        return try {
            val token = sessionManager.userToken.first() ?: ""
            val response = RetrofitClient.instance.processScan("Bearer $token", gymId, ScanRequest(userId))

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en el servidor"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}