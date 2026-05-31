package com.gymrats.gymratsapp.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gymrats.gymratsapp.data.GymResponse
import com.gymrats.gymratsapp.data.SessionManager
import com.gymrats.gymratsapp.data.UserSubscriptionResponse
import com.gymrats.gymratsapp.remote.RetrofitClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserHomeViewModel(
    application: Application,
    private val sessionManager: SessionManager
) : AndroidViewModel(application) {

    var subscriptions by mutableStateOf<List<UserSubscriptionResponse>>(emptyList())
        private set

    var gyms by mutableStateOf<List<GymResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var recentGyms by mutableStateOf<List<GymResponse>>(emptyList())
        private set

    fun loadData() {

        viewModelScope.launch {

            isLoading = true
            errorMessage = null

            try {

                val token = sessionManager.userToken.first()

                if (token.isNullOrEmpty()) {
                    errorMessage = "Token no encontrado"
                    isLoading = false
                    return@launch
                }

                // Suscripciones
                val subscriptionsResponse =
                    RetrofitClient.instance.getUserSubscriptions(
                        "Bearer $token"
                    )

                if (
                    subscriptionsResponse.isSuccessful &&
                    subscriptionsResponse.body() != null
                ) {

                    subscriptions = subscriptionsResponse.body()!!

                }

                // Gimnasios
                val gymsResponse =
                    RetrofitClient.instance.getAllGyms(
                        "Bearer $token"
                    )
                println("CODE: ${gymsResponse.code()}")
                println("BODY: ${gymsResponse.body()}")
                println("ERROR: ${gymsResponse.errorBody()?.string()}")

                if (
                    gymsResponse.isSuccessful &&
                    gymsResponse.body() != null
                ) {

                    gyms = gymsResponse.body()!!
                    println("GYMS CARGADOS: $gyms")

                }

            } catch (e: Exception) {

                errorMessage = e.message

            } finally {

                isLoading = false

            }
        }
    }
    fun addRecentGym(gym: GymResponse) {

        recentGyms =
            listOf(gym) +
                    recentGyms.filter { it.id != gym.id }

        recentGyms = recentGyms.take(5)
    }
}