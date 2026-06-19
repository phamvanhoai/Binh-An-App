package com.example.data

import retrofit2.http.*

interface BinhAnApiService {

    @GET("config")
    suspend fun getConfig(): ConfigResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("auth/me")
    suspend fun getMe(): AuthResponse

    @POST("auth/logout")
    suspend fun logout(): GenericResponse

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Query("email") email: String): GenericResponse

    @PATCH("auth/password")
    suspend fun updatePassword(@Body request: PasswordUpdateRequest): GenericResponse

    @GET("profile")
    suspend fun getProfile(): AuthResponse

    @PATCH("profile")
    suspend fun updateProfile(@Body request: ProfileUpdateRequest): AuthResponse

    @GET("daily-messages/today")
    suspend fun getTodayMessage(): DailyMessageResponse

    @GET("prayers")
    suspend fun getPrayers(): PrayerListResponse

    @POST("prayers")
    suspend fun createPrayer(@Body request: PrayerCreateRequest): PrayerDetailResponse

    @GET("prayers/{id}")
    suspend fun getPrayerDetail(@Path("id") id: String): PrayerDetailResponse

    @PATCH("prayers/{id}")
    suspend fun updatePrayer(@Path("id") id: String, @Body request: PrayerCreateRequest): PrayerDetailResponse

    @DELETE("prayers/{id}")
    suspend fun deletePrayer(@Path("id") id: String): GenericResponse

    @POST("prayers/{id}/reactions")
    suspend fun addReaction(@Path("id") id: String): PrayerDetailResponse

    @DELETE("prayers/{id}/reactions")
    suspend fun removeReaction(@Path("id") id: String): PrayerDetailResponse

    @GET("gratitude")
    suspend fun getGratitudeEntries(): GratitudeListResponse

    @POST("gratitude")
    suspend fun createGratitudeEntry(@Body request: GratitudeCreateRequest): GratitudeDetailResponse

    @PATCH("gratitude/{id}")
    suspend fun updateGratitudeEntry(@Path("id") id: String, @Body request: GratitudeCreateRequest): GratitudeDetailResponse

    @DELETE("gratitude/{id}")
    suspend fun deleteGratitudeEntry(@Path("id") id: String): GenericResponse

    @GET("letters")
    suspend fun getFutureLetters(): FutureLetterListResponse

    @POST("letters")
    suspend fun createFutureLetter(@Body request: FutureLetterCreateRequest): FutureLetterDetailResponse

    @GET("letters/{id}")
    suspend fun getFutureLetterDetail(@Path("id") id: String): FutureLetterDetailResponse

    @DELETE("letters/{id}")
    suspend fun deleteFutureLetter(@Path("id") id: String): GenericResponse

    @GET("memorials")
    suspend fun getMemorials(): MemorialListResponse

    @POST("memorials")
    suspend fun createMemorial(@Body request: MemorialCreateRequest): MemorialDetailResponse

    @GET("memorials/{id}")
    suspend fun getMemorialDetail(@Path("id") id: String): MemorialDetailResponse

    @PATCH("memorials/{id}")
    suspend fun updateMemorial(@Path("id") id: String, @Body request: MemorialCreateRequest): MemorialDetailResponse

    @DELETE("memorials/{id}")
    suspend fun deleteMemorial(@Path("id") id: String): GenericResponse

    @POST("memorials/{id}/candles")
    suspend fun lightCandle(@Path("id") id: String): MemorialDetailResponse

    @GET("notifications")
    suspend fun getNotifications(): NotificationListResponse
}
