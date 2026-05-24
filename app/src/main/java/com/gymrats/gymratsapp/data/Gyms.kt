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
    val status: String,
    val start_date: String,
    val expiration_date: String
)

data class MemberLinkRequest(
    val user_identifier: String,
    val start_date: String, // ISO format
    val expiration_date: String // ISO format
)

data class SubscriptionUpdateRequest(
    val status: String,
    val start_date: String,
    val expiration_date: String
)

data class EnterpriseStats(
    val total_gyms: Int,
    val active_subscribers: Int,
    val total_current_capacity: Int
)

data class ToggleOpenResponse(
    val message: String,
    val is_open: Boolean
)

data class UserSubscriptionResponse(
    val subscription_id: String,
    val status: String,
    val start_date: String,
    val expiration_date: String,
    val gym: GymResponse
)
data class ScanRequest(
    val user_id: String
)

data class ScanResponse(
    val success: Boolean,
    val action: String?, // "entry" o "exit"
    val message: String,
    val user_name: String?
)