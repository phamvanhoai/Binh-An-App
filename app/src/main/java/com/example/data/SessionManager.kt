package com.example.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("binh_an_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_BIO = "user_bio"
        private const val KEY_USER_AVATAR = "user_avatar"
    }

    fun saveSession(token: String, user: User?) {
        prefs.edit().apply {
            putString(KEY_AUTH_TOKEN, token)
            putString(KEY_USER_ID, user?.resolvedId)
            putString(KEY_USER_EMAIL, user?.email)
            putString(KEY_USER_NAME, user?.resolvedName)
            putString(KEY_USER_BIO, user?.bio)
            putString(KEY_USER_AVATAR, user?.avatar)
            apply()
        }
    }

    fun updateProfile(name: String?, bio: String?) {
        prefs.edit().apply {
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_BIO, bio)
            apply()
        }
    }

    fun updateAvatar(avatarUrl: String?) {
        prefs.edit().apply {
            putString(KEY_USER_AVATAR, avatarUrl)
            apply()
        }
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, "")
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, "")
    }

    fun getUserBio(): String? {
        return prefs.getString(KEY_USER_BIO, "")
    }

    fun getUserAvatar(): String? {
        return prefs.getString(KEY_USER_AVATAR, "")
    }

    fun getUser(): User? {
        val email = prefs.getString(KEY_USER_EMAIL, null) ?: return null
        return User(
            id = prefs.getString(KEY_USER_ID, ""),
            email = email,
            name = prefs.getString(KEY_USER_NAME, ""),
            bio = prefs.getString(KEY_USER_BIO, ""),
            avatar = prefs.getString(KEY_USER_AVATAR, "")
        )
    }

    // Interactive Stats & Local Storage
    fun getSavedMessages(): Set<String> {
        return prefs.getStringSet("saved_message_ids", emptySet()) ?: emptySet()
    }

    fun saveMessageId(id: String) {
        val current = getSavedMessages().toMutableSet()
        current.add(id)
        prefs.edit().putStringSet("saved_message_ids", current).apply()
    }

    fun removeSavedMessageId(id: String) {
        val current = getSavedMessages().toMutableSet()
        current.remove(id)
        prefs.edit().putStringSet("saved_message_ids", current).apply()
    }

    fun isMessageSaved(id: String): Boolean {
        return getSavedMessages().contains(id)
    }

    fun getStreakCount(): Int {
        return prefs.getInt("streak_count", 3) // Defaults to a peaceful 3-day starter streak
    }

    fun incrementStreak() {
        val current = getStreakCount()
        prefs.edit().putInt("streak_count", current + 1).apply()
    }

    fun getCoPrayCount(): Int {
        return prefs.getInt("copray_count", 18) // Defaults to beautiful 18 co-prayers
    }

    fun incrementCoPrayCount() {
        val current = getCoPrayCount()
        prefs.edit().putInt("copray_count", current + 1).apply()
    }

    fun getSentPrayersCount(): Int {
        return prefs.getInt("sent_prayers_count", 5) // Defaults to 5 sent wishes starter
    }

    fun incrementSentPrayersCount() {
        val current = getSentPrayersCount()
        prefs.edit().putInt("sent_prayers_count", current + 1).apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }
}
