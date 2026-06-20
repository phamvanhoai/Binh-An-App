package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyMessage
import com.example.ui.BinhAnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: BinhAnViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val prayers by viewModel.prayers.collectAsState()
    val savedIds by viewModel.savedMessageIds.collectAsState()
    val streakCount by viewModel.streakCount.collectAsState()
    val coPrayCount by viewModel.coPrayCount.collectAsState()
    val sentPrayersCount by viewModel.sentPrayersCount.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showAvatarPickerDialog by remember { mutableStateOf(false) }

    // Tab state: 0 = My wishes, 1 = Saved messages
    var selectedSubTab by remember { mutableStateOf(0) }

    // Forms
    var editName by remember { mutableStateOf(currentUser?.resolvedName ?: "") }
    var editBio by remember { mutableStateOf(currentUser?.bio ?: "") }

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    // Available aesthetic indigenous avatars to choose from (No URL entries!)
    val presetAvatars = remember {
        listOf(
            Triple("🪷", "Sen Tịnh Độ", "Thanh khiết, khai mở"),
            Triple("🕯️", "Chúc Đăng Ấm", "Sưởi ấm, soi sáng"),
            Triple("💨", "Hương Trầm Nhiên", "Tĩnh tụ, tụ khí"),
            Triple("🌟", "Ánh Như Ý", "Duyên khởi cát tường"),
            Triple("🌙", "An Yên Nguyệt", "Dịu mát, dung hòa"),
            Triple("🧘", "Điểm Thiền Lâm", "Vừa vặn, định tâm")
        )
    }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            editName = it.resolvedName ?: ""
            editBio = it.bio ?: ""
        }
    }

    // Filter my wishes (either generated locally or matching currentUser name/author)
    val myPrayers = remember(prayers, currentUser) {
        prayers.filter {
            it.user?.resolvedId == currentUser?.resolvedId || it.id.startsWith("temp_")
        }
    }

    // Mapped saved messages database (mock corresponding titles to bookmarks)
    val savedMessages = remember(savedIds) {
        val predefinedList = listOf(
            DailyMessage("today_1", "An Trú Trong Hiện Tại", "Bình an không phải ở phương trời xa nào đó, nó hiện diện ngay trong từng hơi thở nhẹ nhàng và từng khoảng lặng chánh niệm giữa đời thường hối hả.", "Thiền Sư Pháp Hạnh"),
            DailyMessage("msg_arch_1", "Gieo Hạt Mầm Bình Yên", "Cơ thể lắng dịu, tâm trí an yên. Khi biết mỉm cười với những khó khăn vô thường, ta gặt hái quả ngọt tự do và tự tại ấm áp tuyệt vời.", "Thầy Thích Minh Niệm"),
            DailyMessage("msg_arch_2", "Biết Ơn Những Đơn Sơ", "Khi ta dừng lại để cảm nhận sự kỳ diệu của một chén trà ấm, lòng biết ơn tự khắc nở rộ như đóa hoa hướng dương đón nắng sớm.", "Khuyết Danh"),
            DailyMessage("msg_arch_3", "Thả Lỏng Áp Lực", "Chăm sóc tâm hồn như chăm một đóa thảo lan quý giá. Đừng bắt bản thân gánh vác quá nhiều, nước chảy êm mây trôi nhẹ vốn tự an vui.", "Sách Ngẫm"),
            DailyMessage("msg_arch_4", "Dung Thứ Bản Thân", "Vấp ngã hay yếu sầu cũng tốt, đó chỉ là chặng chuyển tiếp trên hành trình thức tỉnh sâu sắc để thấu hiểu thế giới tươi đẹp hơn.", "An Trú"),
            DailyMessage("msg_arch_5", "Mở Rộng Không Gian Lòng", "Trong tim càng ít phán xét, cuộc hành trình tiếp xúc vạn vật càng rộng mở chứa chan tình yêu thương vô điều kiện kỳ diệu nhất.", "Cát An")
        )
        predefinedList.filter { savedIds.contains(it.id ?: "") }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF060913),
                        Color(0xFF091225),
                        Color(0xFF0F1E36)
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            Text(
                text = "HÀNH TRÌNH TĨNH LẶNG",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
            )
            Text(
                text = "Ghi nhận quả ngọt tu dưỡng tâm thức mỗi ngày",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Expanded Profile Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Aesthetic Circle Avatar Picker trigger
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .clickable { showAvatarPickerDialog = true }
                            .testTag("avatar_container"),
                        contentAlignment = Alignment.Center
                    ) {
                        val avatarStamp = currentUser?.avatar ?: "🪷"
                        if (avatarStamp.length == 1 || avatarStamp.length == 2) {
                            Text(avatarStamp, fontSize = 52.sp)
                        } else {
                            // Standard default
                            Text("🪷", fontSize = 52.sp)
                        }

                        // Edit Overlay Badge Indicator
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .align(Alignment.BottomEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Edit Avatar",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentUser?.displayName ?: "Bạn Hữu Bình An",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = currentUser?.email ?: "@binhan_user",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentUser?.bio?.ifEmpty { "Gieo hạt lành chánh niệm, tích lũy khoảng lặng bình an trôi chảy." }
                            ?: "Gieo hạt lành chánh niệm, tích lũy khoảng lặng bình an trôi chảy.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic, lineHeight = 18.sp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Chỉnh hồ sơ", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }

                        OutlinedButton(
                            onClick = { showPasswordDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        ) {
                            Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Đổi mật khẩu", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        // Elegant Statistics Grid Section
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileMetricCard(
                    title = "Gửi Bình An",
                    value = "$sentPrayersCount",
                    unit = "Lời nguyện",
                    modifier = Modifier.weight(1f)
                )
                ProfileMetricCard(
                    title = "Đồng Nguyện",
                    value = "$coPrayCount",
                    unit = "Lần tâm tụ",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileMetricCard(
                    title = "Lưu Thư Thái",
                    value = "${savedIds.size}",
                    unit = "Thông điệp",
                    modifier = Modifier.weight(1f)
                )
                ProfileMetricCard(
                    title = "Chuỗi Ngày",
                    value = "$streakCount",
                    unit = "Ngày thói quen",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Subtabs selection panel
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf("Tâm nguyện đã gửi", "Thông điệp đã lưu").forEachIndexed { idx, label ->
                    val isCurr = selectedSubTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedSubTab = idx }
                            .drawBehindUnderline(
                                isCurr,
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp.value
                            )
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isCurr) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCurr) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // List contents based on active tab
        if (selectedSubTab == 0) {
            if (myPrayers.isEmpty()) {
                item {
                    EmptyAreaLayout(
                        icon = Icons.Default.Spa,
                        label = "Bạn chưa gửi lời bình an nào.",
                        subLabel = "Hãy thắp một ngọn nến hoặc dâng một nén hương lòng nhẹ để bắt đầu hành trình nhé."
                    )
                }
            } else {
                items(myPrayers) { prayer ->
                    val prayerType = prayer.type ?: "peace"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = prayerTypeIcon(prayerType),
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = prayerTypeLabel(prayerType),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = prayer.content,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = prayer.createdAt ?: "Hôm nay",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Saved daily messages
            if (savedMessages.isEmpty()) {
                item {
                    EmptyAreaLayout(
                        icon = Icons.Default.BookmarkBorder,
                        label = "Những thông điệp bạn lưu sẽ nằm ở đây.",
                        subLabel = "Khi đọc thông điệp mỗi ngày, hãy bấm nút Lưu để xem lại lời khuyên ấm cúng ấy nhé."
                    )
                }
            } else {
                items(savedMessages) { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = msg.title ?: "Trọn Vẹn",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                                        .clickable { viewModel.unsaveMessage(msg.id ?: "") }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Bỏ lưu", fontSize = 9.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = msg.content,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "— ${msg.author ?: "Bình An"}",
                                fontSize = 10.sp,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // Logout
        item {
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp)
                    .testTag("logout_button")
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đăng xuất tài khoản khỏi hệ thống", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            }
        }
    }

    // AESTHETIC SPARK NATIVE AVATAR DIALOG PICKER
    if (showAvatarPickerDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarPickerDialog = false },
            confirmButton = {},
            title = {
                Text(
                    "Chọn Pháp Danh Đại Diện",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(presetAvatars) { (emoji, label, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .clickable {
                                    viewModel.updateAvatar(emoji)
                                    showAvatarPickerDialog = false
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        TextButton(
                            onClick = { showAvatarPickerDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Bỏ qua")
                        }
                    }
                }
            }
        )
    }

    // EDIT PROFILE DIALOG
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Cập nhật chữ ký hồ sơ") },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotEmpty()) {
                            viewModel.updateProfile(editName.trim(), editBio.trim())
                            showEditProfileDialog = false
                        }
                    },
                    enabled = editName.isNotEmpty()
                ) {
                    Text("Lưu hồ sơ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Bỏ qua")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Tên hiển thị / Pháp danh *") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Châm ngôn niệm hành") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }

    // UPDATE PASSWORD DIALOG
    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Thay đổi khóa mật mã") },
            confirmButton = {
                Button(
                    onClick = {
                        if (oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                            viewModel.updatePassword(oldPassword, newPassword)
                            showPasswordDialog = false
                            oldPassword = ""
                            newPassword = ""
                        }
                    },
                    enabled = oldPassword.isNotEmpty() && newPassword.isNotEmpty()
                ) {
                    Text("Lưu mật khẩu mới")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Bỏ qua")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it },
                        label = { Text("Mật khẩu hiện tại") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Mật khẩu bảo mật mới") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}

@Composable
fun ProfileMetricCard(
    title: String,
    value: String,
    unit: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(82.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(value, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
            Text(unit, fontSize = 9.sp, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun EmptyAreaLayout(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    subLabel: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f),
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
        Text(
            text = subLabel,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp,
            modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp)
        )
    }
}

fun Modifier.drawBehindUnderline(enabled: Boolean, color: Color, strokeWidth: Float) = this.drawBehind {
    if (enabled) {
        val y = this.size.height - strokeWidth / 2
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(this.size.width, y),
            strokeWidth = strokeWidth
        )
    }
}
