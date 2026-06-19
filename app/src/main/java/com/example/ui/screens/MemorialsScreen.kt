package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.Memorial
import com.example.ui.BinhAnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorialsScreen(
    viewModel: BinhAnViewModel
) {
    val memorials by viewModel.memorials.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedMemorial by remember { mutableStateOf<Memorial?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    // Dialog form state
    var newName by remember { mutableStateOf("") }
    var newTitle by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }
    var newBorn by remember { mutableStateOf("") }
    var newDecess by remember { mutableStateOf("") }
    var newImg by remember { mutableStateOf("") }

    val filtered = memorials.filter {
        it.name.contains(searchQuery, ignoreCase = true) || 
        (it.title ?: "").contains(searchQuery, ignoreCase = true)
    }

    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .testTag("add_memorial_fab") // access tag
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tạo trang tưởng niệm")
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
                text = "TRANG TƯỞNG NIỆM",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Tìm kiếm trang tưởng niệm...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            if (isLoading && memorials.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filtered.isEmpty()) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Chưa có trang tưởng niệm nào phù hợp.",
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Hãy nhấp (+) để tự thiết lập hoặc cầu phúc lành.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered) { memorial ->
                        MemorialItemCard(
                            memorial = memorial,
                            onLightCandle = {
                                viewModel.lightCandle(memorial.id)
                            },
                            onClick = {
                                selectedMemorial = memorial
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

        // Selected Memorial DETAIL DIALOG
        selectedMemorial?.let { memorial ->
            AlertDialog(
                onDismissRequest = { selectedMemorial = null },
                confirmButton = {
                    TextButton(onClick = { selectedMemorial = null }) {
                        Text("Đóng")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteMemorial(memorial.id)
                            selectedMemorial = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Xóa trang")
                    }
                },
                title = {
                    Text(
                        text = memorial.name,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Image
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(memorial.imageUrl ?: "https://picsum.photos/400/300")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Memorial Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Life range
                        Text(
                            text = "${memorial.bornDate ?: "..."}  —  ${memorial.decessDate ?: "..."}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Text(
                            text = memorial.title ?: "Hiền thế an giấc",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        Divider(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = memorial.description ?: "Tâm hồn luôn hướng về chốn thanh nhã, cội nguồn gia đình thương mến.",
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Light candle interactive button
                        Button(
                            onClick = {
                                viewModel.lightCandle(memorial.id)
                                // Refresh dialogue details
                                selectedMemorial = selectedMemorial?.copy(
                                    candleCount = (selectedMemorial?.candleCount ?: 0) + 1
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.LocalFireDepartment, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Dâng Hương / Thắp Nến (${memorial.candleCount ?: 0} lượt)")
                        }
                    }
                }
            )
        }

        // CREATE DIALOG
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Tống nguyện tưởng niệm") },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newName.isNotEmpty()) {
                                viewModel.createMemorial(
                                    name = newName.trim(),
                                    title = newTitle.trim(),
                                    description = newDesc.trim(),
                                    born = newBorn.trim().ifEmpty { "1940" },
                                    decess = newDecess.trim().ifEmpty { "2024" },
                                    imageUrl = newImg.trim()
                                ) {
                                    showCreateDialog = false
                                    // Reset fields
                                    newName = ""
                                    newTitle = ""
                                    newDesc = ""
                                    newBorn = ""
                                    newDecess = ""
                                    newImg = ""
                                }
                            }
                        },
                        enabled = newName.isNotEmpty(),
                        modifier = Modifier.testTag("add_memorial_button")
                    ) {
                        Text("Tiến hành khởi tạo")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Bỏ qua")
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = newName,
                            onValueChange = { newName = it },
                            label = { Text("Tên người mất *") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("memorial_name_input")
                        )
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("Mối quan hệ (VD: Ông ngoại yêu quý)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("memorial_title_input")
                        )
                        OutlinedTextField(
                            value = newBorn,
                            onValueChange = { newBorn = it },
                            label = { Text("Năm sinh (VD: 15/06/1945)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newDecess,
                            onValueChange = { newDecess = it },
                            label = { Text("Năm khuất (VD: 10/02/2025)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newDesc,
                            onValueChange = { newDesc = it },
                            label = { Text("Lời tưởng nhớ / Tiếu sử") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = newImg,
                            onValueChange = { newImg = it },
                            label = { Text("Đường dẫn ảnh (để trống nếu dùng mặc định)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun MemorialItemCard(
    memorial: Memorial,
    onLightCandle: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle Image
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(memorial.imageUrl ?: "https://picsum.photos/400/300")
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = memorial.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = memorial.title ?: "Hiền thế an giấc",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${memorial.bornDate ?: "..."} - ${memorial.decessDate ?: "..."}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Candle count highlight circle button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .clickable { onLightCandle() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "${memorial.candleCount ?: 0}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontSize = 10.sp
                )
            }
        }
    }
}
