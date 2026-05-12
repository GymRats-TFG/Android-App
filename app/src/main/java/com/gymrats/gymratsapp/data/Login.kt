package com.gymrats.gymratsapp.data

// Respuesta del Login
data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val user: UserData
)

data class UserData(
    val id: String,
    val email: String,
    val user_metadata: UserMetadata
)

data class UserMetadata(
    val username: String,
    val is_enterprise: Boolean
)

data class SignupRequest(
    val email: String,
    val username: String,
    val password: String,
    val is_enterprise: Boolean = false
)