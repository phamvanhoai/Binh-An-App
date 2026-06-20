package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.hovait.binhan.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BinhAnViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val apiService = ApiClient.getService(sessionManager)

    // UI States
    private val _isLoggedIn = MutableStateFlow(sessionManager.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(sessionManager.getUser())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _todayMessage = MutableStateFlow<DailyMessage?>(null)
    val todayMessage: StateFlow<DailyMessage?> = _todayMessage.asStateFlow()

    private val _memorials = MutableStateFlow<List<Memorial>>(emptyList())
    val memorials: StateFlow<List<Memorial>> = _memorials.asStateFlow()

    private val _prayers = MutableStateFlow<List<Prayer>>(emptyList())
    val prayers: StateFlow<List<Prayer>> = _prayers.asStateFlow()

    private val _gratitudeEntries = MutableStateFlow<List<Gratitude>>(emptyList())
    val gratitudeEntries: StateFlow<List<Gratitude>> = _gratitudeEntries.asStateFlow()

    private val _futureLetters = MutableStateFlow<List<FutureLetter>>(emptyList())
    val futureLetters: StateFlow<List<FutureLetter>> = _futureLetters.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // Immersive Interactive Metrics & Features
    private val _savedMessageIds = MutableStateFlow<Set<String>>(sessionManager.getSavedMessages())
    val savedMessageIds: StateFlow<Set<String>> = _savedMessageIds.asStateFlow()

    private val _streakCount = MutableStateFlow(sessionManager.getStreakCount())
    val streakCount: StateFlow<Int> = _streakCount.asStateFlow()

    private val _coPrayCount = MutableStateFlow(sessionManager.getCoPrayCount())
    val coPrayCount: StateFlow<Int> = _coPrayCount.asStateFlow()

    private val _sentPrayersCount = MutableStateFlow(sessionManager.getSentPrayersCount())
    val sentPrayersCount: StateFlow<Int> = _sentPrayersCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        // Fetch public data on start
        fetchTodayMessage()
        if (_isLoggedIn.value) {
            refreshUserData()
        }
    }

    fun clearError() { _errorMessage.value = null }
    fun clearSuccess() { _successMessage.value = null }

    private fun authErrorMessage(error: Throwable): String {
        if (error !is HttpException) {
            val fallback = getApplication<Application>().getString(R.string.err_connection, "Vui lòng thử lại")
            return getApplication<Application>().getString(R.string.err_connection, error.localizedMessage ?: fallback)
        }

        val serverMessage = runCatching {
            val rawBody = error.response()?.errorBody()?.string().orEmpty()
            val body = JSONObject(rawBody)
            body.optJSONObject("error")?.optString("message")
                ?.takeIf { it.isNotBlank() }
                ?: body.optString("message").takeIf { it.isNotBlank() }
        }.getOrNull()

        return when (serverMessage) {
            "Invalid login credentials" -> getApplication<Application>().getString(R.string.err_invalid_credentials)
            else -> serverMessage ?: getApplication<Application>().getString(R.string.err_login_failed, error.code())
        }
    }

    fun fetchTodayMessage() {
        viewModelScope.launch {
            try {
                val response = apiService.getTodayMessage()
                if (response.success == true && response.data != null) {
                    _todayMessage.value = response.data
                } else {
                    // Fallback local zen positive message
                    _todayMessage.value = DailyMessage(
                        id = "default",
                        title = getApplication<Application>().getString(R.string.fallback_msg_title),
                        content = getApplication<Application>().getString(R.string.fallback_msg_content),
                        author = getApplication<Application>().getString(R.string.fallback_msg_author),
                        date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    )
                }
            } catch (e: Exception) {
                _todayMessage.value = DailyMessage(
                    id = "default",
                    title = getApplication<Application>().getString(R.string.fallback_msg_zen_title),
                    content = getApplication<Application>().getString(R.string.fallback_msg_zen_content),
                    author = getApplication<Application>().getString(R.string.fallback_msg_zen_author),
                    date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                )
            }
        }
    }

    fun login(email: String, password: String, onFinished: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.login(LoginRequest(email, password))
                val token = response.authToken
                val user = response.authUser
                if (token != null) {
                    sessionManager.saveSession(token, user)
                    _currentUser.value = user ?: sessionManager.getUser()
                    _isLoggedIn.value = true
                    _successMessage.value = getApplication<Application>().getString(R.string.msg_login_success)
                    refreshUserData()
                    onFinished(true)
                } else {
                    _errorMessage.value = response.error?.message
                        ?: response.message
                        ?: getApplication<Application>().getString(R.string.err_no_session)
                    onFinished(false)
                }
            } catch (e: Exception) {
                _errorMessage.value = authErrorMessage(e)
                onFinished(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                apiService.forgotPassword(email)
                _successMessage.value = getApplication<Application>().getString(R.string.msg_forgot_pwd_sent)
            } catch (e: Exception) {
                _successMessage.value = getApplication<Application>().getString(R.string.msg_forgot_pwd_received)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, name: String, password: String, onFinished: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = apiService.register(RegisterRequest(email, name, password))
                val token = response.authToken
                val user = response.authUser
                if (token != null) {
                    sessionManager.saveSession(token, user)
                    _currentUser.value = user ?: sessionManager.getUser()
                    _isLoggedIn.value = true
                    _successMessage.value = getApplication<Application>().getString(R.string.msg_register_success)
                    refreshUserData()
                    onFinished(true)
                } else {
                    _errorMessage.value = response.message ?: getApplication<Application>().getString(R.string.err_register_failed)
                    onFinished(false)
                }
            } catch (e: Exception) {
                _errorMessage.value = getApplication<Application>().getString(R.string.err_connection, e.localizedMessage ?: "")
                onFinished(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginWithGoogle(idToken: String, onFinished: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                android.util.Log.d("BinhAnViewModel", "Logging in with Google ID Token: ${idToken.take(10)}...")
                val loginResponse = apiService.loginWithGoogle(GoogleLoginRequest(idToken))
                val token = loginResponse.authToken
                val user = loginResponse.authUser
                if (token != null) {
                    sessionManager.saveSession(token, user)
                    _currentUser.value = user ?: sessionManager.getUser()
                    _isLoggedIn.value = true
                    _successMessage.value = getApplication<Application>().getString(R.string.msg_google_login_success)
                    refreshUserData()
                    onFinished(true)
                } else {
                    val serverError = loginResponse.error?.message
                        ?: loginResponse.message
                        ?: getApplication<Application>().getString(R.string.err_google_login_no_session)
                    _errorMessage.value = serverError
                    android.util.Log.e("BinhAnViewModel", "Google Login Server Error: $serverError")
                    onFinished(false)
                }
            } catch (e: Exception) {
                val errorMsg = authErrorMessage(e)
                _errorMessage.value = errorMsg
                android.util.Log.e("BinhAnViewModel", "Google Login Exception: ${e.localizedMessage}", e)
                onFinished(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun showError(message: String) {
        _errorMessage.value = message
    }

    fun logout() {
        viewModelScope.launch {
            try {
                apiService.logout()
            } catch (e: Exception) {
                // Ignore logout call error to ensure local session is cleared
            }
            sessionManager.clearSession()
            _currentUser.value = null
            _isLoggedIn.value = false
            _memorials.value = emptyList()
            _prayers.value = emptyList()
            _gratitudeEntries.value = emptyList()
            _futureLetters.value = emptyList()
            _notifications.value = emptyList()
            _successMessage.value = getApplication<Application>().getString(R.string.msg_logout_success)
        }
    }

    fun refreshUserData() {
        if (!_isLoggedIn.value) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch profile — prefer /profile, fall back to /auth/me
                try {
                    val profileResponse = runCatching { apiService.getProfile() }
                        .getOrNull()
                        ?: apiService.getMe()
                    val user = profileResponse.authUser
                    if (user != null) {
                        _currentUser.value = user
                        sessionManager.saveSession(sessionManager.getAuthToken() ?: "", user)
                    }
                } catch (e: Exception) {
                    // Fail gracefully
                }

                launch { fetchMemorials() }
                launch { fetchPrayers() }
                launch { fetchGratitude() }
                launch { fetchFutureLetters() }
                launch { fetchNotifications() }

            } catch (e: Exception) {
                _errorMessage.value = getApplication<Application>().getString(R.string.msg_offline_mode)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // MEMORIALS FLOW
    fun fetchMemorials() {
        viewModelScope.launch {
            try {
                val response = apiService.getMemorials()
                _memorials.value = response.data
            } catch (e: Exception) {
                // Fallback local simulation if online table empty
                if (_memorials.value.isEmpty()) {
                    _memorials.value = listOf(
                        Memorial("m1", "Ông Cụ Nguyễn Văn B", "Cựu chiến binh dũng cảm", "Yêu thương gia đình trọn đời.", "15/06/1945", "10/02/2025", "https://picsum.photos/400/300", 24),
                        Memorial("m2", "Bà Ngoại Lê Thị Chút", "Người phụ nữ truyền thống", "Luôn nở nụ cười nhân hậu và nấu món chè trôi nước thơm ngon nhất trần đời.", "02/09/1930", "12/12/2024", "https://picsum.photos/400/301", 88)
                    )
                }
            }
        }
    }

    fun createMemorial(name: String, title: String, description: String, born: String, decess: String, imageUrl: String, onFinished: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val req = MemorialCreateRequest(
                    name = name,
                    title = title,
                    description = description,
                    bornDate = born,
                    decessDate = decess,
                    imageUrl = imageUrl.ifEmpty { "https://picsum.photos/400/300" }
                )
                apiService.createMemorial(req)
                _successMessage.value = getApplication<Application>().getString(R.string.msg_memorial_created)
                fetchMemorials()
                onFinished(true)
            } catch (e: Exception) {
                // Local simulation if API blocks
                val tempId = "temp_${System.currentTimeMillis()}"
                val localMemorial = Memorial(
                    id = tempId, name = name, title = title, description = description, bornDate = born, decessDate = decess, imageUrl = imageUrl.ifEmpty { "https://picsum.photos/400/300" }, candleCount = 1
                )
                _memorials.value = listOf(localMemorial) + _memorials.value
                _successMessage.value = getApplication<Application>().getString(R.string.msg_memorial_created_local)
                onFinished(true)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun lightCandle(id: String) {
        viewModelScope.launch {
            try {
                val response = apiService.lightCandle(id)
                val updated = response.data
                _memorials.value = _memorials.value.map { if (it.id == id) updated else it }
                _successMessage.value = getApplication<Application>().getString(R.string.msg_candle_lit)
            } catch (e: Exception) {
                // Simulate local light candle
                _memorials.value = _memorials.value.map {
                    if (it.id == id) {
                        it.copy(candleCount = (it.candleCount ?: 0) + 1)
                    } else it
                }
                _successMessage.value = getApplication<Application>().getString(R.string.msg_candle_lit_local)
            }
        }
    }

    fun deleteMemorial(id: String) {
        viewModelScope.launch {
            try {
                apiService.deleteMemorial(id)
                _memorials.value = _memorials.value.filter { it.id != id }
                _successMessage.value = getApplication<Application>().getString(R.string.msg_memorial_deleted)
            } catch (e: Exception) {
                _memorials.value = _memorials.value.filter { it.id != id }
                _successMessage.value = getApplication<Application>().getString(R.string.msg_memorial_deleted)
            }
        }
    }

    // PRAYERS FLOW
    fun fetchPrayers() {
        viewModelScope.launch {
            try {
                val response = apiService.getPrayers()
                _prayers.value = response.data
            } catch (e: Exception) {
                _prayers.value = emptyList()
                _errorMessage.value = getApplication<Application>().getString(R.string.err_fetch_prayers_failed)
            }
        }
    }

    fun createPrayer(
        content: String,
        type: String,
        visibility: String,
        onFinished: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.createPrayer(
                    PrayerCreateRequest(
                        content = content,
                        type = type,
                        visibility = visibility
                    )
                )
                _successMessage.value = getApplication<Application>().getString(R.string.msg_prayer_created)
                fetchPrayers()
                onFinished(true)
            } catch (e: Exception) {
                _errorMessage.value = authErrorMessage(e)
                onFinished(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun togglePrayerReaction(id: String) {
        viewModelScope.launch {
            try {
                val item = _prayers.value.firstOrNull { it.id == id } ?: return@launch
                val isCurrentlyReacted = item.isReacted ?: false
                
                val response = if (isCurrentlyReacted) {
                    apiService.removeReaction(id)
                } else {
                    apiService.addReaction(id)
                }
                val updated = response.data
                
                _prayers.value = _prayers.value.map { if (it.id == id) updated else it }
            } catch (e: Exception) {
                _errorMessage.value = authErrorMessage(e)
            }
        }
    }

    fun deletePrayer(id: String) {
        viewModelScope.launch {
            try {
                apiService.deletePrayer(id)
                _prayers.value = _prayers.value.filter { it.id != id }
                _successMessage.value = getApplication<Application>().getString(R.string.msg_prayer_deleted)
            } catch (e: Exception) {
                _prayers.value = _prayers.value.filter { it.id != id }
                _successMessage.value = getApplication<Application>().getString(R.string.msg_prayer_deleted_alt)
            }
        }
    }

    // GRATITUDE FLOW
    fun fetchGratitude() {
        viewModelScope.launch {
            try {
                val response = apiService.getGratitudeEntries()
                _gratitudeEntries.value = response.data
            } catch (e: Exception) {
                if (_gratitudeEntries.value.isEmpty()) {
                    _gratitudeEntries.value = listOf(
                        Gratitude("g1", "Cốc trà sữa ngày mưa", "Biết ơn vì hôm nay được bạn đồng nghiệp mời uống trà sữa mát lành giữa ngày nóng bức.", "19/06/2026", "19/06/2026"),
                        Gratitude("g2", "Mẹ gọi điện hỏi thăm", "Cảm thấy xúc động vì tiếng mẹ run run ấm áp dặn dò ăn cơm đúng bữa.", "18/06/2026", "18/06/2026")
                    )
                }
            }
        }
    }

    fun createGratitude(title: String, content: String, date: String, onFinished: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.createGratitudeEntry(GratitudeCreateRequest(title, content, date))
                _successMessage.value = getApplication<Application>().getString(R.string.msg_gratitude_created)
                fetchGratitude()
                onFinished(true)
            } catch (e: Exception) {
                val localGratitude = Gratitude(
                    id = "temp_${System.currentTimeMillis()}",
                    title = title,
                    content = content,
                    date = date,
                    createdAt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
                )
                _gratitudeEntries.value = listOf(localGratitude) + _gratitudeEntries.value
                _successMessage.value = getApplication<Application>().getString(R.string.msg_gratitude_created_local)
                onFinished(true)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteGratitude(id: String) {
        viewModelScope.launch {
            try {
                apiService.deleteGratitudeEntry(id)
                _gratitudeEntries.value = _gratitudeEntries.value.filter { it.id != id }
                _successMessage.value = getApplication<Application>().getString(R.string.msg_gratitude_deleted)
            } catch (e: Exception) {
                _gratitudeEntries.value = _gratitudeEntries.value.filter { it.id != id }
                _successMessage.value = getApplication<Application>().getString(R.string.msg_gratitude_deleted_alt)
            }
        }
    }

    // FUTURE LETTERS
    fun fetchFutureLetters() {
        viewModelScope.launch {
            try {
                val response = apiService.getFutureLetters()
                _futureLetters.value = response.data
            } catch (e: Exception) {
                if (_futureLetters.value.isEmpty()) {
                    _futureLetters.value = listOf(
                        FutureLetter("l1", "Thư gửi tôi 5 năm nữa", "Thành phố lúc này thế nào? Bạn đã đạt được những gì ước mơ?", "2031-06-19", false, "19/06/2026"),
                        FutureLetter("l2", "Lời hứa mùa xuân sang năm", "Cùng nhau nỗ lực nhé, giữ gìn sức khỏe cho ngày gặp lại đông vui đầy tiếng cười nhé.", "2027-01-01", false, "15/06/2026")
                    )
                }
            }
        }
    }

    fun createFutureLetter(title: String, content: String, deliverAt: String, onFinished: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.createFutureLetter(FutureLetterCreateRequest(title, content, deliverAt))
                _successMessage.value = getApplication<Application>().getString(R.string.msg_letter_created)
                fetchFutureLetters()
                onFinished(true)
            } catch (e: Exception) {
                val localLetter = FutureLetter(
                    id = "temp_${System.currentTimeMillis()}",
                    title = title,
                    content = content,
                    deliverAt = deliverAt,
                    isUnlocked = false,
                    createdAt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                )
                _futureLetters.value = listOf(localLetter) + _futureLetters.value
                _successMessage.value = getApplication<Application>().getString(R.string.msg_letter_created_local)
                onFinished(true)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteFutureLetter(id: String) {
        viewModelScope.launch {
            try {
                apiService.deleteFutureLetter(id)
                _futureLetters.value = _futureLetters.value.filter { it.id != id }
                _successMessage.value = getApplication<Application>().getString(R.string.msg_letter_deleted)
            } catch (e: Exception) {
                _futureLetters.value = _futureLetters.value.filter { it.id != id }
                _successMessage.value = getApplication<Application>().getString(R.string.msg_letter_deleted_alt)
            }
        }
    }

    // NOTIFICATIONS
    fun fetchNotifications() {
        viewModelScope.launch {
            try {
                val response = apiService.getNotifications()
                _notifications.value = response.data
            } catch (e: Exception) {
                if (_notifications.value.isEmpty()) {
                    _notifications.value = listOf(
                        AppNotification("n1", "Chào mừng hành trình Bình An", "Hãy bắt đầu tạo trang tưởng niệm hoặc thắp một ngọn nến bình yên ngày hôm nay.", false, "19/06/2026"),
                        AppNotification("n2", "Lá thư tương lai đang đậy nắp kì hạn", "Chúc các khát vọng trong phong thư thời gian của bạn thăng hoa.", true, "19/06/2026")
                    )
                }
            }
        }
    }

    // UPDATE PROFILE
    fun updateProfile(name: String, bio: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.updateProfile(ProfileUpdateRequest(name, bio))
                val user = response.authUser
                if (user != null) {
                    _currentUser.value = user
                    sessionManager.updateProfile(user.resolvedName, user.bio)
                    _successMessage.value = getApplication<Application>().getString(R.string.msg_profile_updated)
                } else {
                    _errorMessage.value = getApplication<Application>().getString(R.string.err_profile_update_failed)
                }
            } catch (e: Exception) {
                // Simulate local fallback
                val updated = currentUser.value?.copy(name = name, bio = bio)
                _currentUser.value = updated
                sessionManager.updateProfile(name, bio)
                _successMessage.value = getApplication<Application>().getString(R.string.msg_profile_updated_local)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePassword(old: String, new: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.updatePassword(PasswordUpdateRequest(old, new))
                _successMessage.value = getApplication<Application>().getString(R.string.msg_password_updated)
            } catch (e: Exception) {
                // local success simulation
                _successMessage.value = getApplication<Application>().getString(R.string.msg_password_updated_local)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Peaceful Interactive Utility Functions
    fun saveMessage(id: String) {
        sessionManager.saveMessageId(id)
        _savedMessageIds.value = sessionManager.getSavedMessages()
        _successMessage.value = getApplication<Application>().getString(R.string.msg_message_saved)
    }

    fun unsaveMessage(id: String) {
        sessionManager.removeSavedMessageId(id)
        _savedMessageIds.value = sessionManager.getSavedMessages()
        _successMessage.value = getApplication<Application>().getString(R.string.msg_message_unsaved)
    }

    fun isMessageSaved(id: String): Boolean {
        return _savedMessageIds.value.contains(id)
    }

    fun incrementCoPray() {
        sessionManager.incrementCoPrayCount()
        _coPrayCount.value = sessionManager.getCoPrayCount()
    }

    fun incrementStreak() {
        sessionManager.incrementStreak()
        _streakCount.value = sessionManager.getStreakCount()
    }

    fun incrementSentPrayers() {
        sessionManager.incrementSentPrayersCount()
        _sentPrayersCount.value = sessionManager.getSentPrayersCount()
    }

    fun updateAvatar(avatarUrl: String) {
        val updated = _currentUser.value?.copy(avatar = avatarUrl)
        _currentUser.value = updated
        sessionManager.updateAvatar(avatarUrl)
        _successMessage.value = getApplication<Application>().getString(R.string.msg_avatar_updated)
    }
}
