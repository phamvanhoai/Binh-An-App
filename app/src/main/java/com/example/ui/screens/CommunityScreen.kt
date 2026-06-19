package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Prayer
import com.example.ui.BinhAnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(viewModel: BinhAnViewModel) {
    val prayers by viewModel.prayers.collectAsState()
    var selectedFilter by remember { mutableStateOf("Tất cả") }

    // Detail dialog state
    var selectedPrayerForDetail by remember { mutableStateOf<Prayer?>(null) }

    // Filter list
    val filteredPrayers = remember(prayers, selectedFilter) {
        if (selectedFilter == "Tất cả") {
            prayers
        } else {
            prayers.filter { prayer ->
                val (_, rType, _) = decodePrayerTitle(prayer.title)
                rType.equals(selectedFilter, ignoreCase = true)
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
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
            )
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Title
            Text(
                text = "CỘNG ĐỒNG ĐỒNG NGUYỆN",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
            )
            Text(
                text = "Hợp duyên tâm ý, cùng hướng lành bình an muôn nơi",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Filtering choices
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Tất cả" to "✨ Tất cả",
                    "Nến" to "🕯️ Nến",
                    "Hương" to "💨 Hương",
                    "Hoa đăng" to "🪷 Hoa đăng"
                ).forEach { (filterType, label) ->
                    val isSelected = selectedFilter == filterType
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(19.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                            )
                            .clickable { selectedFilter = filterType }
                            .padding(horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body List of wishes
            if (filteredPrayers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FilterListOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Hôm nay thật yên tĩnh.",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Hãy gửi lời nguyện đầu tiên cho chuyên mục này nhé.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredPrayers, key = { it.id }) { prayer ->
                        val (recipient, ritualType, privacy) = decodePrayerTitle(prayer.title)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPrayerForDetail = prayer }
                                .testTag("community_prayer_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                // Card Top: Icon representation + Recipient Text
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = when (ritualType) {
                                                    "Nến" -> "🕯️"
                                                    "Hương" -> "💨"
                                                    else -> "🪷"
                                                },
                                                fontSize = 16.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = "Cầu bình an cho: $recipient",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = when (ritualType) {
                                                    "Nến" -> "Thắp Nến Đăng Đàn"
                                                    "Hương" -> "Thắp Hương Đỉnh Trầm"
                                                    else -> "Thả Hoa Đăng Tự Tại"
                                                },
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }

                                    // Badge date
                                    Text(
                                        text = prayer.createdAt ?: "Hôm nay",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Content snippet
                                Text(
                                    text = prayer.content,
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.06f))

                                Spacer(modifier = Modifier.height(12.dp))

                                // Footer: Author + Co-pray CTA
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Tâm nguyện của: ${
                                            if (privacy == "Riêng tư") "Bản thân"
                                            else if (privacy == "Công khai ẩn danh") "Người Bạn Bình An"
                                            else prayer.author ?: "Bạn Hữu"
                                        }",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    // Co-pray interactive button
                                    val coPrayCountStr = "${prayer.prayCount ?: 0}"
                                    val isReacted = prayer.isReacted ?: false

                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isReacted) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                            )
                                            .clickable {
                                                viewModel.togglePrayerReaction(prayer.id)
                                                if (!isReacted) {
                                                    viewModel.incrementCoPray()
                                                }
                                            }
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isReacted) Icons.Default.Favorite else Icons.Default.Spa,
                                            contentDescription = "Đồng Nguyện",
                                            tint = if (isReacted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Đồng nguyện ($coPrayCountStr)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isReacted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail Dialog overlay modal
    selectedPrayerForDetail?.let { activePrayer ->
        val (recipient, ritualType, privacy) = decodePrayerTitle(activePrayer.title)
        val isReacted = activePrayer.isReacted ?: false

        AlertDialog(
            onDismissRequest = { selectedPrayerForDetail = null },
            confirmButton = {},
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier.padding(horizontal = 20.dp),
            text = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prayer_detail_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // Title header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CHI TIẾT LỜI CẦU NGUYỆN",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            IconButton(onClick = { selectedPrayerForDetail = null }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Immersive Ritual Animation Preview
                        Box(
                            modifier = Modifier
                                .fillOuterSpaceAnimWidth()
                                .height(125.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            when (ritualType) {
                                "Nến" -> AnimatedCandleCanvas(isLit = true)
                                "Hương" -> AnimatedIncenseCanvas(isLit = true)
                                else -> AnimatedLanternCanvas(isLit = true)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Wish Title details
                        Text(
                            text = "Gửi tới: $recipient",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Bởi: ${
                                if (privacy == "Riêng tư") "Chỉ mình bạn"
                                else if (privacy == "Công khai ẩn danh") "Bạn Hữu Ẩn Danh"
                                else activePrayer.author ?: "Pháp Hữu"
                            } • ${activePrayer.createdAt ?: "Hôm nay"}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                        Spacer(modifier = Modifier.height(14.dp))

                        // Wish full content
                        Text(
                            text = activePrayer.content,
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Stats Co-Pray display area
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Spa,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${activePrayer.prayCount ?: 0} người đã cùng đồng nguyện",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // List of co-prayed accounts
                        Spacer(modifier = Modifier.height(10.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Đồng hiếu đồng niệm gồm:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Simulated list of spiritual names
                                    listOf("Cát Vân", "Minh Tâm", "Trí Đức", "Tuệ Lâm", "Diệu Ân").take(minOf(5, (activePrayer.prayCount ?: 2) + 1)).forEach { name ->
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(name, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action rows
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.togglePrayerReaction(activePrayer.id)
                                    if (!isReacted) {
                                        viewModel.incrementCoPray()
                                    }
                                },
                                modifier = Modifier.weight(1.5f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isReacted) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.primary,
                                    contentColor = if (isReacted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (isReacted) Icons.Default.Favorite else Icons.Default.Spa,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isReacted) "Đã Đồng Nguyện" else "Hiệp Lực Đồng Nguyện", fontSize = 12.sp)
                            }

                            // Safe flag content report button
                            var isReported by remember { mutableStateOf(false) }
                            OutlinedButton(
                                onClick = { isReported = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Flag,
                                    contentDescription = "Report",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isReported) "Đã Báo" else "Báo cáo", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        )
    }
}

// Global decoders to decode recipient, ritualType, privacy from compound titles
fun decodePrayerTitle(title: String): Triple<String, String, String> {
    val parts = title.split("|")
    return if (parts.size >= 3) {
        Triple(parts[0], parts[1], parts[2])
    } else {
        // Safe fallbacks depending on name guessing
        val rType = when {
            title.contains("hương", ignoreCase = true) -> "Hương"
            title.contains("đăng", ignoreCase = true) || title.contains("hoa đăng", ignoreCase = true) -> "Hoa đăng"
            else -> "Nến"
        }
        Triple(title.ifEmpty { "Gia đình" }, rType, "Công khai ẩn danh")
    }
}

fun Modifier.fillOuterSpaceAnimWidth() = this.fillMaxWidth()
