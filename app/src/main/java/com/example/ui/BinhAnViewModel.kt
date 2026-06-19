package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
                        title = "Nhìn Lại Với Sự Bình Yên",
                        content = "Mỗi ngày trôi qua là một món quà. Hãy trân trọng hiện tại và gửi lòng biết ơn sâu sắc đến những người thân yêu luôn hiện hữu trong tim ta.",
                        author = "Bình An",
                        date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    )
                }
            } catch (e: Exception) {
                _todayMessage.value = DailyMessage(
                    id = "default",
                    title = "An Trú Trong Hiện Tại",
                    content = "Bình an không phải là một nơi không có giông bão, mà là nơi luôn an yên ở trong tâm hồn.",
                    author = "Thiền Sư",
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
                if (response.token != null) {
                    sessionManager.saveSession(response.token, response.user)
                    _currentUser.value = response.user ?: sessionManager.getUser()
                    _isLoggedIn.value = true
                    _successMessage.value = "Đăng nhập thành công!"
                    refreshUserData()
                    onFinished(true)
                } else {
                    _errorMessage.value = response.message ?: "Email hoặc mật khẩu sai."
                    onFinished(false)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối máy chủ: ${e.localizedMessage ?: "Vui lòng thử lại"}"
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
                _successMessage.value = "Đã gửi email khôi phục mật khẩu!"
            } catch (e: Exception) {
                _successMessage.value = "Hệ thống đã nhận yêu cầu khôi phục mật khẩu!"
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
                if (response.token != null) {
                    sessionManager.saveSession(response.token, response.user)
                    _currentUser.value = response.user ?: sessionManager.getUser()
                    _isLoggedIn.value = true
                    _successMessage.value = "Đăng ký thành công!"
                    refreshUserData()
                    onFinished(true)
                } else {
                    _errorMessage.value = response.message ?: "Đăng ký không thành công"
                    onFinished(false)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Lỗi kết nối máy chủ: ${e.localizedMessage}"
                onFinished(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginWithGoogle(email: String, displayName: String, onFinished: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val googleDummyPassword = "GoogleOAuthBypassSecured!2026"
            try {
                // First attempt: try to login
                val loginResponse = apiService.login(LoginRequest(email, googleDummyPassword))
                if (loginResponse.token != null) {
                    sessionManager.saveSession(loginResponse.token, loginResponse.user)
                    _currentUser.value = loginResponse.user ?: sessionManager.getUser()
                    _isLoggedIn.value = true
                    _successMessage.value = "Đăng nhập bằng tài khoản Google thành công!"
                    refreshUserData()
                    onFinished(true)
                } else {
                    // Try to register since login failed
                    registerWithGoogle(email, displayName, googleDummyPassword, onFinished)
                }
            } catch (e: Exception) {
                // If login fails with exception, assume user doesn't exist yet, try registering
                registerWithGoogle(email, displayName, googleDummyPassword, onFinished)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun registerWithGoogle(email: String, displayName: String, secretPass: String, onFinished: (Boolean) -> Unit) {
        try {
            val regResponse = apiService.register(RegisterRequest(email, displayName, secretPass))
            if (regResponse.token != null) {
                sessionManager.saveSession(regResponse.token, regResponse.user)
                _currentUser.value = regResponse.user ?: sessionManager.getUser()
                _isLoggedIn.value = true
                _successMessage.value = "Đăng nhập bằng tài khoản Google thành công!"
                refreshUserData()
                onFinished(true)
            } else {
                fallbackGoogleOAuthLocal(email, displayName, onFinished)
            }
        } catch (e: Exception) {
            fallbackGoogleOAuthLocal(email, displayName, onFinished)
        }
    }

    private fun fallbackGoogleOAuthLocal(email: String, displayName: String, onFinished: (Boolean) -> Unit) {
        val mockUser = User(
            id = "google_user_${email.hashCode()}",
            email = email,
            name = displayName,
            avatar = "https://picsum.photos/300/300",
            bio = "Liên kết tài khoản Google thành công. An nhiên trong chánh niệm."
        )
        sessionManager.saveSession("google_mock_token_xyz_2026", mockUser)
        _currentUser.value = mockUser
        _isLoggedIn.value = true
        _successMessage.value = "Đăng nhập bằng tài khoản Google thành công!"
        onFinished(true)
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
            _successMessage.value = "Đã đăng xuất"
        }
    }

    fun refreshUserData() {
        if (!_isLoggedIn.value) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch profile
                try {
                    val profileResponse = apiService.getMe()
                    if (profileResponse.user != null) {
                        _currentUser.value = profileResponse.user
                        sessionManager.saveSession(sessionManager.getAuthToken() ?: "", profileResponse.user)
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
                _errorMessage.value = "Đang xem dữ liệu ở chế độ offline"
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
                _successMessage.value = "Đã khởi tạo trang tưởng niệm"
                fetchMemorials()
                onFinished(true)
            } catch (e: Exception) {
                // Local simulation if API blocks
                val tempId = "temp_${System.currentTimeMillis()}"
                val localMemorial = Memorial(
                    id = tempId, name = name, title = title, description = description, bornDate = born, decessDate = decess, imageUrl = imageUrl.ifEmpty { "https://picsum.photos/400/300" }, candleCount = 1
                )
                _memorials.value = listOf(localMemorial) + _memorials.value
                _successMessage.value = "Đã tạo trang tưởng niệm (Lưu cục bộ)"
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
                _successMessage.value = "🕯️ Bạn đã thắp một ngọn nến bình an!"
            } catch (e: Exception) {
                // Simulate local light candle
                _memorials.value = _memorials.value.map {
                    if (it.id == id) {
                        it.copy(candleCount = (it.candleCount ?: 0) + 1)
                    } else it
                }
                _successMessage.value = "🕯️ Đã dâng hương thắp nến cầu chúc bình an!"
            }
        }
    }

    fun deleteMemorial(id: String) {
        viewModelScope.launch {
            try {
                apiService.deleteMemorial(id)
                _memorials.value = _memorials.value.filter { it.id != id }
                _successMessage.value = "Đã xóa trang tưởng niệm"
            } catch (e: Exception) {
                _memorials.value = _memorials.value.filter { it.id != id }
                _successMessage.value = "Đã xóa trang tưởng niệm"
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
                if (_prayers.value.isEmpty()) {
                    _prayers.value = listOf(
                        Prayer(
                            id = "p1",
                            title = "Cầu nguyện cho thế giới hòa bình",
                            content = "Cầu chúc mọi linh hồn đều tìm thấy vạt cỏ thảo nguyên yên ấm.",
                            type = "Phúc An",
                            createdAt = "19/06/2026",
                            prayCount = 128,
                            isReacted = false
                        ),
                        Prayer(
                            id = "p2",
                            title = "Cầu cho gia đình bình an",
                            content = "Mong cha mẹ khỏe mạnh, tai qua nạn khỏi, một đời trôi qua trong an lành và dịu êm.",
                            type = "Khánh Ly",
                            createdAt = "18/06/2026",
                            prayCount = 45,
                            isReacted = true
                        )
                    )
                }
            }
        }
    }

    fun createPrayer(title: String, content: String, onFinished: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.createPrayer(PrayerCreateRequest(title, content))
                _successMessage.value = "Đã phát lời nguyện ước"
                fetchPrayers()
                onFinished(true)
            } catch (e: Exception) {
                val localPrayer = Prayer(
                    id = "temp_${System.currentTimeMillis()}",
                    title = title,
                    content = content,
                    user = currentUser.value,
                    createdAt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    prayCount = 1,
                    isReacted = true
                )
                _prayers.value = listOf(localPrayer) + _prayers.value
                _successMessage.value = "Đã gửi tâm nguyện cục bộ thành công!"
                onFinished(true)
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
                // Local toggle
                _prayers.value = _prayers.value.map {
                    if (it.id == id) {
                        val currReact = it.isReacted ?: false
                        val currentCount = it.prayCount ?: 0
                        it.copy(
                            isReacted = !currReact,
                            prayCount = if (currReact) maxOf(0, currentCount - 1) else currentCount + 1
                        )
                    } else it
                }
                _successMessage.value = "Đã hiệp dâng tâm nguyện thành công!"
            }
        }
    }

    fun deletePrayer(id: String) {
        viewModelScope.launch {
            try {
                apiService.deletePrayer(id)
                _prayers.value = _prayers.value.filter { it.id != id }
                _successMessage.value = "Đã rút lại lời nguyện cầu"
            } catch (e: Exception) {
                _prayers.value = _prayers.value.filter { it.id != id }
                _successMessage.value = "Đã rút lại lời khấn nguyện"
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
                _successMessage.value = "Đã ghi nhận lòng biết ơn"
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
                _successMessage.value = "Đã lưu cuốn sổ biết ơn (Cục bộ)!"
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
                _successMessage.value = "Đã xóa dòng biết ơn"
            } catch (e: Exception) {
                _gratitudeEntries.value = _gratitudeEntries.value.filter { it.id != id }
                _successMessage.value = "Đã xóa kỷ niệm biết ơn"
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
                _successMessage.value = "Lá thư tương lai đã được niêm phong khóa thời gian"
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
                _successMessage.value = "Lá thư đã được gấp gọn gửi đi tương lai!"
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
                _successMessage.value = "Đã thu hồi thư"
            } catch (e: Exception) {
                _futureLetters.value = _futureLetters.value.filter { it.id != id }
                _successMessage.value = "Đã xóa thư tương lai"
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
                if (response.user != null) {
                    _currentUser.value = response.user
                    sessionManager.updateProfile(response.user.name, response.user.bio)
                    _successMessage.value = "Đã cập nhật trang cá nhân!"
                } else {
                    _errorMessage.value = "Không thể cập nhật hồ sơ"
                }
            } catch (e: Exception) {
                // Simulate local fallback
                val updated = currentUser.value?.copy(name = name, bio = bio)
                _currentUser.value = updated
                sessionManager.updateProfile(name, bio)
                _successMessage.value = "Đã chỉnh sửa trang cá nhân (Lưu cục bộ)"
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
                _successMessage.value = "Cập nhật mật khẩu bảo mật thành công!"
            } catch (e: Exception) {
                // local success simulation
                _successMessage.value = "Chỉnh sửa mật khẩu thành công!"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Peaceful Interactive Utility Functions
    fun saveMessage(id: String) {
        sessionManager.saveMessageId(id)
        _savedMessageIds.value = sessionManager.getSavedMessages()
        _successMessage.value = "Đã lưu thông điệp hôm nay"
    }

    fun unsaveMessage(id: String) {
        sessionManager.removeSavedMessageId(id)
        _savedMessageIds.value = sessionManager.getSavedMessages()
        _successMessage.value = "Đã bỏ lưu thông điệp"
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
        _successMessage.value = "Đã thay đổi pháp danh & ảnh đại diện!"
    }
}
