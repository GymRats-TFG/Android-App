package com.gymrats.gymratsapp.remote

import com.gymrats.gymratsapp.data.*
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
}