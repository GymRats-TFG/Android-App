package com.gymrats.gymratsapp.remote

import com.gymrats.gymratsapp.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") email: String,
        @Field("password") pass: String
    ): Response<LoginResponse>

    @POST("signup")
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<SignupResponse>

    @POST("refresh")
    suspend fun refreshToken(
        @Body request: RefreshRequest
    ): Response<RefreshResponse>

    @GET("users/me")
    suspend fun getMyProfile(
        @Header("Authorization") token: String
    ): Response<UserData>

    @Multipart
    @PATCH("users/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody?,
        @Part("username") username: RequestBody?,
        @Part avatar_file: MultipartBody.Part?
    ): Response<UserData>

    @GET("gyms/my")
    suspend fun getMyGyms(
        @Header("Authorization") token: String
    ): Response<List<GymResponse>>

    @Multipart
    @POST("gyms/")
    suspend fun createGym(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("address") address: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("email") email: RequestBody,
        @Part("price") price: RequestBody,
        @Part("max_capacity") maxCapacity: RequestBody,
        @Part image_file: MultipartBody.Part?
    ): Response<GymCreateResponse>

    @GET("gyms/{gym_id}/members")
    suspend fun getGymMembers(
        @Header("Authorization") token: String,
        @Path("gym_id") gymId: String
    ): Response<List<MemberInfoResponse>>

    @GET("gyms/{gym_id}")
    suspend fun getGym(
        @Header("Authorization") token: String,
        @Path("gym_id") gymId: String
    ): Response<GymResponse>

    @POST("gyms/members")
    suspend fun addMemberToGym(
        @Header("Authorization") token: String,
        @Body request: MemberLinkRequest
    ): Response<Unit>

    @PATCH("subscriptions/{subscription_id}")
    suspend fun updateSubscription(
        @Header("Authorization") token: String,
        @Path("subscription_id") subscriptionId: String,
        @Body request: SubscriptionUpdateRequest
    ): Response<Unit>

    @DELETE("subscriptions/{subscription_id}")
    suspend fun deleteSubscription(
        @Header("Authorization") token: String,
        @Path("subscription_id") subscriptionId: String
    ): Response<Unit>

    @Multipart
    @PATCH("gyms/{gym_id}")
    suspend fun updateGym(
        @Header("Authorization") token: String,
        @Path("gym_id") gymId: String,
        @Part("name") name: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("price") price: RequestBody?,
        @Part("max_capacity") maxCapacity: RequestBody?,
        @Part image_file: MultipartBody.Part?
    ): Response<GymCreateResponse>

    @GET("gyms/stats/summary")
    suspend fun getEnterpriseStats(
        @Header("Authorization") token: String
    ): Response<EnterpriseStats>

    @PATCH("gyms/{gym_id}/toggle-open")
    suspend fun toggleGymOpenStatus(
        @Header("Authorization") token: String,
        @Path("gym_id") gymId: String
    ): Response<ToggleOpenResponse>

    @POST("gyms/{gym_id}/scan")
    suspend fun processScan(
        @Header("Authorization") token: String,
        @Path("gym_id") gymId: String,
        @Body request: ScanRequest
    ): Response<ScanResponse>
}