package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserMetadata(
    val name: String? = null,
    @Json(name = "full_name") val fullName: String? = null
)

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "_id") val id: String? = null,
    @Json(name = "id") val idStr: String? = null,
    val email: String,
    val name: String? = null,
    @Json(name = "full_name") val fullName: String? = null,
    @Json(name = "display_name") val displayNameField: String? = null,
    @Json(name = "user_metadata") val userMetadata: UserMetadata? = null,
    val avatar: String? = null,
    val bio: String? = null
) {
    val resolvedId: String? get() = id ?: idStr

    val resolvedName: String?
        get() = sequenceOf(
            name,
            fullName,
            displayNameField,
            userMetadata?.name,
            userMetadata?.fullName
        ).firstOrNull { !it.isNullOrBlank() }

    val displayName: String
        get() = resolvedName ?: email.substringBefore("@").takeIf { it.isNotBlank() } ?: "Bạn Hữu Bình An"
}

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val token: String? = null,
    val user: User? = null,
    val data: AuthData? = null,
    val error: ApiError? = null
) {
    val authToken: String?
        get() = token
            ?: data?.token
            ?: data?.accessToken
            ?: data?.session?.accessToken

    val authUser: User?
        get() = user ?: data?.user ?: data?.session?.user
}

@JsonClass(generateAdapter = true)
data class AuthData(
    val token: String? = null,
    @Json(name = "access_token") val accessToken: String? = null,
    val user: User? = null,
    val session: AuthSession? = null
)

@JsonClass(generateAdapter = true)
data class AuthSession(
    @Json(name = "access_token") val accessToken: String? = null,
    val user: User? = null
)

@JsonClass(generateAdapter = true)
data class ApiError(
    val code: String? = null,
    val message: String? = null
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val email: String,
    val name: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class GoogleLoginRequest(
    @Json(name = "id_token") val idToken: String
)

@JsonClass(generateAdapter = true)
data class ProfileUpdateRequest(
    val name: String? = null,
    val bio: String? = null
)

@JsonClass(generateAdapter = true)
data class PasswordUpdateRequest(
    val oldPassword: String,
    val newPassword: String
)

@JsonClass(generateAdapter = true)
data class ConfigResponse(
    val success: Boolean? = null,
    val data: ConfigData? = null
)

@JsonClass(generateAdapter = true)
data class ConfigData(
    @Json(name = "api_enabled") val apiEnabled: Boolean? = null,
    @Json(name = "api_maintenance") val apiMaintenance: Boolean? = null,
    @Json(name = "api_maintenance_message") val apiMaintenanceMessage: String? = null,
    @Json(name = "rate_limit_per_minute") val rateLimitPerMinute: Int? = null,
    @Json(name = "registration_enabled") val registrationEnabled: Boolean? = null,
    @Json(name = "public_community_enabled") val publicCommunityEnabled: Boolean? = null,
    @Json(name = "community_page_size") val communityPageSize: Int? = null,
    @Json(name = "support_email") val supportEmail: String? = null
)

@JsonClass(generateAdapter = true)
data class DailyMessage(
    @Json(name = "_id") val id: String? = null,
    val title: String? = null, // Kept for backward compatibility if needed
    @Json(name = "message") val content: String,
    val author: String? = null,
    val date: String? = null,
    @Json(name = "reflection_question") val reflectionQuestion: String? = null,
    val category: String? = null,
    @Json(name = "opened_date") val openedDate: String? = null
)

@JsonClass(generateAdapter = true)
data class DailyMessageResponse(
    val success: Boolean? = null,
    val data: DailyMessage? = null
)

@JsonClass(generateAdapter = true)
data class Reaction(
    @Json(name = "_id") val id: String? = null,
    val userId: String? = null,
    val type: String? = null
)

@JsonClass(generateAdapter = true)
data class PrayerReactions(
    val pray: Int = 0,
    val peace: Int = 0,
    val candle: Int = 0
)

@JsonClass(generateAdapter = true)
data class Prayer(
    val id: String,
    val title: String? = null,
    val content: String,
    val type: String? = null,
    val visibility: String? = null,
    @Json(name = "allow_reactions") val allowReactions: Boolean? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    val reactions: PrayerReactions? = null,
    val user: User? = null,
    val prayCount: Int? = null, // legacy
    val isReacted: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class PrayerListResponse(
    val success: Boolean,
    val data: List<Prayer>
)

@JsonClass(generateAdapter = true)
data class PrayerDetailResponse(
    val success: Boolean,
    val data: Prayer
)

@JsonClass(generateAdapter = true)
data class PrayerCreateRequest(
    val content: String,
    val type: String,
    val visibility: String,
    @Json(name = "allow_reactions") val allowReactions: Boolean = true
)

@JsonClass(generateAdapter = true)
data class Gratitude(
    @Json(name = "_id") val id: String,
    val title: String? = null,
    val content: String,
    val date: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class GratitudeListResponse(
    val success: Boolean,
    val data: List<Gratitude>
)

@JsonClass(generateAdapter = true)
data class GratitudeDetailResponse(
    val success: Boolean,
    val data: Gratitude
)

@JsonClass(generateAdapter = true)
data class GratitudeCreateRequest(
    val title: String? = null,
    val content: String,
    val date: String? = null
)

@JsonClass(generateAdapter = true)
data class FutureLetter(
    @Json(name = "_id") val id: String,
    val title: String,
    val content: String? = null,
    @Json(name = "deliver_at") val deliverAt: String,
    @Json(name = "is_unlocked") val isUnlocked: Boolean? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class FutureLetterListResponse(
    val success: Boolean,
    val data: List<FutureLetter>
)

@JsonClass(generateAdapter = true)
data class FutureLetterDetailResponse(
    val success: Boolean,
    val data: FutureLetter
)

@JsonClass(generateAdapter = true)
data class FutureLetterCreateRequest(
    val title: String,
    val content: String,
    @Json(name = "deliver_at") val deliverAt: String // ISO String
)

@JsonClass(generateAdapter = true)
data class Memorial(
    @Json(name = "_id") val id: String,
    val name: String,
    val title: String? = null,
    val description: String? = null,
    @Json(name = "born_date") val bornDate: String? = null,
    @Json(name = "decess_date") val decessDate: String? = null,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "candle_count") val candleCount: Int? = null,
    val user: User? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "is_public") val isPublic: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class MemorialListResponse(
    val success: Boolean,
    val data: List<Memorial>
)

@JsonClass(generateAdapter = true)
data class MemorialDetailResponse(
    val success: Boolean,
    val data: Memorial
)

@JsonClass(generateAdapter = true)
data class MemorialCreateRequest(
    val name: String,
    val title: String? = null,
    val description: String? = null,
    @Json(name = "born_date") val bornDate: String? = null,
    @Json(name = "decess_date") val decessDate: String? = null,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "is_public") val isPublic: Boolean = true
)

@JsonClass(generateAdapter = true)
data class AppNotification(
    @Json(name = "_id") val id: String,
    val title: String,
    val message: String,
    val read: Boolean? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class NotificationListResponse(
    val success: Boolean,
    val data: List<AppNotification>
)

@JsonClass(generateAdapter = true)
data class GenericResponse(
    val success: Boolean? = null,
    val message: String? = null
)
