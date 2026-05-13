package com.gymrats.gymratsapp.data

// Respuesta del Login
data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val user: UserData
)

data class UserData(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val name: String? = null,
    val avatar_url: String? = null,
    val description: String? = null,
    val is_enterprise: Boolean = false
)

data class SignupRequest(
    val email: String,
    val username: String,
    val password: String,
    val is_enterprise: Boolean = false
)