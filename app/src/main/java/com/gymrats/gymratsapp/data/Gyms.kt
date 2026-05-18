package com.gymrats.gymratsapp.data

data class GymResponse(
    val id: Int,
    val name: String,
    val address: String,
    val max_capacity: Int,
    val current_capacity: Int,
    val image_url: String?,
    val is_open: Boolean
)