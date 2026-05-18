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
    ): Response<UserData>

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
}