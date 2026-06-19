package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DailyMessage
import com.example.ui.BinhAnViewModel
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayMessageScreen(viewModel: BinhAnViewModel) {
    val todayMsg by viewModel.todayMessage.collectAsState()
    val savedIds by viewModel.savedMessageIds.collectAsState()
    val context = LocalContext.current

    // Local state for selected archive messages to view them in the quote card
    var activeMessage by remember { mutableStateOf<DailyMessage?>(null) }

    LaunchedEffect(todayMsg) {
        if (todayMsg != null && activeMessage == null) {
            activeMessage = todayMsg
        }
    }

    val currentMsg = activeMessage ?: todayMsg ?: DailyMessage(
        id = "today_1",
        title = "An Trú Trong Hiện Tại",
        content = "Bình an không phải ở phương trời xa nào đó, nó hiện diện ngay trong từng hơi thở nhẹ nhàng và từng khoảng lặng chánh niệm giữa đời thường hối hả.",
        author = "Thiền Sư Pháp Hạnh",
        date = "Hôm nay"
    )

    val isSaved = savedIds.contains(currentMsg.id ?: "")

    // Beautiful historical messages
    val archiveMessages = remember {
        listOf(
            DailyMessage(
                id = "msg_arch_1",
                title = "Gieo Hạt Mầm Bình Yên",
                content = "Cơ thể lắng dịu, tâm trí an yên. Khi biết mỉm cười với những khó khăn vô thường, ta gặt hái quả ngọt tự do và tự tại ấm áp tuyệt vời.",
                author = "Thầy Thích Minh Niệm",
                date = "Hôm qua"
            ),
            DailyMessage(
                id = "msg_arch_2",
                title = "Biết Ơn Những Đơn Sơ",
                content = "Khi ta dừng lại để cảm nhận sự kỳ diệu của một chén trà ấm, lòng biết ơn tự khắc nở rộ như đóa hoa hướng dương đón nắng sớm.",
                author = "Khuyết Danh",
                date = "2 ngày trước"
            ),
            DailyMessage(
                id = "msg_arch_3",
                title = "Thả Lỏng Áp Lực",
                content = "Chăm sóc tâm hồn như chăm một đóa thảo lan quý giá. Đừng bắt bản thân gánh vác quá nhiều, nước chảy êm mây trôi nhẹ vốn tự an vui.",
                author = "Sách Ngẫm",
                date = "3 ngày trước"
            ),
            DailyMessage(
                id = "msg_arch_4",
                title = "Dung Thứ Bản Thân",
                content = "Vấp ngã hay yếu sầu cũng tốt, đó chỉ là chặng chuyển tiếp trên hành trình thức tỉnh sâu sắc để thấu hiểu thế giới tươi đẹp hơn.",
                author = "An Trú",
                date = "4 ngày trước"
            ),
            DailyMessage(
                id = "msg_arch_5",
                title = "Mở Rộng Không Gian Lòng",
                content = "Trong tim càng ít phán xét, cuộc hành trình tiếp xúc vạn vật càng rộng mở chứa chan tình yêu thương vô điều kiện kỳ diệu nhất.",
                author = "Cát An",
                date = "5 ngày trước"
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF060913),
                        Color(0xFF091225),
                        Color(0xFF0B1B2A)
                    )
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // App header tab title
        item {
            Text(
                text = "THÔNG ĐIỆP CHỮA LÀNH",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
            )
            Text(
                text = "Khoảng lặng suy ngẫm cho tâm hồn an yên",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Beautiful Interactive Glowing Lotus Canvas
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Lotus drawing
                LotusWaterCanvas()

                // Small glass overlay text
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .background(
                            Color.Black.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Vạn vật phẳng lặng, lòng người bình yên",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Subtitle & Main Quote Card
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("quote_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = "Quote Icon",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentMsg.title ?: "Tâm An Trú",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = Alignment.CenterHorizontally.let { TextAlign.Center }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentMsg.content,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = currentMsg.author?.let { "— $it" } ?: "— Lời Sương Mai",
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reflection prompt
                    Text(
                        text = "[ Câu hỏi suy ngẫm ]",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text = when (currentMsg.id) {
                            "msg_arch_1" -> "Đâu là khó khăn bạn đang mỉm cười đón nhận hôm nay?"
                            "msg_arch_2" -> "Bạn cảm thấy hạnh phúc nhất khi nhớ tới chén trà hay góc phố bé nhỏ nào?"
                            "msg_arch_3" -> "Hãy hít một hơi sâu và hạ bớt gánh nặng trên vai xuống được không?"
                            else -> "Ngay giây phút này, điều gì ở xung quanh đang mang lại hơi ấm cho trái tim bạn?"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Dialog Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val msgId = currentMsg.id ?: ""
                                if (isSaved) {
                                    viewModel.unsaveMessage(msgId)
                                } else {
                                    viewModel.saveMessage(msgId)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Lưu"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isSaved) "Đã Lưu" else "Lưu thông điệp", fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "\"${currentMsg.content}\"\n\n— Trích từ ${currentMsg.author ?: "Ứng dụng Bình An"}"
                                    )
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ bình an qua..."))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(imageVector = Icons.Outlined.Share, contentDescription = "Chia sẻ")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Chia sẻ khí lành", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Archive selection list header
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Lịch sử chiêm nghiệm gần đây",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Horizontally scrolling archive items
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Today as a row option too
                item {
                    val isTodayActive = activeMessage == todayMsg || activeMessage?.id == todayMsg?.id
                    Card(
                        modifier = Modifier
                            .width(180.dp)
                            .height(115.dp)
                            .clickable { activeMessage = todayMsg },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isTodayActive) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            } else {
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                            }
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isTodayActive) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "HÔM NAY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = todayMsg?.title ?: "An Trú Hôm Nay",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = todayMsg?.content ?: "Bình an trong hơi thở nhẹ nhàng ấm áp...",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Archives
                items(archiveMessages) { archive ->
                    val isActive = activeMessage?.id == archive.id
                    Card(
                        modifier = Modifier
                            .width(180.dp)
                            .height(115.dp)
                            .clickable { activeMessage = archive },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isActive) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                            } else {
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                            }
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = archive.date?.uppercase() ?: "KHOẢNG LẶNG",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = archive.title ?: "Bảo Lộc",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = archive.content,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 2,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LotusWaterCanvas() {
    val transition = rememberInfiniteTransition(label = "lotus_anim")
    val waveOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    val scaleLotus by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerY = height * 0.7f

        // Draw Cosmic backdrop gradients
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF101B34),
                    Color(0xFF030509)
                ),
                center = Offset(width / 2f, height / 2f),
                radius = width * 0.8f
            )
        )

        // Draw subtle stardust/gleams
        val stars = listOf(
            Offset(width * 0.15f, height * 0.25f) to 2.5f,
            Offset(width * 0.25f, height * 0.15f) to 1.5f,
            Offset(width * 0.45f, height * 0.35f) to 2.0f,
            Offset(width * 0.75f, height * 0.15f) to 3.0f,
            Offset(width * 0.85f, height * 0.3f) to 1.8f,
            Offset(width * 0.6f, height * 0.25f) to 1.2f,
            Offset(width * 0.35f, height * 0.5f) to 2.2f,
            Offset(width * 0.65f, height * 0.55f) to 1.4f
        )
        for ((pos, sizeFactor) in stars) {
            val pulse = 0.5f + 0.5f * sin(waveOffset + pos.x)
            drawCircle(
                color = Color(0xFFF7C35F).copy(alpha = 0.3f + 0.6f * pulse),
                radius = sizeFactor.dp.toPx() * (1f + 0.3f * pulse),
                center = pos
            )
        }

        // Draw ambient lighting glow behind the lotus
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFD166).copy(alpha = 0.25f),
                    Color.Transparent
                ),
                center = Offset(width / 2f, centerY),
                radius = 120.dp.toPx()
            ),
            radius = 120.dp.toPx(),
            center = Offset(width / 2f, centerY)
        )

        // Draw quiet Water Waves
        val wavePath = Path()
        wavePath.moveTo(0f, centerY)
        for (x in 0..width.toInt() step 5) {
            val relativeX = x.toFloat() / width
            val waveY = centerY + 12f * sin(relativeX * 3f * Math.PI.toFloat() + waveOffset)
            wavePath.lineTo(x.toFloat(), waveY)
        }
        wavePath.lineTo(width, height)
        wavePath.lineTo(0f, height)
        wavePath.close()

        drawPath(
            path = wavePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0F2642).copy(alpha = 0.8f),
                    Color(0xFF050E1B).copy(alpha = 1.0f)
                ),
                startY = centerY,
                endY = height
            )
        )

        // Extra decorative ripples lines
        for (i in 1..3) {
            val ripplePath = Path()
            val yOffset = centerY + (i * 20.dp.toPx())
            ripplePath.moveTo(0f, yOffset)
            for (x in 0..width.toInt() step 10) {
                val relativeX = x.toFloat() / width
                val rippleY = yOffset + 6f * sin(relativeX * 4f * Math.PI.toFloat() - waveOffset - (i * 0.5f))
                ripplePath.lineTo(x.toFloat(), rippleY)
            }
            drawPath(
                path = ripplePath,
                color = Color(0xFFF5B842).copy(alpha = 0.15f / i),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // DRAW LOTUS FLOWER using custom canvas path with pulsing animation scale
        val lotusCenter = Offset(width / 2f, centerY)
        val petalScale = scaleLotus

        // Draw petals (symmetric overlapping ellipses)
        drawLotusPetals(lotusCenter, petalScale)
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLotusPetals(center: Offset, scale: Float) {
    val petalCount = 8
    val offset10 = 10.dp.toPx()
    val baseWidth = 22.dp.toPx() * scale
    val baseHeight = 48.dp.toPx() * scale

    // Draw Outer petals
    for (i in 0 until petalCount) {
        val angleDeg = (i * (360f / petalCount)) + 15f
        withTransform({
            translate(center.x, center.y - offset10)
            rotate(angleDeg + 90f, Offset.Zero)
            scale(0.8f * scale, 1.0f * scale, Offset.Zero)
        }) {
            val path = Path().apply {
                moveTo(0f, 0f)
                cubicTo(-baseWidth, -baseHeight / 3f, -baseWidth / 2f, -baseHeight, 0f, -baseHeight)
                cubicTo(baseWidth / 2f, -baseHeight, baseWidth, -baseHeight / 3f, 0f, 0f)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD4A373).copy(alpha = 0.35f),
                        Color(0xFFF5B842).copy(alpha = 0.7f)
                    )
                )
            )
        }
    }

    // Centre Bud & Lotus Heart Glow
    drawCircle(
        color = Color(0xFFFFD166),
        radius = 12.dp.toPx() * scale,
        center = Offset(center.x, center.y - offset10)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.8f),
        radius = 6.dp.toPx() * scale,
        center = Offset(center.x, center.y - offset10)
    )
}
