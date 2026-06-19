package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BinhAnViewModel
import kotlinx.coroutines.delay
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RitualScreen(
    viewModel: BinhAnViewModel,
    onNavigateToCommunity: () -> Unit,
    preselectedRitual: String? = null
) {
    var selectedRitual by remember { mutableStateOf(preselectedRitual ?: "Nến") }
    var isSubmitted by remember { mutableStateOf(false) }

    // Form states
    var recipientType by remember { mutableStateOf("Cho bản thân") }
    var customRecipientName by remember { mutableStateOf("") }
    var prayerContent by remember { mutableStateOf("") }
    var privacyMode by remember { mutableStateOf("Công khai ẩn danh") }

    val actualRecipient = if (recipientType == "Khác") customRecipientName else recipientType

    // Scroll state
    val scrollState = rememberScrollState()

    if (isSubmitted) {
        RitualSuccessLayout(
            ritualType = selectedRitual,
            recipient = actualRecipient,
            content = prayerContent,
            onClose = {
                isSubmitted = false
                prayerContent = ""
                customRecipientName = ""
            },
            onGotoCommunity = onNavigateToCommunity
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF050B14),
                            Color(0xFF0F1E36)
                        )
                    )
                )
                .verticalScroll(scrollState)
                .padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "GỬI BÌNH AN",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
            )
            Text(
                text = "Gửi một lời chúc nhẹ nhàng, mở lòng đón nhận an vui",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Select Ritual Activity Options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("Nến", "🕯️ Thắp Nến", "Đốt nến ấm"),
                    Triple("Hương", "💨 Thắp Hương", "Dâng hương"),
                    Triple("Hoa đăng", "🪷 Thả Hoa Đăng", "Thả hoa đăng")
                ).forEach { (type, label, _) ->
                    val isSelected = selectedRitual == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedRitual = type },
                        label = {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            selectedLabelColor = MaterialTheme.colorScheme.primary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.primary,
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            borderWidth = 1.dp
                        ),
                        modifier = Modifier.weight(1f).height(42.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Ritual Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                // Renders appropriate drawing
                when (selectedRitual) {
                    "Nến" -> AnimatedCandleCanvas(isLit = false)
                    "Hương" -> AnimatedIncenseCanvas(isLit = false)
                    "Hoa đăng" -> AnimatedLanternCanvas(isLit = false)
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Trở Lành",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ritual Configuration Form Content card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Recipient Section
                    Text(
                        text = "1. Gửi bình an đến ai?",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Cho bản thân", "Cho người thân", "Cho cộng đồng", "Khác").forEach { opt ->
                            val isChosen = recipientType == opt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isChosen) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                    .clickable { recipientType = opt }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = opt,
                                    fontSize = 12.sp,
                                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (recipientType == "Khác") {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = customRecipientName,
                            onValueChange = { customRecipientName = it },
                            placeholder = { Text("Nhập tên người nhận...", fontSize = 14.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Content Section
                    Text(
                        text = "2. Lời nguyện ước của bạn",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = prayerContent,
                        onValueChange = { prayerContent = it },
                        placeholder = {
                            Text(
                                "Viết lời dâng hương thắp nến cầu bình an, chúc cho vạn sự tốt lành ấm cúng...",
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        },
                        minLines = 4,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.2f),
                            unfocusedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().testTag("prayer_input")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Privacy Section
                    Text(
                        text = "3. Chế độ riêng tư",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(
                            "Công khai ẩn danh" to "Mọi người sẽ thấy lời nguyện cầu của bạn dưới dạng ẩn danh.",
                            "Công khai tên" to "Lời nguyện sẽ hiển thị cùng với pháp danh của bạn.",
                            "Riêng tư" to "Chỉ lưu giữ để xem riêng tư trong nhật ký của bản thân."
                        ).forEach { (mode, desc) ->
                            val isSel = privacyMode == mode
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.background.copy(alpha = 0.4f) else Color.Transparent)
                                    .clickable { privacyMode = mode }
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSel,
                                    onClick = { privacyMode = mode },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = mode,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Main Submit CTA button
                    Button(
                        onClick = {
                            if (prayerContent.isNotBlank()) {
                                // Encode metadata in Title: recipient|ritualType|privacy
                                val encodedTitle = "$actualRecipient|$selectedRitual|$privacyMode"
                                viewModel.createPrayer(encodedTitle, prayerContent) { success ->
                                    if (success) {
                                        viewModel.incrementStreak()
                                        viewModel.incrementSentPrayers()
                                        isSubmitted = true
                                    }
                                }
                            }
                        },
                        enabled = prayerContent.isNotBlank() && (recipientType != "Khác" || customRecipientName.isNotBlank()),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("submit_prayer_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Spa, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Khai dâng: ${
                                when (selectedRitual) {
                                    "Nến" -> "Thắp Nến Bình An"
                                    "Hương" -> "Dâng Hương Tĩnh Lặng"
                                    else -> "Thả Hoa Đăng Tự Tại"
                                }
                            }",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RitualSuccessLayout(
    ritualType: String,
    recipient: String,
    content: String,
    onClose: () -> Unit,
    onGotoCommunity: () -> Unit
) {
    var animationRunning by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Run immersive animation for 4.5 seconds then let user view confirmation detail
        delay(4500)
        animationRunning = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030509)),
        contentAlignment = Alignment.Center
    ) {
        if (animationRunning) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ĐANG KHỞI LỄ NGHI THỨC...",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(30.dp))

                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    when (ritualType) {
                        "Nến" -> AnimatedCandleCanvas(isLit = true)
                        "Hương" -> AnimatedIncenseCanvas(isLit = true)
                        "Hoa đăng" -> AnimatedLanternCanvas(isLit = true)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Gửi trọn tôn kính và ước lành lành của bạn...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Immersive clean Success card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Thành công",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Lời Bình An Đã Được Gửi Đi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Nguyện cầu ánh sáng sưởi ấm, hương linh bay bổng và hoa trôi tự tại sẽ đưa tâm ý của bạn đến đúng bến đỗ an lành.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Receipt display box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Nghi lễ:", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                                Text(
                                    text = when (ritualType) {
                                        "Nến" -> "🕯️ Thắp Nến"
                                        "Hương" -> "💨 Thắp Hương"
                                        else -> "🪷 Thả Hoa Đăng"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Gửi đến:", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                                Text(recipient, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "\"$content\"",
                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                color = MaterialTheme.colorScheme.onBackground,
                                maxLines = 3,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onGotoCommunity,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.People, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Xem trong cộng đồng")
                        }

                        OutlinedButton(
                            onClick = onClose,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Gửi thêm lời nguyện ước")
                        }
                    }
                }
            }
        }
    }
}

// ---------------- CANVAS ANIMATIONS ----------------

@Composable
fun AnimatedCandleCanvas(isLit: Boolean) {
    val transition = rememberInfiniteTransition(label = "candle")
    val flameScaleY by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_y"
    )
    val flameWobbleX by transition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(280, easing = SineCrossing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_x"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val cX = width / 2f
        val cY = height * 0.65f

        // Draw background night glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFF5B842).copy(alpha = if (isLit) 0.35f else 0.05f),
                    Color.Transparent
                ),
                center = Offset(cX, cY - 36.dp.toPx()),
                radius = 70.dp.toPx()
            ),
            radius = 70.dp.toPx(),
            center = Offset(cX, cY - 36.dp.toPx())
        )

        // Draw Candle Body (M3 Earth/Ivory tones)
        drawRoundRect(
            color = Color(0xFFEADBAB),
            topLeft = Offset(cX - 18.dp.toPx(), cY),
            size = Size(36.dp.toPx(), 45.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )

        // Candle wax rim top
        drawOval(
            color = Color(0xFFD4C28D),
            topLeft = Offset(cX - 18.dp.toPx(), cY - 4.dp.toPx()),
            size = Size(36.dp.toPx(), 8.dp.toPx())
        )

        // Wick thread
        drawLine(
            color = Color(0xFF3E2723),
            start = Offset(cX, cY),
            end = Offset(cX, cY - 8.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )

        // Flame Drawing (lit always true in feedback, else checks isLit parameter)
        val activeLit = isLit || true // Highlight lit candle with pulse
        if (activeLit) {
            val fY = cY - 10.dp.toPx()
            val fWidth = 10.dp.toPx()
            val fHeight = 24.dp.toPx() * flameScaleY

            // Outer flame glow
            drawCircle(
                color = Color(0xFFFF9E00).copy(alpha = 0.4f),
                radius = 16.dp.toPx(),
                center = Offset(cX + flameWobbleX, fY - 12.dp.toPx())
            )

            // Flame path
            val path = Path().apply {
                moveTo(cX, fY)
                cubicTo(
                    cX - fWidth, fY,
                    cX - fWidth + flameWobbleX, fY - fHeight / 2,
                    cX + flameWobbleX, fY - fHeight
                )
                cubicTo(
                    cX + fWidth + flameWobbleX, fY - fHeight / 2,
                    cX + fWidth, fY,
                    cX, fY
                )
                close()
            }
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFFFD166),
                        Color(0xFFFF5D00)
                    ),
                    startY = fY - fHeight,
                    endY = fY
                )
            )
        }
    }
}

@Composable
fun AnimatedIncenseCanvas(isLit: Boolean) {
    val transition = rememberInfiniteTransition(label = "incense")
    val smokeTime by transition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing)
        ),
        label = "smoke"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val cX = width / 2f
        val cY = height * 0.7f

        // Draw elegant incense burner ceramic bowl (Ivory/Amber)
        val bowlPath = Path().apply {
            moveTo(cX - 45.dp.toPx(), cY)
            lineTo(cX + 45.dp.toPx(), cY)
            quadraticTo(cX + 35.dp.toPx(), cY + 30.dp.toPx(), cX, cY + 30.dp.toPx())
            quadraticTo(cX - 35.dp.toPx(), cY + 30.dp.toPx(), cX - 45.dp.toPx(), cY)
            close()
        }
        drawPath(
            path = bowlPath,
            color = Color(0xFFD4A373)
        )

        // Ash layer top
        drawOval(
            color = Color(0xFF1E243B),
            topLeft = Offset(cX - 42.dp.toPx(), cY - 5.dp.toPx()),
            size = Size(84.dp.toPx(), 10.dp.toPx())
        )

        // 3 incense sticks rising
        val stickHeight = 70.dp.toPx()
        val stickWidth = 1.5.dp.toPx()
        val angles = listOf(-15f, 0f, 15f)

        angles.forEachIndexed { idx, deg ->
            // Draw incense stick
            withTransform({
                translate(cX, cY)
                rotate(deg, pivot = Offset.Zero)
            }) {
                // Brown stick body
                drawLine(
                    color = Color(0xFF8D6E63),
                    start = Offset(0f, 0f),
                    end = Offset(0f, -stickHeight),
                    strokeWidth = stickWidth
                )

                // Burning ember red tip
                drawCircle(
                    color = Color(0xFFFF4500),
                    radius = 2.dp.toPx(),
                    center = Offset(0f, -stickHeight)
                )

                // Glowing small spark
                drawCircle(
                    color = Color(0xFFFFD166).copy(alpha = 0.8f),
                    radius = 1.dp.toPx(),
                    center = Offset(0f, -stickHeight)
                )

                // Draws rising smoke lines
                val activeLit = isLit || true
                if (activeLit) {
                    val smokePath = Path()
                    val startY = -stickHeight
                    smokePath.moveTo(0f, startY)
                    for (step in 1..8) {
                        val currY = startY - (step * 8.dp.toPx())
                        val t = smokeTime * 0.15f + step - (idx * 1.5f)
                        val wobble = 6.dp.toPx() * sin(t) * (step / 8f)
                        smokePath.lineTo(wobble, currY)
                    }

                    drawPath(
                        path = smokePath,
                        color = Color.White.copy(alpha = 0.2f - (deg / 100f)),
                        style = Stroke(width = 2.dp.toPx() + (idx.dp.toPx()))
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedLanternCanvas(isLit: Boolean) {
    val transition = rememberInfiniteTransition(label = "lantern")
    val waveOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    val glideFactor by transition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glide"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val cY = height * 0.65f
        val cX = width / 2f + glideFactor.dp.toPx()

        // Background glows under water
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE29578).copy(alpha = if (isLit) 0.3f else 0.05f),
                    Color.Transparent
                ),
                center = Offset(cX, cY),
                radius = 65.dp.toPx()
            ),
            radius = 65.dp.toPx(),
            center = Offset(cX, cY)
        )

        // Draw rolling river waves
        val path = Path()
        path.moveTo(0f, cY + 10.dp.toPx())
        for (x in 0..width.toInt() step 10) {
            val relativeX = x.toFloat() / width
            val waveY = cY + 10.dp.toPx() + 6f * sin(relativeX * 3.5f * Math.PI.toFloat() + waveOffset)
            path.lineTo(x.toFloat(), waveY)
        }
        path.lineTo(width, height)
        path.lineTo(0f, height)
        path.close()

        drawPath(
            path = path,
            color = Color(0xFF09172A)
        )

        // Draw Lantern Petals (overlapping red/amber curved vectors)
        val lSize = 14.dp.toPx()
        withTransform({
            translate(cX, cY + 4f * sin(waveOffset))
        }) {
            // Draw flower base green leaves
            drawOval(
                color = Color(0xFF2E7D32),
                topLeft = Offset(-lSize * 1.5f, 2.dp.toPx()),
                size = Size(lSize * 3f, lSize * 0.8f)
            )

            // Draw outer petals
            drawCircle(
                color = Color(0xFFE29578),
                radius = lSize * 0.8f,
                center = Offset(-lSize * 0.8f, -4.dp.toPx())
            )
            drawCircle(
                color = Color(0xFFE29578),
                radius = lSize * 0.8f,
                center = Offset(lSize * 0.8f, -4.dp.toPx())
            )
            drawCircle(
                color = Color(0xFFF2A104),
                radius = lSize * 0.8f,
                center = Offset(0f, 2.dp.toPx())
            )

            // Draw center white candle bud
            drawRect(
                color = Color.White,
                topLeft = Offset(-3.dp.toPx(), -lSize),
                size = Size(6.dp.toPx(), lSize)
            )

            // Draw flickering orange fire particle
            val isGlowing = isLit || true
            if (isGlowing) {
                drawCircle(
                    color = Color(0xFFFFD166),
                    radius = 4.dp.toPx(),
                    center = Offset(0f, -lSize - 4.dp.toPx())
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = Offset(0f, -lSize - 4.dp.toPx())
                )
            }
        }
    }
}

// Sine crossing easing custom interpolator for realistic candle breathing
val SineCrossing = Easing { fraction ->
    sin(fraction * Math.PI.toFloat())
}
