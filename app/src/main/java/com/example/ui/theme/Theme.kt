package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val ActiveColorScheme =
  darkColorScheme(
    primary = BinhAnPrimary,
    onPrimary = BinhAnBackground,
    primaryContainer = BinhAnSurfaceVariant,
    onPrimaryContainer = BinhAnPrimary,
    secondary = BinhAnSecondary,
    onSecondary = BinhAnBackground,
    secondaryContainer = BinhAnSurfaceVariant,
    onSecondaryContainer = BinhAnSecondary,
    background = BinhAnBackground,
    surface = BinhAnSurface,
    onBackground = BinhAnOnBackground,
    onSurface = BinhAnOnSurface,
    onSurfaceVariant = BinhAnOnSurfaceVariant,
    error = BinhAnError,
    onError = BinhAnOnBackground
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme for peaceful night look
  dynamicColor: Boolean = false, // Disable dynamic colors to preserve our hand-crafted gold/amber/deep-blue design
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = ActiveColorScheme,
    typography = Typography,
    content = content
  )
}
