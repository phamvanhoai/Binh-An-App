package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FutureLetter
import com.example.data.Gratitude
import com.example.ui.BinhAnViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GratitudeLettersScreen(
    viewModel: BinhAnViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Biết Ơn Cuộc Đời", "Phong Thư Tương Lai")

    val gratitudeEntries by viewModel.gratitudeEntries.collectAsState()
    val futureLetters by viewModel.futureLetters.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showCreateGratitudeDialog by remember { mutableStateOf(false) }
    var showCreateLetterDialog by remember { mutableStateOf(false) }

    var selectedGratitude by remember { mutableStateOf<Gratitude?>(null) }
    var selectedLetter by remember { mutableStateOf<FutureLetter?>(null) }

    // Forms
    var gradTitle by remember { mutableStateOf("") }
    var gradContent by remember { mutableStateOf("") }
    var gradDate by remember { mutableStateOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())) }

    var letTitle by remember { mutableStateOf("") }
    var letContent by remember { mutableStateOf("") }
    var deliverDelayMonths by remember { mutableStateOf("12") } // dropdown fallback/picker options

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) {
                        showCreateGratitudeDialog = true
                    } else {
                        showCreateLetterDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag(if (selectedTab == 0) "add_gratitude_fab" else "add_letter_fab")
            ) {
                Icon(
                    imageVector = if (selectedTab == 0) Icons.Default.MenuBook else Icons.Default.SendAndArchive,
                    contentDescription = null
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TĨNH NIỆM & KÝ THƯ",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Switch tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // Tab 1: GRATITUDE JOURNAL
                if (isLoading && gratitudeEntries.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (gratitudeEntries.isEmpty()) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Hôm nay bạn hữu biết ơn điều gì?",
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Trân quý một tách cafe, ánh mặt trời lung linh... Hãy viết lại để cất giữ nụ cười hạnh phúc.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(gratitudeEntries) { entry ->
                            GratitudeCard(
                                entry = entry,
                                onClick = { selectedGratitude = entry }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
            } else {
                // Tab 2: FUTURE TIME CAPSULE
                if (isLoading && futureLetters.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (futureLetters.isEmpty()) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SendAndArchive,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Chưa có lá thư tương lai phao kín",
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Hãy khóa chặt mục tiêu, tham ước thời gian 1 năm, 5 năm để thúc giục bản thân tiến về phía trước.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(futureLetters) { letter ->
                            LetterCard(
                                letter = letter,
                                onClick = { selectedLetter = letter }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(100.dp)) }
                    }
                }
            }
        }

        // Selected Gratitude DETAILED AlertDialog
        selectedGratitude?.let { entry ->
            AlertDialog(
                onDismissRequest = { selectedGratitude = null },
                confirmButton = {
                    TextButton(onClick = { selectedGratitude = null }) {
                        Text("Trân trọng")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteGratitude(entry.id)
                            selectedGratitude = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Xóa trang nhật ký")
                    }
                },
                title = {
                    Text(
                        text = entry.title ?: "Khoảnh khắc ngọt dịu",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Lưu ngày: ${entry.date ?: "Hôm nay"}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = entry.content,
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }

        // Selected Letter DETAILED View
        selectedLetter?.let { letter ->
            // Check if actual date has passed or fake unlocked is set
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            var isLocked = true
            try {
                val targetDate = format.parse(letter.deliverAt)
                if (targetDate != null && targetDate.before(Date())) {
                    isLocked = false
                }
            } catch (e: Exception) {
                isLocked = letter.isUnlocked == false
            }

            AlertDialog(
                onDismissRequest = { selectedLetter = null },
                confirmButton = {
                    TextButton(onClick = { selectedLetter = null }) {
                        Text("Đã hiểu")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteFutureLetter(letter.id)
                            selectedLetter = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Xóa thư")
                    }
                },
                title = {
                    Text(
                        text = letter.title,
                        color = if (isLocked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Ngày niêm phong: ${letter.createdAt ?: "Vừa qua"}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "Kỳ hạn mở cửa: ${letter.deliverAt}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))

                        if (isLocked) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer.copy(
                                            alpha = 0.2f
                                        ), RoundedCornerShape(12.dp)
                                    )
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "PHONG THƯ KHÓA THỜI GIAN",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Vũ trụ đang bảo mật phong thư này cho đến ${letter.deliverAt}. Bạn hãy chú tâm gặt hái hiện tại nhé!",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            Text(
                                text = letter.content ?: "Chào bạn, đây là những dòng khát vọng bạn ghi nhận x xưa.",
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        }

        // CREATE GRATITUDE DIALOG
        if (showCreateGratitudeDialog) {
            AlertDialog(
                onDismissRequest = { showCreateGratitudeDialog = false },
                title = { Text("Bạn biết ơn điều gì hôm nay?") },
                confirmButton = {
                    Button(
                        onClick = {
                            if (gradContent.isNotEmpty()) {
                                viewModel.createGratitude(
                                    title = gradTitle.trim().ifEmpty { "Điều biết ơn ngọt dịu" },
                                    content = gradContent.trim(),
                                    date = gradDate.trim()
                                ) {
                                    showCreateGratitudeDialog = false
                                    gradTitle = ""
                                    gradContent = ""
                                }
                            }
                        },
                        enabled = gradContent.isNotEmpty(),
                        modifier = Modifier.testTag("add_gratitude_button")
                    ) {
                        Text("Lưu nhật ký")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateGratitudeDialog = false }) {
                        Text("Bỏ qua")
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = gradTitle,
                            onValueChange = { gradTitle = it },
                            label = { Text("Tên khoảnh khắc (VD: Bữa cơm gia đình)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("gratitude_title_input")
                        )
                        OutlinedTextField(
                            value = gradContent,
                            onValueChange = { gradContent = it },
                            label = { Text("Chi tiết nguyên cớ biết ơn cuộc đời... *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("gratitude_content_input")
                        )
                        OutlinedTextField(
                            value = gradDate,
                            onValueChange = { gradDate = it },
                            label = { Text("Ngày ghi nhận") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )
        }

        // CREATE LETTER DIALOG
        if (showCreateLetterDialog) {
            AlertDialog(
                onDismissRequest = { showCreateLetterDialog = false },
                title = { Text("Niêm phong phong thư thời gian") },
                confirmButton = {
                    Button(
                        onClick = {
                            if (letTitle.isNotEmpty() && letContent.isNotEmpty()) {
                                // Calculate deliverAt ISO date based on months selected
                                val calendar = Calendar.getInstance()
                                val months = deliverDelayMonths.toIntOrNull() ?: 12
                                calendar.add(Calendar.MONTH, months)
                                val deliverAtStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

                                viewModel.createFutureLetter(
                                    title = letTitle.trim(),
                                    content = letContent.trim(),
                                    deliverAt = deliverAtStr
                                ) {
                                    showCreateLetterDialog = false
                                    letTitle = ""
                                    letContent = ""
                                }
                            }
                        },
                        enabled = letTitle.isNotEmpty() && letContent.isNotEmpty(),
                        modifier = Modifier.testTag("add_letter_button")
                    ) {
                        Text("Khóa & Gửi Đi")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateLetterDialog = false }) {
                        Text("Hủy bỏ")
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = letTitle,
                            onValueChange = { letTitle = it },
                            label = { Text("Phong thư gửi tôi năm... *") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("letter_title_input")
                        )
                        OutlinedTextField(
                            value = letContent,
                            onValueChange = { letContent = it },
                            label = { Text("Viết khát vọng lớn gửi gắm bản thân trong tương lai...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("letter_content_input")
                        )
                        
                        Text("Thời gian niêm phong khóa bảo mật:", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("1", "6", "12", "60").forEach { months ->
                                val text = when(months) {
                                    "1" -> "1 Tháng"
                                    "6" -> "6 Tháng"
                                    "12" -> "1 Năm"
                                    else -> "5 Năm"
                                }
                                var isSelected = deliverDelayMonths == months
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { deliverDelayMonths = months },
                                    label = { Text(text, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun GratitudeCard(
    entry: Gratitude,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.title ?: "Khoảnh khắc biết ơn",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = entry.date ?: "Hôm nay",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}

@Composable
fun LetterCard(
    letter: FutureLetter,
    onClick: () -> Unit
) {
    // Check if target date is in future
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var isLocked = true
    try {
        val targetDate = format.parse(letter.deliverAt)
        if (targetDate != null && targetDate.before(Date())) {
            isLocked = false
        }
    } catch (e: Exception) {
        isLocked = letter.isUnlocked == false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isLocked) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isLocked) Icons.Outlined.Lock else Icons.Default.MarkAsUnread,
                    contentDescription = null,
                    tint = if (isLocked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = letter.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isLocked) "Đang khóa đến: ${letter.deliverAt}" else "Đã mở khóa! Chạm để đọc",
                    fontSize = 11.sp,
                    color = if (isLocked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    fontWeight = if (isLocked) FontWeight.Normal else FontWeight.Bold
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            )
        }
    }
}
