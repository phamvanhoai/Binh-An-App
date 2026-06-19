package com.example.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "_id") val id: String? = null,
    val idStr: String? = null, // fallback
    val email: String,
    val name: String? = null,
    val avatar: String? = null,
    val bio: String? = null
) {
    val displayName: String get() = name ?: email.substringBefore("@")
}

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val success: Boolean? = null,
    val message: String? = null,
    val token: String? = null,
    val user: User? = null
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
    val appName: String? = null,
    val version: String? = null,
    val maintenance: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class DailyMessage(
    @Json(name = "_id") val id: String? = null,
    val title: String? = null,
    val content: String,
    val author: String? = null,
    val date: String? = null
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
data class Prayer(
    @Json(name = "_id") val id: String,
    val title: String,
    val content: String,
    val author: String? = null, // fallback if author is simple string
    val user: User? = null,
    val createdAt: String? = null,
    val prayCount: Int? = null,
    val reactions: List<Reaction>? = null,
    val isReacted: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class PrayerCreateRequest(
    val title: String,
    val content: String,
    val isPublic: Boolean = true
)

@JsonClass(generateAdapter = true)
data class Gratitude(
    @Json(name = "_id") val id: String,
    val title: String? = null,
    val content: String,
    val date: String? = null,
    val createdAt: String? = null
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
    val deliverAt: String,
    val isUnlocked: Boolean? = null,
    val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class FutureLetterCreateRequest(
    val title: String,
    val content: String,
    val deliverAt: String // ISO String
)

@JsonClass(generateAdapter = true)
data class Memorial(
    @Json(name = "_id") val id: String,
    val name: String,
    val title: String? = null,
    val description: String? = null,
    val bornDate: String? = null,
    val decessDate: String? = null,
    val imageUrl: String? = null,
    val candleCount: Int? = null,
    val user: User? = null,
    val createdAt: String? = null,
    val isPublic: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class MemorialCreateRequest(
    val name: String,
    val title: String? = null,
    val description: String? = null,
    val bornDate: String? = null,
    val decessDate: String? = null,
    val imageUrl: String? = null,
    val isPublic: Boolean = true
)

@JsonClass(generateAdapter = true)
data class AppNotification(
    @Json(name = "_id") val id: String,
    val title: String,
    val message: String,
    val read: Boolean? = null,
    val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class GenericResponse(
    val success: Boolean? = null,
    val message: String? = null
)
