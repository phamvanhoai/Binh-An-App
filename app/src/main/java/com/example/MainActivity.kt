package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.BinhAnViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val viewModel: BinhAnViewModel = viewModel()
        val isLoggedIn by viewModel.isLoggedIn.collectAsState()

        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          if (!isLoggedIn) {
            AuthScreen(
              viewModel = viewModel,
              onAuthSuccess = {
                viewModel.refreshUserData()
              }
            )
          } else {
            MainAppLayout(viewModel = viewModel)
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppLayout(viewModel: BinhAnViewModel) {
  var currentTab by remember { mutableIntStateOf(0) }
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  val errorMessage by viewModel.errorMessage.collectAsState()
  val successMessage by viewModel.successMessage.collectAsState()

  // Listen to network/logic toast notifications
  LaunchedEffect(errorMessage) {
    errorMessage?.let {
      scope.launch {
        snackbarHostState.showSnackbar(it)
        viewModel.clearError()
      }
    }
  }

  LaunchedEffect(successMessage) {
    successMessage?.let {
      scope.launch {
        snackbarHostState.showSnackbar(it)
        viewModel.clearSuccess()
      }
    }
  }

  val menuItems = listOf(
    NavigationTabItem("Trang Chủ", Icons.Default.Home, "home_tab"),
    NavigationTabItem("Thông Điệp", Icons.Default.MenuBook, "today_message_tab"),
    NavigationTabItem("Gửi Bình An", Icons.Default.Spa, "ritual_tab"),
    NavigationTabItem("Cộng Đồng", Icons.Default.People, "community_tab"),
    NavigationTabItem("Cá Nhân", Icons.Default.Person, "profile_tab")
  )

  var preselectedRitualType by remember { mutableStateOf<String?>(null) }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface,
          titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = {
          Row(
            modifier = Modifier.wrapContentSize(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Spa,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "BÌNH AN",
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              letterSpacing = 2.sp
            )
          }
        },
        actions = {
          IconButton(onClick = { viewModel.refreshUserData() }) {
            Icon(
              imageVector = Icons.Default.Sync,
              contentDescription = "Sync records",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("bottom_nav")
      ) {
        menuItems.forEachIndexed { index, item ->
          NavigationBarItem(
            icon = { Icon(item.icon, contentDescription = item.label) },
            label = { Text(item.label, fontSize = 10.sp) },
            selected = currentTab == index,
            onClick = {
              if (index != 2) {
                preselectedRitualType = null
              }
              currentTab = index
            },
            modifier = Modifier.testTag(item.tag)
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTab) {
        0 -> HomeScreen(
          viewModel = viewModel,
          onQuickAction = { ritual ->
            preselectedRitualType = ritual
            currentTab = 2
          },
          onNavigateToTab = { index ->
            currentTab = index
          }
        )
        1 -> TodayMessageScreen(viewModel = viewModel)
        2 -> {
          RitualScreen(
            viewModel = viewModel,
            onNavigateToCommunity = {
              currentTab = 3
            },
            preselectedRitual = preselectedRitualType
          )
        }
        3 -> CommunityScreen(viewModel = viewModel)
        4 -> ProfileScreen(viewModel = viewModel)
      }
    }
  }
}

data class NavigationTabItem(
  val label: String,
  val icon: ImageVector,
  val tag: String
)

