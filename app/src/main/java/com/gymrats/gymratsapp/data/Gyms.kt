package com.gymrats.gymratsapp.data

data class GymResponse(
    val id: String,
    val name: String,
    val description: String?,
    val address: String,
    val phone: String,
    val email: String,
    val price: Double,
    val max_capacity: Int,
    val current_capacity: Int,
    val image_url: String?,
    val is_open: Boolean
)

data class GymCreateResponse(
    val message: String,
    val gym: GymResponse
)

data class MemberInfoResponse(
    val id: String,
    val username: String,
    val name: String?,
    val avatar_url: String?,
    val subscription_id: String,
    val status: String
)

data class MemberLinkRequest(
    val gym_id: String,
    val user_id: String? = null,
    val username: String? = null,
    val start_date: String, // ISO format
    val expiration_date: String // ISO format
)