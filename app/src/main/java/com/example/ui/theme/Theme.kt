package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
  darkColorScheme(
    primary = ArcCyan,
    onPrimary = FridayDarkBg,
    primaryContainer = FridayDarkCard,
    onPrimaryContainer = ArcCyan,
    secondary = NeonEmerald,
    onSecondary = FridayDarkBg,
    secondaryContainer = FridayDarkCard,
    onSecondaryContainer = NeonEmerald,
    tertiary = ElectricBlue,
    onTertiary = FridayDarkBg,
    background = FridayDarkBg,
    onBackground = FridayTextPrimary,
    surface = FridayDarkSurface,
    onSurface = FridayTextPrimary,
    surfaceVariant = FridayDarkCard,
    onSurfaceVariant = FridayTextSecondary,
    outline = FridayDarkCardBorder,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}

