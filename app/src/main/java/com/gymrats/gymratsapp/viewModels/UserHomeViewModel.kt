package com.gymrats.gymratsapp.viewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gymrats.gymratsapp.data.SessionManager
import com.gymrats.gymratsapp.data.UserActivityResponse
import com.gymrats.gymratsapp.data.UserSubscriptionResponse
import com.gymrats.gymratsapp.remote.RetrofitClient
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ActivitySession(
    val gymName: String,
    val date: String,
    val entryTime: String,
    val exitTime: String?
)

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

    var activityLogs by mutableStateOf<List<UserActivityResponse>>(emptyList())
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

    fun loadActivityLogs() {
        viewModelScope.launch {

            isLoading = true
            errorMessage = null

            try {
                val token = sessionManager.userToken.first()
                val response = RetrofitClient.instance.getUserActivity("Bearer $token")
                if (response.isSuccessful) {
                    activityLogs = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun getGroupedActivity(): List<ActivitySession> {val sessions = mutableListOf<ActivitySession>()
        val allLogs = activityLogs.sortedByDescending { it.recorded_at }

        val processedLogIds = mutableSetOf<String>()

        for (log in allLogs) {
            if (log.id in processedLogIds) continue

            if (log.action_type == "exit") {
                val entry = allLogs.find {
                    it.action_type == "entry" &&
                            it.gym_name == log.gym_name &&
                            it.recorded_at < log.recorded_at &&
                            it.id !in processedLogIds
                }

                if (entry != null) {
                    sessions.add(ActivitySession(
                        gymName = log.gym_name,
                        date = entry.recorded_at,
                        entryTime = entry.recorded_at.split("T").last().take(5),
                        exitTime = log.recorded_at.split("T").last().take(5)
                    ))
                    processedLogIds.add(log.id)
                    processedLogIds.add(entry.id)
                }
            } else if (log.action_type == "entry") {
                sessions.add(ActivitySession(
                    gymName = log.gym_name,
                    date = log.recorded_at,
                    entryTime = log.recorded_at.split("T").last().take(5),
                    exitTime = null
                ))
                processedLogIds.add(log.id)
            }
        }

        return sessions.sortedByDescending { it.date }
    }
}