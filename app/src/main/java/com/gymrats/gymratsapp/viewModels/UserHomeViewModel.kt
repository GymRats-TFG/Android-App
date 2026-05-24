package com.gymrats.gymratsapp.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadSubscriptions() {

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

                val response = RetrofitClient.instance.getUserSubscriptions(
                    "Bearer $token"
                )

                if (response.isSuccessful && response.body() != null) {

                    subscriptions = response.body()!!

                } else {

                    errorMessage = "Error ${response.code()}"

                }

            } catch (e: Exception) {

                errorMessage = e.message

            } finally {

                isLoading = false

            }
        }
    }
}